package com.simple.bank.service.biz;

import com.simple.bank.api.request.CustomerAddRequest;
import com.simple.bank.api.request.CustomerUpdateRequest;
import com.simple.bank.converter.CustomerConverter;
import com.simple.bank.dto.CustomerDTO;
import com.simple.bank.entity.CustomerEntity;
import com.simple.bank.mapper.CustomerMapper;
import com.simple.bank.exception.BusinessException;
import com.simple.bank.validator.CustomerAddValidator;
import com.simple.bank.validator.CustomerDeleteValidator;
import com.simple.bank.validator.CustomerUpdateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerMaintServiceImpl implements CustomerMaintService {
    @Autowired
    private CustomerMapper customerMapper;  // auto inject the mapper

//  @Autowired 是 Spring 的依赖注入注解，
//  用于自动获取 Spring 容器中已管理的 Bean，并注入到当前类的属性中。
//  告诉 Spring“我需要这个类型的实例，请从容器里找一个给我
    @Autowired
    private CustomerConverter customerConverter;
    @Autowired
    private CustomerUpdateValidator customerUpdateValidator;
    @Autowired
    private CustomerAddValidator customerAddValidator;
    @Autowired
    private CustomerDeleteValidator customerDeleteValidator;
    @Autowired
    private CustomerInquireService customerInquireService;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired CustomerRedisService customerRedisService;

    @Override
    @Transactional
    public Long createCustomer(CustomerAddRequest customerAddRequest) throws BusinessException {
        customerAddValidator.Validate(customerAddRequest);
        CustomerEntity customerEntity = customerConverter.customerDtoToEntity(customerAddRequest.getCustomer());
        try{
            // insert to customer table
            int result = customerMapper.insertCusotmer(customerEntity);
            if (result < 1) {
                throw new BusinessException("DB_INSERT_FAIL", "No record inserted, pls check backend");
            }

            CustomerDTO createdCustomerDTO = customerInquireService.getCustomerById(customerEntity.getCustomerId());
            // output to audit log
            auditLogService.logCustomerOperation(
                    customerEntity.getCustomerId(),
                    "CREATE",
                    null,
                    auditLogService.toJson(createdCustomerDTO),
                    customerAddRequest.getOperContext().getUserId()
            );
            return customerEntity.getCustomerId();
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Customer existing");
        }
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(CustomerUpdateRequest customerUpdateRequest) throws BusinessException {

        customerUpdateValidator.Validate(customerUpdateRequest);
//        CustomerDTO beforeCustomerDTO = customerInquireService.getCustomerById(customerUpdateRequest.getCustomer().getCustomerId());
        CustomerEntity beforeCustomerEntity = customerMapper.
                getCustomerByIdForUpdate(customerUpdateRequest.getCustomer().getCustomerId());
        CustomerDTO beforeCustomerDTO = customerConverter.customerToDto(beforeCustomerEntity);
        String beforeData = auditLogService.toJson(beforeCustomerDTO);

        CustomerEntity customerEntity = customerConverter.customerDtoToEntity(customerUpdateRequest.getCustomer());
        try{
            int result = customerMapper.dynamicUpdateCustomer(customerEntity);
            if (result < 1) {
                throw new BusinessException("DB_UPDATE_FAIL", "No record updated, pls check backend");
            }
            customerRedisService.delete(customerEntity.getCustomerId());
            // get customer details after update
            CustomerDTO updatedCustomerDTO = customerInquireService.getCustomerById(customerUpdateRequest.getCustomer().getCustomerId());
            auditLogService.logCustomerOperation(
                    customerUpdateRequest.getCustomer().getCustomerId(),
                    "UPDATE",
                    beforeData,
                    auditLogService.toJson(updatedCustomerDTO),
                    customerUpdateRequest.getOperContext().getUserId()
            );
            // redis delayed double deletion
            customerRedisService.delete(customerEntity.getCustomerId());
            return updatedCustomerDTO;
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Customer existing");
        }

    }

    @Override
    @Transactional
    public int deleteCustomer(Long customerId) throws BusinessException{
        customerDeleteValidator.Validate(customerId);
        CustomerDTO beforeCustomerDTO = customerInquireService.getCustomerById(customerId);
        String beforeData = auditLogService.toJson(beforeCustomerDTO);
        try{
            int result = customerMapper.deleteCustomer(customerId);
            if (result < 1) {
                throw new BusinessException("DB_DELETE_FAIL", "No record deleted, pls check backend");
            }
            customerRedisService.delete(customerId);
            auditLogService.logCustomerOperation(
                    customerId,
                    "DELETE",
                    beforeData,
                    null, // 删除操作无后置数据
                    "system"
            );
            return(result);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("DUPLICATE_KEY", "Customer existing");
        }
    }

}
