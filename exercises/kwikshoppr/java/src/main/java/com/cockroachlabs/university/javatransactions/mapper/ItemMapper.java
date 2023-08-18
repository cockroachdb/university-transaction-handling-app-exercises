package com.cockroachlabs.university.javatransactions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import com.cockroachlabs.university.javatransactions.domain.Item;

@Component
public class ItemMapper implements RowMapper<Item>{

    @Override
    public Item map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Item.builder()
            .item_id(UUID.fromString(rs.getString("item_id")))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .quantity(rs.getInt("quantity"))
            .price(rs.getDouble("price"))
            .build();
    }

}