package model;

import play.mvc.Http.Session;

/**
 * Abstract class which managed Sessions!
 * 
 * @author gordon
 *
 */
public abstract class SessionAuth {
	
	public final static SessionAuth instance = new DatabaseSessionAuth();
	protected SessionAuth(){}
	
	/**
	 * Class that holds the info about the current session
	 * 
	 * @author gordon
	 *
	 */
	public static class SessionInfo {
		public static enum Role {
			Patient,
			Doctor,
			Resident,
			Intern,
			Nurse,
			NurseShiftSupervisor,
			Technician,
			Director,
			Administrator, OperatingRoomNurse, PlaymateNurse, SeniorAdministrator, OpenAccessAdmin,
		};
		
		public SessionInfo(Role role, String username, int id, String firstName, String familyName) {
			this.role = role;
			this.username = username;
			this.id = id;
			this.firstName = firstName;
			this.familyName = familyName;
		}
		
		public final String firstName;
		public final String familyName;
		public final Role role;
		public final String username;
		public final int id;
	}
	
	private static String USERNAME_KEY    = "u";
	private static String EMP_ID_KEY      = "e";
	private static String ROLE_KEY        = "r";
	private static String FIRST_NAME_KEY  = "f";
	private static String FAMILY_NAME_KEY = "F";
	
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
	protected abstract SessionInfo getUserInfo(String username);
	
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
			SessionInfo sess = getUserInfo(username);
			
			session.put(USERNAME_KEY, sess.username);
			session.put(ROLE_KEY, sess.role.name());
			session.put(EMP_ID_KEY, Integer.toString(sess.id));
			session.put(FIRST_NAME_KEY, sess.firstName);
			session.put(FAMILY_NAME_KEY, sess.familyName);
						
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
		String role       = session.get(ROLE_KEY);
		String username   = session.get(USERNAME_KEY);
		String id         = session.get(EMP_ID_KEY);
		String fname      = session.get(FIRST_NAME_KEY);
		String lname      = session.get(FAMILY_NAME_KEY);
		
		
		if(username != null && id != null){
			try{
				return new SessionInfo(
						SessionInfo.Role.valueOf(role),
						username,
						Integer.parseInt(id),
						fname,
						lname);
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
