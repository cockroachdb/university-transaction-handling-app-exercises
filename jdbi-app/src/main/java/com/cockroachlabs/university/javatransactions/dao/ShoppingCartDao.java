package com.cockroachlabs.university.javatransactions.dao;

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
    UUID insertShoppingCart(@BindBean ShoppingCart shoppingCart) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertShoppingCart'");
    }
  
    @SqlQuery
    ShoppingCart findActiveCartByUser(@Bind("user_email") String userEmailString);
}
