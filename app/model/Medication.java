package model;

/**
 * Class representing medications
 * 
 * @author gordon
 *
 */
public class Medication {

	// TODO - actually this represents supplies in general, should be renamed to reflect that
	// TODO - missing almost all attributes
	
	private final int id;
	private final String name;
	
	public Medication(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
