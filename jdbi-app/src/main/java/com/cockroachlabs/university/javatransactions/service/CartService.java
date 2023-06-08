package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

public interface CartService {

    public UUID addItemToCart(UUID cartId, UUID itemId) throws SQLException;

    public UUID addItemToCartManualRetry(UUID cartId, UUID itemId, int quantity) throws SQLException;
    
}
