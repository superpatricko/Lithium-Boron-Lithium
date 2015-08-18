package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.SessionAuth.SessionInfo.Role;

public class DatabaseSessionAuth extends SessionAuth {

	@Override
	protected boolean authenticate(String username, String password) {
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement("SELECT * FROM intranet_account WHERE username=? AND password=?");
			
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

	private static interface SessionInfoBuilder {
		public SessionInfo build(ResultSet r) throws SQLException;
	}
	
	private SessionInfo getSession(String username, String queryTemplate, SessionInfoBuilder sessBuilder){
		try{
			ResultSet r = null;

			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(queryTemplate);
			
			s.setString(1, username);
			
			try{
				r = s.executeQuery();
				
				if(r.next()){
					return sessBuilder.build(r);
				}
				
			}finally{
				if(r != null){
					r.close();
				}
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected SessionInfo getUserInfo(final String username) {
		SessionInfo session = null;

		// Try to get patient info -----------------------------------------------------
		session = getSession(
				username, 
				"SELECT "
				+ " patient_id, first_name, family_name "
				+ " FROM intranet_account NATURAL JOIN patient "
				+ " WHERE username=?",
				new SessionInfoBuilder() {
					@Override public SessionInfo build(ResultSet r) throws SQLException {
						return new SessionInfo(
							Role.Patient,
							username,
							r.getInt("patient_id"),
							r.getString("first_name"),
							r.getString("family_name"));
					}
				});
		if (session != null) return session;
		
		// Try to get doctor info -------------------------------------------------------
		session = getSession(
				username,
				"SELECT "
				+ " employee_id, first_name, family_name, "
				+ " is_intern, is_resident "
				+ " FROM intranet_account NATURAL JOIN employee NATURAL JOIN doctor "
				+ " WHERE username=?",
				new SessionInfoBuilder() {
					@Override public SessionInfo build(ResultSet r) throws SQLException {
						Role role;
						if(r.getBoolean("is_intern")){
							role = Role.Intern;
						}else if(r.getBoolean("is_resident")){
							role = Role.Resident;
						}else{
							role = Role.Doctor;
						}
						return new SessionInfo(
								role,
								username,
								r.getInt("employee_id"),
								r.getString("first_name"),
								r.getString("family_name"));
						};
					});
		if (session != null) return session;
		
		// Try to get nurse info -----------------------------------------------------
		session = getSession(
				username,
				"SELECT "
				+ " employee_id, first_name, family_name, "
				+ " is_or_nurse, is_shift_supervisor, is_playmate_nurse "
				+ " FROM intranet_account NATURAL JOIN employee NATURAL JOIN nurse "
				+ " WHERE username=?",
				new SessionInfoBuilder() {
					@Override public SessionInfo build(ResultSet r) throws SQLException {
						Role role;
						if(r.getBoolean("is_or_nurse")){
							role = Role.OperatingRoomNurse;
						}else if(r.getBoolean("is_shift_supervisor")){
							role = Role.NurseShiftSupervisor;
						}else if(r.getBoolean("is_playmate_nurse")){
							role = Role.PlaymateNurse;
						}else{
							role = Role.Nurse;
						}
						return new SessionInfo(
								role,
								username,
								r.getInt("employee_id"),
								r.getString("first_name"),
								r.getString("family_name"));
						};
					});
		if (session != null) return session;

		// Try to get director info -----------------------------------------------------
		session = getSession(
				username,
				"SELECT "
				+ " employee_id, first_name, family_name, "
				+ " is_director, is_open_access_admin, is_senior "
				+ " FROM intranet_account NATURAL JOIN employee NATURAL JOIN administrator "
				+ " WHERE username=?",
				new SessionInfoBuilder() {
					@Override public SessionInfo build(ResultSet r) throws SQLException {
						Role role;
						if(r.getBoolean("is_director")){
							role = Role.Director;
						}else if(r.getBoolean("is_open_access_admin")){
							role = Role.OpenAccessAdmin;
						}else if(r.getBoolean("is_senior")){
							role = Role.SeniorAdministrator;
						}else{
							role = Role.Administrator;
						}
						return new SessionInfo(
								role,
								username,
								r.getInt("employee_id"),
								r.getString("first_name"),
								r.getString("family_name"));
						};
					});
		if (session != null) return session;
				
		return null;
	}

}
