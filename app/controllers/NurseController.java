package controllers;

import model.Nurse;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import model.SessionAuth.SessionInfo.Role;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.nurse;

public class NurseController extends Controller {

	public static Result view(int id){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){

			switch(sess.role){
			case Administrator:
			case Director:
			case Doctor:
			case Intern:
			case NurseShiftSupervisor:
			case OpenAccessAdmin:
			case OperatingRoomNurse:
			case Resident:
			case SeniorAdministrator:
				Nurse n = Nurse.getNurse(id);
				if(n != null){
					return ok(nurse.render(sess, n));					
				}else{
					return notFound("404 - Nurse not found");
				}
				
			case Patient:
			case Nurse:
			case PlaymateNurse:
			case Technician:
			default:
				return redirect(routes.Application.index());
			}
			
			
		}else{
			return redirect(routes.LogInOutController.login());
		}
		
	}

}
