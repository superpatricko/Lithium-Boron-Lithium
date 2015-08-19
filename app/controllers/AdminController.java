package controllers;

import model.Administrator;
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
	
	
	

}
