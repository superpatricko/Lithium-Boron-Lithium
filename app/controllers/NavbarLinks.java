package controllers;

import model.SessionAuth.SessionInfo;

public class NavbarLinks {

	private NavbarLinks(){}
	
	public static class Link {
		public final String name;
		public final String href;
		
		public Link(String name, String href) {
			this.name = name;
			this.href = href;
		}
	}
	
	private static Link VIEW_SCHEDULE     = new Link("Schedule", routes.Application.viewSchedule().url());
	private static Link VIEW_SOME_PATIENT = new Link("Patient 1", routes.PatientController.view(1).url());
	private static Link Services =  new Link("Service",routes.AdminController.modifyService().url());
	
	public static Link[] getLinksForSession(SessionInfo s){
		
		if(s == null){
			return new Link[]{};
		}
		
		switch(s.role){
			case Administrator:
				return new Link[]{VIEW_SCHEDULE};
			case Director:
				return new Link[]{VIEW_SCHEDULE};
			case Doctor:
				return new Link[]{VIEW_SOME_PATIENT, VIEW_SCHEDULE};
			case Intern:
				return new Link[]{VIEW_SOME_PATIENT, VIEW_SCHEDULE};
			case Nurse:
				return new Link[]{VIEW_SCHEDULE};
			case NurseShiftSupervisor:
				return new Link[]{VIEW_SCHEDULE};
			case OpenAccessAdmin:
				return new Link[]{VIEW_SCHEDULE};
			case OperatingRoomNurse:
				return new Link[]{VIEW_SCHEDULE};
			case Patient:
				return new Link[]{VIEW_SCHEDULE};
			case PlaymateNurse:
				return new Link[]{VIEW_SCHEDULE};
			case Resident:
				return new Link[]{VIEW_SOME_PATIENT, VIEW_SCHEDULE};
			case SeniorAdministrator:
				return new Link[]{VIEW_SCHEDULE};
			case Technician:
				return new Link[]{VIEW_SCHEDULE};
			default:
				return new Link[]{};
		
		}
	}
	
}
