package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "product"})
@Setter @Getter
public class ProductPurchaseResponse {
    private Response response;
    private ProductPurchaseDTO purchase;

    public ProductPurchaseResponse(ProductPurchaseDTO dto, String message) {
        this.response = new Response(message);
        this.purchase = dto;
    }
}