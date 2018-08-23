/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 *
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMHealthState implements Named, Comparable<T1DMHealthState> {
	private final String name;
	private final String description;
	private final MainComplications mainComp;
	private int ord = -1;
	
	public T1DMHealthState(String name, String description, MainComplications mainComp) {
		this.name = name;
		this.description = description;
		this.mainComp = mainComp;
	}
	
	public String getDescription() {
		return description;
	}
	public MainComplications getComplication() {
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
	public int compareTo(T1DMHealthState o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
}
