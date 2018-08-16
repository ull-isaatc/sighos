/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.Intervention;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class T1DMMonitoringIntervention implements Intervention {
	final private String shortName;
	final private String description;
	final private int id;
	/**
	 * 
	 */
	public T1DMMonitoringIntervention(int id, String shortName, String description) {
		this.shortName = shortName;
		this.description = description;
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.Intervention#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.Intervention#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	public abstract double getHBA1cLevel(T1DMPatient pat);
	
}
