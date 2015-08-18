package model;

/**
 * Base class for all employees.
 * 
 * @author gordon
 *
 */
public class Employee {

	// TODO - Missing a lot of attributes
	// TODO - first and family names should be stored here, not in derived classes
	
	private int employeeId;

	public Employee(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

}
