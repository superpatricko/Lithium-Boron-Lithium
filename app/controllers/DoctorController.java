package controllers;

import java.util.List;

import model.Doctor;
import model.Patient;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.doctorPatients;
import views.html.doctorInterns;

public class DoctorController extends Controller {

	public static Result viewPatients(){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){
			if(sess.role == SessionInfo.Role.Doctor){
				Doctor doc = new Doctor(sess.id);
				
				
				List<Patient> patients = doc.getAllPatients();

				return ok(doctorPatients.render(sess, patients));
						
			}else{
				return redirect(routes.Application.index());
			}
		}else{
			return redirect(routes.LogInOutController.login());
		}
	}
	
	public static Result viewSubordinates(){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){
			if(sess.role == SessionInfo.Role.Doctor){

				Doctor doc = new Doctor(sess.id);
				
				List<Doctor> subordinates = doc.getAllInternsResidents();

				return ok(doctorInterns.render(sess, subordinates));
				
			}else{
				return redirect(routes.Application.index());
			}
		}else{
			return redirect(routes.LogInOutController.login());
		}
	}
	
}
