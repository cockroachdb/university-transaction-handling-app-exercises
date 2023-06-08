package com.cockroachlabs.university.javatransactions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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


import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
public class SpringBootJdbiApplicationIntegrationTest {
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ShoppingCartItemDao cartItemDao;

    @Autowired
    private ShopperDao shopperDao;

    @Autowired
    private ShoppingCartDao shoppingCartDao;
    
    
    @Autowired
    private ShoppingCartDao cartDao;
    
    @Autowired
    private CartService cartService;
    
    @Mock
	private ShoppingCartItemDao cartItemDaoMock;

	@InjectMocks
	private CartServiceImpl cartItemService; 

	@Test
	public void addItemToCartManualRetryTest() throws SQLException {
    	Item itemA = Item.builder().name("foof")
        .description("fang")
        .build();

        String emailForRetryTest = "some_email@foo.com";

        Shopper retryTestShopper = Shopper.builder()
        .email(emailForRetryTest)
        .name("Charlie")
        .address("123 Avenue C, New York, NY 10010")
        .build();

        int retryTestShopperInserted = shopperDao.insertShopper(retryTestShopper);

        UUID retryTestCartId = shoppingCartDao.insertShoppingCart(emailForRetryTest);

        UUID generatedIdA = itemDao.insertItem(itemA);
        log.info("[I37] generatedId = {}", generatedIdA);
        assertNotNull(generatedIdA);

        ShoppingCartItem retryTestCartItem = ShoppingCartItem.builder()
        .cart_id(retryTestCartId)
        .item_id(generatedIdA)
        .quantity(200)
        .build();

    	// Force an SQLException to occur when insert is called
    	doThrow(new SQLException()).when(cartItemDaoMock).updateItemQuantity(generatedIdA, 3);

    	// We expect a SQLException to be thrown after max retries
    	assertThrows(SQLException.class, () -> {
        	cartService.addItemToCartManualRetry(retryTestCartId, generatedIdA, 3);
    	});

    	// Verify that the insert method was called max retries times
    	verify(cartItemDaoMock, times(3)).updateItemQuantity(generatedIdA, 3);
	}

    @Test
    public void insertNewShopper() throws SQLException{
        
        String shopperEmail = "somebody@fake.com";

        Shopper shopperA = Shopper.builder()
        .email(shopperEmail)
        .name("Alan Alda")
        .address("123 Fake Street, New York, NY 10010")
        .build();

        int shopperInserted = shopperDao.insertShopper(shopperA);

        assertEquals(shopperInserted, 1);

        // cleanup
        shopperDao.deleteShopper(shopperEmail);

     }
 
    @Test
    public void givenNewItem_whenInsertNewItem_thenSuccess() {

        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(200)
        .build();
         
        UUID generatedId = itemDao.insertItem(item);
        log.info("[I37] generatedId = {}", generatedId);
        assertNotNull(generatedId);

        itemDao.deleteItem(generatedId);

    }

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

        shopperDao.insertShopper(shopperB);

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

        // cleanup
        cartItemDao.deleteCartItem(cartItemId);
        shoppingCartDao.deleteShoppingCart(cartIdA);
        shopperDao.deleteShopper(anEmail);
        itemDao.deleteItem(itemIdA);
        
    }

    @Test
    public void testSerializableIsolation() throws SQLException {
        System.out.println("Checkpoint 1");

        // Build a single item for both users to add to their cart
        Item itemA = Item.builder().name("foof")
            .description("fang")
            .quantity(200)
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

            synchronized (this) {
                cartItemId = cartService.addItemToCartManualRetry(shopperTwoCart, itemIdA, 2);
            }

            return cartItemId;
        };

        System.out.println("Checkpoint 3");
        
        Future<UUID> resultA = executor.submit(userOneAddItemToCart);
        Future<UUID> resultB = executor.submit(userTwoAddItemToCart);

        UUID cartItemAId = null;
        UUID cartItemBId = null;


         try {

            cartItemAId = resultA.get();
            cartItemBId = resultB.get();

        } catch (InterruptedException e) {
            fail("should not throw {} ", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("actually you should throw this execution exception");
        }

        System.out.println("Checkpoint 4");
        
        executor.shutdown();

        // cleanup
        cartItemDao.deleteCartItem(cartItemAId);
        cartItemDao.deleteCartItem(cartItemBId);
        shoppingCartDao.deleteShoppingCart(shopperOneCart);
        shoppingCartDao.deleteShoppingCart(shopperTwoCart);
        shopperDao.deleteShopper(shopperOneEmail);
        shopperDao.deleteShopper(shopperTwoEmail);
        itemDao.deleteItem(itemIdA);
        

    }


}
