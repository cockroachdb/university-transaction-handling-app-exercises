package com.cockroachlabs.university.javatransactions.domain;

import java.sql.Date;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShoppingCart {
    private UUID cart_id;
    private String user_email;
    private Date purchased_at;
}
