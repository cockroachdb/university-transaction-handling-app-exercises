package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;

@Component(value = "itemInventoryService")
public class ItemInventoryServiceImpl implements ItemInventoryService {

    private ItemDao itemDao;

    public ItemInventoryServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Transactional
    public void updateItemInventoryTxn(UUID itemId, int quantity) throws SQLException {
        itemDao.updateItemInventory(itemId, quantity);
    }

    @Override
    public void updateItemInventory(UUID itemId, int quantity) throws SQLException {
      boolean retryTransaction = true;

        /*
        int maxRetries = 3;
        int retryDelay = 1000;
        int retryCount = 0;

  
        while (retryTransaction) {
            try {
                 */
                // to ensure a new transaction is created on every retry
                this.updateItemInventoryTxn(itemId, quantity);
                retryTransaction = false;
/*
            } catch (org.jdbi.v3.core.statement.UnableToExecuteStatementException exception) {

                Throwable cause = exception.getCause();

                if (cause instanceof PSQLException) {
                    PSQLException psqlException = (PSQLException) cause;

                    if ("40001".equals(psqlException.getSQLState())) {
                        retryCount++;
                        int delay = (int) (retryDelay * Math.pow(2, retryCount));
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        if (retryCount > maxRetries) {
                            throw new RuntimeException("Max retries exceeded", exception);
                        }
                        continue;
                    }
                }
                throw exception;
            }
            
        }
         */
    }

}
