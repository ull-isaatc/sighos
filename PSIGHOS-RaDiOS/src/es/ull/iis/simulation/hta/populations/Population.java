/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.DefinesBaseUtility;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.model.Generator.GenerationInfo;
import simkit.random.RandomNumber;

/**
 * A class that can create patient profiles belonging to a population description
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Population extends GenerationInfo implements CreatesSecondOrderParameters, DefinesBaseUtility, NamedAndDescribed {
	/** Random number generator */
	private final RandomNumber rng;
	private final String name;
	private final String description;
	private final SecondOrderParamsRepository secParams;

	public Population(String name, String description, SecondOrderParamsRepository secParams) throws MalformedSimulationModelException {
		super(1.0);
		this.name = name;
		this.description = description;
		this.secParams = secParams;
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @return the common random number generator for random variates used within this class
	 */
	public RandomNumber getCommonRandomNumber() {
		return rng;
	}
	
	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	/**
	 * Returns the minimum age for the patients
	 * @return the minimum age for the patients
	 */
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;		
	}
	/**
	 * Returns the maximum age for the patients
	 * @return the maximum age for the patients
	 */
	public int getMaxAge() {
		return BasicConfigParams.DEF_MAX_AGE;
	}
	
	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @param pat A patient
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public abstract int getSex(Patient pat);

	/**
	 * Returns the initial age (in years) assigned to the patient
	 * @param pat A patient
	 * @return The initial age (in years) assigned to the patient
	 */
	public abstract double getInitAge(Patient pat);

	/**
	 * Returns the disease of the patient or {@link Disease.HEALTHY} in case the patient is healthy
	 * @param pat A patient
	 * @return the disease of the patient or {@link Disease.HEALTHY} in case the patient is healthy
	 */
	public abstract Disease getDisease(Patient pat);

	/**
	 * Returns true if the patient is diagnosed from the start 
	 * @param pat A patient
	 * @return true if the patient is diagnosed from the start
	 */
	public abstract boolean isDiagnosedFromStart(Patient pat);
}
