package model;

import java.util.HashMap;
import java.util.Map;

public class HardCodedSessionAuth extends SessionAuth {
	
	private final static Map<String, String> passwordDb = new HashMap<String, String>();
	private final static Map<String, Integer> empIdDb = new HashMap<String, Integer>();
	
	static {
		passwordDb.put("Doctor", "passdoc");
		passwordDb.put("Nurse", "passnursed");
	}
	
	static {
		empIdDb.put("Doctor", 12358);
		empIdDb.put("Nurse", 353);
	}
	

	@Override
	protected boolean authenticate(String username, String password){
		return 
				password != null
				&& password.equals(passwordDb.get(username));
	}


	@Override
	protected SessionInfo getUserInfo(String username) {
		return new SessionInfo(
				SessionInfo.Role.Doctor,
				username,
				empIdDb.get(username),
				"Hardcoded",
				"User");
	}
		
}
