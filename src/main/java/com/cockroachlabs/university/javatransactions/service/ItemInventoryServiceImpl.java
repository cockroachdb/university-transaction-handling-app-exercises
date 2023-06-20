package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.postgresql.util.PSQLException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import lombok.extern.slf4j.Slf4j;

@Component(value = "itemInventoryService")
@Slf4j
public class ItemInventoryServiceImpl implements ItemInventoryService {
    private ItemDao itemDao;

    public ItemInventoryServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Transactional
    public void updateItemInventoryTxn(UUID itemId, int quantity) throws SQLException, InterruptedException {
        itemDao.updateItemInventory(itemId, quantity);
    }

    @Override
    public void updateItemInventory(UUID itemId, int quantity) throws SQLException, InterruptedException {

        // Initialize some parameters for the retry logic
        int maxRetries = 3; //retry a maximum of 3 times
        int retryDelay = 1000; //delay parameter for exponential backoff
        int retryCount = 0; //keep track of how many times we've retried the transaction

        // We will execute the update in a while loop. "retryTransaction" is the control variable
        // for the while loop. 
        boolean retryTransaction = true;

        while (retryTransaction) {
            try {
                log.info("Attempting item inventory update... "); 
                
                // to ensure a new transaction is created on every retry
                this.updateItemInventoryTxn(itemId, quantity);

                // if the update succeeds, break out of the while loop
                retryTransaction = false;
            } catch (UnableToExecuteStatementException exception) {
                
                // We've caught the UnableToExecuteStatementException
                // We should get the cause of this exception to determine 
                // whether it's one we want to retry
                Throwable cause = exception.getCause(); 
                log.error(String.format("ENCOUNTERED  %s", cause.toString()));

                // We are looking for a type of PSQLException "TransactionRetryError"
                if (cause instanceof PSQLException) {
                    PSQLException psqlException = (PSQLException) cause;

                    /**
                     * The SQL State code 40001 refers to RETRY_WRITE_TOO_OLD error.
                     * This error occurs when a transaction A tries to write to a row R, 
                     * but another transaction B that was supposed to be serialized after A 
                     * (i.e., had been assigned a higher timestamp), has already written to 
                     * that row R, and has already committed. 
                     * 
                     * This is a common error when you have too much contention in your workload.
                     * 
                     * Ensure the SQL State code is 40001
                     */
                    if ("40001".equals(psqlException.getSQLState())) {
                        // Since we've encountered UnableToExecuteStatementException, with a cause
                        // of PSQLException with an error code of 40001 (RETRY_WRITE_TOO_OLD), 
                        // we want to retry
                         
                        // Increment retryCount by 1 
                        retryCount++; 

                        // Once we get to the max number of retries, we want to throw an exception
                        if (retryCount > maxRetries) {
                            // Throw a RuntimeException exception telling users "Max retries exceeded"
                            throw new RuntimeException("Max retries exceeded", exception);
                        } else {

                            // We don't want to keep retrying right away as that could overload the system
                            // Instead, use an exponential backoff to set the retry delay interval
                            int delay = (int) (retryDelay * Math.pow(2, retryCount));
                        
                            log.info(String.format("On retry #%d waiting %d milliseconds", retryCount, delay));
                            // Wait for the delay period before retrying
                            Thread.sleep(delay);
                        }
                         // Back to the beginning of the while loop
                        continue;
                    }
                }
                throw exception;
            }
            
        }
    }

}
