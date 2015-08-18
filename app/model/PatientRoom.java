package model;

public class PatientRoom {

	private String roomNumber;
	private int maxCapacity;

	private Nurse nurse;

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

}
