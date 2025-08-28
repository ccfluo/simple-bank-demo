package com.simple.bank.service.biz;

import com.simple.bank.api.request.TransferRequest;
import com.simple.bank.dto.TransferDTO;
import com.simple.bank.exception.BusinessException;

public interface TransferService {
    TransferDTO transfer(TransferRequest request) throws BusinessException;
}