package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Administrator extends Employee {
	private String firstName;
	private String lastName;
	private boolean senior;
	private boolean director;
	private boolean openAccess;
	private static Doctor doctor;
	private static Nurse nurse;
	private static Reports reports;
	private  CostReport costReport = new CostReport();
	private static Schedule schedule;
	private static Service service;

	public Administrator(int employeeId) {
		super(employeeId);
		// TODO Auto-generated constructor stub
	}
	public Administrator(int employeeId, String firstName, String lastName,boolean director,
			boolean openAccess,boolean senior){
		super(employeeId);
		this.firstName = firstName;
		this.lastName = lastName;
		this.director = director;
		this.openAccess = openAccess;
		this.senior = senior;


	}
	public Administrator(int employeeId, String firstName, String lastName) {
		super(employeeId);
		this.firstName = firstName;
		this.lastName  = lastName;
	}
	public void setIsSeniorAdmin( boolean isSeniorAdmin){
		this.senior = isSeniorAdmin;
	}
	public static List<Administrator> getAllAdministrators(){
		List<Administrator> administrators = new LinkedList<Administrator>();

		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT employee_id, first_name, family_name,is_Director,is_open_access_admin,is_senior"
							+ "		FROM employee NATURAL JOIN administrator");

			ResultSet r = null;

			try{
				r = s.executeQuery();

				while(r.next()){
					administrators.add(new Administrator(r.getInt("employee_id"), r.getString("first_name"), r.getString("family_name"),
							r.getBoolean("is_Director"),r.getBoolean("is_open_access_admin"),r.getBoolean("is_senior")));
				}

			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		return administrators;

	}
	public List<Reports> getAllReports(){
		return Reports.getAllReports();
	}
	public  List<Doctor> getAllDoctors(){
		return Doctor.getAllDoctors();
	}
	//Not not the same getAllServices using mega all atributes
	public  List<Service> getAllServices(){
		return Service.getMegaServices();
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
	public boolean isSenior() {
		return senior;
	}
	public void setSenior(boolean senior) {
		this.senior = senior;
	}
	public boolean isDirector() {
		return director;
	}
	public void setDirector(boolean director) {
		this.director = director;
	}
	public boolean isOpenAccess() {
		return openAccess;
	}
	public void setOpenAccess(boolean openAccess) {
		this.openAccess = openAccess;
	}
	public List<Nurse> getAllNurses(){
		return nurse.getAllNurses();
	}
	public void addService(int id,String name,float cost,int unit,int isActive){
		try{
			String query = "Insert Into service Values(?,?,?,?,?)";
			PreparedStatement s =DatabaseManager.instance.createPreparedStatement(
					query);

			s.setInt(1, id);
			s.setString(2, name);
			s.setFloat(3,cost);
			s.setInt(4, unit);

			s.setBoolean(5,isActive==1);
			System.out.println(s);
			System.out.println(s.executeUpdate());
		}
		catch(Exception e){

		}
	}
	public  double getTotalCosts(){
		return costReport.getTotalCosts();
	}
	public double getCurrentTotalCosts(){
		return costReport.getCostCurrentSupplies();
	}
	public List<CostReport> getTotalSupplyUsing(){
		return costReport.getTotalUsage();
		
	}
	public List<CostReport> getMonthUsage(){
		return costReport.getMonthUsageReport();
	}
	public ArrayList<Double> getCostsPerMonth(){
		return costReport.getCostMonth();
	}
	public boolean deleteId(int id){
		try{
			/*String findIsActive = "Select is_active from service where service_id=?";
			PreparedStatement active =DatabaseManager.instance.createPreparedStatement(
					findIsActive);
			active.setInt(1, id);
			ResultSet result = active.executeQuery();*/
			
			String query = "Update service"
					+ " SET is_active=FALSE"
					+ " WHERE service_id=?";
					
			PreparedStatement s =DatabaseManager.instance.createPreparedStatement(
					query);

			s.setInt(1, id);

			System.out.println("excuting " +s);

			System.out.println(s.executeUpdate());		
		}
		catch(Exception e){

		}
		return true;


	}
}