/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
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
public class SimpleRETSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage RET = new DiabetesComplicationStage("RET", "Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage BLI = new DiabetesComplicationStage("BLI", "Blindness", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage[] RETSubstates = new DiabetesComplicationStage[] {RET, BLI};
	
	private static final double REF_HBA1C = 9.1; 
	private static final double P_RET_BLI = 0.0038;
	private static final double P_DNC_RET = 0.0013;
	private static final double[] CI_DNC_RET = {0.00104, 0.00156};
	private static final double[] CI_RET_BLI = {0.00304, 0.00456};
	private static final double C_RET = 3651.31;
	private static final double C_BLI = 469.22;
	private static final double TC_RET = 0.0;
	private static final double TC_BLI = 0.0;
	// Utility (avg, SD) from either Fenwick et al.; or Sullivan
	private static final double[] DU_RET = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.04, (0.066 - 0.014) / 3.92} : new double[] {0.0156, 0.0002};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_BLI = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[] {0.074, (0.124 - 0.025) / 3.92} : new double[] {0.0498, 0.0002};
	
	public enum RETTransitions {
		HEALTHY_RET,
		RET_BLI,
		HEALTHY_BLI;
	}

	public SimpleRETSubmodel() {
		super(DiabetesChronicComplications.RET, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_RET = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_RET, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_RET));
		final double[] paramsRET_BLI = SecondOrderParamsRepository.betaParametersFromNormal(P_RET_BLI, SecondOrderParamsRepository.sdFrom95CI(CI_RET_BLI));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, SimpleRETSubmodel.RET), "", 
				"", P_DNC_RET, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(SimpleRETSubmodel.RET, SimpleRETSubmodel.BLI), "", 
				"", P_RET_BLI, RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, SimpleRETSubmodel.BLI), "Probability of healthy to blindness", 
				"Sheffield (WESDR XXII)", 1.9e-6));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.29, 0.41}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + RET, "Cost of RET", "", 2015, C_RET, SecondOrderParamsRepository.getRandomVariateForCost(C_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "", 2015, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + RET, "Transition cost to RET", "", 2015, TC_RET, SecondOrderParamsRepository.getRandomVariateForCost(TC_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + BLI, "Transition cost to BLI", "", 2015, TC_BLI, SecondOrderParamsRepository.getRandomVariateForCost(TC_BLI)));
		
		final double[] paramsDuRET = SecondOrderParamsRepository.betaParametersFromNormal(DU_RET[0], DU_RET[1]);
		final double[] paramsDuBLI = SecondOrderParamsRepository.betaParametersFromNormal(DU_BLI[0], DU_BLI[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + RET, "Disutility of RET", 
				"", DU_RET[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuRET[0], paramsDuRET[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BLI, "Disutility of BLI", 
				"", DU_BLI[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuBLI[0], paramsDuBLI[1])));
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
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new SimpleRETSubmodelInstance(secParams);
	}

	public class SimpleRETSubmodelInstance extends ChronicComplicationSubmodel {
		private final double[] invProb;
		private final RRCalculator[] rr;
		private final double [][] rnd;
		private final double[] costRET;
		private final double[] costBLI;
		
		private final double duRET;
		private final double duBLI;
		/**
		 * 
		 */
		public SimpleRETSubmodelInstance(SecondOrderParamsRepository secParams) {
			super();
			
			invProb = new double[RETTransitions.values().length];
			invProb[SimpleRETSubmodel.RETTransitions.HEALTHY_RET.ordinal()] = -1 / secParams.getProbability(RET);
			invProb[SimpleRETSubmodel.RETTransitions.HEALTHY_BLI.ordinal()] = -1 / secParams.getProbability(BLI);
			invProb[SimpleRETSubmodel.RETTransitions.RET_BLI.ordinal()] = -1 / secParams.getProbability(RET, BLI);
			
			this.rr = new RRCalculator[RETTransitions.values().length];;
			rr[RETTransitions.HEALTHY_RET.ordinal()] = new HbA1c10ReductionComplicationRR(
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + RET.name()), REF_HBA1C);
			rr[RETTransitions.HEALTHY_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
			rr[RETTransitions.RET_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = secParams.getRngFirstOrder();
			rnd = new double[nPatients][RETSubstates.length];
			for (int i = 0; i < nPatients; i++) {
				for (int j = 0; j < RETSubstates.length; j++) {
					rnd[i][j] = rng.draw();
				}
			}
			
			costRET = secParams.getCostsForChronicComplication(RET);
			costBLI = secParams.getCostsForChronicComplication(BLI);

			duRET = secParams.getDisutilityForChronicComplication(RET);
			duBLI = secParams.getDisutilityForChronicComplication(BLI);
		}
		
		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(BLI)) {
					long timeToBLI = Long.MAX_VALUE;
					long timeToRET = Long.MAX_VALUE;
					final long previousTimeToRET = pat.getTimeToChronicComorbidity(RET);
					final long previousTimeToBLI = pat.getTimeToChronicComorbidity(BLI);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToBLI)
						limit = previousTimeToBLI;
					if (state.contains(RET)) {
						// RR from RET to BLI
						timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.RET_BLI, limit);
					}
					else {
						// RR from healthy to BLI
						timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_BLI, limit);
						if (limit > timeToBLI)
							limit = timeToBLI;
						if (limit > previousTimeToRET)
							limit = previousTimeToRET;
						// RR from healthy to RET (must be previous to BLI and a (potential) formerly scheduled RET event)
						timeToRET = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_RET, limit);
					}
					// Check previously scheduled events
					if (timeToRET != Long.MAX_VALUE) {
						if (previousTimeToRET < Long.MAX_VALUE) {
							prog.addCancelEvent(RET);
						}
						prog.addNewEvent(RET, timeToRET);
					}
					if (timeToBLI != Long.MAX_VALUE) {
						if (previousTimeToBLI < Long.MAX_VALUE) {
							prog.addCancelEvent(BLI);
						}
						prog.addNewEvent(BLI, timeToBLI);
						// If the new BLI event happens before a previously scheduled RET event, the latter must be cancelled 
						if (previousTimeToRET < Long.MAX_VALUE && timeToBLI < previousTimeToRET)
							prog.addCancelEvent(RET);
					}
				}
			}
			return prog;
		}

		private long getAnnualBasedTimeToEvent(DiabetesPatient pat, RETTransitions transition, long limit) {
			final int ord = RETTransitions.HEALTHY_RET.equals(transition) ? 0 : 1;
			return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
			
		}

		@Override
		public TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat) {
			return new TreeSet<>();
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(BLI))
				return costBLI[0];
			return costRET[0];
		}

		@Override
		public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent) {
			if (BLI.equals(newEvent))
				return costBLI[1];
			return costRET[1];
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(BLI))
				return duBLI;
			return duRET;
		}
	}
}
