package com.cockroachlabs.university.javatransactions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import com.cockroachlabs.university.javatransactions.dao.ShoppingCartItemDao;
import com.cockroachlabs.university.javatransactions.dao.ItemDao;
import com.cockroachlabs.university.javatransactions.dao.ShopperDao;
import com.cockroachlabs.university.javatransactions.dao.ShoppingCartDao;
import com.cockroachlabs.university.javatransactions.domain.Item;
import com.cockroachlabs.university.javatransactions.domain.Shopper;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;
import com.cockroachlabs.university.javatransactions.service.CartService;
import com.cockroachlabs.university.javatransactions.service.CartServiceImpl;

//We'll need this soon
//import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
public class SpringBootJdbiApplicationIntegrationTest {
    
    /*
     * Deleting this for now 
    @BeforeAll
    static void initAll() throws IOException {
        
        Jdbi jdbi = Jdbi.create("jdbc:postgresql://honest-mare-2968.g8z.cockroachlabs.cloud:26257/defaultdb?sslmode=verify-full&password=5jMhbIoPz7wcK53dHfT3ZQ&user=kiki");
        Path filePath = Path.of("src/test/resources/schema.sql");
        String script = Files.readString(filePath);
        jdbi.useHandle( handle -> {
            handle.createScript(script).execute();
        });
        
    }

    @AfterAll
    static void tearDownAll() {
    }

    */
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ShoppingCartItemDao cartItemDao;

    @Autowired
    private ShopperDao shopperDao;

    @Autowired
    private ShoppingCartDao shoppingCartDao;
    
    /*
     * @Autowired
    private CartDao cartDao;
    
    @Autowired
    private CartService cartService;
     **/
    /*
     @Test
     public void insertNewShopper() throws SQLException{
        
        Shopper shopperA = Shopper.builder()
        .email("somebody@fake.com")
        .name("Alan Alda")
        .address("123 Fake Street, New York, NY 10010")
        .build();

        int shopperInserted = shopperDao.insertShopper(shopperA);

        assertEquals(shopperInserted, 1);

     }
 
     */
    @Test
    public void givenNewItem_whenInsertNewItem_thenSuccess() {

        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(2)
        .build();
         
        UUID generatedId = itemDao.insertItem(item);
        log.info("[I37] generatedId = {}", generatedId);
        assertNotNull(generatedId);
    }
/*
    @Test
    public void givenNewCart_whenInsertNewCart_thenSuccess() throws SQLException {
        assertNotNull(cartItemDao);

        Item itemA = Item.builder().name("foof")
        .description("fang")
        .quantity(2)
        .price(3.42)
        .build();

        UUID itemIdA = itemDao.insertItem(itemA);
        log.info("[I37] generatedId = {}", itemIdA);
        assertNotNull(itemIdA);

        String anEmail = "someone@foo.com";

        Shopper shopperB = Shopper.builder()
        .email(anEmail)
        .name("Betty")
        .address("123 Fake Street, New York, NY 10010")
        .build();

        int shopperInserted = shopperDao.insertShopper(shopperB);

        ShoppingCart shoppingCart = ShoppingCart.builder()
        .user_email(anEmail)
        .build();

        UUID cartIdA = shoppingCartDao.insertShoppingCart(anEmail);

        ShoppingCartItem cartItemA = ShoppingCartItem.builder()
        .item_id(itemIdA)
        .cart_id(cartIdA)
        .quantity(2)
        .build();

        UUID cartItemId = null;
        
        try {
            cartItemId = cartItemDao.insertCartItem(cartItemA);
        } catch (SQLException e) {
            // This is not using the service method so there's no retry
            e.printStackTrace();
        }
        log.info("Generated ID = {}", cartItemId);
        assertNotNull(cartItemId);
        
    }

     */

    @Test
    public void testSerializableIsolation() throws SQLException {
        System.out.println("Checkpoint 1");

        // Build a single item for both users to add to their cart
        Item itemA = Item.builder().name("foof")
            .description("fang")
            .quantity(2)
            .price(3.42)
            .build();

        UUID itemIdA = itemDao.insertItem(itemA);
        log.info("[I37] generatedId = {}", itemIdA);
        assertNotNull(itemIdA);

        // Build shopper one
        String shopperOneEmail = "shopper@one.com";
        Shopper shopperOne = Shopper.builder()
        .email(shopperOneEmail)
        .name("Betty")          
        .address("123 Fake Street, New York, NY 10010")
        .build();

        int shopperOneInserted = shopperDao.insertShopper(shopperOne);

        // Build shopper two
        String shopperTwoEmail = "shopper@two.com";
        Shopper shopperTwo = Shopper.builder()
        .email(shopperTwoEmail)
        .name("Betty Boop")          
        .address("123 Fake Street, New York, NY 10010")
        .build();

        int shopperTwoInserted = shopperDao.insertShopper(shopperTwo);

        // Build carts for each shopper

        // Cart for shopper one
        ShoppingCart shoppingCartOne = ShoppingCart.builder()
        .user_email(shopperOneEmail)
        .build();

        // Cart for shopper one
        UUID shopperOneCart = shoppingCartDao.insertShoppingCart(shopperOneEmail);

         // Cart for shopper two
        UUID shopperTwoCart = shoppingCartDao.insertShoppingCart(shopperTwoEmail);
       
        // Run the following twice in parallel, and synchronize
        // Follows the SQL example from the previous exercise
        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(2);
        
        System.out.println("Checkpoint 2");
        
        // NOTE: boiler plate for test; skip 
        CartService cartService = new CartServiceImpl(shopperDao, cartItemDao);

        Callable<UUID> userOneAddItemToCart = () -> {
            UUID cartItemId = null;
            
            latch.countDown();
            latch.await();
            
            System.out.println("Checkpoint 2b");
/*
 * No longer needed
            ShoppingCartItem cartItemA = ShoppingCartItem.builder()
                .item_id(itemIdA)
                .cart_id(shopperOneCart)
                .quantity(2)
                .build();
 */
            synchronized (this) {
                cartItemId = cartService.addItemToCartManualRetry(shopperOneCart, itemIdA, 2);
            }

            return cartItemId;
        };

        Callable<UUID> userTwoAddItemToCart = () -> {
            UUID cartItemId = null;

            latch.countDown();
            latch.await();
            
            System.out.println("Checkpoint 2b");
/*
            ShoppingCartItem cartItemA = ShoppingCartItem.builder()
                .item_id(itemIdA)
                .cart_id(shopperTwoCart)
                .quantity(2)
                .build();
*/
            synchronized (this) {
                cartItemId = cartService.addItemToCartManualRetry(shopperTwoCart, itemIdA, 2);
            }

            return cartItemId;
        };

        System.out.println("Checkpoint 3");
        
        Future<UUID> resultA = executor.submit(userOneAddItemToCart);
        Future<UUID> resultB = executor.submit(userTwoAddItemToCart);

         try {
            resultA.get();
            resultB.get();

        } catch (InterruptedException e) {
            fail("should not throw {} ", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("actually you should throw this execution exception");
        }

        System.out.println("Checkpoint 4");
        
        executor.shutdown();

    }


}