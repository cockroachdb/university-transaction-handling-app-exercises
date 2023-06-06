package com.cockroachlabs.university.javatransactions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import com.cockroachlabs.university.javatransactions.domain.ShoppingCartItem;


@Component
public class CartItemMapper implements RowMapper<ShoppingCartItem>{
    
    @Override
    public ShoppingCartItem map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ShoppingCartItem.builder()
            .id(UUID.fromString(rs.getString("id")))
            .cartId(UUID.fromString(rs.getString("cart_id")))
            .itemId(UUID.fromString(rs.getString("item_id")))
            .quantity(rs.getInt("quantity"))
            .build();

    }

}
