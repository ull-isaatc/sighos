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
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SheffieldComplicationRR;
import es.ull.iis.util.Statistics;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleNEUSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage NEU = new DiabetesComplicationStage("NEU", "Neuropathy", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage LEA = new DiabetesComplicationStage("LEA", "Low extremity amputation", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage[] NEUSubstates = new DiabetesComplicationStage[] {NEU, LEA};
	
	private static final double P_DNC_NEU = 0.0354;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_DNC_LEA = 0.0003;
	private static final double[] CI_DNC_NEU = {0.020, 0.055}; // McQueen
	private static final double[] LIMITS_DNC_LEA = {0.0, 0.0006}; // Assumption
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double BETA_NEU = 5.3;
	private static final double C_NEU = 3108.86;
	private static final double C_LEA = 918.01;
	private static final double TC_NEU = 0.0;
	/** [Avg, SD] cost of amputation, from Spanish national tariffs */
	private static final double[] TC_LEA = {11333.04, 1674.37};
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_NEU = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.084, (0.111 - 0.057) / 3.92} : new double[]{0.0244, 0.00012};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_LEA = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.28, (0.389 - 0.17) / 3.92} : new double[]{(0.0379 + 0.0244) / 2, 0.0002};

	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
		HEALTHY_LEA;
	}

	public SimpleNEUSubmodel() {
		super(DiabetesChronicComplications.NEU, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		// From Sheffield
		final double[] paramsDNC_NEU = Statistics.betaParametersFromNormal(P_DNC_NEU, Statistics.sdFrom95CI(CI_DNC_NEU));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NEU), 
				"Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_NEU, "BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, LEA), 
				"Probability of healthy to PAD with amputation, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 
				P_DNC_LEA, "UniformVariate", LIMITS_DNC_LEA[0], LIMITS_DNC_LEA[1]));
		final double[] paramsNEU_LEA = Statistics.betaParametersFromNormal(P_NEU_LEA, Statistics.sdFrom95CI(CI_NEU_LEA));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NEU, LEA), 
				"Probability of clinically confirmed neuropathy to PAD with amputation", 
				"Klein et al. 2004 (also Sheffield)", 
				P_NEU_LEA, "BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1]));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU, "Beta for confirmed clinical neuropathy", 
		"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", BETA_NEU));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NEU.name(), 
				"Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + LEA.name(), 
				"Increased mortality risk due to peripheral neuropathy (amputation)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2015, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "del Pino et al", 2017, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2015, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		final double[] tcLEAParams = Statistics.gammaParametersFromNormal(TC_LEA[0], TC_LEA[1]); 
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", 
				"Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", 2017, 
				TC_LEA[0], RandomVariateFactory.getInstance("GammaVariate", tcLEAParams[0], tcLEAParams[1])));

		final double[] paramsDuNEU = Statistics.betaParametersFromNormal(DU_NEU[0], DU_NEU[1]);
		final double[] paramsDuLEA = Statistics.betaParametersFromNormal(DU_LEA[0], DU_LEA[1]);
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NEU, "Disutility of NEU", 
				"", DU_NEU[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuNEU[0], paramsDuNEU[1])));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + LEA, "Disutility of LEA", 
				"", DU_LEA[0], RandomVariateFactory.getInstance("BetaVariate", paramsDuLEA[0], paramsDuLEA[1])));

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public int getNStages() {
		return NEUSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return NEUSubstates;
	}
	
	@Override
	public int getNTransitions() {
		return NEUTransitions.values().length;
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
			super(SimpleNEUSubmodel.this);
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			addTime2Event(NEUTransitions.HEALTHY_NEU.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU), new SheffieldComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU))));
			addTime2Event(NEUTransitions.HEALTHY_LEA.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(LEA), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NEUTransitions.NEU_LEA.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU, LEA), SecondOrderParamsRepository.NO_RR));
			
			setStageInstance(NEU, secParams);
			setStageInstance(LEA, secParams);
		}
		
		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
			// Checks whether there is somewhere to transit to
			if (!state.contains(LEA)) {
				long timeToLEA = Long.MAX_VALUE;
				long timeToNEU = Long.MAX_VALUE;
				final long previousTimeToNEU = pat.getTimeToChronicComorbidity(NEU);
				final long previousTimeToLEA = pat.getTimeToChronicComorbidity(LEA);
				long limit = pat.getTimeToDeath();
				if (limit > previousTimeToLEA)
					limit = previousTimeToLEA;
				if (state.contains(NEU)) {
					// RR from NEU to LEA
					timeToLEA = getTimeToEvent(pat, NEUTransitions.NEU_LEA.ordinal(), limit);
				}
				else {
					// RR from healthy to LEA
					timeToLEA = getTimeToEvent(pat, NEUTransitions.HEALTHY_LEA.ordinal(), limit);
					if (limit > timeToLEA)
						limit = timeToLEA;
					if (limit > previousTimeToNEU)
						limit = previousTimeToNEU;
					// RR from healthy to NEU (must be previous to LEA and a (potential) formerly scheduled NEU event)
					timeToNEU = getTimeToEvent(pat, NEUTransitions.HEALTHY_NEU.ordinal(), limit);
				}
				// Check previously scheduled events
				if (timeToNEU != Long.MAX_VALUE) {
					if (previousTimeToNEU < Long.MAX_VALUE) {
						prog.addCancelEvent(NEU);
					}
					prog.addNewEvent(NEU, timeToNEU);
				}
				if (timeToLEA != Long.MAX_VALUE) {
					if (previousTimeToLEA < Long.MAX_VALUE) {
						prog.addCancelEvent(LEA);
					}
					prog.addNewEvent(LEA, timeToLEA);
					// If the new LEA event happens before a previously scheduled NEU event, the latter must be cancelled 
					if (previousTimeToNEU < Long.MAX_VALUE && timeToLEA < previousTimeToNEU)
						prog.addCancelEvent(NEU);
				}
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getCosts(LEA)[0];
			else if (state.contains(NEU))
				return getCosts(NEU)[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getDisutility(LEA);
			else if (state.contains(NEU))
				return getDisutility(NEU);
			return 0.0;
		}
	}
}
