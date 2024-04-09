package com.cockroachlabs.university;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class ItemInventoryService {

    private static final Logger log = LoggerFactory.getLogger(ItemInventoryService.class);

    private final ItemRepository itemRepository;

    ItemInventoryService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    void updateItemInventory(UUID itemId, int quantity) throws InterruptedException {


        while (true) {
            try {
                itemRepository.reduceItemQuantity(itemId, quantity);
                return;
            } catch (DataAccessException exception) {
                if (exception.getRootCause() instanceof PSQLException psqlException
                        && psqlException.getSQLState().equals("40001")) {
                    /*
                     * SQL State "40001" refers to RETRY_WRITE_TOO_OLD. This error occurs when a transaction A tries to write to a
                     * row R, but another transaction B that was supposed to be serialized after A (i.e. had been assigned a
                     * higher timestamp), has already written to that row R, and has already been committed. This is a common
                     * error when you have too much contention in your workload.
                     */
                    log.error("ENCOUNTERED " + psqlException);
                } else {
                    throw exception;
                }
            }
        }
    }
}
