/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.util.ArrayList;

/**
 * Acute complication included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public class AcuteComplication implements Named, Comparable<AcuteComplication> {
	/** An internal counter to assign a different id to each acute complication */
	private static int counter = 0;
	private static ArrayList<AcuteComplication> list = new ArrayList<>();
	/** Short name of the acute complication */
	private final String name;	
	/** Description of the acute complication */
	private final String description;
	/** Internal identifier of the acute complication */
	private final int id;
	
	/**
	 * 
	 * @param name
	 * @param description
	 */
	private AcuteComplication(String name, String description) {
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
	public int compareTo(AcuteComplication o) {
		return (id == o.id) ? 0 : ((id > o.id) ? 1 : -1);
	}

	public int getInternalId() {
		return id;
	}
	
	public static int getNAcuteEvents() {
		return counter;
	}
	
	public static AcuteComplication[] values() {
		return (AcuteComplication[])list.toArray();
	}
}
