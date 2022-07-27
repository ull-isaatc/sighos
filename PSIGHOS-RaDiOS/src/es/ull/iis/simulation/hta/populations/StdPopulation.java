/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients who can either suffer or not a specific disease.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class StdPopulation implements Population {
	/** Random number generator */
	private final RandomNumber rng;
	private final String name;
	private final String description;
	protected final Disease disease;
	protected final SecondOrderParamsRepository secParams;
	private final List<ClinicalParameter> parameters;
	private final RandomVariate[] rndBaselineAge;
	private final DiscreteRandomVariate[] rndSex;
	private final DiscreteRandomVariate[] rndDisease;
	private final DiscreteRandomVariate[] rndDiagnosed;
	
	/**
	 * Creates a standard population
	 */
	public StdPopulation(SecondOrderParamsRepository secParams, String name, String description, Disease disease) {
		this.disease = disease;
		this.name = name;
		this.description = description;
		this.secParams = secParams;
		this.parameters = getPatientParameterList();
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
		rndBaselineAge = new RandomVariate[secParams.getNRuns() + 1];
		rndSex = new DiscreteRandomVariate[secParams.getNRuns() + 1];
		rndDiagnosed = new DiscreteRandomVariate[secParams.getNRuns() + 1];
		rndDisease = new DiscreteRandomVariate[secParams.getNRuns() + 1];
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public PatientProfile getPatientProfile(DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		if (rndSex[id] == null)
			rndSex[id] = getSexVariate(simul);
		if (rndBaselineAge[id] == null)
			rndBaselineAge[id] = getBaselineAgeVariate(simul);
		if (rndDisease[id] == null)
			rndDisease[id] = getDiseaseVariate(simul);
		if (rndDiagnosed[id] == null)
			rndDiagnosed[id] = getDiagnosedVariate(simul);
		final int sex = rndSex[id].generateInt();
		final double initAge = Math.min(Math.max(rndBaselineAge[id].generate(), getMinAge()), getMaxAge());
		final Disease dis = (rndDisease[id].generateInt() == 1) ? disease : secParams.HEALTHY;
		final PatientProfile prof = new PatientProfile(initAge, sex, dis, rndDiagnosed[id].generateInt() == 1);
		for (ClinicalParameter param : parameters)
			prof.addProperty(param.getName(), param.getInitialValue(prof, simul));
		return prof;
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	@Override
	public int getMaxAge() {
		return BasicConfigParams.DEF_MAX_AGE;
	}

	/**
	 * Creates and returns a distribution that represents the probability of having a disease according to the population characteristics.
	 * TODO: Should be changed to a "time to disease" distribution to fully connect with the concepts of prevalence and incidence. If prevalence were
	 * used, time to would be 0 for prevalent and MAX for non-prevalent; if incidence were used, different times to event would be created. The latter
	 * would also require a new type of patient event and patient info.  
	 * @param simul Current simulation replica
	 * @return a distribution that represents the probability of having a disease according to the population characteristics
	 */
	protected abstract DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul);

	/**
	 * Creates and returns a distribution that should return {@link BasicConfigParams.MAN} if the patient is a male, 
	 * and {@link BasicConfigParams.WOMAN} if she is a female.
	 * @param simul Current simulation replica
	 * @return Distribution that should return 0 if the patient is a male, and 1 if she is a female.
	 */
	protected abstract DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul);
	
	/**
	 * Creates and returns a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 * @param simul Current simulation replica
	 * @return a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 */
	protected abstract DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul);	
	
	/**
	 * Creates and returns a function to assign the baseline age
	 * @param simul Current simulation replica
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul);
	
	protected List<ClinicalParameter> getPatientParameterList() {
		return new ArrayList<>();
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
}
