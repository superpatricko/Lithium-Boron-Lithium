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
 * Class representing a Nurse in the database. Very similar to {@link Doctor}
 * 
 * @author gordon
 *
 */
public class Nurse extends Employee {

	private String firstName;
	private String lastName;
	
	private List<ServiceRecord> serviceRecords;

	/**
	 * @see Doctor#Doctor(int, String, String)
	 */
	public Nurse(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}

	/**
	 * @see Doctor#Doctor(int)
	 */
	public Nurse(int employeeId) {
		super(employeeId);
		this.firstName = null;
		this.lastName  = null;
	}
	
	
	public static Nurse getNurse(int id){
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement("SELECT * FROM nurse NATURAL JOIN employee WHERE employee_id=?");

			s.setInt(1, id);

			ResultSet r = null;

			try{
				r = s.executeQuery();

				while(r.next()){
					return new Nurse(id, r.getString("first_name"), r.getString("family_name"));
				}

			}finally{
				if (r != null) r.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ServiceRecord> getServiceRecords(){
		if(serviceRecords != null){
			return serviceRecords;
		}else{
			List<ServiceRecord> records = new LinkedList<ServiceRecord>();

			try {
				PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
						" SELECT  " +
								" 	Service_Log.start_date_time AS service_start, " +
								" 	Service_Log.end_date_time AS service_end, " +
								" 	Service.service_id AS service_id, " +
								" 	Service.name AS service_name, " +
								" 	Service.cost AS service_cost, " +
								" 	Doctor.first_name AS doctor_fname, " +
								" 	Doctor.family_name  AS doctor_lname, " +
								" 	Doctor.employee_id AS doctor_id, " +
								" 	Patient.first_name AS patient_fname, " +
								" 	Patient.family_name AS patient_lname, " +
								" 	Patient.patient_id AS patient_id " +
								"  FROM  " +
								" 	Service_Log  " +
								" 		JOIN " +
								" 	Service ON Service.service_id=Service_Log.service_id " +
								" 		LEFT JOIN  " +
								" 	Employee AS Doctor ON Doctor.employee_id=Service_Log.doctor_id  " +
								" 		JOIN " +
								" 	Employee AS Nurse  ON Nurse.employee_id=Service_Log.nurse_id  " +
								" 		JOIN " +
								" 	Patient ON Service_Log.patient_id=Patient.patient_id " +
								" WHERE Nurse.employee_id=? " +
								"  ORDER BY service_start DESC ");

				s.setInt(1, getEmployeeId());

				ResultSet r = null;

				try{
					r = s.executeQuery();

					while(r.next()){
						Doctor doctor = null;
						int doctorId = r.getInt("doctor_id");
						if( ! r.wasNull()){
							doctor = new Doctor(
									doctorId,
									r.getString("doctor_fname"),
									r.getString("doctor_lname"));
						}

						Patient patient = new Patient(
								r.getInt("patient_id"),
								r.getString("patient_fname"),
								r.getString("patient_lname"), null, null, null);
						
						Service service = new Service(
								r.getInt("service_id"),
								r.getString("service_name"),
								r.getString("service_cost"));

						records.add(new ServiceRecord(
								patient,
							 	new Date(r.getTimestamp("service_start").getTime()),
							 	new Date(r.getTimestamp("service_end").getTime()),
								service, doctor, this));

					}

				}finally{
					if (r != null) r.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return (serviceRecords = records);
		}
	}
	
	public List<PatientRoom> getAssignedRooms(){
		
		List<PatientRoom> rooms = new LinkedList<PatientRoom>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"select * from "
					+ " patient_room "
					+ " NATURAL JOIN patient "
					+ " WHERE nurse_id=? "
					+ " ORDER BY patient_room_id");
	
			s.setInt(1, getEmployeeId());
			
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				PatientRoom room = null;
				List<Patient> patients = null;
				while(r.next()){
										
					int roomId = r.getInt("patient_room_id");
					
					if(room == null || roomId != room.getRoomId()){
						if(room != null){
							room.setPatients(patients);
							rooms.add(room);
						}
						
						room = new PatientRoom(roomId, r.getString("room_number"), r.getInt("max_capacity"), this);
						patients = new LinkedList<Patient>();
					}
					
					patients.add(new Patient(
							r.getInt("patient_id"),
							r.getString("first_name"),
							r.getString("family_name"),
							r.getString("medicare_number"),
							r.getString("hospital_card_number"),
							room));
				}
				
				if(room != null && patients != null){
					room.setPatients(patients);
					rooms.add(room);
				}
								
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return rooms;
	
		
	}
	
	/**
	 * @see Doctor#getAllDoctors()
	 */
	public static List<Nurse> getAllNurses(){
		List<Nurse> nurses = new LinkedList<Nurse>();
		
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name FROM employee NATURAL JOIN nurse");
	
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					nurses.add(new Nurse(r.getInt("employee_id"), r.getString("first_name"), r.getString("family_name")));
				}
								
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return nurses;
		
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
	 * Schedule a service for a nurse. This should check that:
	 * 
	 * <ul>
     *   <li>The nurse has a shift scheduled during the time</li>
     *   <li>The nurse does not already have a service scheduled for that time</li>
	 * </ul>
	 * 
	 * This method does not actually insert anything, it just checks that the nurse is available
	 * 
	 * @param start The start time of the service to be scheduled
	 * @param end The end time of the service to be scheduled
	 * @param conn The connection object to use (because this may need to be run inside a transaction)
	 * @return True if the service can be scheduled, false otherwise, or if there is any error
	 */
	public boolean scheduleService(Date start, Date end, Connection conn) {
		
		Timestamp startTime = new Timestamp(start.getTime());
		Timestamp endTime   = new Timestamp(end.getTime());
		
		try{
			PreparedStatement hasAvailableShift = conn.prepareStatement("SELECT * FROM schedule WHERE"+
					" employee_id=? "+
					" AND "+
					" start_time < ? "+
					" AND "+
					" end_time > ? ");
			
			hasAvailableShift.setInt(1, getEmployeeId());
			hasAvailableShift.setTimestamp(2, startTime);
			hasAvailableShift.setTimestamp(3, endTime);
			
			ResultSet r = null;
			try{
				r = hasAvailableShift.executeQuery();
				if(! r.next()){
					return false; // There is no available shift
				}
			}finally{
				if(r != null)r.close();
			}
			
			PreparedStatement hasConflict = conn.prepareStatement(
					"SELECT * FROM service_log "+
					" WHERE nurse_id=? "+
					" AND   ( "+
					"	( ? BETWEEN start_date_time AND end_date_time ) "+
					"	OR "+
					"    ( ? BETWEEN start_date_time AND end_date_time ) "+
					"   OR "
					+ "  (start_date_time BETWEEN ? AND ?)  "
					+ ""
					+ ")");
			
			hasConflict.setInt(1, getEmployeeId());
			hasConflict.setTimestamp(2, startTime);
			hasConflict.setTimestamp(3, endTime);
			hasConflict.setTimestamp(4, startTime);
			hasConflict.setTimestamp(5, endTime);
			
			r = null;
			try{
				r = hasConflict.executeQuery();
				if(r.next()){
					return false; // There is a conflicting service scheduled
				}
			}finally{
				if(r != null)r.close();
			}
			
			return true;
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}



}
