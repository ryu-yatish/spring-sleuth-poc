package com.harivemula.products.services;

import com.harivemula.products.dto.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Slf4j

@Service
public class ItemService {

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8082/";

    @Autowired
    private RestTemplate restTemplate;

    public void printAllItems() {
        String url = INVENTORY_SERVICE_URL + "itemAll";
        ResponseEntity<List<Item>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Item>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            List<Item> items = response.getBody();
            if (items != null) {
                items.forEach(System.out::println);
            } else {
                log.warn("Response body is empty");
            }
        } else {
            throw new ResponseStatusException(response.getStatusCode(), "Failed to fetch items");
        }
    }

    public void printItemById(String itemId) {
        String url = INVENTORY_SERVICE_URL + "item/" + itemId;
        ResponseEntity<Item> response = restTemplate.getForEntity(url, Item.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Item item = response.getBody();
            if (item != null) {
                System.out.println(item);
            } else {
                log.warn("Response body is empty");
            }
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            System.out.println("Item not found");
        } else {
            throw new ResponseStatusException(response.getStatusCode(), "Failed to fetch item details");
        }
    }
}

