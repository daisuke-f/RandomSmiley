package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import com.example.jakarta.hello.HelloWorldResource;

class TestHelloWorldResource {

    @Test
    void test123() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setUrl("jdbc:h2:mem:test");

        HelloWorldResource helloWorldResource = new HelloWorldResource(ds);

        String out = helloWorldResource.dbtest();
        assertEquals("OK", out);
    }
}