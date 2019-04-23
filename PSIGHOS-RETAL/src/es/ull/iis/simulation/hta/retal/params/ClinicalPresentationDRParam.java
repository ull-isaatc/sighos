package es.ull.iis.simulation.hta.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class ClinicalPresentationDRParam extends Param {
	/** An index to locate a specific eye state in the {@link #probabilities} array */
	final private static EnumMap<EyeState, Integer> order  = new EnumMap<EyeState, Integer>(EyeState.class);
	//FIXME: Currently assigning arbitrary clinical detection rates
	/** The annual probability to be clinically detected depending on the states of the first eye. Assumption */	
	private final static double [] probabilities = {0.001, 0.002, 0.8, 0.9}; 

	static {
		int cont = 0;
		order.put(EyeState.NPDR, cont++);
		order.put(EyeState.NON_HR_PDR, cont++);
		order.put(EyeState.HR_PDR, cont++);
		order.put(EyeState.CSME, cont++);		
	}

	public ClinicalPresentationDRParam() {
		super();
	}

	public double getProbability(RetalPatient pat) {
		final EnumSet<EyeState> eye1 = pat.getEyeState(0);
		final EnumSet<EyeState> eye2 = pat.getEyeState(1);
		if (eye1.contains(EyeState.CSME) || eye2.contains(EyeState.CSME))
			return probabilities[order.get(EyeState.CSME)];
		if (eye1.contains(EyeState.HR_PDR) || eye2.contains(EyeState.HR_PDR))
			return probabilities[order.get(EyeState.HR_PDR)];
		if (eye1.contains(EyeState.NON_HR_PDR) || eye2.contains(EyeState.NON_HR_PDR))
			return probabilities[order.get(EyeState.NON_HR_PDR)];
		if (eye1.contains(EyeState.NPDR) || eye2.contains(EyeState.NPDR))
			return probabilities[order.get(EyeState.NPDR)];
		return 0.0;
	}

	public long getValidatedTimeToEvent(RetalPatient pat) {
		final double yearlyProb = getProbability(pat);
		final int currentAge = (int) pat.getAge();
		final int yearsToDeath = (int)pat.getAgeAtDeath() - currentAge;
		// First year
		if (pat.draw(RandomForPatient.ITEM.DR_CLINICAL_PRESENTATION) < yearlyProb)
			return pat.getTs();
		// Following years but the last
		for (int age = 1; age < yearsToDeath; age++) {
			if (pat.draw(RandomForPatient.ITEM.DR_CLINICAL_PRESENTATION) < yearlyProb)
				return pat.getTs() + pat.getSimulation().getTimeUnit().convert(age, TimeUnit.YEAR);
		}
		// Last year
		if (pat.draw(RandomForPatient.ITEM.DR_CLINICAL_PRESENTATION) < yearlyProb)
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(yearsToDeath, TimeUnit.YEAR);
		return Long.MAX_VALUE;
	}

}
