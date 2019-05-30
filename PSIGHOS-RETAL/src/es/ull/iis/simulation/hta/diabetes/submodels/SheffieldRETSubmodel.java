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
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SheffieldComplicationRR;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SheffieldRETSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage BGRET = new DiabetesComplicationStage("BGRET", "Background Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage PRET = new DiabetesComplicationStage("PRET", "Proliferative Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage ME = new DiabetesComplicationStage("ME", "Macular edema", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage BLI = new DiabetesComplicationStage("BLI", "Blindness", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage[] RETSubstates = new DiabetesComplicationStage[] {BGRET, PRET, ME, BLI};
	
	private static final double P_DNC_BGRET = 0.0454 * BasicConfigParams.CALIBRATION_COEF_BGRET;
	private static final double P_DNC_PRET = 0.0013;
	private static final double P_DNC_ME = 0.0012;
	private static final double P_BGRET_PRET = 0.0595;
	private static final double P_BGRET_ME = 0.0512;
	private static final double P_BGRET_BLI = 0.0001;
	private static final double P_PRET_BLI = 0.0038;
	private static final double P_ME_BLI = 0.0016;
	private static final double P_DNC_BLI = 1.9e-6;
	private static final double BETA_BGRET = 10.10 * BasicConfigParams.CALIBRATION_COEF_BETA_BGRET;
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
	
	public SheffieldRETSubmodel() {
		super(DiabetesChronicComplications.RET, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public int getNStages() {
		return RETSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return RETSubstates;
	}

	@Override
	public int getNTransitions() {
		return RETTransitions.values().length;
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
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

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(SheffieldRETSubmodel.this);
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = secParams.getRngFirstOrder();
			
			final RRCalculator rrBGRET = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + BGRET));
			final RRCalculator rrPRET = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + PRET));
			final RRCalculator rrME = new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + ME));

			addTime2Event(RETTransitions.HEALTHY_BGRET.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET), rrBGRET));
			addTime2Event(RETTransitions.HEALTHY_PRET.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(PRET), rrPRET));
			addTime2Event(RETTransitions.HEALTHY_ME.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(ME), rrME));
			addTime2Event(RETTransitions.HEALTHY_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BLI), SecondOrderParamsRepository.NO_RR));
			addTime2Event(RETTransitions.BGRET_PRET.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET, PRET), rrPRET));
			addTime2Event(RETTransitions.BGRET_ME.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET, ME), rrME));
			addTime2Event(RETTransitions.BGRET_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(BGRET, BLI), SecondOrderParamsRepository.NO_RR));
			addTime2Event(RETTransitions.PRET_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(PRET, BLI), SecondOrderParamsRepository.NO_RR));
			addTime2Event(RETTransitions.ME_BLI.ordinal(),
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(ME, BLI), SecondOrderParamsRepository.NO_RR));
			
			addData(secParams, BGRET);
			addData(secParams, PRET);
			addData(secParams, ME);
			addData(secParams, BLI);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			// Only schedules new events if the the patient has not suffered the complication yet, and the time of the event is lower
			// than the expected time to death and the previously computed (if any) time to the event
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
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
					// Already at PRET but not at ME: can progress to BLI and ME
					else if (state.contains(PRET)) {
						timeToBLI = getTimeToEvent(pat, RETTransitions.PRET_BLI.ordinal(), limit);
						limit = min(limit, timeToBLI, previousTimeToME);
						timeToME = getTimeToEvent(pat, RETTransitions.BGRET_ME.ordinal(), limit);						
					}
					// Already at BGRET: can progress to BLI, ME and PRET
					else if (state.contains(BGRET)) {
						timeToBLI = getTimeToEvent(pat, RETTransitions.BGRET_BLI.ordinal(), limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						timeToPRET = getTimeToEvent(pat, RETTransitions.BGRET_PRET.ordinal(), (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
						timeToME = getTimeToEvent(pat, RETTransitions.BGRET_ME.ordinal(), (limit > previousTimeToME) ? previousTimeToME : limit);						
					}
					// Healthy: can progress to any state
					else {
						// RR from healthy to BLI
						timeToBLI = getTimeToEvent(pat, RETTransitions.HEALTHY_BLI.ordinal(), limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						timeToPRET = getTimeToEvent(pat, RETTransitions.HEALTHY_PRET.ordinal(), (limit > previousTimeToPRET) ? previousTimeToPRET : limit);
						timeToME = getTimeToEvent(pat, RETTransitions.HEALTHY_ME.ordinal(), (limit > previousTimeToME) ? previousTimeToME : limit);						
						// Adjust limit for BGRET
						limit = min(limit, timeToPRET, timeToME, previousTimeToPRET, previousTimeToME, previousTimeToBGRET);
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
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			double cost = 0.0;
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();

			if (state.contains(LyRETSubmodel.BLI))
				cost += getData(BLI).getCosts()[0];
			else if (state.contains(LyRETSubmodel.ME))
				cost += getData(ME).getCosts()[0];
			else if (state.contains(LyRETSubmodel.PRET))
				cost += getData(PRET).getCosts()[0];
			else if (state.contains(LyRETSubmodel.BGRET))
				cost += getData(BGRET).getCosts()[0];				
			return cost;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			
			if (state.contains(BLI))
				return getData(BLI).getDisutility();
			if (state.contains(ME)) {
				if (state.contains(PRET)) {
					return method.combine(getData(ME).getDisutility(), getData(PRET).getDisutility());
				}
				else if (state.contains(BGRET)) {
					return method.combine(getData(ME).getDisutility(), getData(BGRET).getDisutility());				
				}
			}
			if (state.contains(PRET)) {
				return getData(PRET).getDisutility();
			}
			else if (state.contains(BGRET)) {
				return getData(BGRET).getDisutility();				
			}
			return 0.0;
		}
	}
}
