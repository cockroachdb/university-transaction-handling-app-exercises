package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;

import io.github.resilience4j.retry.annotation.Retry;

@Component(value = "itemInventoryService")
public class ItemInventoryServiceImpl implements ItemInventoryService {

    private ItemDao itemDao;

    public ItemInventoryServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    @Transactional
    @Retry(name = "transactionRetry")
    public void updateItemInventoryA(UUID itemId, int quantity) throws SQLException {
        
        throw new UnsupportedOperationException("This method is not implemented yet");
    }

    @Override
    @Transactional
    public void updateItemInventoryB(UUID itemId, int quantity) throws SQLException {
        System.out.println("updateItemInventory(UUID itemId, int quantity) RUNNING");

        int maxRetries = 3;
        int retryDelay = 1000;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {

                System.out.println(String.format("RUNNING UPDATE ITEM INVENTORY COUNT = {}", retryCount));
                itemDao.updateItemInventory(itemId, quantity);
                 
                break;
            } catch (org.jdbi.v3.core.statement.UnableToExecuteStatementException 
            | org.jdbi.v3.core.transaction.UnableToManipulateTransactionIsolationLevelException exception) {
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
    }
    
}
