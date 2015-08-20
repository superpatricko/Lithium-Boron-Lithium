package model;

import java.util.List;

/**
 * Class representing a patient room in the database
 * 
 * @author gordon
 *
 */
public class PatientRoom {

	private String roomNumber;
	private int maxCapacity;

	private Nurse nurse;
	private int roomId;
	private List<Patient> patients;

	// Default room
	public PatientRoom(String roomNumber) {
		this.roomNumber = roomNumber;
		this.maxCapacity = 4;
		this.nurse = null;
	}

	/*package*/PatientRoom(int roomid, String roomNumber, int maxCapacity, Nurse nurse){
		this.roomId = roomid;
		this.roomNumber = roomNumber;
		this.maxCapacity = maxCapacity;
		this.nurse = nurse;
	}

	
	/*package*/PatientRoom(String roomNumber, int maxCapacity, Nurse nurse){
		this.roomNumber = roomNumber;
		this.maxCapacity = maxCapacity;
		this.nurse = nurse;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Patient> getPatients() {
		return patients;
	}

}
