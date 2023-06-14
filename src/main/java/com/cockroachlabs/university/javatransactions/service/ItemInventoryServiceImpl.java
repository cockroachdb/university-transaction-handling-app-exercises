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

      // THIS IS WHERE YOU WILL MODIFY THE CODE
      // Initially, the transaction is performed with no error handling.
      this.updateItemInventoryTxn(itemId, quantity);
      retryTransaction = false;

    }

}
