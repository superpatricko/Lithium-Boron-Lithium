package controllers;

import model.Administrator;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Result;
import play.mvc.Controller;
import views.html.*;
import play.data.DynamicForm;
import play.i18n.*;

public class AdminController extends Controller{
	
	 private static Administrator admin = new Administrator(-35);
	 public static Result modifyService(){
		 SessionInfo s =SessionAuth.instance.getSession(session());
		 return ok(views.html.services.render(admin.getAllServices()));		 
	 }
	 public static Result getModified(){
		 return modifyService();
	 }
	 public static Result deleteService(){
		 DynamicForm f= form().bindFromRequest();
		 int idToDelete = Integer.parseInt(f.get("deleteService"));
		 System.out.println(idToDelete);
		 admin.deleteId(idToDelete);
		 return modifyService();
	 }
	
	
	

}