/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Modification;
import es.ull.iis.simulation.model.Describable;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * The second order characterization of an intervention. The {@link #registerSecondOrderParameters()} method 
 * must be invoked from the {@link SecondOrderParamsRepository} to register the second order parameters. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Intervention implements Named, Describable, CreatesSecondOrderParameters, Comparable<Intervention> {
	/** A short name for the intervention */
	final private String name;
	/** A full description of the intervention */
	final private String description;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * interventions defined to be used within a simulation */ 
	private int ord = -1;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;

	private Modification lifeExpectancyModification;
	private Modification allParameterModification;
	
	/**
	 * Creates a second order characterization of an intervention
	 * @param name Short name
	 * @param description Full description
	 */
	public Intervention(final SecondOrderParamsRepository secParams, final String name, final String description) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		lifeExpectancyModification = secParams.NO_MODIF;
		allParameterModification = secParams.NO_MODIF;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a short name for the intervention 
	 * @return A short name for the intervention 
	 */
	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the annual cost of this intervention
	 * @param pat A patient
	 * @return the annual cost of this intervention
	 */
	public abstract double getAnnualCost(Patient pat);

	/**
	 * Returns the initial cost of this intervention
	 * @param pat A patient
	 * @return the initial cost of this intervention
	 */
	public abstract double getStartingCost(Patient pat);
	
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
	public Intervention setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
		return this;
	}

	@Override
	public int compareTo(Intervention o) {
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

	/**
	 * @return the lifeExpectancyModification
	 */
	public Modification getLifeExpectancyModification() {
		return lifeExpectancyModification;
	}

	/**
	 * @param lifeExpectancyModification the lifeExpectancyModification to set
	 */
	public Intervention setLifeExpectancyModification(Modification lifeExpectancyModification) {
		this.lifeExpectancyModification = lifeExpectancyModification;
		return this;
	}

	/**
	 * @return the allParameterModification
	 */
	public Modification getAllParameterModification() {
		return allParameterModification;
	}

	/**
	 * @param allParameterModification the allParameterModification to set
	 */
	public Intervention setAllParameterModification(Modification allParameterModification) {
		this.allParameterModification = allParameterModification;
		return this;
	}

	/**
	 * Returns a collection of events that happens to patients that are treated with this intervention
	 * @param pat A patient
	 * @return A collection of events that happens to patients that are treated with this intervention
	 */
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		return new ArrayList<>();
	}
}
