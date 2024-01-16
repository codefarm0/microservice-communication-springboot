package com.codefarm.productclient.service;

import com.codefarm.productclient.model.Product;
import com.codefarm.productclient.model.ProductClientCreateRessponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ProductClientService {

    private WebClient productWebClient;

    public ProductClientService(WebClient.Builder webclientBuilder) {
        productWebClient = webclientBuilder.baseUrl("http://localhost:8081")
                .build();
    }

    public ProductClientCreateRessponse createProduct(Product product) {
        return productWebClient.post()

                .uri("/product")
                .header("","")
                .cookie("", "")
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(ProductClientCreateRessponse.class)
                .block();
    }

    public String updateProduct(Product product) {
        return productWebClient.put()
                .uri("/product")
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public Product getProductById(int id) {
        return productWebClient.get()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    public List<Product> getProductsByName(String name) {
        String uri = UriComponentsBuilder.fromPath("/product/")
                .queryParam("name", name)
                .buildAndExpand()
                .toUriString();

        return productWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();
    }

    public String deleteProduct(int id) {
        return productWebClient.delete()
                .uri("/product/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
