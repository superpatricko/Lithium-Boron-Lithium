package controllers;

import model.Payroll;
import model.Payroll.PayPeriodInfo;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;

import views.html.payroll;

public class PayrollController extends Controller {

	public static Result view(int employeeId){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){
			
			PayPeriodInfo info = Payroll.getPayroll(employeeId).getPayAmountForCurrentPeriod();
			
			return ok(payroll.render(sess, info));
			
		}else{
			return redirect(routes.LogInOutController.login());
		}
	}
}
