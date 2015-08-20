package controllers;

import model.Payroll;
import model.Payroll.PayPeriodInfo;
import model.SessionAuth;
import model.SessionAuth.SessionInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.payroll;

public class PayrollController extends Controller {

	private static Result render(int employeeId, SessionInfo sess){
		PayPeriodInfo info = Payroll.getPayroll(employeeId).getPayAmountForCurrentPeriod();
		
		return ok(payroll.render(sess, info));
	}
	
	public static Result view(int employeeId){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){
			return render(employeeId, sess);
			
		}else{
			return redirect(routes.LogInOutController.login());
		}
	}
	
	public static Result viewSelf(){
		SessionInfo sess = SessionAuth.instance.getSession(session());
		
		if(sess != null){
			
			return render(sess.id, sess);
			
		}else{
			return redirect(routes.LogInOutController.login());
		}
	}

}
