package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.CartItemDao;
import com.cockroachlabs.university.javatransactions.dao.ShopperDao;
import com.cockroachlabs.university.javatransactions.domain.Item;

import io.github.resilience4j.retry.annotation.Retry;

import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

@Component(value = "cartService")
public class CartServiceImpl implements CartService{

    private ShopperDao shopperDao;
    private CartItemDao cartItemDao;

    public CartServiceImpl(ShopperDao shopperDao, CartItemDao cartItemDao) {
        this.shopperDao = shopperDao;
        this.cartItemDao = cartItemDao;

    }

    @Override
    @Transactional
    @Retry(name = "transactionRetry")
    public UUID addItemToCart(Item item, ShoppingCartItem cartItem) throws SQLException{
        
        throw new UnsupportedOperationException("This method is not implemented yet");
    }

    @Override
    @Transactional
    public UUID addItemToCartManualRetry(ShoppingCartItem cartItem) throws SQLException{
        
        int maxRetries = 3;
        int retryDelay = 1000;
        int retryCount = 0;
        UUID cartItemId = null;

        while (retryCount < maxRetries) {
            try {
                cartItemDao.insert(cartItem);
            } catch (SQLException exception){
                
                if(retryCount < maxRetries) {
                    retryCount++;
                    int delay = (int)(retryDelay * Math.pow(2, retryCount));
                    try {
                    Thread.sleep(delay);
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw exception;
                }

            }
        }

        return cartItemId;

    }
    
}
