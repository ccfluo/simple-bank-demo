package com.simple.bank.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonPropertyOrder({"response", "productPurchases"})
@Setter @Getter
public class ListOfProductPurchaseResponse {
    private Response response;
    private List<ProductPurchaseDTO> productPurchases;

    public ListOfProductPurchaseResponse(List<ProductPurchaseDTO> productPurchaseDTOList){
//        this.response = ResultSetter.success().getResponse();
        this.response = new Response();
        this.productPurchases = productPurchaseDTOList;
    }

}
