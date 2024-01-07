/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PatientCommonRandomNumbers;
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
    /** Identifier code for women */
    public final static int WOMAN = 1;
    /** Identifier code for men */
    public final static int MAN = 0;
    /** Maximum age reachable by patients */
    public final static int DEF_MAX_AGE = 100;
    /** Default minimum age of patients */
    public final static int DEF_MIN_AGE = 18;
	/** Random number generator */
	private final RandomNumber rng;
	/** Minimum age of the patients of this population */
	private int minAge;
	/** Maximum age of the patients of this population */
	private int maxAge;
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	public static double DEF_U_GENERAL_POP = 0.911400915;

	/**
	 * Creates a population
	 * @param model The model this population belongs to
	 * @param name The name of the population
	 * @param description A description of the population
	 * @throws MalformedSimulationModelException if the population is already defined in the model
	 */
	public Population(HTAModel model, String name, String description) throws MalformedSimulationModelException {
		super(model, name, description);
		this.rng = PatientCommonRandomNumbers.getRNG();
		this.minAge = Population.DEF_MIN_AGE;
		this.maxAge = Population.DEF_MAX_AGE;
		if (!model.register(this))
			throw new MalformedSimulationModelException("Population already defined");
		registerUsedParameter(StandardParameter.POPULATION_BASE_UTILITY);
		registerUsedParameter(StandardParameter.PREVALENCE);
		registerUsedParameter(StandardParameter.BIRTH_PREVALENCE);
		registerUsedParameter(StandardParameter.INCIDENCE);
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
		return minAge;		
	}

	/**
	 * Sets the minimum age for the patients
	 * @param minAge the minimum age for the patients
	 */
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	/**
	 * Returns the maximum age for the patients
	 * @return the maximum age for the patients
	 */
	public int getMaxAge() {
		return maxAge;
	}
	
	/**
	 * Sets the maximum age for the patients
	 * @param maxAge the maximum age for the patients
	 */
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
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
