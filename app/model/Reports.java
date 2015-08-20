package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Reports {
	private int serviceLog;
	private String startTime;
	private String endTime;
	private int service;
	private int doctor;
	private int nurse;
	private int patient;
	private int surgeryLog;
	private int operationRoom;
	private String roomNumber;
	private String serviceName;
	private float cost;
	private int unit;
	private boolean isActive;
	private String unitName;
	public Reports(int unit,int service,int operationRoom,int serviceLog,String startTime,String endTime,
			int doctor,
			int nurse,int patient,int surgeryLog,String roomNumber,String serviceName,float cost,
			boolean isActive, String unitName){
		this.serviceLog =serviceLog;
		this.startTime = startTime;
		this.endTime = endTime;
		this.service = service;
		this.doctor = doctor;
		this.nurse = nurse;
		this.patient = patient;
		this.surgeryLog = surgeryLog;
		this.operationRoom = operationRoom;
		this.roomNumber = roomNumber;
		this.serviceName = serviceName;
		this.cost = cost;
		this.unit = unit;
		this.isActive = isActive;
		this.unitName = unitName;



	}
	public static List<Reports> getAllReports(){
		List<Reports> reports = new LinkedList<Reports>();

		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"Select * from service_log natural join operating_room natural join service"
							+ "  natural join unit "
							+ "  order by unit_id,service_id;");

			ResultSet r = null;

			try{
				r = s.executeQuery();

				while(r.next()){
					reports.add(new Reports(r.getInt("unit_id"), r.getInt("service_id"), r.getInt("operating_room_id"),r.getInt("service_log_id"),
							r.getString("start_date_time"),r.getString("end_date_time"),r.getInt("doctor_id"),
							r.getInt("nurse_id"),r.getInt("patient_id"),r.getInt("surgery_log_id"),
							r.getString("room_number"),r.getString("name"),r.getFloat("cost"),
							r.getBoolean("is_active"),r.getString("unit_name")));



				}

			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		return reports;

	}
	
	public int getServiceLog() {
		return serviceLog;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public int getService() {
		return service;
	}
	public int getDoctor() {
		return doctor;
	}
	public int getNurse() {
		return nurse;
	}
	public int getPatient() {
		return patient;
	}
	public int getSurgeryLog() {
		return surgeryLog;
	}
	public int getOperationRoom() {
		return operationRoom;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public String getServiceName() {
		return serviceName;
	}
	public float getCost() {
		return cost;
	}
	public int getUnit() {
		return unit;
	}
	public boolean isActive() {
		return isActive;
	}
	public String getUnitName() {
		return unitName;
	}

}
