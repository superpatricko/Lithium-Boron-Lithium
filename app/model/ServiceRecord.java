package model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

public class ServiceRecord {

	private Nurse nurse;
	private Doctor doctor;
	private Service service;
	
	private Date start;
	private Date end;
	
	private Patient patient;
	
	public ServiceRecord(Date start, Date end, Service service, Doctor doctor, Nurse nurse) {
		this.nurse = nurse;
		this.doctor = doctor;
		this.service = service;
		this.start = start;
		this.end   = end;
	}
	
	public void writeToDb(){
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
