package com.cockroachlabs.university.javatransactions.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import com.cockroachlabs.university.javatransactions.domain.Shopper;

@Component
public class ShopperMapper implements RowMapper<Shopper>{

    @Override
    public Shopper map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Shopper.builder()
            .email(rs.getString("email"))
            .name(rs.getString("name"))
            .address(rs.getString("address"))
            .build();
    }

}