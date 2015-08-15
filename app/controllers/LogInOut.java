package controllers;

import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;


public class LogInOut extends Controller {

	public static Result login() {
		return ok(login.render("Please log in now"));
	}

	public static Result logout() {
		SessionAuth.instance.endSession(session());
		
		return redirect(controllers.routes.LogInOut.login());
	}
	
	public static Result validateLogin() {
		DynamicForm f = form().bindFromRequest();
		
		String user = f.get("user");
		String pass = f.get("pass");
		
		SessionInfo s = SessionAuth.instance.createSession(user, pass, session());
		
		if(s != null){
			return redirect(controllers.routes.Application.index());
		}else{
			return redirect(controllers.routes.LogInOut.login());
		}
	}

	
}
