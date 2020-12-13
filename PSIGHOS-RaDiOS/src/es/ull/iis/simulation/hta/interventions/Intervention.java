/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.GenerateSecondOrderInstances;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * The second order characterization of an intervention. The {@link #registerSecondOrderParameters()} method 
 * must be invoked from the {@link SecondOrderParamsRepository} to register the second order parameters. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Intervention implements Describable, GenerateSecondOrderInstances, CreatesSecondOrderParameters, Comparable<Intervention> {
	/** A short name for the intervention */
	final private String shortName;
	/** A full description of the intervention */
	final private String description;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * interventions defined to be used within a simulation */ 
	private int ord = -1;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;

	/**
	 * Creates a second order characterization of an intervention
	 * @param shortName Short name
	 * @param description Full description
	 */
	public Intervention(final SecondOrderParamsRepository secParams, final String shortName, final String description) {
		this.secParams = secParams;
		this.shortName = shortName;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a short name for the intervention 
	 * @return A short name for the intervention 
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Returns the HbA1c level of a patient at a specific timestamp
	 * @param pat A patient
	 * @return the HbA1c level of a patient at a specific timestamp
	 */
	public abstract double getHBA1cLevel(Patient pat);
	
	/**
	 * Returns the annual cost of this intervention
	 * @param pat A patient
	 * @return the annual cost of this intervention
	 */
	public abstract double getAnnualCost(Patient pat);

	/**
	 * Returns a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
	 * @param pat A patient
	 * @return a disutility value inherent to the intervention. A negative value represents an intervention that improves the utility
	 */
	public double getDisutility(Patient pat) {
		return 0.0;
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
	public int compareTo(Intervention o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
}
