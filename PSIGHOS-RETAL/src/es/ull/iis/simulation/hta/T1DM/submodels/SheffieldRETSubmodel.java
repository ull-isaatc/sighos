/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.SheffieldComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SheffieldRETSubmodel extends ChronicComplicationSubmodel {
	public static T1DMComorbidity BGRET = new T1DMComorbidity("BGRET", "Background Retinopathy", MainChronicComplications.RET);
	public static T1DMComorbidity PRET = new T1DMComorbidity("PRET", "Proliferative Retinopathy", MainChronicComplications.RET);
	public static T1DMComorbidity ME = new T1DMComorbidity("ME", "Macular edema", MainChronicComplications.RET);
	public static T1DMComorbidity BLI = new T1DMComorbidity("BLI", "Blindness", MainChronicComplications.RET);
	public static T1DMComorbidity[] RETSubstates = new T1DMComorbidity[] {BGRET, PRET, ME, BLI};
	
	private static final double CALIBRATION_COEF_BGRET = 1.0;
	private static final double CALIBRATION_COEF_BETA_BGRET = 1.0;
	private static final double P_DNC_BGRET = 0.0454 * CALIBRATION_COEF_BGRET;
	private static final double P_DNC_PRET = 0.0013;
	private static final double P_DNC_ME = 0.0012;
	private static final double P_BGRET_PRET = 0.0595;
	private static final double P_BGRET_ME = 0.0512;
	private static final double P_BGRET_BLI = 0.0001;
	private static final double P_PRET_BLI = 0.0038;
	private static final double P_ME_BLI = 0.0016;
	private static final double P_DNC_BLI = 1.9e-6;
	private static final double BETA_BGRET = 10.10 * CALIBRATION_COEF_BETA_BGRET;
	private static final double BETA_PRET = 6.30;
	private static final double BETA_ME = 1.20;
	// Assumption
	private static final double DU_BGRET = 0.0;
	// Utility (avg, SD) from either Fenwick et al.; or Sullivan
	private static final double[] DU_PRET = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.04, (0.066 - 0.014) / 3.92} : new double[] {0.0156, 0.0002};
	// Utility (avg, SD) from either Fenwick et al.; or Sullivan
	private static final double[] DU_ME = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.04, (0.066 - 0.014) / 3.92} : new double[] {0.0156, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_BLI = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.074, (0.124 - 0.025) / 3.92} : new double[] {0.0498, 0.0002};
	private static final double C_BGRET = 146.4525; // Detail: Parametros.xls
	private static final double C_PRET = 6394.62; // Detail: Parametros.xls
	private static final double C_ME = 6785.16; // Detail: Parametros.xls
	private static final double C_BLI = 2405.35;
	
	public enum RETTransitions {
		HEALTHY_BGRET(null, BGRET),
		HEALTHY_PRET(null, PRET),
		HEALTHY_ME(null, ME),
		BGRET_PRET(BGRET, PRET),
		BGRET_ME(BGRET, ME),
		BGRET_BLI(BGRET, BLI),
		PRET_BLI(PRET, BLI),
		ME_BLI(ME, BLI),
		HEALTHY_BLI(null, BLI);
		
		final private T1DMComorbidity from;
		final private T1DMComorbidity to;
		
		private RETTransitions(T1DMComorbidity from, T1DMComorbidity to) {
			this.from = from;
			this.to = to;
		}

		/**
		 * @return the from
		 */
		public T1DMComorbidity getFrom() {
			return from;
		}

		/**
		 * @return the to
		 */
		public T1DMComorbidity getTo() {
			return to;
		}
	}
	
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double[][] rnd;
	private final double[] rndBGRETAtStart;
	private final double[] costBGRET;
	private final double[] costPRET;
	private final double[] costME;
	private final double[] costBLI;
	private final double duBGRET;
	private final double duPRET;
	private final double duME;
	private final double duBLI;
	
	/**
	 * 
	 */
	public SheffieldRETSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[RETTransitions.values().length];
		invProb[RETTransitions.HEALTHY_BGRET.ordinal()] = -1 / secParams.getProbability(BGRET);
		invProb[RETTransitions.HEALTHY_PRET.ordinal()] = -1 / secParams.getProbability(PRET);
		invProb[RETTransitions.HEALTHY_ME.ordinal()] = -1 / secParams.getProbability(ME);
		invProb[RETTransitions.HEALTHY_BLI.ordinal()] = -1 / secParams.getProbability(BLI);
		invProb[RETTransitions.BGRET_BLI.ordinal()] = -1 / secParams.getProbability(BGRET, BLI);
		invProb[RETTransitions.BGRET_PRET.ordinal()] = -1 / secParams.getProbability(BGRET, PRET);
		invProb[RETTransitions.BGRET_ME.ordinal()] = -1 / secParams.getProbability(BGRET, ME);
		invProb[RETTransitions.PRET_BLI.ordinal()] = -1 / secParams.getProbability(PRET, BLI);
		invProb[RETTransitions.ME_BLI.ordinal()] = -1 / secParams.getProbability(ME, BLI);
		
		this.rr = new ComplicationRR[RETTransitions.values().length];
		final ComplicationRR rrBGRET = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + BGRET));
		final ComplicationRR rrPRET = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + PRET));
		final ComplicationRR rrME = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME));
		rr[RETTransitions.HEALTHY_BGRET.ordinal()] = rrBGRET;
		rr[RETTransitions.HEALTHY_PRET.ordinal()] = rrPRET;
		rr[RETTransitions.HEALTHY_ME.ordinal()] = rrME;
		rr[RETTransitions.HEALTHY_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[RETTransitions.BGRET_PRET.ordinal()] = rrPRET;
		rr[RETTransitions.BGRET_ME.ordinal()] = rrME;
		rr[RETTransitions.BGRET_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[RETTransitions.PRET_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[RETTransitions.ME_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;

		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][RETSubstates.length];
		rndBGRETAtStart = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rndBGRETAtStart[i] = rng.draw();
			for (int j = 0; j < RETSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
		
		costBGRET = secParams.getCostsForHealthState(BGRET);
		costPRET = secParams.getCostsForHealthState(PRET);
		costME = secParams.getCostsForHealthState(ME);
		costBLI = secParams.getCostsForHealthState(BLI);
		
		duBGRET = secParams.getDisutilityForHealthState(BGRET);
		duPRET = secParams.getDisutilityForHealthState(PRET);
		duME = secParams.getDisutilityForHealthState(ME);
		duBLI = secParams.getDisutilityForHealthState(BLI);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, BGRET), "Probability of healthy to background retinopathy", 
				"Sheffield (WESDR XXII)", P_DNC_BGRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_BGRET)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, PRET), "Probability of healthy to proliferative retinopathy", 
				"Sheffield (WESDR XXII)", P_DNC_PRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_PRET)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, ME),	"Probability of healthy to macular edema", 
				"Sheffield (WESDR XXII)", P_DNC_ME, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_ME)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(BGRET, PRET),	"Probability of BG ret to proliferative retinopathy", 
				"Sheffield (WESDR XXII)", P_BGRET_PRET, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_PRET)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(BGRET, ME),	"Probability of BG ret to ME", 
				"Sheffield (WESDR XXII)", P_BGRET_ME, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_ME)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(BGRET, BLI),	"Probability of BG ret to blindness", 
				"Sheffield (WESDR XXII)", P_BGRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_BLI)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(PRET, BLI),	"Probability of Proliferative ret to blindness", 
				"Sheffield (WESDR XXII)", P_PRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_PRET_BLI)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ME, BLI),	"Probability of macular edema to blindness", 
				"Sheffield (WESDR XXII)", P_ME_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_ME_BLI)));			
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, BLI),	"Probability of healthy to blindness", 
				"Sheffield (WESDR XXII)", P_DNC_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_DNC_BLI)));			

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + BGRET, "Beta for background retinopathy", 
				"WESDR XXII, as adapted by Sheffield", BETA_BGRET));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + PRET, "Beta for proliferative retinopathy", 
				"WESDR XXII, as adapted by Sheffield", BETA_PRET));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME, "Beta for macular edema", 
				"WESDR XXII, as adapted by Sheffield", BETA_ME));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BGRET, "Cost of BGRET", "Original analysis", 2018, C_BGRET, SecondOrderParamsRepository.getRandomVariateForCost(C_BGRET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + PRET, "Cost of PRET", "Original analysis", 2018, C_PRET, SecondOrderParamsRepository.getRandomVariateForCost(C_PRET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ME, "Cost of ME", "Original analysis", 2018, C_ME, SecondOrderParamsRepository.getRandomVariateForCost(C_ME)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "Conget et al.", 2016, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		
		final double[] paramsDuPRET = SecondOrderParamsRepository.betaParametersFromNormal(DU_PRET[0], DU_PRET[1]);
		final double[] paramsDuME = SecondOrderParamsRepository.betaParametersFromNormal(DU_ME[0], DU_ME[1]);
		final double[] paramsDuBLI = SecondOrderParamsRepository.betaParametersFromNormal(DU_BLI[0], DU_BLI[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BGRET, "Disutility of BGRET", "", DU_BGRET));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + PRET, "Disutility of PRET", 
				"", DU_PRET[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuPRET[0], paramsDuPRET[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ME, "Disutility of RET", 
				"", DU_ME[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuME[0], paramsDuME[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BLI, "Disutility of BLI", 
				"", DU_BLI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuBLI[0], paramsDuBLI[1])));
		
		secParams.registerComplication(MainChronicComplications.RET);
		secParams.registerHealthStates(RETSubstates);		
	}
	
	@Override
	public T1DMProgression getNextComplication(T1DMPatient pat) {
		// Only schedules new events if the the patient has not suffered the complication yet, and the time of the event is lower
		// than the expected time to death and the previously computed (if any) time to the event
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			final TreeSet<T1DMComorbidity> state = pat.getDetailedState();
			// Checks whether there is somewhere to transit to
			if (!state.contains(BLI)) {
				long timeToBLI = Long.MAX_VALUE;
				long timeToBGRET = Long.MAX_VALUE;
				long timeToPRET = Long.MAX_VALUE;
				long timeToME = Long.MAX_VALUE;
				final long previousTimeToBGRET = pat.getTimeToChronicComorbidity(BGRET);
				final long previousTimeToPRET = pat.getTimeToChronicComorbidity(PRET);
				final long previousTimeToME = pat.getTimeToChronicComorbidity(ME);
				final long previousTimeToBLI = pat.getTimeToChronicComorbidity(BLI);
				long limit = pat.getTimeToDeath();
				if (limit > previousTimeToBLI)
					limit = previousTimeToBLI;
				// Already at ME: can progress to PRET (if not yet) and BLI
				if (state.contains(ME)) {
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.ME_BLI, limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					// Already at PRET and ME: calculate alternative time to BLI
					if (state.contains(PRET)) {
						final long altTimeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.PRET_BLI, limit);
						// If the time from PRET to BLI is lower than from ME to BLI, use the former
						if (timeToBLI > altTimeToBLI)
							timeToBLI = altTimeToBLI;
					}
					else {
						if (limit > previousTimeToPRET)
							limit = previousTimeToPRET;
						timeToPRET = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_PRET, limit);						
					}
				}
				// Already at PRET but not at ME: can progress to BLI and ME
				else if (state.contains(PRET)) {
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.PRET_BLI, limit);
					limit = min(limit, timeToBLI, previousTimeToME);
					timeToME = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_ME, limit);						
				}
				// Already at BGRET: can progress to BLI, ME and PRET
				else if (state.contains(BGRET)) {
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_BLI, limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					timeToPRET = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_PRET, (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
					timeToME = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_ME, (limit > previousTimeToME) ? previousTimeToME : limit);						
				}
				// Healthy: can progress to any state
				else {
					// RR from healthy to BLI
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_BLI, limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					timeToPRET = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_PRET, (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
					timeToME = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_ME, (limit > previousTimeToME) ? previousTimeToME : limit);						
					// Adjust limit for BGRET
					limit = min(limit, timeToPRET, timeToME, previousTimeToPRET, previousTimeToME, previousTimeToBGRET);
					timeToBGRET = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_BGRET, limit);
				}
				// Check previously scheduled events
				if (timeToBGRET != Long.MAX_VALUE) {
					if (previousTimeToBGRET < Long.MAX_VALUE) {
						prog.addCancelEvent(BGRET);
					}
					prog.addNewEvent(BGRET, timeToBGRET);
				}
				if (timeToPRET != Long.MAX_VALUE) {
					if (previousTimeToPRET < Long.MAX_VALUE) {
						prog.addCancelEvent(PRET);
					}
					prog.addNewEvent(PRET, timeToPRET);
					// If the new PRET event happens before a previously scheduled BGRET event, the latter must be cancelled 
					if (previousTimeToBGRET < Long.MAX_VALUE && timeToPRET < previousTimeToBGRET)
						prog.addCancelEvent(BGRET);
				}
				if (timeToME != Long.MAX_VALUE) {
					if (previousTimeToME < Long.MAX_VALUE) {
						prog.addCancelEvent(ME);
					}
					prog.addNewEvent(ME, timeToME);
					// If the new ME event happens before a previously scheduled BGRET event, the latter must be cancelled 
					if (previousTimeToBGRET < Long.MAX_VALUE && timeToME < previousTimeToBGRET)
						prog.addCancelEvent(BGRET);
				}
				if (timeToBLI != Long.MAX_VALUE) {
					if (previousTimeToBLI < Long.MAX_VALUE) {
						prog.addCancelEvent(BLI);
					}
					prog.addNewEvent(BLI, timeToBLI);
					// If the new BLI event happens before any previously scheduled RET event, the latter must be cancelled 
					if (previousTimeToBGRET < Long.MAX_VALUE && timeToBLI < previousTimeToBGRET)
						prog.addCancelEvent(BGRET);
					if (previousTimeToPRET < Long.MAX_VALUE && timeToBLI < previousTimeToPRET)
						prog.addCancelEvent(PRET);
					if (previousTimeToME < Long.MAX_VALUE && timeToBLI < previousTimeToME)
						prog.addCancelEvent(ME);
				}
			}
		}
		return prog;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, RETTransitions transition, long limit) {
		final int ord = BGRET.equals(transition.getTo()) ? 0 : (PRET.equals(transition.getTo()) ? 1 : (ME.equals(transition.getTo()) ? 2 : 3));
		return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
		
	}

	@Override
	public int getNSubstates() {
		return RETSubstates.length;
	}

	@Override
	public T1DMComorbidity[] getSubstates() {
		return RETSubstates;
	}

	@Override
	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		TreeSet<T1DMComorbidity> init = new TreeSet<>();
//		if (rndBGRETAtStart[pat.getIdentifier()] < 0.496183206) // (715 / 1441)
//			init.add(BGRET);
		return init;
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = 0.0;
		final Collection<T1DMComorbidity> state = pat.getDetailedState();

		if (state.contains(SheffieldRETSubmodel.BLI))
			cost += costBLI[0];
		else if (state.contains(SheffieldRETSubmodel.ME))
			cost += costME[0];
		else if (state.contains(SheffieldRETSubmodel.PRET))
			cost += costPRET[0];
		else if (state.contains(SheffieldRETSubmodel.BGRET))
			cost += costBGRET[0];				
		return cost;
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		return 0.0;
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		
		if (state.contains(BLI))
			return duBLI;
		if (state.contains(ME)) {
			if (state.contains(PRET)) {
				return method.combine(duME, duPRET);
			}
			else if (state.contains(BGRET)) {
				return method.combine(duME, duBGRET);				
			}
		}
		if (state.contains(PRET)) {
			return duPRET;
		}
		else if (state.contains(BGRET)) {
			return duBGRET;				
		}
		return 0.0;
	}
}
