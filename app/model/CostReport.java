package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CostReport {
	private double totalCost =0;
	private double totalCostInStock;
	private ArrayList<Double> suppliesCostPerMonth;
	public Double getCostPerMonth(int month){
		try{
			
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT SUM(`cost`*`quantity`) as 'money'" +
					"  FROM `order_history`, `supply`" +
					"WHERE `order_history`.`supply_id`=`supply`.`supply_id`" +
					" AND `order_history`.`date_of_order` >= '2015-08-01'" +
					"AND `order_history`.`date_of_order` <= '2015-08-30'" );


			ResultSet r = null;

			try{
				r = s.executeQuery();
				totalCost = r.getDouble("money");

			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return 0.0;
		
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

	public Double getCostCurrentSupplies(){
		try{
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT SUM(`cost`*`current_stock`) as 'money'"+
					"  FROM `supply`");

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
}

