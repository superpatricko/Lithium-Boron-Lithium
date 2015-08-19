package model;

public class Payroll {

	public static enum Pay_Type { wage, year };

	private int payroll_id;
	private String title;
	private Pay_Type pay_type;
	private double base_rate;
	private int seniority_bonus_amount;
	private float seniority_bonus_multiplier;
	private float overtime_multiplier;

	public Payroll(int payroll_id, String title, Pay_Type pay_type, double base_rate, 
		int seniority_bonus_amount, float seniority_bonus_multiplier, float overtime_multiplier) {
		this.payroll_id = payroll_id;
		this.title = title;
		this.pay_type = pay_type;
		this.base_rate = base_rate;
		this.seniority_bonus_amount = seniority_bonus_amount;
		this.seniority_bonus_multiplier = seniority_bonus_multiplier;
		this.overtime_multiplier = overtime_multiplier;
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

	public double base_rate() {
		return base_rate;
	}

	public void setBase_rate(double base_rate) {
		this.base_rate = base_rate;
	}

	public int seniority_bonus_amount() {
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