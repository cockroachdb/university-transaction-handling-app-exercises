package com.cockroachlabs.university.javatransactions.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShoppingCartItem {
    private UUID id;
    private UUID cart_id;
    private UUID item_id;
    private int quantity;
}