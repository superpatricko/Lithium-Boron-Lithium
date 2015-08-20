package model;

import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class CostReport {
	private int supply;
	private int usage;
	private double totalCost =0;
	private double totalCostInStock;
	private ArrayList<Double> suppliesCostPerMonth = new ArrayList<Double>();

	private String[][] beginAndEndMonth = {{"2015-01-01","2015-01-31"},{"2015-02-01","2015-02-29"},
			{"2015-03-01","2015-03-30"},{"2015-04-01","2015-04-30"}, {"2015-05-01","2015-05-31"},
			{"2015-06-01","2015-06-30"},{"2015-07-01","2015-07-30"},{"2015-08-01","2015-08-30"}};

	protected CostReport(int supply,int usage){
		this.supply = supply;
		this.usage = usage;
	}
	public CostReport(){

	}
	public void setCostPerMonth(){
		for(int i=0;i<8;i++){
			suppliesCostPerMonth.add(getCostPerMonth(i));

		}

	}
	public List<CostReport> getMonthUsageReport(){
		List<CostReport> listOfUsage = new LinkedList<CostReport>();
		for(int i=0;i<8;i++){
			listOfUsage.add(getUsagePerMonth(i));

		}
		return listOfUsage;
	}
	public ArrayList<Double> getCostMonth(){
		setCostPerMonth();
		return suppliesCostPerMonth;
	}
	public CostReport getUsagePerMonth(int month){
		CostReport costReport = new CostReport(0,0);
		try{

			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT order_history.supply_id  AS Supply ,( quantity-current_stock ) AS `usage`" +
							"  FROM order_history ,supply " +
							"  WHERE order_history.supply_id = supply.supply_id" +
							" AND order_history.date_of_order >= ? " +
							" AND order_history.date_of_order <= ?");
			Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(beginAndEndMonth[month][0]);
			Date utilDate2 =  new SimpleDateFormat("yyyy-MM-dd").parse(beginAndEndMonth[month][1]);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
			java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime()); 


			s.setDate(1,sqlDate);
			s.setDate(2,sqlDate2);
			System.out.println(s);

			ResultSet r = null;
			System.out.println("did make it here");
			try{

				r = s.executeQuery();
				while(r.next()){
					costReport = new CostReport(r.getInt("Supply"),r.getInt("usage"));
				}


			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return costReport;
	}

	public Double getCostPerMonth(int month){
		try{

			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT SUM(cost*quantity) as money " +
							"  FROM order_history,supply" +
							" WHERE order_history.supply_id=supply.supply_id " +
							" AND order_history.date_of_order >= ? " +
					" AND order_history.date_of_order <= ? " );
			Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(beginAndEndMonth[month][0]);
			Date utilDate2 =  new SimpleDateFormat("yyyy-MM-dd").parse(beginAndEndMonth[month][1]);
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); 
			java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime()); 

			s.setDate(1,sqlDate);
			s.setDate(2,sqlDate2);
			System.out.println(s);

			ResultSet r = null;

			try{

				r = s.executeQuery();
				while(r.next()){
					totalCost = r.getDouble("money");



				}


			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalCost;

	}
	public Double getTotalCosts(){
		try{
			System.out.println("started statement");
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT SUM(cost*quantity) AS money " +
							"  FROM order_history,supply " +
					"  WHERE order_history.supply_id=supply.supply_id");

			ResultSet r = null;

			try{
				r = s.executeQuery();
				while(r.next()){
					totalCost = r.getDouble("money");
				}

				System.out.println(totalCost);

			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		return totalCost;
	}
	public  List<CostReport> getTotalUsage(){
		List<CostReport> reports = new LinkedList<CostReport>();
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT order_history.supply_id AS Supply, (quantity-current_stock) AS 'usage' "+
							" FROM order_history, supply " +
					" WHERE order_history.supply_id=supply.supply_id");

			ResultSet r = null;

			try{
				r = s.executeQuery();
				while(r.next()){
					reports.add(new CostReport(r.getInt("Supply"),r.getInt("usage")));
				}


			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return reports;

	}
	public Double getCostCurrentSupplies(){
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT SUM(cost* current_stock) as money"+
					"  FROM supply");

			ResultSet r = null;

			try{
				r = s.executeQuery();
				while(r.next()){
					totalCostInStock = r.getDouble("money");
				}


			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

		return totalCostInStock;

	}
	public int getSupply(){
		return supply;
	}
	public int getUsage(){
		return usage;
	}
}

