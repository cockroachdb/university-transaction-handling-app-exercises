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

    private boolean isRetryError(UnableToExecuteStatementException exception) {

        // We've caught the UnableToExecuteStatementException
        // We'll check the cause to determine if it's a retry error
        Throwable cause = exception.getCause(); 
        log.error(String.format("ENCOUNTERED  %s", cause.toString()));

        // We are looking for a type of PSQLException, "TransactionRetryError"
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
                // it can only be a retry error.
                return true;

            }

        }

        // If we reach this point, it wasn't a retry error.
        return false;

    }

    public ItemInventoryServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Transactional
    public void updateItemInventoryTxn(UUID itemId, int quantity) throws SQLException, InterruptedException {
        itemDao.updateItemInventory(itemId, quantity);
    }

    @Override
    public void updateItemInventory(UUID itemId, int quantity) throws SQLException, InterruptedException {

        // BEGINNING OF EXERCISE
        // YOU WILL MODIDFY ONLY THIS `updateItemInventory` METHOD

        // STEP 1: INITIALIZE ANY PARAMETERS YOU PLAN TO USE FOR THE LOOP,
        //         THEN WRAP THE FOLLOWING IN A RETRY LOOP.

        try {
            log.info("Attempting item inventory update... "); 
            
            // to ensure a new transaction is created on every retry
            this.updateItemInventoryTxn(itemId, quantity);

            // STEP 1: AFTER WRAPPING THIS CLAUSE IN A LOOP,
            //         YOU MAY NEED TO BREAK OUT OF THE LOOP HERE

        } catch (UnableToExecuteStatementException exception) {
            
            // Since we've caught an UnableToExecuteStatementException,
            // confirm that this is, in fact, a retry error;
            if (this.isRetryError(exception)) {
                
                // INITIALLY, THIS WILL SIMPLY THROW THE EXCEPTION
                // WRAP THIS IN A CHECK TO ENSURE THAT THE RETRIES HAVE BEEN
                // EXCEEDED BEFORE THROWING

                // Throw a RuntimeException exception telling users "Max retries exceeded"
                throw new RuntimeException("Max retries exceeded", exception);

            } else {

                // if it wasn't a retry error, re-throw the exception
                throw exception;

            }

            // STEP 1: PERFORM ANY BOOKKEEPING FOR THE RETRY LOOP HERE
            //         SUCH AS INCREMENTING A COUNTER

        }

    }
    // END OF EXERCISE; DO NOT MODIFY BEYOND THIS POINT

}
