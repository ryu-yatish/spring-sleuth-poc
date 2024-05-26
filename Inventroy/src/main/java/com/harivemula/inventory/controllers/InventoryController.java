package com.harivemula.inventory.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harivemula.inventory.Repository.ItemRepository;
import com.harivemula.inventory.dto.Item;
import com.harivemula.inventory.service.SQSEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class InventoryController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ItemRepository itemRepository;

    private final ObjectMapper objectMapper= new ObjectMapper();

    @GetMapping("/itemAll")
    public List<Item> sayHello() {
        List<Item> items= itemRepository.findAll();
        return items;
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Item> getCustomerDetails(@PathVariable String itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if(itemOptional.isPresent())return ResponseEntity.ok(itemOptional.get());
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
