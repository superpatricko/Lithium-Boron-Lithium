package controllers;

import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
	
	public static Result index() {
		SessionInfo sess = SessionAuth.instance.getSession(session());

		if (sess != null) {
			return ok(index.render("Your new application is ready."));
		} else {
			return redirect(controllers.routes.LogInOut.login());
		}
	}

}