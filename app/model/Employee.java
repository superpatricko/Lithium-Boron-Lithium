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
	private Payroll payroll_id;

	public Employee(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public Payroll getPayroll_id() {
		return payroll_id;
	}

	public void setPayroll_id(Payroll payroll_id) {
		this.payroll_id = payroll_id;
	}

}
