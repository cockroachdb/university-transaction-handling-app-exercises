package com.cockroachlabs.university.javatransactions.domain;

import java.sql.Date;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShoppingCart {
    private UUID cartId;
    private String userEmail;
    private Date purchasedAt;
}
