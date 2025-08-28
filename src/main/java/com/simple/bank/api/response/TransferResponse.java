package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.Response;
import com.simple.bank.dto.TransferDTO;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "transferDetails"})
@Setter @Getter
public class TransferResponse {
    private Response response;
    private TransferDTO transferDetails;

    public TransferResponse(TransferDTO transferDTO, String message) {
        this.response = new Response(message);
        this.transferDetails = transferDTO;
    }
}