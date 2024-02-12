package com.bronx.productservice.repository;

import com.bronx.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product,String> {
    Boolean existsProductBySkuCode(String skuCode);
}
