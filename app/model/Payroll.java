package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
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
			public final String howMuch;
			
			public PayPeriodInfoLine(String w, String h){
				this.what = w;
				this.howMuch = h;
			}
		};
		
		public final List<PayPeriodInfoLine> payPeriodInfo;
		public String total;
		
		public PayPeriodInfo(){
			payPeriodInfo = new LinkedList<Payroll.PayPeriodInfo.PayPeriodInfoLine>();
		}
		
	};
	
	private int getYearsWorked(){
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
		
		return deltaYear;
	}
	
	private final static NumberFormat percentFormat  = NumberFormat.getPercentInstance();
	private final static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	private final static NumberFormat numberFormat   = NumberFormat.getInstance();
	
	static {
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
	}
	
	private PayPeriodInfo getWagePayAmountForCurrentPeriod(){
		PayPeriodInfo result = new PayPeriodInfo();
		
		BigDecimal baseRate = base_rate == null ? new BigDecimal(0) : base_rate;
		result.payPeriodInfo.add(new PayPeriodInfoLine("Hourly Rate", currencyFormat.format(baseRate)));
				
		if(this.seniority_bonus_amount != null && this.seniority_bonus_multiplier != null){
			
			int yearsWorked = getYearsWorked();
			
			// Calculate how much extra money that actually gives
			BigDecimal seniorityBonus = new BigDecimal(
				(yearsWorked / this.seniority_bonus_amount.intValue()) * this.seniority_bonus_multiplier.floatValue() );
			
			result.payPeriodInfo.add(new PayPeriodInfoLine("Seniority bonus (" + yearsWorked + " years of work)", numberFormat.format(seniorityBonus)));
			
			baseRate = baseRate.add(seniorityBonus);
		}
		
		BigDecimal hoursWorked = PayPeriod.getHoursWorkedInCurrentPeriod(employeeId);
		
		result.payPeriodInfo.add(new PayPeriodInfoLine("Hours worked this period", numberFormat.format(hoursWorked)));
		
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
							currencyFormat.format(regularPay)));
			
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
									currencyFormat.format(overtimePay)));
			
			result.total = currencyFormat.format(regularPay.add(overtimePay));
			
		}else{
			result.payPeriodInfo.add(
					new PayPeriodInfoLine(
							"Regular time pay ("
									+hoursWorked.toPlainString()
									+" @ "
									+baseRate.toPlainString()
									+")", 
							numberFormat.format(hoursWorked.multiply(baseRate))));
			
			result.total = currencyFormat.format(hoursWorked.multiply(baseRate));
		}
		
		return result;
				
	}

	private PayPeriodInfo getSalaryPayAmountForCurrentPeriod() {
		PayPeriodInfo result = new PayPeriodInfo();
		
		BigDecimal baseRate = base_rate == null ? new BigDecimal(0) : base_rate;
		result.payPeriodInfo.add(new PayPeriodInfoLine("Base Salary", currencyFormat.format(baseRate)));
				
		if(this.seniority_bonus_amount != null && this.seniority_bonus_multiplier != null){
			
			int yearsWorked = getYearsWorked();
			
			// Calculate how much extra money that actually gives
			BigDecimal seniorityBonus = 
					new BigDecimal(this.seniority_bonus_multiplier.floatValue()).pow(
				(yearsWorked / this.seniority_bonus_amount.intValue())  );
			
			if(seniorityBonus.compareTo(new BigDecimal(1)) > 0){
				result.payPeriodInfo.add(new PayPeriodInfoLine("Seniority bonus percentage (" + yearsWorked + " years of work)", percentFormat.format(seniorityBonus)));
				baseRate = baseRate.multiply(seniorityBonus);
				result.payPeriodInfo.add(new PayPeriodInfoLine("Adjusted Salaray", currencyFormat.format(baseRate)));
			}
		}
		
		result.total = currencyFormat.format(baseRate.divide(new BigDecimal(52/2), MathContext.DECIMAL64)); // divide by number of periods in a year
		
		return result;
	}

	private PayPeriodInfo getDoctorPayAmountForCurrentPeriod() {
		PayPeriodInfo result = new PayPeriodInfo();
		
		BigDecimal baseRate = base_rate == null ? new BigDecimal(0) : base_rate;
		result.payPeriodInfo.add(new PayPeriodInfoLine("Base Salary", currencyFormat.format(baseRate)));
				
		if(this.seniority_bonus_amount != null && this.seniority_bonus_multiplier != null){
			
			int yearsWorked = getYearsWorked();
			
			// Calculate how much extra money that actually gives
			BigDecimal seniorityBonus = 
					new BigDecimal(this.seniority_bonus_multiplier.floatValue()).pow(
				(yearsWorked / this.seniority_bonus_amount.intValue())  );
			
			if(seniorityBonus.compareTo(new BigDecimal(1)) > 0){
				result.payPeriodInfo.add(new PayPeriodInfoLine("Seniority bonus percentage (" + yearsWorked + " years of work)", percentFormat.format(seniorityBonus)));
				baseRate = baseRate.multiply(seniorityBonus);
				result.payPeriodInfo.add(new PayPeriodInfoLine("Adjusted Salaray", currencyFormat.format(baseRate)));
			}
		}
		
		BigDecimal weekly = baseRate.divide(new BigDecimal(52/2), MathContext.DECIMAL64);
		
		Doctor doc = new Doctor(employeeId);
		List<ServiceRecord> services = doc.getServicesBetween(PayPeriod.getStartOfCurrentWeek(), PayPeriod.getEndOfCurrentWeek());
		
		BigDecimal percent = new BigDecimal(0.5);
		
		for (ServiceRecord sr : services) {
			BigDecimal serviceCost = new BigDecimal(sr.getService().getCost()).multiply(percent);
			weekly = weekly.add( serviceCost );
			result.payPeriodInfo.add(new PayPeriodInfoLine(
					
					sr.getService().getName() + " for patient " + sr.getPatient().getFirstName() + " " + sr.getPatient().getLastName()
					+ " (" + sr.getService().getCost() + " @ " + percentFormat.format(percent) + ")", currencyFormat.format(serviceCost)));
			
		}
		
		
		result.total = currencyFormat.format(weekly); // divide by number of periods in a year
		
		return result;
	}
	
	public PayPeriodInfo getPayAmountForCurrentPeriod(){
		
		switch(pay_type){
		case wage:
			return getWagePayAmountForCurrentPeriod();
		case year:
			return getSalaryPayAmountForCurrentPeriod();
		case doctor:
			return getDoctorPayAmountForCurrentPeriod();
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