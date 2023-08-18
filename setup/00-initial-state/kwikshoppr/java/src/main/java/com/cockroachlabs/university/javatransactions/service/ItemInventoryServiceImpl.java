package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cockroachlabs.university.javatransactions.dao.ItemDao;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
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
        // TODO: This will be implemented as part of exercises 4 & 5

        throw new UnsupportedOperationException("Unimplemented method 'updateItemInventory'");
    }

    private boolean isRetryError(UnableToExecuteStatementException exception) {

        // Helper function. 
        // Once we've caught the UnableToExecuteStatementException,
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

}
