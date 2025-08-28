package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonPropertyOrder({"response", "products"})
@Setter @Getter
public class ListOfProductResponse {
    private Response response;
    private List<ProductDTO> products;

    public ListOfProductResponse(List<ProductDTO> productDTOList){
        this.response = new Response();
        this.products = productDTOList;
    }

}
