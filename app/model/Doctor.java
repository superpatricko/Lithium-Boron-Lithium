package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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



}
