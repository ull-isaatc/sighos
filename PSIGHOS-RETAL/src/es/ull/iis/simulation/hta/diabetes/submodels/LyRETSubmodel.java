/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.Named;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AgeRelatedRR;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.CompoundRRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.DurationOfDiabetesBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LyRETSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage BGRET = new DiabetesComplicationStage("BGRET", "Background Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage PRET = new DiabetesComplicationStage("PRET", "Proliferative Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage ME = new DiabetesComplicationStage("ME", "Macular edema", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage BLI = new DiabetesComplicationStage("BLI", "Blindness", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage[] STAGES = new DiabetesComplicationStage[] {BGRET, PRET, ME, BLI};
	
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
	
	public enum RETTransitions implements Named {
		HEALTHY_BGRET(null, BGRET),
		HEALTHY_ME(null, ME),
		BGRET_PRET(BGRET, PRET),
		BGRET_BLI(BGRET, BLI),
		PRET_BLI(PRET, BLI),
		ME_BLI(ME, BLI);
		
		final private DiabetesComplicationStage from;
		final private DiabetesComplicationStage to;
		
		private RETTransitions(DiabetesComplicationStage from, DiabetesComplicationStage to) {
			this.from = from;
			this.to = to;
		}

		/**
		 * @return the from
		 */
		public DiabetesComplicationStage getFrom() {
			return from;
		}

		/**
		 * @return the to
		 */
		public DiabetesComplicationStage getTo() {
			return to;
		}
	}
	
	/**
	 * 
	 */
	public LyRETSubmodel() {
		super(DiabetesChronicComplications.RET, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
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

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public int getNStages() {
		return STAGES.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return STAGES;
	}

	@Override
	public int getNTransitions() {
		return RETTransitions.values().length;
	}

	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledChronicComplicationInstance(this);
	}

	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(LyRETSubmodel.this);
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			final RRCalculator rrBGRET = new CompoundRRCalculator(new RRCalculator[] 
					{new HbA1c10ReductionComplicationRR(RR_BGRET, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
					CompoundRRCalculator.DEF_COMBINATION.ADD);
			final RRCalculator rrPRET = new CompoundRRCalculator(new RRCalculator[] 
					{new HbA1c10ReductionComplicationRR(RR_PRET, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
					CompoundRRCalculator.DEF_COMBINATION.ADD);
			final RRCalculator rrME = new CompoundRRCalculator(new RRCalculator[]
					{new HbA1c10ReductionComplicationRR(RR_ME, REF_DCCT_HBA1C), new AgeRelatedRR(RR_ADOLESCENCE)},
					CompoundRRCalculator.DEF_COMBINATION.ADD);

			addTime2Event(RETTransitions.HEALTHY_BGRET.ordinal(), 
					new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_DNC_BGRET, rrBGRET));
			addTime2Event(RETTransitions.BGRET_PRET.ordinal(),
					new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_BGRET_PRET, rrPRET));
			addTime2Event(RETTransitions.HEALTHY_ME.ordinal(),
					new DurationOfDiabetesBasedTimeToEventParam(rng, nPatients, P_DNC_ME, rrME));
			addTime2Event(RETTransitions.BGRET_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET, BLI), SecondOrderParamsRepository.NO_RR));
			addTime2Event(RETTransitions.PRET_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(PRET, BLI), SecondOrderParamsRepository.NO_RR));
			addTime2Event(RETTransitions.ME_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(ME, BLI), SecondOrderParamsRepository.NO_RR));

			setStageInstance(BGRET, secParams);
			setStageInstance(PRET, secParams);
			setStageInstance(ME, secParams);
			setStageInstance(BLI, secParams);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			// Only schedules new events if the the patient has not suffered the complication yet, and the time of the event is lower
			// than the expected time to death and the previously computed (if any) time to the event
			final DiabetesProgression prog = new DiabetesProgression();
			final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
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
					timeToBLI = getTimeToEvent(pat, RETTransitions.ME_BLI.ordinal(), limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					// Already at PRET and ME: calculate alternative time to BLI
					if (state.contains(PRET)) {
						final long altTimeToBLI = getTimeToEvent(pat, RETTransitions.PRET_BLI.ordinal(), limit);
						// If the time from PRET to BLI is lower than from ME to BLI, use the former
						if (timeToBLI > altTimeToBLI)
							timeToBLI = altTimeToBLI;
					}
					else {
						if (limit > previousTimeToPRET)
							limit = previousTimeToPRET;
						timeToPRET = getTimeToEvent(pat, RETTransitions.BGRET_PRET.ordinal(), limit);						
					}
				}
				// Already at PRET but not at ME: can progress to BLI
				else if (state.contains(PRET)) {
					timeToBLI = getTimeToEvent(pat, RETTransitions.PRET_BLI.ordinal(), limit);
				}
				// Already at BGRET: can progress to BLI, ME and PRET
				else if (state.contains(BGRET)) {
					timeToBLI = getTimeToEvent(pat, RETTransitions.BGRET_BLI.ordinal(), limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
					timeToPRET = getTimeToEvent(pat, RETTransitions.BGRET_PRET.ordinal(), (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
				}
				// Healthy: can progress to BGRET or ME
				else {
					timeToME = getTimeToEvent(pat, RETTransitions.HEALTHY_ME.ordinal(), (limit > previousTimeToME) ? previousTimeToME : limit);						
					// Adjust limit for BGRET
					limit = min(limit, timeToME, previousTimeToME, previousTimeToBGRET);
					timeToBGRET = getTimeToEvent(pat, RETTransitions.HEALTHY_BGRET.ordinal(), limit);
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
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			double cost = 0.0;
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();

			if (state.contains(LyRETSubmodel.BLI))
				cost += getCosts(BLI)[0];
			else if (state.contains(LyRETSubmodel.ME))
				cost += getCosts(ME)[0];
			else if (state.contains(LyRETSubmodel.PRET))
				cost += getCosts(PRET)[0];
			else if (state.contains(LyRETSubmodel.BGRET))
				cost += getCosts(BGRET)[0];				
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			
			if (state.contains(BLI))
				return getDisutility(BLI);
			if (state.contains(ME)) {
				if (state.contains(PRET)) {
					return method.combine(getDisutility(ME), getDisutility(PRET));
				}
				else if (state.contains(BGRET)) {
					return method.combine(getDisutility(ME), getDisutility(BGRET));				
				}
			}
			if (state.contains(PRET)) {
				return getDisutility(PRET);
			}
			else if (state.contains(BGRET)) {
				return getDisutility(BGRET);				
			}
			return 0.0;
		}

		
	}
}
