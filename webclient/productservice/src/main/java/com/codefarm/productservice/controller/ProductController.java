package com.codefarm.productservice.controller;

import com.codefarm.productservice.model.Product;
import com.codefarm.productservice.model.ProductCreateRessponse;
import com.codefarm.productservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("")
    ProductCreateRessponse createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }
    @PutMapping("")
    String updateProduct(@RequestBody Product product){
        return productService.updateProduct(product);
    }

    @GetMapping("/{id}")
    Product getProductById(@PathVariable int id){
        return productService.getProductById(id);
    }

    //http:/localhost:8080/product?name='java'
    @GetMapping("/")
    List<Product> getProductById(@RequestParam String name){
        return productService.getProductsByName(name);
    }
    @DeleteMapping("/{id}")
    String deleteProduct(@PathVariable int id){
        return productService.deleteProduct(id);
    }

}
