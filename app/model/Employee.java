package model;

import model.Payroll.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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

	// Get all payroll statements for this employee
	public List<Payroll> getPayrolls() {
		List<Payroll> payrolls = new LinkedList<Payroll>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT payroll_id, title, pay_type, base_rate, seniority_bonus_amount, seniority_bonus_multiplier, overtime_multiplier " + 
					"FROM Payroll NATURAL JOIN Employee " + 
					"WHERE Payroll." + this.payroll_id + "=Employee." + this.payroll_id);
	
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					payrolls.add(new Payroll(
						r.getInt("payroll_id"), 
						r.getString("title"), 
						Pay_Type.valueOf(r.getString("payType")), // not sure if we can do this for enums... 
						r.getDouble("base_rate"),
						r.getInt("seniority_bonus_amount"),
						r.getFloat("seniority_bonus_multiplier"),
						r.getFloat("overtime_multiplier")));
				}
				
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return payrolls;
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
