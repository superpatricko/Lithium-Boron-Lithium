package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	private Date startTime;
	private Date endTime;

	private Schedule(Date start, Date end){
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
					"SELECT start_time, end_time FROM Schedule WHERE employee_id=? ORDER BY start_time ");

			s.setInt(1, employeeId);

			ResultSet r = null;

			try{
				r = s.executeQuery();


				while(r.next()){
					scheduleList.add(new Schedule(
							r.getTimestamp("start_time"),
							r.getTimestamp("end_time")));
				}

			}finally{
				if (r != null) r.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return scheduleList;
	}


	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
 