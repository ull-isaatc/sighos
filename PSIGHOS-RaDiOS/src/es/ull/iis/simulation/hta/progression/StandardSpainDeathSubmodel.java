/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.GompertzVariate;
import simkit.random.RandomNumber;

/**
 * A death submodel based on the Spanish 2016 Mortality risk from the Instituto Nacional de Estadística (INE). The
 * parameters are adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 18 to 100.
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardSpainDeathSubmodel extends DiseaseProgression {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};
	private final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	/** A random value [0, 1] for each patient (useful for common numbers techniques) and simulation */
	// FIXME: To be coherent, the same value should be generated for the same patient in every simulation
	private final double[][] rnd;

	/**
	 * Creates a death submodel based on the Spanish 2016 Mortality risk
	 * @param rng A random number generator
	 * @param nPatients Number of simulated patients
	 */
	public StandardSpainDeathSubmodel(HTAModel model, Disease disease) {
		super(model, "DEATH", "Death according to the Spanish 2017 Mortality risk from the Instituto Nacional de Estadística (INE), modelled with a Gompertz distribution", disease, DiseaseProgression.Type.DEATH);
		rnd = new double[model.getExperiment().getNRuns() + 1][model.getExperiment().getNPatients()];
		for (int i = 0; i < model.getExperiment().getNRuns() + 1; i++)
			for (int j = 0; j < model.getExperiment().getNPatients(); j++) {
				rnd[i][j] = rng.draw();
			}
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables and increased according to the 
	 * state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public long getTimeTo(Patient pat, long limit) {
		final DiseaseProgressionSimulation simul = pat.getSimulation();
		final int simulId = simul.getIdentifier();
		final double age = pat.getAge();
		double imr = 1.0;
		double ler = 0.0;
		for (final DiseaseProgression state : pat.getState()) {
			final double newIMR = state.getStandardParameterValue(StandardParameter.INCREASED_MORTALITY_RATE, pat);
			if (newIMR > imr) {
				imr = newIMR;
			}
			final double newLER = state.getStandardParameterValue(StandardParameter.LIFE_EXPECTANCY_REDUCTION, pat);
			if (newLER > ler) {
				ler = newLER;
			}
		}
		
		// Taking into account modification of death due to the intervention
		final ParameterModifier leModif = pat.getIntervention().getLifeExpectancyModification();
		// Taking into account modification of death due to the intervention
		final ParameterModifier imrModif = pat.getIntervention().getMortalityRiskModification();
		imr = imrModif.getModifiedValue(pat, imr);
		
		final double time = Math.min(GompertzVariate.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[simulId][pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - age);
		// TODO: Check that this works properly
		return pat.getTs() + simul.getTimeUnit().convert(Math.max(0.0,  Math.min(leModif.getModifiedValue(pat, time) - ler, BasicConfigParams.DEF_MAX_AGE - age)), TimeUnit.YEAR);			
	}
}
