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

import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;

import org.jdbi.v3.sqlobject.statement.SqlQuery;

@UseClasspathSqlLocator
public interface ShoppingCartItemDao {

    @SqlUpdate("insertCartItem")
    @GetGeneratedKeys
    UUID insertCartItem(@BindBean ShoppingCartItem cartItem) throws SQLException;
    
    @SqlBatch("insertCartItem")
    @GetGeneratedKeys
    List<UUID> insertCartItem(@BindBean List<ShoppingCartItem> cartItems)  throws SQLException;
    
    @SqlUpdate("insertCartItem")
    @GetGeneratedKeys
    UUID insertCartItem(@Bind("cart_id") UUID cartId, @Bind("item_id") UUID itemId, @Bind("quantity") int quantity) throws SQLException;

    @SqlQuery
    ShoppingCartItem findCartItemById(@Bind("cart_id") UUID cartId);

    @SqlUpdate("updateItemQuantity")
    int updateItemQuantity(@Bind("item_id") UUID itemId, @Bind("amount") int amount);
}