package model;

public class Medication {

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
