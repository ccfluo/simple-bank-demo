package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"response", "products"})
@Setter @Getter
public class ProductResponse {
    private Response response;
    private ProductDTO product;

    public ProductResponse(ProductDTO productDTO){
        this.response = new Response();
        this.product = productDTO;
    }

}
