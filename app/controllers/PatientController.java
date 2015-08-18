package controllers;

import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.patient;

public class PatientController extends Controller {

	public static Result view(int id){
		SessionInfo sess = SessionAuth.instance.getSession(session());

		if(sess != null){

			model.Patient p = model.Patient.get(id);
			if(p != null){
				return ok(patient.render(p));
			}else{
				return notFound("No such patient"); // TODO
			}
		}else{
			return redirect(controllers.routes.LogInOutController.login());
		}
	}
}
