package com.cockroachlabs.university.javatransactions.dao;

import java.util.List;
import java.util.UUID;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.cockroachlabs.university.javatransactions.domain.Item;

@UseClasspathSqlLocator
public interface ItemDao {

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

    @SqlUpdate("deleteItem")
    int deleteItem(@Bind("item_id") UUID itemId);

}
