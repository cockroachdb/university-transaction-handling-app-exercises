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
    public void givenExistingItem_WhenInventoryUpdated_thenSuccess() {
        assertNotNull(itemDao);
        
        Item item = Item.builder().name("foo")
        .description("fang")
        .quantity(200)
        .build();

        UUID generatedId = itemDao.insertItem(item);

        assertNotNull(generatedId);

        try {
            itemDao.updateItemInventory(generatedId, 3);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            fail(String.format("Should not reach exception: %s ", e.getMessage()));
        }

    }

    @Test
    public void givenItemUpdatedWhileTransactionOpen_shouldthrowRetryError() {
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

        try {
            Boolean updatedInventory = a.get();
            Boolean updatedItemQuanity = b.get();

            assertTrue(updatedInventory, "successfully updated inventory");
            assertTrue(updatedItemQuanity, "successfully updated the item quantity");

            int updatedQuanity = itemDao.findItemById(generatedId).getQuantity();
            log.info(String.format("Updated item quantity is: %d", updatedQuanity));

            assertTrue(itemDao.findItemById(generatedId).getQuantity() == 195);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    } 

    @Test
    public void givenItemUpdatedWhileTransactionOpen_shouldFailAfterFourRetries() {
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
            log.info("BEGIN -- updateItemInventory in Test");
            itemInventoryService.updateItemInventory(generatedId, 3);
            log.info("END -- updateItemInventory in Test: Success!");
            return true;
        };

        // A call that directly updates an item's quantity using the dao. It happens in 
        // a single transaction. 
        Callable<Boolean> updateItem1 = () -> {
            // we pause to ensure we are in the middle of the FIRST updateItemInventory transaction
            Thread.sleep(1000);

            // updates the quantity in a single transaction
            itemDao.updateItemQuantity(generatedId, 2);
            log.info("Ran updateItem1 in the middle of updateItemInventory! ");
            return true;
        };

        Callable<Boolean> updateItem2 = () -> {
            // we pause to ensure we are in the middle of updateItemInventory retry 1
            Thread.sleep(8000);

            // updates the quantity in a single transaction
            itemDao.updateItemQuantity(generatedId, 2);
            log.info("Ran updateItem2 in the middle of updateItemInventory retry 1! ");
            return true;
        };

        Callable<Boolean> updateItem3 = () -> {
            // we pause to ensure we are in the middle of the update inventory transaction
            Thread.sleep(17000);
            
            // updates the quantity in a single transaction
            itemDao.updateItemQuantity(generatedId, 2);
            log.info("Ran updateItem3 in the middle of updateItemInventory retry 2! ");
            return true;
        };

         Callable<Boolean> updateItem4 = () -> {
            // we pause to ensure we are in the middle of the update inventory transaction
            Thread.sleep(17000);
            
            // updates the quantity in a single transaction
            itemDao.updateItemQuantity(generatedId, 2);
            log.info("Ran updateItem4 in the middle of updateItemInventory retry 3! ");
            return true;
        };

        ExecutorService executor = Executors.newCachedThreadPool();
        
        // We execute both calls in parallel, this is why the pause is necessary on "updateItem"
        Future<Boolean> a = executor.submit(updateItemInventory);
        Future<Boolean> b = executor.submit(updateItem1);
        Future<Boolean> c = executor.submit(updateItem2);
        Future<Boolean> d = executor.submit(updateItem3);
        Future<Boolean> e = executor.submit(updateItem4);
        
        // We are expecting this wrapped code block to throw an ExecutionException 
        Exception exception = assertThrows(ExecutionException.class, () -> {

        try {
            Boolean updatedInventory = a.get();
            Boolean updatedItemQuanity1 = b.get();
            Boolean updatedItemQuanity2 = c.get();
            Boolean updatedItemQuanity3 = d.get();
            Boolean updatedItemQuanity4 = e.get();

            assertTrue(updatedInventory, "successfully updated inventory");
            assertTrue(updatedItemQuanity1, "successfully updated the item quantity 1 time");
            assertTrue(updatedItemQuanity2, "successfully updated the item quantity 2 times");
            assertTrue(updatedItemQuanity3, "successfully updated the item quantity 3 times");
            assertTrue(updatedItemQuanity4, "successfully updated the item quantity 4 times");

            int updatedQuanity = itemDao.findItemById(generatedId).getQuantity();
            log.info(String.format("Updated item quantity is: %d", updatedQuanity));

        } catch (InterruptedException ex) {
            ex.printStackTrace();
            fail("should not receive InterruptedException ", ex);
        }       
        
    });
        Throwable cause = exception.getCause();
        String message = cause.getMessage();
        assertTrue("Max retries exceeded".equalsIgnoreCase(message), "We exceeded the maximum retries as expected");
    } 

}
