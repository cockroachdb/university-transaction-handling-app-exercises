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

import com.cockroachlabs.university.javatransactions.domain.CartItem;

@UseClasspathSqlLocator
public interface CartItemDao {

    @SqlUpdate("insert")
    @GetGeneratedKeys
    UUID insert(@BindBean CartItem cartItem) throws SQLException;

    @SqlBatch("insert")
    @GetGeneratedKeys
    List<UUID> bulkInsert(@BindBean List<CartItem> cartItems);

    @SqlQuery
    List<CartItem> findCartByUser(@Bind("username") String username);

    @SqlQuery
    CartItem findCartById(@Bind("id") UUID id);
    
}
