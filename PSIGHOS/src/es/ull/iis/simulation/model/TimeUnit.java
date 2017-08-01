/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * Different time units that can be used in a simulation.
 * @author Iván Castilla Rodríguez
 *
 */
public enum TimeUnit {
	MILLISECOND("Milliseconds"), 
	SECOND("Seconds"), 
	MINUTE("Minutes"), 
	HOUR("Hours"), 
	DAY("Days"), 
	WEEK("Weeks"), 
	MONTH("Months"), 
	YEAR("Years");
	
	private final static double[][] conversion = {
		{1, 0.001, 1/60000.0, 1/3600000.0, 1/86400000.0, 1/608400000.0, 1/2592000000.0, 1/31536000000.0},
		{1000, 1, 1/60.0, 1/3600.0, 1/8640.0, 1/60840.0, 1/259200.0, 1/3153600.0},
		{60000, 60, 1, 1/60.0, 1/1440.0, 1/10080.0, 1/43200.0, 1/525600.0},
		{3600000, 3600, 60, 1, 1/24.0, 1/168.0, 1/720.0, 1/8760.0},
		{86400000, 86400, 1440, 24, 1, 1/7.0, 1/30.0, 1/365.0},
		{608400000, 608400, 10080, 168, 7, 1, 0.25, 1/52.0},
		{2592000000.0, 2592000, 43200, 720, 30, 4, 1, 1/12.0},
		{31536000000.0, 31536000, 525600, 8760, 365, 52, 12, 1}
	};
	
	/** The name of the unit */
	private String name;
	/**
	 * Creates a time unit
	 * @param name Name of the time unit
	 */
	private TimeUnit(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the time unit
	 * @return the name of the time unit
	 */
	public String getName() { 
		return name; 
	}

	/**
	 * Converts from one time unit to this one.
	 * @param sourceValue Source timestamp
	 * @param sourceUnit Source time unit
	 * @return The value of the source when expressed as this time unit.
	 */
	public long convert(double sourceValue, TimeUnit sourceUnit) {
		return Math.round(sourceValue * conversion[sourceUnit.ordinal()][ordinal()]);
	}
	
	/**
	 * Converts from one time unit to this one.
	 * @param sourceValue Source timestamp
	 * @return The value of the source when expressed as this time unit.
	 */
	public long convert(TimeStamp sourceValue) {
		return Math.round(sourceValue.getValue() * conversion[sourceValue.getUnit().ordinal()][ordinal()]);
	}

}
