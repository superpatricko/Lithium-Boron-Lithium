package model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Class representing a ServiceRecord in the database
 * @author gordon
 *
 */
public class ServiceRecord {

	private Nurse nurse;
	private Doctor doctor;
	private Service service;
	
	private Date start;
	private Date end;
	
	private Patient patient;
	
	/**
	 * Create a new service record Object. Used both for retrieving values, and for creating and
	 * subsequently saving Service Records.
	 * 
	 * @param start The start time of the ServiceRecord
	 * @param end The end time of the ServiceRecord
	 * @param service The service to be performed (must not be null)
	 * @param doctor The doctor to perform the service (may be null)
	 * @param nurse The nurse to perform the service (may be null)
	 */
	public ServiceRecord(Date start, Date end, Service service, Doctor doctor, Nurse nurse) {
		this.nurse = nurse;
		this.doctor = doctor;
		this.service = service;
		this.start = start;
		this.end   = end;
	}
	
	/**
	 * Save this ServiceRecord to the database. Assumes this is a new ServiceRecord, so INSERTs a 
	 * new row. 
	 * 
	 * Assumes that the service, and optional doctor and nurse already exist in the database.
	 */
	public void writeToDb(){
		
		// TODO - ensure that there are no time conflicts for doctors!!! (hmmmmmm)
		
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(""
					+ "INSERT INTO service_log "
					+ " ( "
					//+ "   service_log_id, "
					+ "   start_date_time, "
					+ "   end_date_time, "
					+ "   service_id, "
					+ "   doctor_id, "
					+ "   nurse_id, "
					+ "   patient_id, "
					+ "   surgery_log_id, "
					+ "   operating_room_id "
					+ ") "
					+ " VALUES "
					+ "("
					+ " ?, ?, ?, ?, ?, ?, ?, ?)"
					+ "");
			
			s.setTimestamp(1, new Timestamp(start.getTime()));
			s.setTimestamp(2, new Timestamp(end.getTime()));
			
			s.setInt(3, service.getId());
			
			if(doctor == null){
				s.setNull(4, Types.INTEGER);
			}else{
				s.setInt(4, doctor.getEmployeeId());
			}

			if(nurse == null){
				s.setNull(5, Types.INTEGER);
			}else{
				s.setInt(5, nurse.getEmployeeId());
			}
			
			s.setInt(6, patient.getId());
			
			s.setNull(7, Types.INTEGER);
			s.setNull(8, Types.INTEGER);

			s.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}