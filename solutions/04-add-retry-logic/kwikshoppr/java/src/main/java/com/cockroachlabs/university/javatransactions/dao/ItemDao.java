package com.cockroachlabs.university.javatransactions.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.springframework.boot.logging.LoggingSystemFactory;

import com.cockroachlabs.university.javatransactions.domain.Item;


@UseClasspathSqlLocator
public interface ItemDao {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ItemDao.class);

    @SqlUpdate("insertItem")
    @GetGeneratedKeys
    UUID insertItem(@BindBean Item item);

    @SqlBatch("insertItem")
    @GetGeneratedKeys
    List<UUID> bulkInsert(@BindBean List<Item> items);

    @SqlQuery
    Item findItemById(@Bind("item_id") UUID item_id);

    @SqlQuery
    Item findItemsByName(@Bind("name") List<Item> items);

    @SqlUpdate("updateItemQuantity")
    int updateItemQuantity(@Bind("item_id") UUID itemId, @Bind("amount") int amount);

    @Transaction
    default void updateItemInventory(UUID itemId, int quantity) throws SQLException, InterruptedException {
        findItemById(itemId);

        log.info("Artificial 5 second delay! Gives time for another transaction to happen while this is still open");
        Thread.sleep(5000);
        
        updateItemQuantity(itemId, quantity);

    }

}
