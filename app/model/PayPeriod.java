package model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class PayPeriod {

	private PayPeriod() {
	}

	public static Calendar getStartOfCurrentPeriod() {
		Calendar c = Calendar.getInstance();

		int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);

		if (weekOfYear % 2 == 1) {
			c.set(Calendar.WEEK_OF_YEAR, weekOfYear - 1);
		}

		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c;
	}

	public static Calendar getEndOfCurrentPeriod() {
		Calendar c = getStartOfCurrentPeriod();

		int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
		c.set(Calendar.WEEK_OF_YEAR, weekOfYear + 2);

		return c;
	}

	public static BigDecimal getHoursWorkedInCurrentPeriod(int employeeId) {
		try {

			PreparedStatement s = DatabaseManager.instance
					.createPreparedStatement("SELECT "
							+ " SUM(TIMESTAMPDIFF(MINUTE,start_time,end_time))/60.0 "
							+ " AS hours_worked "
							+ " FROM schedule WHERE "
							+ " employee_id=? "
							+ " AND "
							+ " end_time BETWEEN ? AND ? ");

			s.setInt(1, employeeId);
			s.setTimestamp(2, new Timestamp(getStartOfCurrentPeriod()
					.getTimeInMillis()));
			s.setTimestamp(3, new Timestamp(getEndOfCurrentPeriod()
					.getTimeInMillis()));

			ResultSet r = null;

			//System.out.println("Execing " + s.toString());
			
			try {
				r = s.executeQuery();

				if (r.next()) {
					
					//System.out.println("Returning hours_worked: " + r.getFloat(1) + " " + r.getFloat("hours_worked") + " " + r.getInt("hours_worked"));
					
					return r.getBigDecimal("hours_worked");
				}
			} finally {
				if (r != null) r.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new BigDecimal(0);
	}

}
