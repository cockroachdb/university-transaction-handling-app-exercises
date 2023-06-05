package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;


import com.cockroachlabs.university.javatransactions.domain.CartItem;
import com.cockroachlabs.university.javatransactions.domain.Item;

public interface CartService {

    public UUID addItemToCart(Item item, CartItem cartItem) throws SQLException;

    public UUID addItemToCartManualRetry(CartItem cartItem) throws SQLException;
    
}
