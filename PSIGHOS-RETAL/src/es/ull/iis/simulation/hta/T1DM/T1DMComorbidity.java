/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * A chronic comorbidity for a patient. 
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMComorbidity implements Named, Comparable<T1DMComorbidity> {
	private final String name;
	private final String description;
	private final MainChronicComplications mainComp;
	private int ord = -1;
	
	public T1DMComorbidity(String name, String description, MainChronicComplications mainComp) {
		this.name = name;
		this.description = description;
		this.mainComp = mainComp;
	}
	
	public String getDescription() {
		return description;
	}
	public MainChronicComplications getComplication() {
		return mainComp;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	public int ordinal() {
		return ord;
	}
	
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
