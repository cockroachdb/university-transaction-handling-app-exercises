package com.cockroachlabs.university.javatransactions.dao;

import java.sql.SQLException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.cockroachlabs.university.javatransactions.domain.Shopper;

@UseClasspathSqlLocator
public interface ShopperDao {

    @SqlUpdate("insertShopper")
    int insertShopper(@BindBean Shopper shopper) throws SQLException;

    @SqlQuery
    Shopper findShopperByEmail(@Bind("email") String email) throws SQLException;

    @SqlUpdate("deleteShopper")
    int deleteShopper(@Bind("email") String email) throws SQLException;

}