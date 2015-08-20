package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Supplies {
	private int supply_Id;

	private String name;
	private String lastPurchase;
	private int currentStock;
	private int max_Capacity;
	private double cost;
	private int amountSelected;
	private int room;
	private String type;
	private String secondary;
	private String tertiary;
	private String currentDate="2015-08-20";
	private String [] typeOfSupply = {"Medical","Nutritional","Non-Medical"};
	public Supplies(){
		
	}
	public Supplies(int supply_Id,String name,String lastPurchase,int currentStock,int max_Capacity,double cost,
			int room,String type){
		this.supply_Id = supply_Id;
		this.name = name;
		this.currentStock = currentStock;
		this.lastPurchase = lastPurchase;
		this.max_Capacity = max_Capacity;
		this.room =room;


	}
	public Supplies(int supply_Id,int amountSelected,String type){
		this.supply_Id =supply_Id;
		this.amountSelected = amountSelected;
		this.type = type;
	}
	public List<Supplies> totalSupplies(){
		List<Supplies> l = new LinkedList<Supplies>();

		try{PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
				"Select * from Supply");

		ResultSet r = null;

		try{
			r = s.executeQuery();
			while(r.next()){
				l.add(new Supplies(r.getInt("supply_id"),r.getString("name"),"",r.getInt("current_Stock"),r.getInt("max_capacity"),
						r.getDouble("cost"),r.getInt("room_id"),r.getString("type")));
			}
			


		}finally{
			if (r != null) r.close();
		}
		}catch(SQLException e){
			e.printStackTrace();
		}

		return l;

	}
	public int getSupply_Id() {
		return supply_Id;
	}
	public void setSupply_Id(int supply_Id) {
		this.supply_Id = supply_Id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastPurchase() {
		return lastPurchase;
	}
	public void setLastPurchase(String lastPurchase) {
		this.lastPurchase = lastPurchase;
	}
	public int getCurrentStock() {
		return currentStock;
	}
	public void setCurrentStock(int currentStock) {
		this.currentStock = currentStock;
	}
	public int getMax_Capacity() {
		return max_Capacity;
	}
	public void setMax_capacity(int max_capacity) {
		this.max_Capacity = max_capacity;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getAmountSelected() {
		return amountSelected;
	}
	public void setAmountSelected(int amountSelected) {
		this.amountSelected = amountSelected;
	}
	public int getRoom() {
		return room;
	}
	public void setRoom(int room) {
		this.room = room;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSecondary() {
		return secondary;
	}
	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}
	public String getTertiary() {
		return tertiary;
	}
	public void setTertiary(String tertiary) {
		this.tertiary = tertiary;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public String[] getTypeOfSupply() {
		return typeOfSupply;
	}
	public void setTypeOfSupply(String[] typeOfSupply) {
		this.typeOfSupply = typeOfSupply;
	}
}
