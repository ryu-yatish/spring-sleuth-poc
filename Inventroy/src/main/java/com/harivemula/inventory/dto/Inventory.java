package com.harivemula.inventory.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Inventory {
    private String name;
    private String id;
}
