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

        int max = 3; // Maximum number of attempts
        int attempt = 0; // Current attempt

        while (attempt < max) {
            try {

                attempt++;
                log.info("Attempt " + attempt);

                itemRepository.reduceItemQuantity(itemId, quantity);

                return;
            } catch (DataAccessException exception) {
                if (exception.getRootCause() instanceof PSQLException psqlException
                        && psqlException.getSQLState().equals("40001")) {
                    log.error("ENCOUNTERED " + psqlException);

                    Thread.sleep((long) (Math.pow(2.0, attempt) * 100L));

                } else {
                    throw exception;
                }
            }
        }
    }
}
