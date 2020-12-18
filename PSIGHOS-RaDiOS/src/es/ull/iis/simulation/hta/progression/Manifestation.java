/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.GeneratesSecondOrderInstances;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.MultipleBernoulliParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.BernoulliParam;
import es.ull.iis.simulation.model.Describable;

/**
 * A stage of a {@link ChronicComplication chronic complication} defined in the model. Different chronic complications submodels
 * can define different stages that are registered at the beginning of the simulation. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Manifestation implements Named, Describable, Comparable<Manifestation>, GeneratesSecondOrderInstances, CreatesSecondOrderParameters {
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
		associatedDeath = new MultipleBernoulliParam[secParams.getnRuns() + 1];
		pDiagnose = new MultipleBernoulliParam[secParams.getnRuns() + 1];
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

	/**
	 * Returns true if the acute onset of the manifestation produces the death of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation produces the death of the patient
	 */
	public boolean leadsToDeath(Patient pat) {
		return associatedDeath[pat.getSimulation().getIdentifier()].getValue(pat);
	}
	
	/**
	 * Returns true if the acute onset of the manifestation leads to the diagnosis of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation leads to the diagnosis of the patient
	 */
	public boolean leadsToDiagnose(Patient pat) {
		return pDiagnose[pat.getSimulation().getIdentifier()].getValue(pat);
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

	@Override
	public void generate() {
		final int n = secParams.getnRuns();
		for (int i = 0; i < n + 1; i++) {
			// This works only because Manifestation.generate() (this method) is called after SecondOrderParam.generate() in SecondOrderParamsRepository.generate()... Dangerous!
			pInit[i] = new BernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getInitProbParam(this, i));
			associatedDeath[i] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getDeathProbParam(this, i));
			pDiagnose[i] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), secParams.getDiagnosisProbParam(this, i));
		}			
	}
	
	public void reset(int id) {
		associatedDeath[id].reset();
	}
	
	public boolean hasManifestationAtStart(Patient pat) {
		final BernoulliParam param = pInit[pat.getSimulation().getIdentifier()]; 
		return (param == null) ? false : param.getValue(pat);
	}
}
