package com.simple.bank.controller;

import com.simple.bank.api.request.ProductPurchaseRequest;
import com.simple.bank.api.response.ListOfProductPurchaseResponse;
import com.simple.bank.api.response.ProductPurchaseResponse;
import com.simple.bank.api.response.ListOfProductResponse;
import com.simple.bank.dto.ProductDTO;
import com.simple.bank.dto.ProductPurchaseDTO;
import com.simple.bank.dto.Response;
import com.simple.bank.service.biz.ProductService;
import com.simple.bank.service.biz.ProductPurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductPurchaseService productPurchaseService;

    // inquire on-sale product
    @GetMapping("/on-sale")
    public ResponseEntity<ListOfProductResponse> getOnSaleProducts() {
        List<ProductDTO> products = productService.getOnSaleProducts();
        return ResponseEntity.ok(new ListOfProductResponse(products));
    }

    // purchase
    @PostMapping("/purchase")
    public ResponseEntity<ProductPurchaseResponse> purchase(@RequestBody ProductPurchaseRequest request) {
        ProductPurchaseDTO purchase = productPurchaseService.purchase(request);
        return ResponseEntity.ok(new ProductPurchaseResponse(purchase, "Purchase successfully"));
    }

    // inquire purchase history
    @GetMapping("/purchase/history/{customerId}")
    public ResponseEntity<ListOfProductPurchaseResponse> getPurchaseHistory(@PathVariable Long customerId) {
        List<ProductPurchaseDTO> history = productPurchaseService.getPurchaseHistory(customerId);

        if (history == null || history.isEmpty()) {
            ListOfProductPurchaseResponse responseList = new ListOfProductPurchaseResponse(null);
            responseList.setResponse(new Response("NOT_FOUND", "No purchase history found"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseList);
        }
        return ResponseEntity.ok(new ListOfProductPurchaseResponse(history));
    }
}