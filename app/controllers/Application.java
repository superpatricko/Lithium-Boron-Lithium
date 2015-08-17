package controllers;

import java.util.List;

import model.Schedule;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.schedule;

public class Application extends Controller {
	
	public static Result index() {
		SessionInfo sess = SessionAuth.instance.getSession(session());

		if (sess != null) {
			return ok(index.render("Your new application is ready."));
		} else {
			return redirect(controllers.routes.LogInOut.login());
		}
	}
	
	public static Result viewSchedule(){		
		SessionInfo sess = SessionAuth.instance.getSession(session());

		if(sess != null){
			List<Schedule> s = Schedule.getSchedule(sess.employeeId);
			
			return ok(schedule.render(s));			
		}else{
			return redirect(controllers.routes.LogInOut.login());
		}
	}


}