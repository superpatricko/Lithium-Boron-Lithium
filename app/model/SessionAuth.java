package model;

import play.mvc.Http.Session;

/**
 * Abstract class which managed Sessions!
 * 
 * @author gordon
 *
 */
public abstract class SessionAuth {
	
	// TODO - more info about the current user (their role, their full name)
	// TODO - support patients logging in too!!
	
	public final static SessionAuth instance = new DatabaseSessionAuth();
	protected SessionAuth(){}
	
	/**
	 * Class that holds the info about the current session
	 * 
	 * @author gordon
	 *
	 */
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
	
	/**
	 * Method to be implemented by subclasses to check if a given username/password pair corresponds
	 * to a valid login.
	 * 
	 * @param username The username 
	 * @param password The password
	 * @return True if the user/pass combination is good, false otherwise
	 */
	protected abstract boolean authenticate(String username, String password);
		
	/**
	 * Method to be implemented by subclasses to get the employee ID for a given username
	 * 
	 * @param username The username to retrieve the employee_id for
	 * @return The employee ID
	 */
	protected abstract int getEmployeeId(String username);
	
	/**
	 * Create a new session if the supplied username/password combination is valid
	 * 
	 * @param username The username
	 * @param password The password
	 * @param session The Play Session object (returned by calling {@link play.mvc.Controller#session() Controller.session() } )
	 * @return A SessionInfo if the login was good, or null otherwise
	 */
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
	
	/**
	 * Get the SessionInfo for the currently logged in user.
	 * 
	 * @param session The Play Session object (returned by calling {@link play.mvc.Controller#session() Controller.session() } )
	 * @return The current user's SessionInfo, or null if there is no logged in user for this request
	 */
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

	/**
	 * Clear any logged in session info
	 * 
	 * @param session The Play Session object (returned by calling {@link play.mvc.Controller#session() Controller.session() } )
	 */
	public void endSession(Session session) {
		session.clear();
	}	
}
