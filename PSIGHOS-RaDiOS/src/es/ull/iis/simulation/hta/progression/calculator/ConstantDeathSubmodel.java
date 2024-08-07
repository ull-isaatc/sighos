package es.ull.iis.simulation.hta.progression.calculator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.model.TimeUnit;

public class ConstantDeathSubmodel implements TimeToEventCalculator {
    final private double lifeExpectancy;
	/**
	 * Creates a death submodel based on a fixed life expectancy.
	 * @param lifeExpectancy Years of life expectancy
	 */
	public ConstantDeathSubmodel(double lifeExpectancy) {
        this.lifeExpectancy = lifeExpectancy;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.YEAR;
	}

	/**
	 * Returns the simulation time until the death of the patient. Initially the life expectancy is used, but increased mortality rates and/or
	 * reduction of life expectancy due to manifestations apply according to the state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public double getTimeToEvent(Patient pat) {
		final double age = pat.getAge();
		final double maxLifeExpectancy = lifeExpectancy - age + pat.getInitAge();
		double imr = 1.0;
		double ler = 0.0;
		for (final DiseaseProgression state : pat.getState()) {
			final double newIMR = state.getUsedParameterValue(StandardParameter.INCREASED_MORTALITY_RATE, pat);
			if (newIMR > imr) {
				imr = newIMR;
			}
			final double newLER = state.getUsedParameterValue(StandardParameter.LIFE_EXPECTANCY_REDUCTION, pat);
			if (newLER > ler) {
				ler = newLER;
			}
		}
		
		// Taking into account modification of death due to the intervention
		final ParameterModifier leModif = pat.getIntervention().getLifeExpectancyModification();
		// Taking into account modification of death due to the intervention
		final ParameterModifier imrModif = pat.getIntervention().getMortalityRiskModification();
		imr = imrModif.getModifiedValue(pat, imr);
		return Math.max(0.0,  Math.min(leModif.getModifiedValue(pat, maxLifeExpectancy / imr) - ler, maxLifeExpectancy));			
	}
    
}
