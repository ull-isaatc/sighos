/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StartWithComplicationParam;

/**
 * A stage of a {@link ChronicComplication chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Manifestation implements Named, Comparable<Manifestation> {
	public enum Type {
		ACUTE,
		CHRONIC
	}
	/** Short name of the complication stage */
	private final String name;
	/** Full description of the complication stage */
	private final String description;
	/** Disease this manifestation is related to */
	private final Disease disease;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	private final Type type;
	
	/** Probability that a patient starts in this stage */
	private final ArrayList<StartWithComplicationParam> pInit;
	
	/**
	 * Creates a new complication stage of a {@link ChronicComplication chronic complication} defined in the model
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param disease Main chronic complication
	 */
	public Manifestation(String name, String description, Disease disease, Type type) {
		this.name = name;
		this.description = description;
		this.disease = disease;
		this.type = type;
		pInit = new ArrayList<>();
	}
	
	/**
	 * Returns the description of the complication
	 * @return the description of the complication
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@link Disease} this manifestation is related to.
	 * @return the {@link Disease} this manifestation is related to
	 */
	public Disease getDisease() {
		return disease;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
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
	public int compareTo(Manifestation o) {
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

	public void newInstance(final double du, final double[] cost, final StartWithComplicationParam pInit, final double imr) {
		this.du.add(du);
		this.cost.add(cost);
		this.pInit.add(pInit);
		this.imr.add(imr);
	}

	public void newInstance(final double du, final double[] cost, double pInit, final double imr, final int nPatients) {
		final StartWithComplicationParam param = (pInit > 0.0) ? new StartWithComplicationParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, pInit) : null;
		newInstance(du, cost, param, imr);
	}

	public double getDisutility(Patient pat) {
		return du.get(pat.getSimulation().getIdentifier());
	}
	
	public double[] getCosts(Patient pat) {
		return cost.get(pat.getSimulation().getIdentifier());
	}
	
	public boolean hasComplicationAtStart(Patient pat) {
		final StartWithComplicationParam param = pInit.get(pat.getSimulation().getIdentifier()); 
		return (param == null) ? false : param.getValue(pat);
	}
	
	public double getIMR(Patient pat) {
		return imr.get(pat.getSimulation().getIdentifier());
	}
	
	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);
}
