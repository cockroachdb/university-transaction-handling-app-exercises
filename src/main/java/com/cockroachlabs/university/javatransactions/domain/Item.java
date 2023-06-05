package com.cockroachlabs.university.javatransactions.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Item {
    private UUID id;
    private String name;
    private String description;
}
