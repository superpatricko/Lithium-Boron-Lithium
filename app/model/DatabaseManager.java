package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import play.api.Play;
import scala.Option;
import scala.Some;
import scala.runtime.AbstractFunction0;

public class DatabaseManager {
   
   private Connection conn;
	   
   public final static DatabaseManager instance = new DatabaseManager();
      
   private DatabaseManager() {
	   
	   String jdbcDriver = null;
	   String url  = null;
	   String password = null;
	   String username = null;
	   
	   try{
		   
		   // Register JDBC driver
		   
		   // Verbose as fuck way of getting the value with a default of com.mysql.jdbc.Driver
		   jdbcDriver = Play.current().configuration().getString("db.default.driver", null).orElse(new AbstractFunction0<Option<String>>() {
				@Override
				public Option<String> apply() {
					return new Some<String>("com.mysql.jdbc.Driver");
				}
			}).get();
		   
		   Class.forName(jdbcDriver); // may throw

		   // Verbose as fuck way of getting other settings
		   url = Play.current().configuration().getString("db.default.url", null).orElse(new AbstractFunction0<Option<String>>() {
				@Override
				public Option<String> apply() {
					return new Some<String>("jdbc:mysql://localhost:3306/comp353");
				}
			}).get();
		   username = Play.current().configuration().getString("db.default.user", null).orElse(new AbstractFunction0<Option<String>>() {
				@Override
				public Option<String> apply() {
					return new Some<String>("root");
				}
			}).get();
		   password = Play.current().configuration().getString("db.default.password", null).orElse(new AbstractFunction0<Option<String>>() {
				@Override
				public Option<String> apply() {
					return new Some<String>("root");
				}
			}).get();
		   
		   
		   // Try to connect to DB
		   conn = DriverManager.getConnection(url, username, password);
	   }catch(Exception e){
		   System.err.println("Could not connect to database with :\n"
		   		+ "   driver: " + jdbcDriver + "\n"
		   		+ "   url   : " + url + "\n"
		   		+ "   user  : " + username + "\n"
		   		+ "   pass  : " + password + "\n");
		   
		   e.printStackTrace(System.err);
		   
		   System.exit(1);
		   
	   }
   }
   
   public Statement createStatement() throws SQLException{
	   return conn.createStatement();
   }
   
   public PreparedStatement createPreparedStatement(String s) throws SQLException{
	   return conn.prepareStatement(s);
   }
	   
}
