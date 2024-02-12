package com.bronx.productservice.controller;

import com.bronx.productservice.model.Product;
import com.bronx.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Product> findAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String request){
        System.out.println(request);
        return productRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody Product product){
        productRepository.save(product);
    }

    @GetMapping("/check/{skuCode}")
    public Boolean checkProductExisted(@PathVariable String skuCode){
        return productRepository.existsProductBySkuCode(skuCode);
    }
}
