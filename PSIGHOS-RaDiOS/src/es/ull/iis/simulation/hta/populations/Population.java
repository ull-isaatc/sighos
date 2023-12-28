/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PatientCommonRandomNumbers;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;
import simkit.random.RandomNumber;

/**
 * A class that can create patient profiles belonging to a population description
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Population extends HTAModelComponent {
	/** Random number generator */
	private final RandomNumber rng;

	public Population(HTAModel model, String name, String description) throws MalformedSimulationModelException {
		super(model, name, description);
		rng = PatientCommonRandomNumbers.getRNG();
		if (!model.register(this))
			throw new MalformedSimulationModelException("Population already defined");
			registerUsedParameter(StandardParameter.POPULATION_BASE_UTILITY);
	}

	/**
	 * @return the common random number generator for random variates used within this class
	 */
	public RandomNumber getCommonRandomNumber() {
		return rng;
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
	
	/**
	 * Returns the characterization of the time to death for patients belonging to this population
	 * @return the characterization of the time to death for patients belonging to this population
	 */
	public abstract TimeToEventCalculator getDeathCharacterization();

	/**
	 * Returns the base utility for the specified patient 
	 * @param pat A patient
	 * @return the base utility for the specified patient
	 */
	public double getBaseUtility(Patient pat) {
		return getUsedParameterValue(StandardParameter.POPULATION_BASE_UTILITY, pat);
	}

}
