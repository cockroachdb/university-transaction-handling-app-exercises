package com.cockroachlabs.university.javatransactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cockroachlabs.university.javatransactions.domain.Item;
import com.cockroachlabs.university.javatransactions.domain.CartItem;
import com.cockroachlabs.university.javatransactions.dao.ItemDao;
import com.cockroachlabs.university.javatransactions.dao.CartItemDao;

import com.cockroachlabs.university.javatransactions.service.CartServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
public class SpringBootJdbiApplicationIntegrationTest {
    
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CartItemDao cartItemDao;
    
    /*
     * @Autowired
    private CartDao cartDao;
    
    @Autowired
    private CartService cartService;
     **/
 
    @Test
    public void givenNewItem_whenInsertNewItem_thenSuccess() {

        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .build();
         
        UUID generatedId = itemDao.insert(item);
        log.info("[I37] generatedId = {}", generatedId);
        assertNotNull(generatedId);
    }

    @Test
    public void givenNewCart_whenInsertNewCart_thenSuccess() {
        assertNotNull(cartItemDao);

        Item itemA = Item.builder().name("foof")
        .description("fang")
        .build();

        UUID generatedIdA = itemDao.insert(itemA);
        log.info("[I37] generatedId = {}", generatedIdA);
        assertNotNull(generatedIdA);

        CartItem cartItemA = CartItem.builder().username("myuser")
        .item(generatedIdA)
        .quantity(2)
        .build();

        UUID cartId = null;
        try {
            cartId = cartItemDao.insert(cartItemA);
        } catch (SQLException e) {
            // This is not using the service method so there's no retry
            e.printStackTrace();
        }
        log.info("Generated ID = {}", cartId);
        assertNotNull(cartId);
    }
    
}
