package com.harivemula.inventory.Repository;

import com.harivemula.inventory.dto.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item,String> {
}
