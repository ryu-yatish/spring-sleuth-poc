package com.harivemula.products.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ProductController {
    private static final String Inventory = "http://localhost:8082/products/customer/{id}";
    @RequestMapping("/products")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello From Products!");
    }

    @RequestMapping("/products/customer/{customerId}")
    public ResponseEntity<String> getCustomerProducts(@PathVariable String customerId) {
        log.debug("customerId:{}",customerId);
        log.info("customerId:{}",customerId);
        if ("error".equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(String.format("CustomerId: %s; Products-A,B,C,D", customerId));
    }

}
