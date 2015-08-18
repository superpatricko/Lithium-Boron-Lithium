package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Patient {

	public final String firstName;
	public final String lastName;

	public final String medicareNumber;
	public final String hospitalCardNumber;

	public final PatientRoom room;

	private Patient(String fname, String lname, String medicareNumber, String hospitalCardNumber, PatientRoom room){
		this.firstName = fname;
		this.lastName = lname;
		this.medicareNumber = medicareNumber;
		this.hospitalCardNumber = hospitalCardNumber;
		this.room = room;
	}

	public static Patient get(int id){

		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT medicare_number, hospital_card_number, "
					+ " Patient.first_name as patient_fname, Patient.family_name AS patient_lname, "
					+ "room_number, max_capcity AS max_capacity, "                              // patient_room
					+ "nurse.first_name AS nurse_fname, nurse.family_name AS nurse_lname, nurse.employee_id as nurse_eid "  // nurse details
					+ " FROM Patient, Patient_Room, Employee AS nurse "
					+ "WHERE Patient.patient_id=? "
					+ "AND Patient.patient_room_id=Patient_Room.patient_room_id "
					+ "AND nurse.employee_id=Patient_Room.nurse_id ");

			s.setInt(1, id);

			ResultSet r = null;

			try{
				r = s.executeQuery();

				if(r.next()){
					Nurse nurse = new Nurse(
						r.getInt("nurse_eid"),
						r.getString("nurse_fname"),
						r.getString("nurse_lname"));

					PatientRoom room = new PatientRoom(
							r.getString("room_number"),
							r.getInt("max_capacity"),
							nurse);

					Patient patient = new Patient(
							r.getString("patient_fname"),
							r.getString("patient_lname"),
							r.getString("medicare_number"),
							r.getString("hospital_card_number"),
							room);

					return patient;
				}

			}finally{
				if (r != null) r.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
}
