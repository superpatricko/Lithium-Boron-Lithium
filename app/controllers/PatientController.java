package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.Doctor;
import model.Nurse;
import model.Patient;
import model.Service;
import model.ServiceRecord;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.patient;

public class PatientController extends Controller {

	public static Result view(int id){
		SessionInfo sess = SessionAuth.instance.getSession(session());

		if(sess != null){

			model.Patient p = model.Patient.get(id);
			if(p != null){
				return ok(patient.render(sess, p));
			}else{
				return notFound("No such patient"); // TODO
			}
		}else{
			return redirect(controllers.routes.LogInOutController.login());
		}
	}
	
	public static Result scheduleProcedure(int id){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		// TODO - add it to the doctor's schedule
		
		if(sess != null){

			try {
				DynamicForm form = form().bindFromRequest();
				
				// It took way to long to make this date format match the JS date format... :P
				DateFormat format = new SimpleDateFormat("dd/MM/yyyy, h:mma");
				
				Date start = format.parse(form.get("startTime"));
				Date end   = format.parse(form.get("endTime"));
				
				Integer doctorId;
				Integer nurseId;
				
				// Try to parse the doctor and nurse IDs, if No [Nurse|Doctor] was selected, then they will not
				// be a parsable value so we set it to null.
				try{ doctorId = Integer.parseInt(form.get("doctor")); }catch(NumberFormatException e){ doctorId = null; }
				try{ nurseId = Integer.parseInt(form.get("nurse"));   }catch(NumberFormatException e){ nurseId = null; }
				
				ServiceRecord sr = new ServiceRecord(
						start,
						end,
						new Service(Integer.parseInt(form.get("service"))),
						doctorId == null ? null : new Doctor(doctorId),
						nurseId  == null ? null : new Nurse(nurseId));
				
				sr.setPatient(new Patient(id));
				
				ServiceRecord.Status status = sr.writeToDb();
				
				if(status == ServiceRecord.Status.SUCCESS){
					return redirect(controllers.routes.PatientController.view(id));
				}else{
					return internalServerError(status.name());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Results.internalServerError(e.toString());
			}
			
		}else{
			return redirect(controllers.routes.LogInOutController.login());
		}
	}
}
