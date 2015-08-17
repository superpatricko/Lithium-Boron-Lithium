package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSessionAuth extends SessionAuth {

	@Override
	protected boolean authenticate(String username, String password) {
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement("SELECT * FROM user_management WHERE username=? AND password=?");
			
			s.setString(1, username);
			s.setString(2, password);
			
			ResultSet r = null;
			try{								
				r = s.executeQuery();
				
				return r.next();
			}finally{
				if (r != null) r.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);			
			return false;
		}
	}

	@Override
	protected int getEmployeeId(String username) {
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement("SELECT employee_id FROM user_management WHERE username=?");
			
			s.setString(1, username);
			
			ResultSet r = null;
			try{								
				r = s.executeQuery();
				
				if(r.next()){
					return r.getInt("employee_id");
				}else{
					return 0;
				}
			}finally{
				if (r != null) r.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);			
			return 0;
		}
	}

}
