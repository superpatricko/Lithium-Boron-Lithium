package model;

import java.util.Date;

public class ServiceRecord {

	private Nurse nurse;
	private Doctor doctor;
	private Service service;
	
	private Date start;
	private Date end;
	
	/*package*/ServiceRecord(Date start, Date end, Service service, Doctor doctor, Nurse nurse) {
		this.nurse = nurse;
		this.doctor = doctor;
		this.service = service;
		this.start = start;
		this.end   = end;
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

}
