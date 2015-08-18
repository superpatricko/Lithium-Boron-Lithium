package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a Nurse in the database. Very similar to {@link Doctor}
 * 
 * @author gordon
 *
 */
public class Nurse extends Employee {

	private String firstName;
	private String lastName;

	/**
	 * @see Doctor#Doctor(int, String, String)
	 */
	public Nurse(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	/**
	 * @see Doctor#Doctor(int)
	 */
	public Nurse(int employeeId) {
		super(employeeId);
		this.firstName = null;
		this.lastName  = null;
	}
	
	/**
	 * @see Doctor#getAllDoctors()
	 */
	public static List<Nurse> getAllNurses(){
		List<Nurse> nurses = new LinkedList<Nurse>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name FROM employee NATURAL JOIN nurse");
	
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					nurses.add(new Nurse(r.getInt("employee_id"), r.getString("first_name"), r.getString("family_name")));
				}
								
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return nurses;
		
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Schedule a service for a nurse. This should check that:
	 * 
	 * <ul>
     *   <li>The nurse has a shift scheduled during the time</li>
     *   <li>The nurse does not already have a service scheduled for that time</li>
	 * </ul>
	 * 
	 * This method does not actually insert anything, it just checks that the nurse is available
	 * 
	 * @param start The start time of the service to be scheduled
	 * @param end The end time of the service to be scheduled
	 * @param conn The connection object to use (because this may need to be run inside a transaction)
	 * @return True if the service can be scheduled, false otherwise, or if there is any error
	 */
	public boolean scheduleService(Date start, Date end, Connection conn) {
		
		Timestamp startTime = new Timestamp(start.getTime());
		Timestamp endTime   = new Timestamp(end.getTime());
		
		try{
			PreparedStatement hasAvailableShift = conn.prepareStatement("SELECT * FROM schedule WHERE"+
					" employee_id=? "+
					" AND "+
					" start_time < ? "+
					" AND "+
					" end_time > ? ");
			
			hasAvailableShift.setInt(1, getEmployeeId());
			hasAvailableShift.setTimestamp(2, startTime);
			hasAvailableShift.setTimestamp(3, endTime);
			
			ResultSet r = null;
			try{
				r = hasAvailableShift.executeQuery();
				if(! r.next()){
					return false; // There is no available shift
				}
			}finally{
				if(r != null)r.close();
			}
			
			PreparedStatement hasConflict = conn.prepareStatement(
					"SELECT * FROM service_log "+
					" WHERE nurse_id=? "+
					" AND   ( "+
					"	( ? BETWEEN start_date_time AND end_date_time ) "+
					"	OR "+
					"    ( ? BETWEEN start_date_time AND end_date_time ) "+
					"   OR "
					+ "  (start_date_time BETWEEN ? AND ?)  "
					+ ""
					+ ")");
			
			hasConflict.setInt(1, getEmployeeId());
			hasConflict.setTimestamp(2, startTime);
			hasConflict.setTimestamp(3, endTime);
			hasConflict.setTimestamp(4, startTime);
			hasConflict.setTimestamp(5, endTime);
			
			r = null;
			try{
				r = hasConflict.executeQuery();
				if(r.next()){
					return false; // There is a conflicting service scheduled
				}
			}finally{
				if(r != null)r.close();
			}
			
			return true;
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}



}
