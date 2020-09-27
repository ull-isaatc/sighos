package es.ull.iis.simulation.hta;

import java.util.ArrayList;

/**
 * Chronic complications included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public class ChronicComplication implements Named, Comparable<ChronicComplication> {
	/** An internal counter to assign a different id to each acute complication */
	private static int counter = 0;
	private static ArrayList<ChronicComplication> list = new ArrayList<>();
	/** Short name of the acute complication */
	private final String name;	
	/** Description of the acute complication */
	private final String description;
	/** Internal identifier of the acute complication */
	private final int id;
	
	private ChronicComplication(String name, String description) {
		this.description = description;
		this.name = name;
		this.id = counter++;
		list.add(this);
	}

	/**
	 * Returns the description of the complication
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int compareTo(ChronicComplication o) {
		return (id == o.id) ? 0 : ((id > o.id) ? 1 : -1);
	}

	public int getInternalId() {
		return id;
	}
	
	public static int getNAcuteEvents() {
		return counter;
	}
	
	public static ChronicComplication[] values() {
		return (ChronicComplication[])list.toArray();
	}
	
}