package com.cockroachlabs.university.javatransactions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import com.cockroachlabs.university.javatransactions.domain.ShoppingCart;


@Component
public class ShoppingCartMapper implements RowMapper<ShoppingCart>{

    @Override
    public ShoppingCart map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ShoppingCart.builder()
            .cart_id(UUID.fromString(rs.getString("cart_id")))
            .user_email(rs.getString("user_email"))
            .purchased_at(rs.getDate("purchased_at"))
            .build();
    }

}