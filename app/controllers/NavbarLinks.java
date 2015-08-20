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
	private static Link VIEW_ALL_PATIENTS = new Link("Patients", routes.DoctorController.viewPatients().url());
	private static Link VIEW_ALL_SUBORDINATES = new Link("Interns/Residents", routes.DoctorController.viewSubordinates().url());
	private static Link VIEW_SERVICES_MODIFY=  new Link("Service",routes.AdminController.modifyService().url());
	private static Link VIEW_SERVICES_READONLY =  new Link("Service ReadOnly",routes.AdminController.serviceReadOnly().url());
	private static Link VIEW_HUGE_REPORT = new Link("Big Report",routes.AdminController.reports().url());
	private static Link	VIEW_HUMANRESSOURCES = new Link("Human Ressouces", routes.AdminController.humanRessources().url());
	private static Link VIEW_STATS_COST = new Link("Stats Cost",routes.AdminController.statsCost().url());
	
	
	public static Link[] getLinksForSession(SessionInfo s){
		
		if(s == null){
			return new Link[]{};
		}
		
		switch(s.role){
			case Administrator:
				return new Link[]{VIEW_SCHEDULE,VIEW_SERVICES_READONLY,VIEW_HUMANRESSOURCES,VIEW_HUGE_REPORT,VIEW_STATS_COST};
			case Director:
				return new Link[]{VIEW_SCHEDULE,VIEW_SERVICES_MODIFY,VIEW_HUMANRESSOURCES,VIEW_HUGE_REPORT,VIEW_STATS_COST};
			case Doctor:
				return new Link[]{VIEW_ALL_PATIENTS, VIEW_ALL_SUBORDINATES, VIEW_SCHEDULE};
			case Intern:
				return new Link[]{VIEW_ALL_PATIENTS, VIEW_SCHEDULE};
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
				return new Link[]{VIEW_ALL_PATIENTS, VIEW_SCHEDULE};
			case SeniorAdministrator:
				return new Link[]{VIEW_SCHEDULE,VIEW_SERVICES_MODIFY,VIEW_HUMANRESSOURCES};
			case Technician:
				return new Link[]{VIEW_SCHEDULE};
			default:
				return new Link[]{};
		
		}
	}
	
}
