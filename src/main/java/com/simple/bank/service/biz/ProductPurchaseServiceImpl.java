package com.simple.bank.service.biz;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.api.request.TransactionRequest;
import com.simple.bank.converter.ProductConverter;
import com.simple.bank.dto.AccountDTO;
import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.entity.ProductPurchaseEntity;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.mapper.ProductMapper;
import com.simple.bank.mapper.ProductPurchaseMapper;
import com.simple.bank.service.redis.RedissionLock;
import com.simple.bank.validator.ProductPurchaseValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public ProductPurchaseDTO purchase(ProductPurchaseRequest request) throws BusinessException {
        // lock until purchase transaction completed
        // -- only 1 thread can update Product remaining amount at the same time
        return redissionLock.lock("purchase_product_lock:" + String.valueOf(request.getProductId()), 500L,
                () -> doPurchase(request));
    }

    private ProductPurchaseDTO doPurchase(ProductPurchaseRequest request) throws BusinessException {
        // 1. inquire account info (customerId, balance etc)
        AccountDTO accountDTO = accountInquireService.getAccountById(request.getAccountId());

        // 2. validate request
        purchaseValidator.validate(request, accountDTO.getBalance());

        // 3. deduct amount from account
        AccountTransactionDTO accountTransactionDTO = debitAccountBalance(request);

        // 4. deduct product remaining amount - sql will check if still have enough remaining amount again.
        int productResult = productMapper.deductRemainingAmount(
                request.getProductId(),
                request.getPurchaseAmount()
        );
        if (productResult < 1) {
            throw new BusinessException("PRODUCT_OUT_OF_STOCK", "Insufficient remaining subscription quota");
        }

        // 5. log purchase record
        ProductPurchaseEntity purchaseEntity = createPurchaseRecord(request, accountDTO.getCustomerId());
        try {
            purchaseMapper.insertPurchase(purchaseEntity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Duplicate Purchase");
        }

        // 6. send purchase notification
        CustomerDTO customerDTO = customerInquireService.getCustomerById(accountDTO.getCustomerId());
        messageNotification.sendPurchaseNotification(purchaseEntity, accountTransactionDTO.getAccountBalance(), customerDTO.getMobile(), customerDTO.getEmail());

        return productConverter.productPurchaseToDto(purchaseEntity);
    }

    // deduct purchase amount from account
    // call transactionService.debitAccountBalance to make sure transaction history/audit log etc to be handled.
    //      debitAccountBalance : select for update to make sure balance not updated by others
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
    private ProductPurchaseEntity createPurchaseRecord(ProductPurchaseRequest request, Long customerId) {
        ProductPurchaseEntity purchase = new ProductPurchaseEntity();
        purchase.setProductId(request.getProductId());
        purchase.setCustomerId(customerId);
        purchase.setAccountId(request.getAccountId());
        purchase.setPurchaseAmount(request.getPurchaseAmount());
        purchase.setPurchaseTime(LocalDateTime.now());
        purchase.setStatus("HOLDING");
        purchase.setTransactionTraceId(request.getTransactionTraceId());
        return purchase;
    }

    @Override
    public List<ProductPurchaseDTO> getPurchaseHistory(Long customerId) throws BusinessException {
        List<ProductPurchaseEntity> productPurseList = purchaseMapper.getPurchaseByCustomerId(customerId);

        return productPurseList.stream()
                .map(productConverter::productPurchaseToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductPurchaseDTO getPurchaseByTraceId(String transactionTraceId) throws BusinessException {
        ProductPurchaseEntity productPurseEntity = purchaseMapper.getPurchaseByTraceId(transactionTraceId);
        ProductPurchaseDTO productPurchaseDTO = productConverter.productPurchaseToDto(productPurseEntity);
        return productPurchaseDTO;
    }
}