package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.ShoppingCartItemDao;
import com.cockroachlabs.university.javatransactions.dao.ShopperDao;
import com.cockroachlabs.university.javatransactions.domain.Item;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

import io.github.resilience4j.retry.annotation.Retry;

@Component(value = "cartService")
public class CartServiceImpl implements CartService{

    private ShopperDao shopperDao;
    private ShoppingCartItemDao cartItemDao;

    public CartServiceImpl(ShopperDao shopperDao, ShoppingCartItemDao cartItemDao) {
        this.shopperDao = shopperDao;
        this.cartItemDao = cartItemDao;

    }

    @Override
    @Transactional
    @Retry(name = "transactionRetry")
    public UUID addItemToCart(Item item, ShoppingCartItem cartItem) throws SQLException {
        
        throw new UnsupportedOperationException("This method is not implemented yet");
    }

    @Override
    @Transactional
    public UUID addItemToCartManualRetry(ShoppingCartItem cartItem) throws SQLException {
        System.out.println("addItemToCartManualRetry(ShoppingCartItem cartItem) RUNNING");

        int maxRetries = 3;
        int retryDelay = 1000;
        int retryCount = 0;
        UUID cartItemId = null;

        while (retryCount < maxRetries) {
            try {
                System.out.println("RUNNING count number " + retryCount);
                cartItemDao.insertCartItem(cartItem);
                break;
                // Thread.sleep(2000);
                // itemDao.updateItemQuantity(itemId, quantity);
            } catch (SQLException exception) {
                System.out.println("Exception caught during count number " + retryCount);

                if(retryCount < maxRetries) {
                    System.out.println("Retry count " + retryCount);
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
