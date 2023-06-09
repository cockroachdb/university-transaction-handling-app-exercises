package com.cockroachlabs.university.javatransactions.service;

import java.sql.SQLException;
import java.util.UUID;

public interface ItemInventoryService {

    public void updateItemInventoryA(UUID itemId, int quantity) throws SQLException;

    public void updateItemInventoryB(UUID itemId, int quantity) throws SQLException;

}
