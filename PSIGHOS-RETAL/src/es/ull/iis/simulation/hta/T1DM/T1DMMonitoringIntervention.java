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
	/** The duration (in years) of the effect of the intervention */
	final private double yearsOfEffect;
	/** A short name for the intervention */
	final private String shortName;
	/** A full description of the intervention */
	final private String description;
	/** A unique identifier of the intervention */
	final private int id;

	/**
	 * Creates a new intervention for T1DM
	 * @param id Unique identifier 
	 * @param shortName Short name
	 * @param description Full description
	 * @param yearsOfEffect duration (in years) of the effect of the intervention
	 */
	public T1DMMonitoringIntervention(int id, String shortName, String description, double yearsOfEffect) {
		this.yearsOfEffect = yearsOfEffect;
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
	
	/**
	 * Returns the duration (in years) of the effect of the intervention
	 * @return the duration (in years) of the effect of the intervention
	 */
	public double getYearsOfEffect() {
		return yearsOfEffect;
	}
	
	/**
	 * Returns the HbA1c level of a patient at a specific timestamp
	 * @param pat A patient
	 * @return the HbA1c level of a patient at a specific timestamp
	 */
	public abstract double getHBA1cLevel(T1DMPatient pat);
	
	/**
	 * Returns the annual cost of this intervention
	 * @param pat A patient
	 * @return the annual cost of this intervention
	 */
	public abstract double getAnnualCost(T1DMPatient pat);

	
}
