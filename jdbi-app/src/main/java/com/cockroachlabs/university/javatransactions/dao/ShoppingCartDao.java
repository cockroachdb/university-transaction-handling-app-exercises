package com.cockroachlabs.university.javatransactions.dao;

import java.sql.SQLException;
import java.util.UUID;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;

@UseClasspathSqlLocator
public interface ShoppingCartDao {

    @SqlUpdate("insertShoppingCart")
    @GetGeneratedKeys
    static
    UUID insertShoppingCart(@Bind("user_email") String userEmailString) throws SQLException;
  
    @SqlQuery
    ShoppingCart findActiveCartByUser(@Bind("user_email") String userEmailString) throws SQLException;
}
