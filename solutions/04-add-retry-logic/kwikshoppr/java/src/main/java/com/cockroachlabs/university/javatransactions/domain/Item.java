package com.cockroachlabs.university.javatransactions.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Item {
    private UUID item_id;
    private String name;
    private String description;
    private int quantity;
    private double price;
}
