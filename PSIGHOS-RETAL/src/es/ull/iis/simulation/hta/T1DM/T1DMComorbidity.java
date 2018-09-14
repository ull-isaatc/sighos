/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * A specific complication belonging to one of the {@link MainChronicComplications} defined in the model. Different complication submodels
 * can define different specific complications that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMComorbidity implements Named, Comparable<T1DMComorbidity> {
	/** Short name of the complication */
	private final String name;
	/** Full description of the complication */
	private final String description;
	/** Main chronic complication this complication is related to */
	private final MainChronicComplications mainComp;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	
	public T1DMComorbidity(String name, String description, MainChronicComplications mainComp) {
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
	 * Returns the {@link MainChronicComplications} this specific complication is related to.
	 * @return the {@link MainChronicComplications} this specific complication is related to
	 */
	public MainChronicComplications getComplication() {
		return mainComp;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the order assigned to this complication in a simulation.
	 * @return the order assigned to this complication in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this complication have in a simulation
	 * @param ord order that this complication have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}

	@Override
	public int compareTo(T1DMComorbidity o) {
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
