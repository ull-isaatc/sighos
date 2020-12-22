/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Arrays;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BernoulliParam;
import es.ull.iis.simulation.hta.params.MultipleBernoulliParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * A stage of a {@link ChronicComplication chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Manifestation implements Named, Describable, Comparable<Manifestation>, CreatesSecondOrderParameters {
	public enum Type {
		ACUTE,
		CHRONIC
	}
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
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
	private final BernoulliParam[] pInit;
	/** Death associated to the acute events */
	private final MultipleBernoulliParam[] associatedDeath;
	/** Probability that this manifestation leads to diagnose the patient in case he/she is not already diagnosed */
	private final MultipleBernoulliParam[] pDiagnose;
	
	/**
	 * Creates a new complication stage of a {@link ChronicComplication chronic complication} defined in the model
	 * @param secParams Common parameters repository
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param disease Main chronic complication
	 * @param type The {@link Type} of the manifestation
	 */
	public Manifestation(SecondOrderParamsRepository secParams, String name, String description, Disease disease, Type type) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.disease = disease;
		this.type = type;
		pInit = new BernoulliParam[secParams.getnRuns() + 1];
		Arrays.fill(pInit, null);
		associatedDeath = new MultipleBernoulliParam[secParams.getnRuns() + 1];
		Arrays.fill(associatedDeath, null);
		pDiagnose = new MultipleBernoulliParam[secParams.getnRuns() + 1];
		Arrays.fill(pDiagnose, null);
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

	public void reset(int id) {
		if (associatedDeath[id] != null)
			associatedDeath[id].reset();
	}
	
	public boolean hasManifestationAtStart(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (pInit[id] == null)
			pInit[id] = new BernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getInitProbParam(this, id));
		return pInit[id].getValue(pat);
	}

	/**
	 * Returns true if the acute onset of the manifestation produces the death of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation produces the death of the patient
	 */
	public boolean leadsToDeath(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (associatedDeath[id] == null)
			associatedDeath[id] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getDeathProbParam(this, id));
		return associatedDeath[id].getValue(pat);
	}
	
	/**
	 * Returns true if the acute onset of the manifestation leads to the diagnosis of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation leads to the diagnosis of the patient
	 */
	public boolean leadsToDiagnose(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (pDiagnose[id] == null)
			pDiagnose[id] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getDiagnosisProbParam(this, id));
		return pDiagnose[id].getValue(pat);
	}
}
