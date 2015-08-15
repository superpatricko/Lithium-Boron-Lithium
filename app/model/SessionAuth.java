package model;

import play.mvc.Http.Session;

public abstract class SessionAuth {
	
	public final static SessionAuth instance = new HardCodedSessionAuth();
	
	public static class SessionInfo {
		public SessionInfo(String username, int employeeId) {
			this.username = username;
			this.employeeId = employeeId;
		}
		
		public final String username;
		public final int employeeId;
	}
	
	private static String USERNAME_KEY = "u";
	private static String EMP_ID_KEY   = "e";	
	
	protected abstract boolean authenticate(String username, String password);
		
	protected abstract int getEmployeeId(String username);
	
	public SessionInfo createSession(String username, String password, Session session) {
		if(authenticate(username, password)){
			
			// Get the employeeId
			int empId = getEmployeeId(username);
			
			session.put(USERNAME_KEY, username);
			session.put(EMP_ID_KEY, Integer.toString(empId));
			
			SessionInfo sess =  new SessionInfo(username, empId);
			
			return sess;
			
		}else{
			return null;
		}		
	}
	
	public SessionInfo getSession(Session session) {
		String username   = session.get(USERNAME_KEY);
		String employeeId = session.get(EMP_ID_KEY);
		
		if(username != null && employeeId != null){
			try{
				return new SessionInfo(username, Integer.valueOf(employeeId));
			}catch(NumberFormatException e){
				;
			}
		}
		
		return null;
	}

	public void endSession(Session session) {
		session.clear();
	}	
}
