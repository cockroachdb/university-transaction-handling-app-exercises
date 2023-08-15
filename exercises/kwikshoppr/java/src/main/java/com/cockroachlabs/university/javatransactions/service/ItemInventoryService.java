package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

public interface ItemInventoryService {

    public void updateItemInventory(UUID itemId, int quantity) throws SQLException, InterruptedException;

}
