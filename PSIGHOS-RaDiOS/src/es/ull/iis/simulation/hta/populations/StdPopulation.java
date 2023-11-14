/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients who can either suffer or not a specific disease.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class StdPopulation extends Population {
	protected final Disease disease;
	private final RandomVariate[] rndBaselineAge;
	private final DiscreteRandomVariate[] rndSex;
	private final DiscreteRandomVariate[] rndDisease;
	private final DiscreteRandomVariate[] rndDiagnosed;
	
	/**
	 * Creates a standard population
	 */
	public StdPopulation(SecondOrderParamsRepository secParams, String name, String description, Disease disease) throws MalformedSimulationModelException {
		super(name, description, secParams);
		this.disease = disease;
		rndBaselineAge = new RandomVariate[secParams.getNRuns() + 1];
		rndSex = new DiscreteRandomVariate[secParams.getNRuns() + 1];
		rndDiagnosed = new DiscreteRandomVariate[secParams.getNRuns() + 1];
		rndDisease = new DiscreteRandomVariate[secParams.getNRuns() + 1];
	}
	
	@Override
	public int getSex(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (rndSex[id] == null)
			rndSex[id] = getSexVariate(pat);
		return rndSex[id].generateInt();
	}

	@Override
	public double getInitAge(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (rndBaselineAge[id] == null)
			rndBaselineAge[id] = getBaselineAgeVariate(pat);
		return Math.min(Math.max(rndBaselineAge[id].generate(), getMinAge()), getMaxAge());
	}

	@Override
	public Disease getDisease(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (rndDisease[id] == null)
			rndDisease[id] = getDiseaseVariate(pat);
		return (rndDisease[id].generateInt() == 1) ? disease : getRepository().HEALTHY;
	}

	@Override
	public boolean isDiagnosedFromStart(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (rndDiagnosed[id] == null)
			rndDiagnosed[id] = getDiagnosedVariate(pat);
		return rndDiagnosed[id].generateInt() == 1;
	}
	
	/**
	 * Creates and returns a distribution that represents the probability of having a disease according to the population characteristics.
	 * TODO: Should be changed to a "time to disease" distribution to fully connect with the concepts of prevalence and incidence. If prevalence were
	 * used, time to would be 0 for prevalent and MAX for non-prevalent; if incidence were used, different times to event would be created. The latter
	 * would also require a new type of patient event and patient info.  
	 * @param simul Current simulation replica
	 * @return a distribution that represents the probability of having a disease according to the population characteristics
	 */
	protected abstract DiscreteRandomVariate getDiseaseVariate(Patient pat);

	/**
	 * Creates and returns a distribution that should return {@link BasicConfigParams.MAN} if the patient is a male, 
	 * and {@link BasicConfigParams.WOMAN} if she is a female.
	 * @param simul Current simulation replica
	 * @return Distribution that should return 0 if the patient is a male, and 1 if she is a female.
	 */
	protected abstract DiscreteRandomVariate getSexVariate(Patient pat);
	
	/**
	 * Creates and returns a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 * @param simul Current simulation replica
	 * @return a distribution which returns 1 (true) if the patient will start with a diagnosis according 
	 * to the population characteristics; and 0 (false) otherwise
	 */
	protected abstract DiscreteRandomVariate getDiagnosedVariate(Patient pat);	
	
	/**
	 * Creates and returns a function to assign the baseline age
	 * @param simul Current simulation replica
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate getBaselineAgeVariate(Patient pat);
}
