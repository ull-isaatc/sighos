/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * Parameters related to diabetic retinopathy and diabetic macular edema
 * @author Ivan Castilla Rodriguez
 *
 */
// TODO: Add second order
// TODO: Check consistency between clinically-significant macular edema and central macular edema
public class DRParams extends ModelParams {
	/**
	 * Prevalence of DR in DM1 by group age
	 * <initial age, final age, proportion non affected, proportion with NPDR*, proportion with PDR*, proportion untreatable*>
	 * * Proportions are expressed cummulatively
	 * Source: Davies 
	 */
	private static final double[][] DM1_PREVALENCE = {
			{0, 10, 1, 1, 1, 1},
			{10, 15, 0.82, 1, 1, 1},
			{15, 20, 0.46, 0.98, 1, 1},
			{20, 30, 0.23, 0.8, 0.97, 1},
			{30, 40, 0.17, 0.67, 0.96, 1},
			{40, 50, 0.09, 0.6, 0.93, 1},
			{50, CommonParams.MAX_AGE, 0.03, 0.54, 0.89, 1}
	};
	/**
	 * Prevalence of DR in DM2 by group age
	 * <initial age, final age, proportion non affected, proportion with NPDR*, proportion with PDR*, proportion untreatable*>
	 * * Proportions are expressed cummulatively
	 * Source: Davies 
	 */
	private static final double[][] DM2_PREVALENCE = {
			{0, 30, 1, 1, 1, 1},
			{30, 40, 0.67, 0.96, 1, 1},
			{40, 50, 0.57, 0.92, 0.99, 1},
			{50, 60, 0.52, 0.9, 0.99, 1},
			{60, 70, 0.52, 0.93, 0.98, 1},
			{70, 80, 0.52, 0.93, 0.98, 1},
			{80, CommonParams.MAX_AGE, 0.52, 0.93, 0.98, 1}
	};
	/** Average percentage of macular edema among patients with NPDR (DM1, DM2) */
	private static final double[] NPDR_PERCENTAGE_ME = {0.071759546, 0.147970641};
	/** Average percentage of macular edema among patients with PDR (DM1, DM2) */
	private static final double[] PDR_PERCENTAGE_ME = {0.337404617, 0.428835979};
	/** Percentage of patients with clinically significant macular edema */
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

	/**
	 * @param simul
	 * @param baseCase
	 */
	public DRParams(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		this.timeToNPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_NPDR, RandomForPatient.ITEM.TIME_TO_NPDR);
		this.timeToPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_PDR, RandomForPatient.ITEM.TIME_TO_PDR);
		this.timeToCSME = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME, RandomForPatient.ITEM.TIME_TO_CSME);
		this.timeToCSMEAndNonHRPDRFromCSME = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_CSME, RandomForPatient.ITEM.TIME_TO_CSME_AND_NONHR_PDR_FROM_CSME);
		this.timeToCSMEAndHRPDRFromCSME = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_CSME);
		this.timeToCSMEAndNonHRPDRFromNonHRPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME_AND_NONHR_PDR_FROM_NONHR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_NONHR_PDR_FROM_NONHR_PDR);
		this.timeToHRPDRFromNonHRPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_HR_PDR_FROM_NONHR_PDR, RandomForPatient.ITEM.TIME_TO_HR_PDR_FROM_NONHR_PDR);
		this.timeToCSMEAndHRPDRFromCSMEAndNonHRPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_CSME_AND_NON_HR_PDR);
		this.timeToCSMEAndHRPDRFromHRPDR = new AnnualBasedTimeToEventParam(simul, baseCase, ANNUAL_PROB_CSME_AND_HR_PDR_FROM_HR_PDR, RandomForPatient.ITEM.TIME_TO_CSME_AND_HR_PDR_FROM_HR_PDR);
	}

	/** 
	 * Returns the state of the patient in case he/she has progressed to some degree to a pathologic state. 
	 * It is based on the prevalence of diabetic retinopathy and the age of the patient.
	 * @param pat A patient
	 * @return the state of the patient
	 */
	public EnumSet<EyeState> startsWith(Patient pat) {
		final EnumSet<EyeState> states = EnumSet.noneOf(EyeState.class);
		final int typeDM = pat.getDiabetesType() - 1;
		final double[][] prevalence;
		if (typeDM == 1) {
			prevalence = DM1_PREVALENCE;
		}
		else if (typeDM == 2) {
			prevalence = DM2_PREVALENCE;
		}
		else {
			return states;
		}
		final double age = pat.getAge();
		int count = 0;
		while (prevalence[count][1] < age) {
			count++;
		}
		final double rnd = pat.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_STATE);
		if (rnd < prevalence[count][2])
			// no new pathological state to add
			;
		else if (rnd < prevalence[count][3]) {
			states.add(EyeState.NPDR);
			if (pat.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_ME) < NPDR_PERCENTAGE_ME[typeDM]) {
				if (pat.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
					states.add(EyeState.CSME);
			}
		}
		else if (rnd < prevalence[count][4]) {
			// FIXME: Check to see what happen with HR_PDR
			states.add(EyeState.NON_HR_PDR);
			if (pat.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_ME) < PDR_PERCENTAGE_ME[typeDM]) {
				if (pat.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_CSME) < CSME)
					states.add(EyeState.CSME);
			}
		}
		else if (rnd < prevalence[count][5]) {
			states.add(EyeState.UNTREATABLE);
		}
		return states;
	}
	
	public long getTimeToNPDR(Patient pat) {
		return timeToNPDR.getTimeToEvent(pat);
	}

	public long getTimeToPDR(Patient pat) {
		return timeToPDR.getTimeToEvent(pat);
	}

	public long getTimeToCSME(Patient pat) {
		return timeToCSME.getTimeToEvent(pat);
	}

	public long getTimeToCSMEAndNonHRPDRFromCSME(Patient pat) {
		return timeToCSMEAndNonHRPDRFromCSME.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndHRPDRFromCSME(Patient pat) {
		return timeToCSMEAndHRPDRFromCSME.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndNonHRPDRFromNonHRPDR(Patient pat) {
		return timeToCSMEAndNonHRPDRFromNonHRPDR.getTimeToEvent(pat);
	}
	
	public long getTimeToHRPDRFromNonHRPDR(Patient pat) {
		return timeToHRPDRFromNonHRPDR.getTimeToEvent(pat);
	}
	public long getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(Patient pat) {
		return timeToCSMEAndHRPDRFromCSMEAndNonHRPDR.getTimeToEvent(pat);
	}
	
	public long getTimeToCSMEAndHRPDRFromHRPDR(Patient pat) {
		return timeToCSMEAndHRPDRFromHRPDR.getTimeToEvent(pat);
	}

}
