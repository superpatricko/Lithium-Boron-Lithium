package model;

public class PatientRoom {

	public final String roomNumber;
	public final int maxCapacity;

	public final Nurse nurse;

	/*package*/PatientRoom(String roomNumber, int maxCapacity, Nurse nurse){
		this.roomNumber = roomNumber;
		this.maxCapacity = maxCapacity;
		this.nurse = nurse;
	}

}
