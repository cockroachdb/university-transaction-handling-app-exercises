package com.cockroachlabs.university;

import org.postgresql.util.PSQLException; // HINT: This is the root cause exception to check
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException; // HINT: This is the exception to catch
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

        // TODO: Wrap the following operation first in a while loop and inside that, with a try-catch block.

        itemRepository.reduceItemQuantity(itemId, quantity);

        // HINT: Look for a Spring DataAccessException.
        // HINT: Check if the exception's root cause has a SQL state of "40001". If so, then it's a WriteTooOld error that should be retried.
        // HINT: Anything else, and you can rethrow it on up the call stack.
    }
}
