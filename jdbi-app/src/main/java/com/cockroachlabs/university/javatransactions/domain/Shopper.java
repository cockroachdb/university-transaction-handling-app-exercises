package com.cockroachlabs.university.javatransactions.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Shopper {
    private String email;
    private String name;
    private String address;
}
