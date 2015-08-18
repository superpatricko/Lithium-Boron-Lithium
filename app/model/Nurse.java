package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Nurse extends Employee {

	private String firstName;
	private String lastName;

	public Nurse(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	public Nurse(int employeeId) {
		super(employeeId);
		this.firstName = null;
		this.lastName  = null;
	}
	
	public static List<Nurse> getAllNurses(){
		List<Nurse> nurse = new LinkedList<Nurse>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name FROM employee NATURAL JOIN nurse");
	
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					nurse.add(new Nurse(r.getInt("employee_id"), r.getString("first_name"), r.getString("family_name")));
				}
				
				return nurse;
				
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
