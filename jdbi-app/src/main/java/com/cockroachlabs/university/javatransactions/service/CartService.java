package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;


import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;
import com.cockroachlabs.university.javatransactions.domain.Item;

public interface CartService {

    public UUID addItemToCart(Item item, ShoppingCartItem cartItem) throws SQLException;

    public UUID addItemToCartManualRetry(ShoppingCartItem cartItem) throws SQLException;
    
}
