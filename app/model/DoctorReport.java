package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DoctorReport {
	private int employeeId;
	private String firstName;
	private String lastName;
	private int serviceId;
	private String serviceName;
	private int count;
	public DoctorReport(){
		
	}
	public DoctorReport(int employeeId,String firstName, String lastName,int serviceId,String serviceName, int count){
		this.employeeId = employeeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.count = count;
	}
	public List<DoctorReport> totalServicesProvided(){
		List<DoctorReport> l = new LinkedList<DoctorReport>();
		{
			try{PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT doctor.employee_id, employee.first_name, employee.family_name, service.service_id, service.name, count(`service_log`.`service_id`)as Counter " +
					" FROM employee, doctor, service, service_log" +
					" WHERE employee.employee_id=doctor.employee_id " +
					" AND doctor.employee_id=service_log.doctor_id " +
					" AND service_log.service_id=service.service_id " +
					" GROUP BY doctor.employee_id, service.service_id ");

			ResultSet r = null;

			try{
				r = s.executeQuery();
				while(r.next()){
					l.add(new DoctorReport(r.getInt("employee_id"),r.getString("first_name"),r.getString("family_name"),
							r.getInt("service_id"),r.getString("name"),r.getInt("Counter")));
				}


			}finally{
				if (r != null) r.close();
			}
			}catch(SQLException e){
			e.printStackTrace();
		}
		return l;

	}
		
	}
	public List<DoctorReport> totalSurgeriesProvided(){
		List<DoctorReport> l = new LinkedList<DoctorReport>();
		
			try{PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT doctor.employee_id, employee.first_name, employee.family_name, service.service_id, service.name, count(service_log.service_id) as Counter" +
					"  FROM employee, doctor, service, service_log " +
					"  WHERE employee.employee_id=doctor.employee_id " +
					" AND doctor.employee_id=service_log.doctor_id " +
					" AND service_log.service_id=service.service_id " +
					" AND service.service_id= 12 " +
					" GROUP BY doctor.employee_id, service.service_id " );

			ResultSet r = null;

			try{
				r = s.executeQuery();
				while(r.next()){
					l.add(new DoctorReport(r.getInt("employee_id"),r.getString("first_name"),r.getString("family_name"),
							r.getInt("service_id"),r.getString("name"),r.getInt("Counter")));
				}


			}finally{
				if (r != null) r.close();
			}
			}catch(SQLException e){
			e.printStackTrace();
		}
		return l;

	}
		
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
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
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
