/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.util.EnumSet;

import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * A listener prepared to filter by different criteria related to the patient's condition.
 * @author Iván Castilla
 *
 */
public abstract class FilteredListener extends Listener {
	protected final EnumSet<RETALSimulation.DISEASES> diseases;
	protected final EyeState filterARMDByState;
	protected final EyeState filterDRByState;
	protected final boolean includeDiabetes;

	/**
	 * 
	 * @param simul
	 * @param description
	 * @param diseases
	 * @param includeDiabetes
	 * @param filterARMDByState
	 * @param filterDRByState
	 */
	public FilteredListener(String description, EnumSet<RETALSimulation.DISEASES> diseases, boolean includeDiabetes, EyeState filterARMDByState, EyeState filterDRByState) {
		super(description);
		this.diseases = diseases;
		this.includeDiabetes = includeDiabetes;
		this.filterARMDByState = filterARMDByState;		
		this.filterDRByState = filterDRByState;		
	}

	/**
	 * Returns true if the filter meets the condition set; false otherwise.
	 * @param pat A patient
	 * @return True if the filter meets the condition set; false otherwise.
	 */
	protected boolean checkFilter(Patient pat) {
		boolean condition = false;
		if (diseases.contains(RETALSimulation.DISEASES.ARMD)) {
			if (filterARMDByState == EyeState.AMD_CNV) {
				condition = condition || (pat.getTimeToEyeState(EyeState.AMD_CNV, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.AMD_CNV, 1) != Long.MAX_VALUE);
			}
			else if (filterARMDByState == EyeState.AMD_GA) {
				condition = condition || (pat.getTimeToEyeState(EyeState.AMD_CNV, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.AMD_CNV, 1) != Long.MAX_VALUE)
						 || (pat.getTimeToEyeState(EyeState.AMD_GA, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.AMD_GA, 1) != Long.MAX_VALUE);					
			}
			else if (filterARMDByState == EyeState.EARM) {
				condition = condition || (pat.getTimeToEyeState(EyeState.EARM, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.AMD_GA, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.AMD_CNV, 0) != Long.MAX_VALUE);
			}
		}
		if (diseases.contains(RETALSimulation.DISEASES.DR)) {
			if (filterDRByState == EyeState.CSME) {
				condition = condition || (pat.getTimeToEyeState(EyeState.CSME, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.CSME, 1) != Long.MAX_VALUE);
			}
			else if (filterDRByState == EyeState.HR_PDR) {
				condition = condition || (pat.getTimeToEyeState(EyeState.HR_PDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.HR_PDR, 1) != Long.MAX_VALUE);
			}
			else if (filterDRByState == EyeState.NON_HR_PDR) {
				condition = condition || (pat.getTimeToEyeState(EyeState.NON_HR_PDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.NON_HR_PDR, 1) != Long.MAX_VALUE)
						 || (pat.getTimeToEyeState(EyeState.HR_PDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.HR_PDR, 1) != Long.MAX_VALUE);
			}
			else if (filterDRByState == EyeState.NPDR) {
				condition = condition || (pat.getTimeToEyeState(EyeState.NPDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.NPDR, 1) != Long.MAX_VALUE)
						 || (pat.getTimeToEyeState(EyeState.NON_HR_PDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.NON_HR_PDR, 1) != Long.MAX_VALUE)
						 || (pat.getTimeToEyeState(EyeState.HR_PDR, 0) != Long.MAX_VALUE || pat.getTimeToEyeState(EyeState.HR_PDR, 1) != Long.MAX_VALUE);
			}
		}
		if (includeDiabetes) {
			condition = condition || pat.isDiabetic();
		}
		return condition;
	}
}
