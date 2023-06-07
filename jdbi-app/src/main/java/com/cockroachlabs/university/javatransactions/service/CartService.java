package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;


import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;
import com.cockroachlabs.university.javatransactions.domain.Item;

public interface CartService {

    public UUID addItemToCart(UUID cartId, UUID itemId) throws SQLException;

    public UUID addItemToCartManualRetry(UUID cartId, UUID itemId, int quantity) throws SQLException;
    
}
