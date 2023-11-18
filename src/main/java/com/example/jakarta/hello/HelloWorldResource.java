package com.example.jakarta.hello;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import jakarta.annotation.Resource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("hello")
public class HelloWorldResource {

	@Resource(name = "jdbc/h2memorydatasource")
	private DataSource myDB;

	private Logger logger = Logger.getLogger(HelloWorldResource.class.getName());

	public HelloWorldResource() {
	}

	public HelloWorldResource(DataSource myDB) {
		this.myDB = myDB;
	}

	@GET
	// @Path("/hellotest")
	@Produces({ MediaType.APPLICATION_JSON })
	public Hello hello(@QueryParam("name") String name) {
		if ((name == null) || name.trim().isEmpty())  {
			name = "world";
		}

		return new Hello(name);
	}

	@GET
	@Path("dbtest")
	@Produces({ MediaType.TEXT_PLAIN })
	public String dbtest() throws SQLException {

		try (Connection con = myDB.getConnection()) {
			logger.info("YEAH");
		} catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return "OK";
	}
}