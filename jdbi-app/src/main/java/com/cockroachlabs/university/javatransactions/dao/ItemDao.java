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

    @SqlUpdate("insert")
    @GetGeneratedKeys
    UUID insert(@BindBean Item item);

    @SqlBatch("insert")
    @GetGeneratedKeys
    List<UUID> bulkInsert(@BindBean List<Item> items);

    @SqlQuery
    Item findItemById(@Bind("id") UUID id);
    
    @SqlQuery
    List<Item> findItemByName(@Bind("name") String name);
}
