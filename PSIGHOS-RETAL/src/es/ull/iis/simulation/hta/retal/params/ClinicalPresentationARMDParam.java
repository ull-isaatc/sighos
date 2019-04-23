/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ClinicalPresentationARMDParam extends Param {
	/** An index to locate a specific eye state in the {@link #probabilities} array */
	final private static EnumMap<EyeState, Integer> order  = new EnumMap<EyeState, Integer>(EyeState.class);
	/** An index to locate a specific CNV stage in the {@link #probabilities} array */
	final private static EnumMap<CNVStage.Position, Integer> orderCNV  = new EnumMap<CNVStage.Position, Integer>(CNVStage.Position.class);
	/** The annual probability to be clinically detected depending on the states of first and fellow eyes */
	final private static double[][] probabilities;
	
	static {
		int cont = 0;
		order.put(EyeState.HEALTHY, cont++);
		order.put(EyeState.EARM, cont++);
		order.put(EyeState.AMD_GA, cont++);
		orderCNV.put(CNVStage.Position.EF, cont++);
		orderCNV.put(CNVStage.Position.JF, cont++);
		orderCNV.put(CNVStage.Position.SF, cont);
		probabilities = new double[][] { 
				{0.0, 0.000523174, 0.102947627, 0.019835782, 0.059508913, 0.060341195},
				{0.000523174, 0.0, 0.098953626, 0.020499405, 0.058588325, 0.104656435},
				{0.102947627, 0.098953626, 0.171665603, 0.098124373, 0.100203503, 0.102339942},
				{0.019835782, 0.020499405, 0.098124373, 0.039980248, 0.059213979, 0.101338676},
				{0.059508913, 0.058588325, 0.100203503, 0.059213979, 0.098470343, 0.102573043},
				{0.060341195, 0.104656435, 0.102339942, 0.101338676, 0.102573043, 0.118084246}
		};
	}

	/**
	 * @param simul
	 * @param 
	 */
	public ClinicalPresentationARMDParam() {
		super();
	}

	private int getOrder(RetalPatient pat, int eyeIndex) {
		final EnumSet<EyeState> eye = pat.getEyeState(eyeIndex);
		if (eye.contains(EyeState.HEALTHY))
			return order.get(EyeState.HEALTHY);
		else if (eye.contains(EyeState.EARM))
			return order.get(EyeState.EARM);
		else if (eye.contains(EyeState.AMD_GA))
			return order.get(EyeState.AMD_GA);
		else if (eye.contains(EyeState.AMD_CNV)) {
			final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
			return orderCNV.get(stage.getPosition());
		}
		else
			return 0;
	}
	
	public double getProbability(RetalPatient pat) {
		return probabilities[getOrder(pat, 0)][getOrder(pat, 1)];
	}
	
	public long getValidatedTimeToEvent(RetalPatient pat) {
		final double yearlyProb = getProbability(pat);
		final int currentAge = (int) pat.getAge();
		final int yearsToDeath = (int)pat.getAgeAtDeath() - currentAge;
		// First year
		if (pat.draw(RandomForPatient.ITEM.ARMD_CLINICAL_PRESENTATION) < yearlyProb)
			return pat.getTs();
		// Following years but the last
		for (int age = 1; age < yearsToDeath; age++) {
			if (pat.draw(RandomForPatient.ITEM.ARMD_CLINICAL_PRESENTATION) < yearlyProb)
				return pat.getTs() + pat.getSimulation().getTimeUnit().convert(age, TimeUnit.YEAR);
		}
		// Last year
		if (pat.draw(RandomForPatient.ITEM.ARMD_CLINICAL_PRESENTATION) < yearlyProb)
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(yearsToDeath, TimeUnit.YEAR);
		return Long.MAX_VALUE;
	}

}
