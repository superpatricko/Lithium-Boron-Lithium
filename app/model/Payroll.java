package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import model.Payroll.PayPeriodInfo.PayPeriodInfoLine;

public class Payroll {

	public static enum Pay_Type { wage, year, doctor };

	private int employeeId;
	private int payroll_id;
	private String title;
	private Pay_Type pay_type;
	private BigDecimal base_rate;
	private Integer seniority_bonus_amount;
	private Float seniority_bonus_multiplier;
	private Float overtime_multiplier;
	private Calendar dateOfHire;

	private Payroll(int employeeId, Date date_of_hire, int payroll_id, String title, Pay_Type pay_type, BigDecimal base_rate,
		Integer seniority_bonus_amount, Float seniority_bonus_multiplier, Float overtime_multiplier
	) {

		this.employeeId = employeeId;

		this.dateOfHire = Calendar.getInstance();
		this.dateOfHire.setTime(date_of_hire);

		this.payroll_id = payroll_id;
		this.title = title;
		this.pay_type = pay_type;
		this.base_rate = base_rate;
		this.seniority_bonus_amount = seniority_bonus_amount;
		this.seniority_bonus_multiplier = seniority_bonus_multiplier;
		this.overtime_multiplier = overtime_multiplier;
	}
	
	public static class PayPeriodInfo {
		
		public static class PayPeriodInfoLine {
			public final String what;
			public final BigDecimal howMuch;
			
			public PayPeriodInfoLine(String w, BigDecimal h){
				this.what = w;
				this.howMuch = h;
			}
		};
		
		public final List<PayPeriodInfoLine> payPeriodInfo;
		public BigDecimal total;
		
		public PayPeriodInfo(){
			payPeriodInfo = new LinkedList<Payroll.PayPeriodInfo.PayPeriodInfoLine>();
		}
		
	};
	
	public PayPeriodInfo getWagePayAmountForCurrentPeriod(){
		PayPeriodInfo result = new PayPeriodInfo();
		
		
		BigDecimal baseRate = base_rate == null ? new BigDecimal(0) : base_rate;
		result.payPeriodInfo.add(new PayPeriodInfoLine("Base Salary", baseRate));
				
		if(this.seniority_bonus_amount != null && this.seniority_bonus_multiplier != null){
			// Get a copy of the date of hire of the employee
			Calendar workStart   = Calendar.getInstance( );
			workStart.setTimeInMillis( dateOfHire.getTimeInMillis() );
			
			// Get the start of the current period
			Calendar periodStart = PayPeriod.getStartOfCurrentPeriod();
			
			// Get the difference in years between the start of work and the start of the period
			int deltaYear = periodStart.get(Calendar.YEAR) - workStart.get(Calendar.YEAR);
			workStart.set(Calendar.YEAR, periodStart.get(Calendar.YEAR));
			
			// If the period started before the employee's work anniversary, then
			// they have not worked this *full* year
			if( periodStart.before(workStart)){
				deltaYear -= 1; // round down the number of years
			}
			
			// Calculate how much extra money that actually gives
			BigDecimal seniorityBonus = new BigDecimal(
				(deltaYear / this.seniority_bonus_amount.intValue()) * this.seniority_bonus_multiplier.floatValue() );
			
			result.payPeriodInfo.add(new PayPeriodInfoLine("Seniority bonus (" + deltaYear + " years of work)", seniorityBonus));
			
			baseRate = baseRate.add(seniorityBonus);
		}
		
		BigDecimal hoursWorked = PayPeriod.getHoursWorkedInCurrentPeriod(employeeId);
		
		result.payPeriodInfo.add(new PayPeriodInfoLine("Hours worked this period", hoursWorked));
		
		BigDecimal regularHours = new BigDecimal(36);
		
		if(overtime_multiplier != null && hoursWorked.compareTo(regularHours) > 0){
			BigDecimal regularPay = regularHours.multiply(baseRate);
			result.payPeriodInfo.add(
					new PayPeriodInfoLine(
							"Regular time pay ("
									+hoursWorked.toPlainString()
									+" @ "
									+baseRate.toPlainString()
									+")", 
							regularPay));
			
			BigDecimal overtimeRate = baseRate.multiply(new BigDecimal(overtime_multiplier));
			BigDecimal overtimeHours = hoursWorked.subtract(regularHours);
			BigDecimal overtimePay = overtimeHours.multiply(overtimeRate);
			result.payPeriodInfo.add(
					new PayPeriodInfoLine(
							"Overtime pay ("
									+overtimeHours.toPlainString()
									+" @ "
									+overtimeRate.toPlainString()
									+")", 
							overtimePay));
			
			result.total = regularPay.add(overtimePay);
			
		}else{
			result.payPeriodInfo.add(
					new PayPeriodInfoLine(
							"Regular time pay ("
									+hoursWorked.toPlainString()
									+" @ "
									+baseRate.toPlainString()
									+")", 
							hoursWorked.multiply(baseRate)));
			
			result.total = hoursWorked.multiply(baseRate);
		}
		
		return result;
				
	}
	
	public PayPeriodInfo getPayAmountForCurrentPeriod(){
		
		switch(pay_type){
		case wage:
			return getWagePayAmountForCurrentPeriod();
		case year:
			return new PayPeriodInfo();
		case doctor:
			return new PayPeriodInfo();
		default:
			return new PayPeriodInfo();		
		}
		
	}
	
	// ----------- UTILS
	private static Float getFloatOrNull(ResultSet r, String column) throws SQLException{
		float v = r.getFloat(column);
		if(r.wasNull()){
			return null;
		}else{
			return v;
		}
	}
	
	private static Integer getIntOrNull(ResultSet r, String column) throws SQLException{
		int v = r.getInt(column);
		if(r.wasNull()){
			return null;
		}else{
			return v;
		}
	}
	
	public static Payroll getPayroll(int empId){
		
		try{
			
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT " +
							" date_of_hire, "
							+ " payroll_id, " +
							" title, " +
							" pay_type, " +
							" base_rate, " +
							" seniority_bonus_amount, " +
							" seniority_bonus_multiplier, " +
							" overtime_multiplier " +
					" FROM employee NATURAL JOIN payroll WHERE employee_id=?");
			
			s.setInt(1, empId);
			
			ResultSet r = null;
			
			try{
				r = s.executeQuery();;
				
				if(r.next()){
					return new Payroll(
							empId,
							r.getDate("date_of_hire"),
							r.getInt("payroll_id"),
							r.getString("title"),
							Pay_Type.valueOf( r.getString("pay_type") ),
							r.getBigDecimal("base_rate"),
							getIntOrNull(r, "seniority_bonus_amount"),
							getFloatOrNull(r, "seniority_bonus_multiplier"),
							getFloatOrNull(r, "overtime_multiplier"));
				}
			}finally{
				if (r != null) r.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return null;
	}
	

	public int getPayroll_id() {
		return payroll_id;
	}

	public void setPayroll_id(int payroll_id) {
		this.payroll_id = payroll_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Pay_Type getPay_type() {
		return pay_type;
	}

	public void setPay_type(Pay_Type pay_type) {
		this.pay_type = pay_type;
	}
	public int getSeniority_bonus_amount() {
		return seniority_bonus_amount;
	}

	public void setSeniority_bonus_amount(int seniority_bonus_amount) {
		this.seniority_bonus_amount = seniority_bonus_amount;
	}

	public float getSeniority_bonus_multiplier() {
		return seniority_bonus_multiplier;
	}

	public void setSeniority_bonus_multiplier(float seniority_bonus_multiplier) {
		this.seniority_bonus_multiplier = seniority_bonus_multiplier;
	}

	public float getOvertime_multiplier() {
		return overtime_multiplier;
	}

	public void setOvertime_multipler(float overtime_multiplier) {
		this.overtime_multiplier = overtime_multiplier;
	}

}