package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing an item in an employee's schedule (a shift basically)u
 * 
 * @author gordon
 *
 */
public class Schedule {

	private Date date;
	private Time startTime;
	private Time endTime;

	private Schedule(Date date, Time start, Time end){
		this.date = date;
		this.startTime = start;
		this.endTime = end;
	}

	/**
	 * Get all scheduled work times for the given employee
	 * 
	 * @param employeeId The employee ID to get the schedule for
	 * @return A list of Schedule objects, or an empty list if there was an error, never null
	 */
	public static List<Schedule> getSchedule(int employeeId){		
		List<Schedule> scheduleList = new LinkedList<Schedule>();

		try {
			PreparedStatement s = DatabaseManager.instance.createPreparedStatement(
					"SELECT date, start_time, end_time FROM Schedule WHERE employee_id=? ORDER BY date, start_time");

			s.setInt(1, employeeId);

			ResultSet r = null;

			try{
				r = s.executeQuery();


				while(r.next()){
					scheduleList.add(new Schedule(
							r.getDate("date"),
							r.getTime("start_time"), 
							r.getTime("end_time")));
				}

			}finally{
				if (r != null) r.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return scheduleList;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}
}
 