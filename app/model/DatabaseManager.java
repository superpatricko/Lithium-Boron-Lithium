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

	String jdbcDriver;
	String url;
	String password;
	String username;

	
	private DatabaseManager() {

		jdbcDriver = null;
		url = null;
		password = null;
		username = null;
	
		Config c = ConfigFactory.load();

		// Register JDBC driver

		jdbcDriver = c.getString("db.default.driver");

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
			System.exit(1);
			e.printStackTrace();
		}

		url = c.getString("db.default.url");
		username = c.getString("db.default.user");
		password = c.getString("db.default.password");
		
		conn = getNewConnection();
		
		if(conn == null){
			System.exit(2);
		}

	}

	/**
	 * Create a new {@link Statement}. This Statment uses
	 * the DatabaseManager's default connection
	 * 
	 * @return A new empty Statement
	 * @throws SQLException if {@link java.sql.Connection#createStatement() Connection.createStatement()} throws
	 */
	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	/**
	 * Create a new {@link PreparedStatement} using the given String. This PreparedStatment uses
	 * the DatabaseManager's default connection
	 * 
	 * @param s The String of the PreparedStatement
	 * @return A new PreparedStatement
	 * @throws SQLException if {@link java.sql.Connection#prepareStatement(String) Connection.prepareStatement()} throws
	 */
	public PreparedStatement createPreparedStatement(String s)
			throws SQLException {
		return conn.prepareStatement(s);
	}

	/**
	 * Open and return a new {@link java.sql.Connection} object. This can be used for
	 * example if you need to run multiple queries inside a transaction.
	 * 
	 * @return The Connection object or null if one could not be opened for whatever reason
	 */
	public Connection getNewConnection() {
		try{
			// Try to connect to DB
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.err.println("Could not connect to database with :\n"
					+ "   driver: " + jdbcDriver + "\n" + "   url   : " + url
					+ "\n" + "   user  : " + username + "\n" + "   pass  : "
					+ password + "\n");
	
			e.printStackTrace(System.err);
	
			return null;
		}
	}
	
}
