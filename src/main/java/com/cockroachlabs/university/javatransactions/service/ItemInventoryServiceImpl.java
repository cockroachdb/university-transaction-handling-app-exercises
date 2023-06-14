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

    private Boolean isRetryError (Exception exception) {

        Throwable cause = exception.getCause();  // need to check and see if this is a retry error

        Boolean passedErrorChecks = false;
        if (cause instanceof PSQLException) {  // *might* be a retry error

                PSQLException psqlException = (PSQLException) cause;

                if ("40001".equals(psqlException.getSQLState())) {  // RETRY ERROR!

                    passedErrorChecks = true;

                }

            } 
        
        return passedErrorChecks;

    }

    public ItemInventoryServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Transactional
    public void updateItemInventoryTxn(UUID itemId, int quantity) throws SQLException {
        itemDao.updateItemInventory(itemId, quantity);
    }

    @Override
    public void updateItemInventory(UUID itemId, int quantity) throws SQLException {

        // EXERCISE SECTION
        // THIS IS WHERE YOU WILL MODIFY THE CODE:
        try {

            // Perform the transaction
            this.updateItemInventoryTxn(itemId, quantity);

        } catch (org.jdbi.v3.core.statement.UnableToExecuteStatementException exception) {

            if (isRetryError(exception) == true ) {

                // Since it's a retry error but it will not be retried, log that the retry limit was exceeded.
                System.out.printf("[WARNING]: Retry error caught in `updateItemInventory` but retry limit exceeded.");

            } else {  // Not a retry error

                throw exception;

            }
        
        }
        // END OF EXERCISE SECTION

    }

}
