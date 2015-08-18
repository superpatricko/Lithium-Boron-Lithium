package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This class takes care of the details of connecting to the database and
 * creating Statements or PreparedStatements.
 * 
 * @author gordon
 *
 */
public class DatabaseManager {

	private Connection conn;

	/**
	 * This is the singleton DatabaseManager instance through which the methods can be called.
	 * It holds a single Connection object which it uses to create Statements and PreparedStatements
	 */
	public final static DatabaseManager instance = new DatabaseManager();

	private DatabaseManager() {

		String jdbcDriver = null;
		String url = null;
		String password = null;
		String username = null;

		try {

			Config c = ConfigFactory.load();

			// Register JDBC driver

			jdbcDriver = c.getString("db.default.driver");

			Class.forName(jdbcDriver); // may throw

			url = c.getString("db.default.url");
			username = c.getString("db.default.user");
			password = c.getString("db.default.password");

			// Try to connect to DB
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.err.println("Could not connect to database with :\n"
					+ "   driver: " + jdbcDriver + "\n" + "   url   : " + url
					+ "\n" + "   user  : " + username + "\n" + "   pass  : "
					+ password + "\n");

			e.printStackTrace(System.err);

			System.exit(1);

		}
	}

	/**
	 * Create a new {@link Statement}
	 * 
	 * @return A new empty Statement
	 * @throws SQLException if {@link java.sql.Connection#createStatement() Connection.createStatement()} throws
	 */
	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	/**
	 * Create a new {@link PreparedStatement} using the given String
	 * 
	 * @param s The String of the PreparedStatement
	 * @return A new PreparedStatement
	 * @throws SQLException if {@link java.sql.Connection#prepareStatement(String) Connection.prepareStatement()} throws
	 */
	public PreparedStatement createPreparedStatement(String s)
			throws SQLException {
		return conn.prepareStatement(s);
	}

}
