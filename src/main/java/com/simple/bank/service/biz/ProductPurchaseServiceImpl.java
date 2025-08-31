package com.simple.bank.service.biz;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.converter.ProductConverter;
import com.simple.bank.dto.*;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.entity.ProductPurchaseExpand;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import com.simple.bank.mapper.ProductPurchaseMapper;
import com.simple.bank.service.redis.ProductRedisService;
import com.simple.bank.service.redis.RedissionLock;
import com.simple.bank.validator.ProductPurchaseValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductPurchaseServiceImpl implements ProductPurchaseService {

    @Autowired
    private ProductPurchaseMapper purchaseMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private AccountInquireService accountInquireService;
    @Autowired
    private CustomerInquireService customerInquireService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ProductConverter productConverter;
    @Autowired
    private ProductPurchaseValidator purchaseValidator;
    @Autowired
    private RedissionLock redissionLock;
    @Autowired
    private MessageNotification messageNotification;
    @Autowired
    private ProductRedisService productRedisService;
    @Autowired
    private ProductStockWarmupService productStockWarmupService;

    @Override
    @Transactional
    public ProductPurchaseDTO purchase(ProductPurchaseRequest request) throws BusinessException {
        // lock until purchase transaction completed
        // -- only 1 thread can update Product remaining amount at the same time
        return redissionLock.lock("purchase:product:lock:" + request.getProductId(), 500L,
                () -> doPurchase(request));
    }

    private ProductPurchaseDTO doPurchase(ProductPurchaseRequest request) throws BusinessException {
        // 1. inquire account info (customerId, balance etc)
        AccountDTO accountDTO = accountInquireService.getAccountById(request.getAccountId());

        // 2. inquire product info
        ProductDTO product = productService.getProductById(request.getProductId());

        // 3. validate request
        purchaseValidator.validate(request, product, accountDTO.getBalance());

        // 4. deduct amount from account - select for update mode
        AccountTransactionDTO accountTransactionDTO = debitAccountBalance(request);

        // 5. log purchase record
        ProductPurchaseEntity purchaseEntity = createPurchaseRecord(request, product, accountDTO.getCustomerId());
        try {
            purchaseMapper.insertPurchase(purchaseEntity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Duplicate Purchase");
        }

        // 6. deduct product remaining amount - will check if still have enough remaining amount again.
        boolean result = deductProductStock(request.getProductId(), request.getPurchaseAmount(), product.getIsHot());

        // 7. send purchase notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(accountDTO.getCustomerId());
        messageNotification.sendPurchaseNotification(purchaseEntity, accountTransactionDTO.getAccountBalance(), customerDTO.getMobile(), customerDTO.getEmail());

        return productConverter.productPurchaseToDto(purchaseEntity);
    }


    @Override
    public List<ProductPurchaseDTO> getPurchaseHistory(Long customerId) throws BusinessException {
        List<ProductPurchaseExpand> productPurseList = purchaseMapper.getPurchaseByCustomerId(customerId);
        return productPurseList.stream()
                .map(productConverter::productPurchaseExpandToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductPurchaseDTO getPurchaseByTraceId(String transactionTraceId) throws BusinessException {
        ProductPurchaseExpand productPurseExpand = purchaseMapper.getPurchaseByTraceId(transactionTraceId);
        ProductPurchaseDTO productPurchaseDTO = productConverter.productPurchaseExpandToDto(productPurseExpand);
        return productPurchaseDTO;
    }

    // deduct purchase amount from account
    // call transactionService.debitAccountBalance to make sure transaction history/audit log etc to be handled.
    //      debitAccountBalance : select for update to make sure balance not updated by others
    //                            validate balance again after select for update in case other trx updated baln
    private AccountTransactionDTO  debitAccountBalance(ProductPurchaseRequest request) throws BusinessException {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOperContext(request.getOperContext());
        transactionRequest.setTransactionAmount(request.getPurchaseAmount());
        transactionRequest.setAccountId(request.getAccountId());
        transactionRequest.setDescription("purchase product");
        transactionRequest.setTransactionTraceId(request.getTransactionTraceId());

        AccountTransactionDTO accountTransactionDTO = transactionService.debitAccountBalance(transactionRequest);
        return accountTransactionDTO;
    }

    // create purchase record
    private ProductPurchaseEntity createPurchaseRecord(ProductPurchaseRequest request,
                                                       ProductDTO product,
                                                       Long customerId) {
        ProductPurchaseEntity purchase = new ProductPurchaseEntity();
        purchase.setProductId(request.getProductId());
        purchase.setCustomerId(customerId);
        purchase.setAccountId(request.getAccountId());
        purchase.setPurchaseAmount(request.getPurchaseAmount());
        purchase.setPurchaseTime(LocalDateTime.now());
        purchase.setStatus("HOLDING");
        purchase.setTransactionTraceId(request.getTransactionTraceId());
//        purchase.setProductName(product.getProductName());   // ###phoebe
        return purchase;
    }

    private boolean deductProductStock(Long productId, BigDecimal amount, Integer isHot) {

        // for hot product
        if (isHot > 0) {
            boolean redisDeductSuccess = deductProductStockFromRedis(productId, amount);
            if (redisDeductSuccess) {
                return true;
            }
            log.warn("[Product Purchase] Hot product [{}] Redis deduction failed, falling back to DB", productId);
        }
        // for fallback case or non-hot product
        return deductProductStockFromDB(productId, amount);
    }

    private boolean deductProductStockFromRedis(Long productId, BigDecimal amount) {
        // 1st try ： deduct from redis
        BigDecimal remainingAmount = productRedisService.deductProductStockById(productId, amount);

        // if deduct from Redis failed, warm up product and deduct again
        if (remainingAmount == null) {
            if (!productStockWarmupService.warmupProductStock(productId)) {
                return false; // warm up failed，return false to fall back to DB
            }
            // warm up succ, deduct from redis again
            remainingAmount = productRedisService.deductProductStockById(productId, amount);
        }

        if (remainingAmount == null) {
            return false; // if still deduct failed, return false to fall back to DB
        }

        // check if still have quota
        if (remainingAmount.compareTo(BigDecimal.ZERO) < 0) {
            // roll back if don't have enough quota
            productRedisService.increaseProductStockById(productId, amount);
            log.warn("[Product Purchase] Product [{}] insufficient remaining subscription quota", productId);
            throw new BusinessException("PRODUCT_OUT_OF_STOCK", "Insufficient remaining subscription quota");
        }

        return true;
    }

    private boolean deductProductStockFromDB(Long productId, BigDecimal amount) {
        int updateCount = productMapper.deductRemainingAmount(productId, amount);
        if (updateCount < 1) {
            log.warn("[Product Purchase] Product [{}] insufficient stock in DB", productId);
            throw new BusinessException("PRODUCT_OUT_OF_STOCK", "Insufficient remaining subscription quota");
        }
        return true;
    }
}