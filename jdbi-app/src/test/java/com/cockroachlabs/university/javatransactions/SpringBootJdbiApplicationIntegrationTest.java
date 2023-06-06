package com.cockroachlabs.university.javatransactions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import com.cockroachlabs.university.javatransactions.dao.CartItemDao;
import com.cockroachlabs.university.javatransactions.dao.ItemDao;
import com.cockroachlabs.university.javatransactions.dao.ShopperDao;
import com.cockroachlabs.university.javatransactions.dao.ShoppingCartDao;
import com.cockroachlabs.university.javatransactions.domain.Item;
import com.cockroachlabs.university.javatransactions.domain.Shopper;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;
import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
public class SpringBootJdbiApplicationIntegrationTest {
    
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
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CartItemDao cartItemDao;

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

    @Test
    public void givenNewCart_whenInsertNewCart_thenSuccess() {
        assertNotNull(cartItemDao);

        Item itemA = Item.builder().name("foof")
        .description("fang")
        .quantity(2)
        .price(3.42)
        .build();

        UUID generatedIdA = itemDao.insertItem(itemA);
        log.info("[I37] generatedId = {}", generatedIdA);
        assertNotNull(generatedIdA);


        ShoppingCart shoppingCart = ShoppingCart.builder()
        .userEmail("someone@foo.com")
        .build();

        ShoppingCartItem cartItemA = ShoppingCartItem.builder()
        .itemId(generatedIdA)
        .quantity(2)
        .build();

        UUID cartId = cartItemA.getCartId();
        try {
            UUID cartItemId = cartItemDao.insert(cartItemA);
        } catch (SQLException e) {
            // This is not using the service method so there's no retry
            e.printStackTrace();
        }
        log.info("Generated ID = {}", cartId);
        assertNotNull(cartId);
    }
    
}