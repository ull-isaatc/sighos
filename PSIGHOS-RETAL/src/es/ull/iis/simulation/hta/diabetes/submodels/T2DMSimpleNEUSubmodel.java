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
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPLinearRegressionRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * Based on the probabilities used in Hoerger et al. 2004 10.7326/0003-4819-140-9-200405040-00008 and in Goldman et al. 2018 10.1016/j.jamcollsurg.2018.09.021
 * @author Iván Castilla Rodríguez
 *
 */
public class T2DMSimpleNEUSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage NEU = new DiabetesComplicationStage("NEU", "Neuropathy", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage LEA = new DiabetesComplicationStage("LEA", "Low extremity amputation", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage[] NEUSubstates = new DiabetesComplicationStage[] {NEU, LEA};
	
	private static final double P_DNC_NEU = 0.036;
	private static final double[] CI_DNC_NEU = {0.020, 0.055}; // Assumed to  be equal to those used in McQueen

	/** Base probability of progressing from neuropathy to amputation, for a reference HbA1c level of 6.5%.
	 * Based on Goldman et al. 2018 10.1016/j.jamcollsurg.2018.09.021 */
	private static final double P_NEU_LEA = 0.000822179; // For 6.5%
	/** Beta parameters for second order variability of the probabilty. Assuming 572 patients (the last data in the Figure where this information
	 * is extracted from. */
	private static final double[] BETA_PARAM_NEU_LEA = {0.46946398, 570.530536}; 
	
	/** Coefficients [A, B] for a linear function A*HbA1c + B of the RR of progressing from neuropathy to amputation according the Hba1c level of the patient  
	 * Based on Goldman et al. 2018 10.1016/j.jamcollsurg.2018.09.021 */
	private static final double[] F_RR_NEU_LEA_HBA1C = {93.54062163, -5.111865917};
	// TODO: Add array-kind second order parameters, and add second order variation using BivariateNormalVariate function.
	/** Standard errors and correlation coefficient for the linear function defined with {@link T2DMSimpleNEUSubmodel.F_RR_NEU_LEA_HBA1C} */  
	private static final double[] OTHER_F_RR_NEU_LEA_HBA1C = {5.495019757, 0.414561519, 0.998278981};

	
	private static final double C_NEU = 3108.86;
	private static final double C_LEA = 918.01;
	private static final double TC_NEU = 0.0;
	/** [Avg, SD] cost of amputation, from Spanish national tariffs */
	private static final double[] TC_LEA = {11333.04, 1674.37};
	// Utility (avg, SD) from either Bagust and Beale; or Sullivan
	private static final double[] DU_NEU = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.084, (0.111 - 0.057) / 3.92} : new double[]{0.0244, 0.00012};
	// Utility (avg, SD) from either Clarke et al.; or Sullivan
	private static final double[] DU_LEA = BasicConfigParams.USE_REVIEW_UTILITIES ? new double[]{0.28, (0.389 - 0.17) / 3.92} : new double[]{(0.0379 + 0.0244) / 2, 0.0002};

	private static final String STR_SOURCE_HOERGER = "Hoerger et al. 2004 10.7326/0003-4819-140-9-200405040-00008";
	private static final String STR_SOURCE_GOLDMAN = "Goldman et al. 2018 10.1016/j.jamcollsurg.2018.09.021";
	
	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA;
	}

	public T2DMSimpleNEUSubmodel() {
		super(DiabetesChronicComplications.NEU, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_NEU = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_NEU, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_NEU));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NEU), 
				"Probability of healthy to clinically confirmed neuropathy, as used in the Hoerger et al. model", 
				STR_SOURCE_HOERGER, 
				P_DNC_NEU, "BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1]));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NEU, LEA), 
				"Probability of clinically confirmed neuropathy to PAD with amputation", 
				STR_SOURCE_GOLDMAN, 
				P_NEU_LEA, "BetaVariate", BETA_PARAM_NEU_LEA[0], BETA_PARAM_NEU_LEA[1]));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + NEU.name(), 
				"Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				1.31, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.31, 1.09, 1.59, 1)));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_IMR_PREFIX + LEA.name(), 
				"Increased mortality risk due to peripheral neuropathy (amputation)", 
				"https://doi.org/10.2337/diacare.28.3.617", 
				2.25, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.25, 1.60, 3.15, 1)));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2015, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "del Pino et al", 2017, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2015, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		final double[] tcLEAParams = SecondOrderParamsRepository.gammaParametersFromNormal(TC_LEA[0], TC_LEA[1]); 
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", 
				"Spanish tariffs: Cantabria; Cataluña; Madrid; Murcia; Navarra; País Vasco", 2017, 
				TC_LEA[0], RandomVariateFactory.getInstance("GammaVariate", tcLEAParams[0], tcLEAParams[1])));

		final double[] paramsDuNEU = SecondOrderParamsRepository.betaParametersFromNormal(DU_NEU[0], DU_NEU[1]);
		final double[] paramsDuLEA = SecondOrderParamsRepository.betaParametersFromNormal(DU_LEA[0], DU_LEA[1]);
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
		return new Instance(secParams);
	}

	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(T2DMSimpleNEUSubmodel.this);
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			addTime2Event(NEUTransitions.HEALTHY_NEU.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU), SecondOrderParamsRepository.NO_RR));
			addTime2Event(NEUTransitions.NEU_LEA.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU, LEA), new HbA1c1PPLinearRegressionRR(F_RR_NEU_LEA_HBA1C)));
			
			addData(secParams, NEU);
			addData(secParams, LEA);
		}
		
		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
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
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getData(LEA).getCosts()[0];
			else if (state.contains(NEU))
				return getData(NEU).getCosts()[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getData(LEA).getDisutility();
			else if (state.contains(NEU))
				return getData(NEU).getDisutility();
			return 0.0;
		}
	}
}
