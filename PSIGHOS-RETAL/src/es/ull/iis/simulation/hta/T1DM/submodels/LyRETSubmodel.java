/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.T1DM.params.AgeRelatedRR;
import es.ull.iis.simulation.hta.T1DM.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CompoundRRCalculator;
import es.ull.iis.simulation.hta.T1DM.params.DurationOfDiabetesBasedTimeToEventParam;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.RRCalculator;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.TimeToEventParam;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LyRETSubmodel extends ChronicComplicationSubmodel {
	public static T1DMComplicationStage BGRET = new T1DMComplicationStage("BGRET", "Background Retinopathy", T1DMChronicComplications.RET);
	public static T1DMComplicationStage PRET = new T1DMComplicationStage("PRET", "Proliferative Retinopathy", T1DMChronicComplications.RET);
	public static T1DMComplicationStage ME = new T1DMComplicationStage("ME", "Macular edema", T1DMChronicComplications.RET);
	public static T1DMComplicationStage BLI = new T1DMComplicationStage("BLI", "Blindness", T1DMChronicComplications.RET);
	public static T1DMComplicationStage[] RETSubstates = new T1DMComplicationStage[] {BGRET, PRET, ME, BLI};
	
	private static final double REF_DCCT_HBA1C = 9.1;
	/** Probability of onset of BGRET depending on duration of diabetes {0, 9} */
	private static final double[][] P_DNC_BGRET = {{9, 0.012}, {Double.MAX_VALUE, 0.037}};
	/** Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC */
	private static final double RR_BGRET = 0.34;
	
	private static final double[][] P_BGRET_PRET = {{9, 0.0}, {Double.MAX_VALUE, 0.011}};
	/** Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC */
	private static final double RR_PRET = 0.37;

	private static final double[][] P_DNC_ME = {{9, 0.0}, {Double.MAX_VALUE, 0.02}};
	/** Risk reduction for a relative 10% lower HbA1c versus comparator in EDIC */
	private static final double RR_ME = 0.13;
	
	private static final double P_BGRET_BLI = 0.2 * 0.0148; //0.0001;
	private static final double P_PRET_BLI = 0.0798; // 0.0284;
	private static final double P_ME_BLI = 0.0016;
	
	private static final double[][] RR_ADOLESCENCE = {{12, 1.0}, {18, 1.7}, {Double.MAX_VALUE, 1.0}};
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
		HEALTHY_ME(null, ME),
		BGRET_PRET(BGRET, PRET),
		BGRET_BLI(BGRET, BLI),
		PRET_BLI(PRET, BLI),
		ME_BLI(ME, BLI);
		
		final private T1DMComplicationStage from;
		final private T1DMComplicationStage to;
		private TimeToEventParam time2Event;
		
		private RETTransitions(T1DMComplicationStage from, T1DMComplicationStage to) {
			this.from = from;
			this.to = to;
		}

		/**
		 * @return the from
		 */
		public T1DMComplicationStage getFrom() {
			return from;
		}

		/**
		 * @return the to
		 */
		public T1DMComplicationStage getTo() {
			return to;
		}

		public TimeToEventParam getTime2Event() {
			return time2Event;
		}

		public void setTime2Event(TimeToEventParam time2Event) {
			this.time2Event = time2Event;
		}
	}
	
	private final double pInitBGRET;
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
	public LyRETSubmodel(SecondOrderParamsRepository secParams) {
		super();
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();

		final RRCalculator rrBGRET = new CompoundRRCalculator(new RRCalculator[] 
				{new HbA1c10ReductionComplicationRR(RR_BGRET, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
				CompoundRRCalculator.DEF_COMBINATION.ADD);
		final RRCalculator rrPRET = new CompoundRRCalculator(new RRCalculator[] 
				{new HbA1c10ReductionComplicationRR(RR_PRET, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
				CompoundRRCalculator.DEF_COMBINATION.ADD);
		final RRCalculator rrME = new CompoundRRCalculator(new RRCalculator[]
				{new HbA1c10ReductionComplicationRR(RR_ME, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
				CompoundRRCalculator.DEF_COMBINATION.ADD);

		RETTransitions.HEALTHY_BGRET.setTime2Event(
				new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_DNC_BGRET, rrBGRET));
		RETTransitions.BGRET_PRET.setTime2Event(
				new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_BGRET_PRET, rrPRET));
		RETTransitions.HEALTHY_ME.setTime2Event(
				new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_DNC_ME, rrME));
		RETTransitions.BGRET_BLI.setTime2Event(
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET, BLI), SecondOrderParamsRepository.NO_RR));
		RETTransitions.PRET_BLI.setTime2Event(
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(PRET, BLI), SecondOrderParamsRepository.NO_RR));
		RETTransitions.ME_BLI.setTime2Event(
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(ME, BLI), SecondOrderParamsRepository.NO_RR));

		rndBGRETAtStart = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rndBGRETAtStart[i] = rng.draw();
		}
		
		costBGRET = secParams.getCostsForChronicComplication(BGRET);
		costPRET = secParams.getCostsForChronicComplication(PRET);
		costME = secParams.getCostsForChronicComplication(ME);
		costBLI = secParams.getCostsForChronicComplication(BLI);
		
		duBGRET = secParams.getDisutilityForChronicComplication(BGRET);
		duPRET = secParams.getDisutilityForChronicComplication(PRET);
		duME = secParams.getDisutilityForChronicComplication(ME);
		duBLI = secParams.getDisutilityForChronicComplication(BLI);
		
		if (BasicConfigParams.INIT_PROP.containsKey(BGRET.name())) {
			pInitBGRET = BasicConfigParams.INIT_PROP.get(BGRET.name());
		}
		else {
			pInitBGRET = 0.0;
		}

	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(BGRET, BLI),	"Probability of BG ret to blindness", 
				"Sheffield (WESDR XXII)", P_BGRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_BGRET_BLI)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(PRET, BLI),	"Probability of Proliferative ret to blindness", 
				"Sheffield (WESDR XXII)", P_PRET_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_PRET_BLI)));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(ME, BLI),	"Probability of macular edema to blindness", 
				"Sheffield (WESDR XXII)", P_ME_BLI, SecondOrderParamsRepository.getRandomVariateForProbability(P_ME_BLI)));			

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
		
		secParams.registerComplication(T1DMChronicComplications.RET);
		secParams.registerComplicationStages(RETSubstates);		
	}
	
	@Override
	public T1DMProgression getProgression(T1DMPatient pat) {
		// Only schedules new events if the the patient has not suffered the complication yet, and the time of the event is lower
		// than the expected time to death and the previously computed (if any) time to the event
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			final TreeSet<T1DMComplicationStage> state = pat.getDetailedState();
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
				// Already at PRET but not at ME: can progress to BLI
				else if (state.contains(PRET)) {
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.PRET_BLI, limit);
				}
				// Already at BGRET: can progress to BLI, ME and PRET
				else if (state.contains(BGRET)) {
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_BLI, limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					timeToPRET = getAnnualBasedTimeToEvent(pat, RETTransitions.BGRET_PRET, (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
				}
				// Healthy: can progress to BGRET or ME
				else {
					timeToME = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_ME, (limit > previousTimeToME) ? previousTimeToME : limit);						
					// Adjust limit for BGRET
					limit = min(limit, timeToME, previousTimeToME, previousTimeToBGRET);
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
		final long time = transition.getTime2Event().getValue(pat);
		return (time >= limit) ? Long.MAX_VALUE : time;
	}

	@Override
	public int getNStages() {
		return RETSubstates.length;
	}

	@Override
	public T1DMComplicationStage[] getStages() {
		return RETSubstates;
	}

	@Override
	public TreeSet<T1DMComplicationStage> getInitialStage(T1DMPatient pat) {
		TreeSet<T1DMComplicationStage> init = new TreeSet<>();
		if (rndBGRETAtStart[pat.getIdentifier()] < pInitBGRET)
			init.add(BGRET);
		return init;
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		double cost = 0.0;
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();

		if (state.contains(LyRETSubmodel.BLI))
			cost += costBLI[0];
		else if (state.contains(LyRETSubmodel.ME))
			cost += costME[0];
		else if (state.contains(LyRETSubmodel.PRET))
			cost += costPRET[0];
		else if (state.contains(LyRETSubmodel.BGRET))
			cost += costBGRET[0];				
		return cost;
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComplicationStage newEvent) {
		return 0.0;
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();
		
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
