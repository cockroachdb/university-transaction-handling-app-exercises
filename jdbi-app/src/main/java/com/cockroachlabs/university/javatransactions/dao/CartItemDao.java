package com.cockroachlabs.university.javatransactions.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

@UseClasspathSqlLocator
public interface CartItemDao {

    @SqlUpdate("insertCartItem")
    @GetGeneratedKeys
    UUID insert(@BindBean ShoppingCartItem cartItem) throws SQLException;

    @SqlBatch("insertCartItem")
    @GetGeneratedKeys
    List<UUID> bulkInsert(@BindBean List<ShoppingCartItem> cartItems)  throws SQLException;    
}
