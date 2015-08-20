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
 * Class representing a Patient in the DB
 * 
 * @author gordon
 *
 */
public class Patient {

	private String firstName;
	private String lastName;

	private String medicareNumber;
	private String hospitalCardNumber;

	private PatientRoom room;

	private List<ServiceRecord> serviceRecords;
	private List<Medication> medications;

	private final int id;
	
	/**
	 * Creates a Patient with fields properly filled out
	 */
	public Patient(int id, String fname, String lname, String medicareNumber, String hospitalCardNumber, PatientRoom room){
		this.id = id;
		this.firstName = fname;
		this.lastName = lname;
		this.medicareNumber = medicareNumber;
		this.hospitalCardNumber = hospitalCardNumber;
		this.room = room;
		this.serviceRecords = null;
		this.medications = null;
	}
	
	/**
	 * Create a patient with only an ID. The purpose of this is the same as {@link Doctor#Doctor(int)}
	 * @param id
	 */
	public Patient(int id){
		this(id, null, null, null, null, null);
	}

	/**
	 * Get a fully populated Patient object for the given Patient ID.
	 * 
	 * This makes a query to retrieve all the info about the Patient, the room they're assigned to,
	 * and the Nurse that is assigned to their room.
	 * 
	 * @param id The id of the patient to retrieve info about
	 * @return A fully filled Patient object, or null if there is no patient with the given ID
	 */
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

	/**
	 * Retrieve a list of medications for this patient.
	 * 
	 * The first time this is called it makes a query to retrieve the medications and
	 * caches the result. Subsequent calls use the saved list of medications.
	 * 
	 * @return A list of {@link Medication}s, or an empty list if there is an error, never null.
	 */
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
					
				}finally{
					if (r != null) r.close();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			return (medications = meds);
		}
	}
	
	/**
	 * Retrieve a list of {@link ServiceRecord}s that the current Patient is associated to. These
	 * can either be ones scheduled for the future, or ones that are in the past, or ones that are
	 * currently happening.
	 * 
	 * The first time this is called it makes a query for all of the records and caches the result.
	 * Subsequent calls, the saved result is returned.
	 * 
	 * Retrieves info about the ServiceRecord, as well as the associated Doctor (if any), Nurse (if any)
	 * and the Service to be performed.
	 * 
	 * @return A list of ServiceRecord objects, or an empty list if there is an error, never null
	 */
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

				}finally{
					if (r != null) r.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return (serviceRecords = records);
		}
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

	/**
	 * Attempt to schedule a service for a patient. This does not actually write anything,
	 * it simply checks that there is not already a conflicting service scheduled.
	 * 
	 * @param start The start of the service to be scheduled
	 * @param end The end of the service to be scheduled
	 * @param conn The Connection object to use to make queries, it's assumed we're probably inside a transaction when we do this.
	 * @return True if there is no conflict false if there is or if an error occurs.
	 */
	public boolean scheduleService(Date start, Date end, Connection conn) {

		Timestamp startTime = new Timestamp(start.getTime());
		Timestamp endTime   = new Timestamp(end.getTime());
		
		try{
			PreparedStatement hasConflict = conn.prepareStatement(
					"SELECT * FROM service_log "+
					" WHERE patient_id=? "+
					" AND   ( "+
					"	( ? BETWEEN start_date_time AND end_date_time ) "+
					"	OR "+
					"    ( ? BETWEEN start_date_time AND end_date_time ) "+
					"   OR "
					+ "  (start_date_time BETWEEN ? AND ?)  "
					+ ""
					+ ")");
			
			hasConflict.setInt(1, getId());
			hasConflict.setTimestamp(2, startTime);
			hasConflict.setTimestamp(3, endTime);
			hasConflict.setTimestamp(4, startTime);
			hasConflict.setTimestamp(5, endTime);
			
			ResultSet r = null;
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
