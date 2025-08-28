package com.simple.bank.converter;

import com.simple.bank.dto.TransferDTO;
import com.simple.bank.entity.TransferEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TransferConverter {
    public TransferDTO transferToDto(TransferEntity transfer) {
        TransferDTO dto = new TransferDTO();
        BeanUtils.copyProperties(transfer, dto);
        return dto;
    }

    public TransferEntity dtoToTransfer(TransferDTO dto) {
        TransferEntity transfer = new TransferEntity();
        BeanUtils.copyProperties(dto, transfer);
        return transfer;
    }
}