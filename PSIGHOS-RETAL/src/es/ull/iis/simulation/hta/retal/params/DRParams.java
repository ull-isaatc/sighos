/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;

/**
 * Parameters related to diabetic retinopathy and diabetic macular edema
 * In most cases, central macular edema and clinically significant macular edema are considered equivalent, based on expert opinion
 * @author Ivan Castilla Rodriguez
 *
 */
// TODO: Add second order
public class DRParams extends ModelParams {
	/**
	 * Prevalence of DR in DM1 for 40-50 group age
	 * <N Total, N patients non affected, N patients with NPDR*, N patients with PDR*>
	 * Source: Davies 
	 */
	private static final int[] DM1_PREVALENCE = {93, 9, 51, 33};
	/**
	 * Prevalence of DR in DM2 for 52.2 (SD 8.6) years-old patients
	 * <N Total, N patients non affected, N patients with NPDR*, N patients with PDR*>
	 * Source: Stratton, Kohner et al 2001 
	 */
	private static final int[] DM2_PREVALENCE = {1919, 1216, 701, 2};
	
	/**
	 * Cumulative probability of <No DR, NPDR, PDR> in the 2nd eye if the 1st eye has NPDR
	 * Source: "Cooked" from Stratton, Kohner et al 2001
	 */
	private static final double[] EYE2_DM_PREVALENCE_EYE1_NPDR = {0.693707398, 1, 1};
	/**
	 * Cumulative probability of <No DR, NPDR, PDR> in the 2nd eye if the 1st eye has PDR
	 * Source: "Cooked" from Stratton, Kohner et al 2001
	 */
	private static final double[] EYE2_DM_PREVALENCE_EYE1_PDR = {0.317162233, 0.5, 1};
			
	/** Average percentage of macular edema among patients with NPDR (DM1, DM2). Source: Davies */
	private static final double[] NPDR_PERCENTAGE_ME = {0.071759546, 0.147970641};
	/** Average percentage of macular edema among patients with PDR (DM1, DM2). Source: Davies */
	private static final double[] PDR_PERCENTAGE_ME = {0.337404617, 0.428835979};
	/** Percentage of patients with clinically significant macular edema. Source: Davies */
	private static final double CSME = 0.8;

	// Source: Aoki, N. et al., 2004. Cost-effectiveness analysis of telemedicine to evaluate diabetic retinopathy in a prison population. 
	// Diabetes care, 27(5), pp.1095–101. Available at: http://www.ncbi.nlm.nih.gov/pubmed/15111527.
	private static final double ANNUAL_PROB_NPDR = 0.065;
	private static final double ANNUAL_PROB_PDR = 0.116;
	private static final double ANNUAL_PROB_CSME = 0.115;
	// Source: Rein (technical report)
	private static final double ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_CSME = 0.2023;
	private static final double ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME = 0.0602;
	private static final double ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_NONHR_PDR = 0.0248;
	private static final double ANNUAL_PROB_HR_PDR_FROM_NONHR_PDR = 0.3339;
	private static final double ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR = 0.1729;
	private static final double ANNUAL_PROB_CSME_AND_HR_PDR_FROM_HR_PDR = 0.0248;
	
	private final AnnualBasedTimeToEventParam timeToNPDR;
	private final AnnualBasedTimeToEventParam timeToPDR;
	private final AnnualBasedTimeToEventParam timeToCSME;
	private final AnnualBasedTimeToEventParam timeToCSMEAndNonHRPDRFromCSME;
	private final AnnualBasedTimeToEventParam timeToCSMEAndHRPDRFromCSME;
	private final AnnualBasedTimeToEventParam timeToCSMEAndNonHRPDRFromNonHRPDR;
	private final AnnualBasedTimeToEventParam timeToHRPDRFromNonHRPDR;
	private final AnnualBasedTimeToEventParam timeToCSMEAndHRPDRFromCSMEAndNonHRPDR;
	private final AnnualBasedTimeToEventParam timeToCSMEAndHRPDRFromHRPDR;
	private final ClinicalPresentationDRParam clinicalPresentation;

	private final double[] prevalenceDM1 = new double[DM1_PREVALENCE.length - 1];
	private final double[] prevalenceDM2 = new double[DM2_PREVALENCE.length - 1];
	
	/**
	 * @param simul
	 * @param 
	 */
	public DRParams() {
		super();
		this.timeToNPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_NPDR, RandomForPatient.ITEM.TIME_TO_NPDR);
		this.timeToPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_PDR, RandomForPatient.ITEM.TIME_TO_PDR);
		this.timeToCSME = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME, RandomForPatient.ITEM.TIME_TO_CSME);
		this.timeToCSMEAndNonHRPDRFromCSME = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_CSME, RandomForPatient.ITEM.TIME_TO_CSME_AND_NONHR_PDR_FROM_CSME);
		this.timeToCSMEAndHRPDRFromCSME = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_CSME);
		this.timeToCSMEAndNonHRPDRFromNonHRPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_NONHR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_NONHR_PDR_FROM_NONHR_PDR);
		this.timeToHRPDRFromNonHRPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_HR_PDR_FROM_NONHR_PDR, RandomForPatient.ITEM.TIME_TO_HR_PDR_FROM_NONHR_PDR);
		this.timeToCSMEAndHRPDRFromCSMEAndNonHRPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR);
		this.timeToCSMEAndHRPDRFromHRPDR = new AnnualBasedTimeToEventParam(ANNUAL_PROB_CSME_AND_HR_PDR_FROM_HR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_HR_PDR);
		this.clinicalPresentation = new ClinicalPresentationDRParam();
		double acum = 0.0;
		for (int i = 0; i < prevalenceDM1.length;i++) {
			acum += DM1_PREVALENCE[i+1] / DM1_PREVALENCE[0];
			prevalenceDM1[i] = acum;
		}
		acum = 0.0;
		for (int i = 0; i < prevalenceDM2.length;i++) {
			acum += DM2_PREVALENCE[i+1] / DM2_PREVALENCE[0];
			prevalenceDM2[i] = acum;
		}
	}

	/** 
	 * Returns the state of the patient in case he/she has progressed to some degree to a pathologic state. 
	 * It is based on the prevalence of diabetic retinopathy and the age of the patient.
	 * @param pat A patient
	 * @return the state of the patient
	 */
	public EnumSet<EyeState>[] startsWith(RetalPatient pat) {
		@SuppressWarnings("unchecked")
		final EnumSet<EyeState>[] states = new EnumSet[] {EnumSet.noneOf(EyeState.class), EnumSet.noneOf(EyeState.class)};
		final int typeDM = pat.getDiabetesType() - 1;
		final double[] prevalence;
		if (typeDM == 1) {
			prevalence = prevalenceDM1;
		}
		else if (typeDM == 2) {
			prevalence = prevalenceDM2;
		}
		else {
			return states;
		}
		final double[] rnd = pat.draw(RandomForPatient.ITEM.DR_INITIAL_STATE, 2);
		if (rnd[0] < prevalence[0])
			// no new pathological state to add
			;
		else if (rnd[0] < prevalence[1]) {
			states[0].add(EyeState.NPDR);
			if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < NPDR_PERCENTAGE_ME[typeDM]) {
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
					states[0].add(EyeState.CSME);
			}
			// Second eye
			if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_NPDR[0]) {
				;
			}
			else if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_NPDR[1]) {
				states[1].add(EyeState.NPDR);
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < NPDR_PERCENTAGE_ME[typeDM]) {
					if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
						states[1].add(EyeState.CSME);
				}				
			}
			else if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_NPDR[2]) {
				states[1].add(EyeState.NON_HR_PDR);
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < PDR_PERCENTAGE_ME[typeDM]) {
					if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
						states[1].add(EyeState.CSME);
				}				
			}
		}
		else if (rnd[0] < prevalence[2]) {
			// FIXME: Check to see what happen with HR_PDR
			states[0].add(EyeState.NON_HR_PDR);
			if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < PDR_PERCENTAGE_ME[typeDM]) {
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
					states[0].add(EyeState.CSME);
			}
			// Second eye
			if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_PDR[0]) {
				;
			}
			else if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_PDR[1]) {
				states[1].add(EyeState.NPDR);
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < NPDR_PERCENTAGE_ME[typeDM]) {
					if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
						states[1].add(EyeState.CSME);
				}				
			}
			else if (rnd[1] < EYE2_DM_PREVALENCE_EYE1_PDR[2]) {
				states[1].add(EyeState.NON_HR_PDR);
				if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_ME) < PDR_PERCENTAGE_ME[typeDM]) {
					if (pat.draw(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
						states[1].add(EyeState.CSME);
				}				
			}
		}
		return states;
	}
	
	public long getTimeToNPDR(RetalPatient pat) {
		return timeToNPDR.getTimeToEvent(pat);
	}

	public long getTimeToPDR(RetalPatient pat) {
		return timeToPDR.getTimeToEvent(pat);
	}

	public long getTimeToCSME(RetalPatient pat) {
		return timeToCSME.getTimeToEvent(pat);
	}

	public long getTimeToCSMEAndNonHRPDRFromCSME(RetalPatient pat) {
		return timeToCSMEAndNonHRPDRFromCSME.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndHRPDRFromCSME(RetalPatient pat) {
		return timeToCSMEAndHRPDRFromCSME.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndNonHRPDRFromNonHRPDR(RetalPatient pat) {
		return timeToCSMEAndNonHRPDRFromNonHRPDR.getTimeToEvent(pat);
	}
	
	public long getTimeToHRPDRFromNonHRPDR(RetalPatient pat) {
		return timeToHRPDRFromNonHRPDR.getTimeToEvent(pat);
	}
	public long getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(RetalPatient pat) {
		return timeToCSMEAndHRPDRFromCSMEAndNonHRPDR.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndHRPDRFromHRPDR(RetalPatient pat) {
		return timeToCSMEAndHRPDRFromHRPDR.getTimeToEvent(pat);
	}

	public double getProbabilityClinicalPresentation(RetalPatient pat) {
		return clinicalPresentation.getProbability(pat);
	}
	
	public long getTimeToClinicalPresentation(RetalPatient pat) {
		return clinicalPresentation.getValidatedTimeToEvent(pat);
	}
}
