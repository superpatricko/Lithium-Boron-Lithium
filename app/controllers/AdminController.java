package controllers;

import model.Administrator;
import model.Doctor;
import model.Nurse;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Result;
import play.mvc.Controller;
import views.html.*;
import play.data.DynamicForm;
import play.i18n.*;
import views.html.ServicesViewOnly;


public class AdminController extends Controller{
	
	 private static Administrator admin = new Administrator(-35);
	 
	 private static Nurse nurse  = new Nurse(21) ;
	 public static Result modifyService(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 return ok(views.html.services.render(s,admin.getAllServices()));		 
	 }
	 public static Result getModified(){
		 return modifyService();
	 }
	 public static Result deleteService(){
		 DynamicForm f= form().bindFromRequest();
		 System.out.println("hello");
		 int idToDelete = Integer.parseInt(f.get("deleteService"));
		 System.out.println(idToDelete);
		 admin.deleteId(idToDelete);
		 return modifyService();
	 }
	 public static Result serviceReadOnly(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 return ok (views.html.ServicesViewOnly.render(s,admin.getAllServices()));
		 
	 }
	 public static Result humanRessources(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		return ok (views.html.HumanRessources.render(s,admin.getAllAdministrators(),
				admin.getAllDoctors(),admin.getAllNurses()));
	 }
	 public static Result reports(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 return ok (views.html.reports.render(s,admin.getAllReports()));
		// return TODO;
	 }
	 public static Result addService(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 //DynamicForm f= form().bindFromRequest();
		 return ok(AddService.render(s));
	 }
	 public static Result insertService(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 DynamicForm f= form().bindFromRequest();
		 int id = Integer.parseInt(f.get("serviceId"));
		 String name = f.get("nameService");
		 float cost = Float.parseFloat(f.get("cost"));
		 int unit = Integer.parseInt(f.get("unit"));
		 int isActive = Integer.parseInt(f.get("isActive"));
		 //insert into service
		 admin.addService(id, name, cost, unit, isActive);
		 return ok(views.html.index.render(s,"new service added"));
	 }
	 public static Result statsCost(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 return ok(views.html.StatsCost.render(s,admin.getTotalCosts(),admin.getCurrentTotalCosts(),admin.getCostsPerMonth(),
				 admin.getTotalSupplyUsing(),admin.getMonthUsage(),admin.getAllDoctorServices(),admin.getAllDoctorSurgeries()));
		 
		 
	 }

	
	

}
