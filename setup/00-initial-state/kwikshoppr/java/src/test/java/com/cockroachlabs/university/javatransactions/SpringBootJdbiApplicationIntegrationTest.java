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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;
import com.cockroachlabs.university.javatransactions.domain.Item;
import com.cockroachlabs.university.javatransactions.service.ItemInventoryService;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {SpringBootJdbiApplication.class, JdbiConfiguration.class})
@Slf4j
public class SpringBootJdbiApplicationIntegrationTest {
    
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemInventoryService itemInventoryService;
 
    @Test
    public void givenNewItem_whenInsertNewItem_thenSuccess() {

        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(200)
        .build();
         
        UUID generatedId = itemDao.insertItem(item);
        log.info("[RUNNING TEST - givenNewItem_whenInsertNewItem_thenSuccess] generatedId = %s", generatedId);
        assertNotNull(generatedId);
    }

    @Test
    public void givenExistingItem_WhenInventoryUpdated_thenSuccess() throws SQLException, InterruptedException {
        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(200)
        .build();

        UUID generatedId = itemDao.insertItem(item);

        assertNotNull(generatedId);
        
        itemDao.updateItemInventory(generatedId, 3);
        

    }

    @Test
    public void givenItemUpdatedWhileTransactionOpen_shouldthrowRetryError() throws InterruptedException, ExecutionException {
        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(200)
        .build();

        UUID generatedId = itemDao.insertItem(item);

        assertNotNull(generatedId);

        // A call to update the item inventory. This calls the service method that
        // reads the item row, then pauses for a duration, then tries to update the 
        // item row
        Callable<Boolean> updateItemInventory = () -> {
            log.info("update the item inventory, read then updated");
            itemInventoryService.updateItemInventory(generatedId, 3);
            log.info("update completed!");
            return true;
        };

        // A call that directly updates an item's quantity using the dao. It happens in 
        // a single transaction. 
        Callable<Boolean> updateItem = () -> {
            log.info("pause then update the item quantity ");
            // we pause to ensure we are in the middle of the update inventory transaction
            Thread.sleep(1000);
            // updates the quantity in a single transaction
            itemDao.updateItemQuantity(generatedId, 2);
            log.info("item quantity updated! ");
            return true;
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        
        // We execute both calls in parallel, this is why the pause is necessary on "updateItem"
        Future<Boolean> a = executor.submit(updateItemInventory);
        Future<Boolean> b = executor.submit(updateItem);

        
        Boolean updatedInventory = a.get();
        Boolean updatedItemQuanity = b.get();

        assertTrue(updatedInventory, "successfully updated inventory");
        assertTrue(updatedItemQuanity, "successfully updated the item quantity");

        int updatedQuanity = itemDao.findItemById(generatedId).getQuantity();
        log.info(String.format("Updated item quantity is: %d", updatedQuanity));

        assertTrue(itemDao.findItemById(generatedId).getQuantity() == 195);

    }       

}
