/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * A stage of a {@link T1DMChronicComplications chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMComplicationStage implements Named, Comparable<T1DMComplicationStage> {
	/** Short name of the complication stage */
	private final String name;
	/** Full description of the complication stage */
	private final String description;
	/** Main chronic complication this stage is related to */
	private final T1DMChronicComplications mainComp;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	
	/**
	 * Creates a new complication stage of a {@link T1DMChronicComplications chronic complication} defined in the model
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param mainComp Main chronic complication
	 */
	public T1DMComplicationStage(String name, String description, T1DMChronicComplications mainComp) {
		this.name = name;
		this.description = description;
		this.mainComp = mainComp;
	}
	
	/**
	 * Returns the description of the complication
	 * @return the description of the complication
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@link T1DMChronicComplications} this complication stage is related to.
	 * @return the {@link T1DMChronicComplications} this complication stage is related to
	 */
	public T1DMChronicComplications getComplication() {
		return mainComp;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the order assigned to this stage in a simulation.
	 * @return the order assigned to this stage in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this stage have in a simulation
	 * @param ord order that this stage have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}

	@Override
	public int compareTo(T1DMComplicationStage o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
