package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a service in the database
 * 
 * @author gordon
 *
 */
public class Service {

	private int id;
	
	private String name;
	private String cost;
	
	public Service(int id, String name, String cost) {
		this.id = id;
		this.name = name;
		this.cost = cost;
	}

	public Service(int id) {
		this.id = id;
		this.name = null;
		this.cost = null;
	}

	/**
	 * Get all Services in the hospital.
	 * 
	 * @return a List of Service objects, or an empty list on an error, never null
	 */
	public static List<Service> getAllServices(){
		List<Service> services = new LinkedList<Service>();
		
		// TODO - optionally filter only active services
		
		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement("SELECT name, service_id, cost FROM service");
			
			ResultSet r = null;
			
			try{
				r = s.executeQuery();
				
				while(r.next()){
					services.add(new Service(r.getInt("service_id"), r.getString("name"), r.getString("cost")));
				}
			}finally{
				if (r != null) r.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return services;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

}
