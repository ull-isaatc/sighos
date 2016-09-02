package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * FIXME: Currently assuming that CSME and HR_PDR are always detected
 * @author Iván Castilla Rodríguez
 *
 */
public class ClinicalPresentationDRParam extends Param {
	/** An index to locate a specific eye state in the {@link #probabilities} array */
	final private static EnumMap<EyeState, Integer> order  = new EnumMap<EyeState, Integer>(EyeState.class);
	/** The annual probability to be clinically detected depending on the states of the first eye */	
	private final static double [] probabilities = {0.1, 0.2, 1.0, 1.0}; 

	static {
		int cont = 0;
		order.put(EyeState.NPDR, cont++);
		order.put(EyeState.NON_HR_PDR, cont++);
		order.put(EyeState.HR_PDR, cont++);
		order.put(EyeState.CSME, cont++);		
	}

	public ClinicalPresentationDRParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}

	public double getProbability(Patient pat) {
		// TODO: Currently only checking first eye
		final EnumSet<EyeState> eye = pat.getEyeState(0);
		if (eye.contains(EyeState.CSME))
			return probabilities[order.get(EyeState.CSME)];
		if (eye.contains(EyeState.HR_PDR))
			return probabilities[order.get(EyeState.HR_PDR)];
		if (eye.contains(EyeState.NON_HR_PDR))
			return probabilities[order.get(EyeState.NON_HR_PDR)];
		if (eye.contains(EyeState.NPDR))
			return probabilities[order.get(EyeState.NPDR)];
		return 0.0;
	}
}
