/**
 * 
 */
package es.ull.isaatc.simulation.results;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementStatistics implements StatisticData {
	private static int counter = 0;
	public final static int START = counter++;
	public final static int FINISH = counter++;
	public final static int REQACT = counter++;
	public final static int STAACT = counter++;
	public final static int ENDACT = counter++;
	protected int elemId;
	protected int type;
	protected double ts;
	protected int value;
	
	/**
	 * @param elemId
	 * @param type
	 * @param ts
	 * @param value
	 */
	public ElementStatistics(int elemId, int type, double ts, int value) {
		this.elemId = elemId;
		this.type = type;
		this.ts = ts;
		this.value = value;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return Returns the elemId.
	 */
	public int getElemId() {
		return elemId;
	}

	/**
	 * @return Returns the ts.
	 */
	public double getTs() {
		return ts;
	}

	/**
	 * @return Returns the value.
	 */
	public int getValue() {
		return value;
	}	

}
