package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Schedule {

	public final Date date;
	public final Time startTime;
	public final Time endTime;

	private Schedule(Date date, Time start, Time end){
		this.date = date;
		this.startTime = start;
		this.endTime = end;
	}

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
}
 