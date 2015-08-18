package model;

public class Nurse extends Employee {

	public final String firstName;
	public final String lastName;

	public Nurse(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}



}
