package com.cockroachlabs.university.javatransactions.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItem {
    private UUID id;
    private String username;
    private UUID item; 
    private int quantity;
}
