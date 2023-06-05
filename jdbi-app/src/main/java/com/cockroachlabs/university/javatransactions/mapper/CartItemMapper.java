package com.cockroachlabs.university.javatransactions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import com.cockroachlabs.university.javatransactions.domain.CartItem;
import com.cockroachlabs.university.javatransactions.domain.CartItem.CartItemBuilder;

@Component
public class CartItemMapper implements RowMapper<CartItem>{
    
    @Override
    public CartItem map(ResultSet rs, StatementContext ctx) throws SQLException {
        CartItemBuilder cartBuilder = CartItem.builder();
        CartItem.builder()
            .id(UUID.fromString(rs.getString("id")))
            .Shopper(rs.getString("username"))
            .item(rs.getObject("item", UUID.class))
            .quantity(rs.getInt("quantity"));
            
        return cartBuilder.build();
    }

}
