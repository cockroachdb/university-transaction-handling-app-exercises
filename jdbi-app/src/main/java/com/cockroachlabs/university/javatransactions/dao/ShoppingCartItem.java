package com.cockroachlabs.university.javatransactions.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

@UseClasspathSqlLocator
public interface ShoppingCartItem {

    @SqlUpdate("insertCartItem")
    @GetGeneratedKeys
    UUID insert(@BindBean ShoppingCartItem cartItem) throws SQLException;

    @SqlBatch("insertCartItem")
    @GetGeneratedKeys
    List<UUID> bulkInsert(@BindBean List<ShoppingCartItem> cartItems)  throws SQLException;
    
    @SqlQuery
    ShoppingCartItem findCartItemById(@Bind("id") UUID item_id);

    UUID getShoppingCartId();
}