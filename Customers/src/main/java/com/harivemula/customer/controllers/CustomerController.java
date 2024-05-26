package com.harivemula.customer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harivemula.customer.dto.Customer;
import com.harivemula.customer.service.SQSEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;


    private final ObjectMapper objectMapper= new ObjectMapper();


    @Autowired
    private SQSEventPublisher sqsEventPublisher;
    private static final String PRODUCTS_URL = "http://localhost:8081/products/customer/{id}";

    @GetMapping("/customers")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from Customers!");
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<String> getCustomerDetails(@PathVariable String customerId) {
        if(customerId=="error") throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok("CustomerId: "+customerId+"; Name: Static User");
    }


    @GetMapping("/customers/products/{customerId}")
    public ResponseEntity<String> getCustomerProducts (@PathVariable String customerId) {
        String products = restTemplate.getForObject(PRODUCTS_URL, String.class, customerId);
        log.info("Products:["+products+"]");
        return ResponseEntity.ok(products);
    }

    @PostMapping("/customer/create/{customerId}")
    public ResponseEntity<String> createCustomer(@PathVariable String customerId) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree("{" + "\"Records\":"+"\""+ customerId +"\"" + "}");
        sqsEventPublisher.publishEvent(jsonNode);

        return ResponseEntity.status(HttpStatus.CREATED).body("Customer Created");
    }


}
