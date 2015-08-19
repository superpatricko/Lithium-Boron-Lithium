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
 * This class represents a Doctor in the database.
 * 
 * @author gordon
 *
 */
public class Doctor extends Employee {

	// TODO hold *ALL* attributes of the doctor
	// TODO make it so that when a getter is called and an attribute is null,
	//      a query will automatically be run to fetch that attribute.
	
	private String firstName;
	private String lastName;
	private boolean is_intern;
	private boolean is_resident;
	private int supervising_physician_id;

	/**
	 * Create a new Doctor object with the given name and ID. This constructor is meant for
	 * use with functions that retrieve doctor objects from the database to display them to the
	 * user.
	 * 
	 * @param employeeId The doctor's employee ID
	 * @param firstName The doctor's first name
	 * @param lastName The doctor's last name
	 */
	/*package*/ Doctor(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	/**
	 * Create a new Doctor object with only an ID. This constructor is meant for
	 * situations where you want to link an existing doctor to some other object,
	 * and only know or care about the doctor's ID.
	 * 
	 * For example, in the case of scheduling a new {@link Service} for a patient, we know the
	 * ID of the doctor to perform it, and want to add that Doctor to the {@link ServiceRecord},
	 * but we don't actually care about the doctor's name or anything else at that point.
	 * 
	 * @param employeeId The doctor's employee ID
	 */
	public Doctor(int employeeId) {
		super(employeeId);
		this.firstName = null;
		this.lastName  = null;
	}

	public Doctor(int employeeId, String firstName, String lastName, boolean is_intern, 
		boolean is_resident, int supervising_physician_id) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
		this.is_intern  = is_intern;
		this.is_resident  = is_resident;
		this.supervising_physician_id  = supervising_physician_id;
	}

	/**
	 * Get a list of all doctors in the database.
	 * 
	 * @return A list of all the doctors that exist in the database (including interns and residents).
	 *         Even in the case of an error this returns an empty list, never null
	 */
	public static List<Doctor> getAllDoctors(){
		List<Doctor> doctors = new LinkedList<Doctor>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name FROM employee NATURAL JOIN doctor");
	
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					doctors.add(new Doctor(r.getInt("employee_id"), r.getString("first_name"), r.getString("family_name")));
				}
								
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return doctors;
		
	}

	// Get a specific doctor from the database, given an ID
	public static Doctor getSpecificDoctor(int givenID){
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name, is_intern, is_resident, supervising_physician_id " +
					"FROM Employee NATURAL JOIN Doctor" +
					"WHERE Employee.employee_id=? "
					+ "AND Doctor.employee_id=? ");

			s.setInt(1, givenID);
			s.setInt(2, givenID);

			ResultSet r = null;

			try{
				r = s.executeQuery();

				if(r.next()){
					Doctor doctor = new Doctor(
						r.getInt("employee_id"),
						r.getString("first_name"),
						r.getString("family_name"),
						r.getBoolean("is_intern"),
						r.getBoolean("is_resident"),
						r.getInt("supervising_physician_id"));

					return doctor;
				}

			}finally{
				if (r != null) r.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	 * Attempt to schedule a service for a Doctor. This should check that the Doctor does
	 * not already have a schedule item which conflicts with this one, and should write a 
	 * new schedule entry if that is the case.
	 * 
	 * @param start The start of the time for this service
	 * @param end The end of the time for this service
	 * @param conn The Connection object to use. It is assumed this is called as part of a larger transaction.
	 * @return True if there was no conflict and the new entry was successfully written, false otherwise
	 */
	public boolean scheduleService(Date start, Date end, Connection conn) {
		
		Timestamp startTime = new Timestamp(start.getTime());
		Timestamp endTime   = new Timestamp(end.getTime());
		
		try{
			PreparedStatement hasConflict = conn.prepareStatement(
					"SELECT * FROM schedule "+
					" WHERE employee_id=? "+
					" AND   ( "+
					"	( ? BETWEEN start_time AND end_time ) "+
					"	OR "+
					"    ( ? BETWEEN start_time AND end_time ) "+
					"   OR"
					+ "  ( start_time BETWEEN ? AND ? ) "
					+ " )");
			
			hasConflict.setInt(1, getEmployeeId());
			hasConflict.setTimestamp(2, startTime);
			hasConflict.setTimestamp(3, endTime);
			hasConflict.setTimestamp(4, startTime);
			hasConflict.setTimestamp(5, endTime);
			
			ResultSet r = null;
			try{
				r = hasConflict.executeQuery();
				if(r.next()){
					return false; // There is a conflict!!
				}
			}finally{
				if(r != null)r.close();
			}
			
			PreparedStatement insertSchedule = conn.prepareStatement("INSERT INTO schedule (employee_id, start_time, end_time) VALUES (?,?,?)");
			
			insertSchedule.setInt(1, getEmployeeId());
			insertSchedule.setTimestamp(2, startTime);
			insertSchedule.setTimestamp(3, endTime);
			
			if(1 == insertSchedule.executeUpdate()){
				return true; // insert worked!
			}else{
				return false; // somehow the insert did not work
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return false;
	}



}
