package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	 * @return True if the service can be scheduled, false otherwise
	 */
	public boolean scheduleService(Date start, Date end, Connection conn) {
		
		
		return false;
	}



}
