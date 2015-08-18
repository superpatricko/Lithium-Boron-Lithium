package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Doctor extends Employee {

	private String firstName;
	private String lastName;

	public Doctor(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	public Doctor(int employeeId) {
		super(employeeId);
		this.firstName = null;
		this.lastName  = null;
	}

	
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
				
				return doctors;
				
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
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



}
