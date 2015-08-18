package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Patient {

	private String firstName;
	private String lastName;

	private String medicareNumber;
	private String hospitalCardNumber;

	private PatientRoom room;

	private List<ServiceRecord> serviceRecords;
	private List<Medication> medications;

	private final int id;
	
	private Patient(int id, String fname, String lname, String medicareNumber, String hospitalCardNumber, PatientRoom room){
		this.id = id;
		this.firstName = fname;
		this.lastName = lname;
		this.medicareNumber = medicareNumber;
		this.hospitalCardNumber = hospitalCardNumber;
		this.room = room;
		this.serviceRecords = null;
		this.medications = null;
	}
	
	public Patient(int id){
		this(id, null, null, null, null, null);
	}

	public static Patient get(int id){

		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT medicare_number, hospital_card_number, "
					+ " Patient.first_name as patient_fname, Patient.family_name AS patient_lname, "
					+ "room_number, max_capacity, "                              // patient_room
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
							id,
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

	public List<Medication> getMedications(){
		if(medications != null){
			return medications;
		}else{
			List<Medication> meds = new LinkedList<Medication>();
			
			try{
				PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT name, supply_id FROM "+
					"    patient_medication "+
					"    JOIN "+
					"        supply  "+
					"            ON supply.supply_id=patient_medication.medication_supply_id "+
					"WHERE patient_id=?");
				
				s.setInt(1, id);
				
				ResultSet r = null;
				
				r = s.executeQuery();
				
				try{
					while(r.next()){
						meds.add(new Medication(
								r.getInt("supply_id"),
								r.getString("name")));
					}
					
					return (medications = meds);
				}finally{
					if (r != null) r.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
				return new LinkedList<Medication>();
			}
		}
	}
	
	public List<ServiceRecord> getServiceRecords(){
		if(serviceRecords != null){
			return serviceRecords;
		}else{
			List<ServiceRecord> records = new LinkedList<ServiceRecord>();

			try {
				PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
						"SELECT "+
						"	Service_Log.start_date_time AS service_start,"+
						"	Service_Log.end_date_time AS service_end,"+
						"	Service.service_id AS service_id,"+
						"	Service.name AS service_name,"+
						"	Service.cost AS service_cost,"+
						"	Doctor.first_name AS doctor_fname,"+
						"	Doctor.family_name  AS doctor_lname,"+
						"	Doctor.employee_id AS doctor_id,"+
						"	Nurse.first_name AS nurse_fname,"+
						"	Nurse.family_name AS nurse_lname,"+
						"	Nurse.employee_id AS nurse_id"+
						" FROM "+
						"	Service_Log "+
						"		JOIN"+
						"	Service ON Service.service_id=Service_Log.service_id"+
						"		LEFT JOIN "+
						"	Employee AS Doctor ON Doctor.employee_id=Service_Log.doctor_id "+
						"		LEFT JOIN"+
						"	Employee AS Nurse  ON Nurse.employee_id=Service_Log.nurse_id "+
						"WHERE Service_Log.patient_id=?"
						+ " ORDER BY service_start DESC");

				s.setInt(1, id);

				ResultSet r = null;

				try{
					r = s.executeQuery();

					while(r.next()){
						Nurse nurse = null;
						int nurseId = r.getInt("nurse_id");
						if( ! r.wasNull()){
							nurse = new Nurse(
								nurseId,
								r.getString("nurse_fname"),
								r.getString("nurse_lname"));
						}

						Doctor doctor = null;
						int doctorId = r.getInt("doctor_id");
						if( ! r.wasNull()){
							doctor = new Doctor(
									doctorId,
									r.getString("doctor_fname"),
									r.getString("doctor_lname"));
						}

						Service service = new Service(
								r.getInt("service_id"),
								r.getString("service_name"),
								r.getString("service_cost"));

						records.add(new ServiceRecord(
							 	new Date(r.getTimestamp("service_start").getTime()),
							 	new Date(r.getTimestamp("service_end").getTime()),
								service, doctor, nurse));

					}

					return (serviceRecords = records);
				}finally{
					if (r != null) r.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
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

	public String getMedicareNumber() {
		return medicareNumber;
	}

	public void setMedicareNumber(String medicareNumber) {
		this.medicareNumber = medicareNumber;
	}

	public String getHospitalCardNumber() {
		return hospitalCardNumber;
	}

	public void setHospitalCardNumber(String hospitalCardNumber) {
		this.hospitalCardNumber = hospitalCardNumber;
	}

	public PatientRoom getRoom() {
		return room;
	}

	public void setRoom(PatientRoom room) {
		this.room = room;
	}

	public int getId() {
		return id;
	}
}
