/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UnconsciousSecondOrderParams extends SecondOrderParams {
//	public enum Population {
//		UNCONTROLLED(27, 8.3, 1.0, "https://doi.org/10.1056/NEJMoa1002853"),
//		UNCONSCIOUS(18.6, 7.5, 0.0, "https://doi.org/10.1016/j.endinu.2018.03.008");
//		
//		private Population(double baselineAge, double baselineHbA1c, double hba1cAfterIntervention, String source) {
//		}
//	}
	/** Duration of effect of the intervention (supposed as in Canada) */
	private static final double YEARS_OF_EFFECT = 1.0;
	private static final String STR_SENSOR_ADHERENCE_LOWER_LIMIT = "SENSOR_ADHERENCE LOWER LIMIT";
	private static final String STR_SENSOR_ADHERENCE_UPPER_LIMIT = "SENSOR_ADHERENCE UPPER LIMIT";
	private static final String STR_AVG_HBA1C_AFTER = "AVG_HBA1C_AFTER_";

	// All the probabilities should be related to the reference HbA1c level
	private static final double P_DNC_RET = 0.0013;
	private static final double P_DNC_NEU = 0.0354; // Sheffield (DCCT, Moss et al)
	private static final double P_DNC_NPH = 0.072;
	private static final double P_DNC_CHD = 0.031;
	private static final double P_NEU_CHD = 0.029;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_CHD = 0.022;
	private static final double P_NPH_ESRD = 0.0133;
	private static final double P_RET_BLI = 0.0038;
	private static final double P_RET_CHD = 0.028;
	
	private static final double[] CI_DNC_RET = {0.00104, 0.00156};
	private static final double[] CI_DNC_NEU = {0.02, 0.055};
	private static final double[] CI_DNC_NPH = {0.041, 0.112};
	private static final double[] CI_DNC_CHD = {0.018, 0.048};
	private static final double[] CI_NEU_CHD = {0.016, 0.044};
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double[] CI_NPH_CHD = {0.013, 0.034};
	private static final double[] CI_NPH_ESRD = {0.01064, 0.01596};
	private static final double[] CI_RET_BLI = {0.00304, 0.00456};
	private static final double[] CI_RET_CHD = {0.016, 0.043};
	
	private static final double C_DNC = 2174.11;
	private static final double C_CHD = 1112.24;
	private static final double C_NEU = 3108.86;
	private static final double C_NPH = 5180.26;
	private static final double C_RET = 3651.31;
	private static final double C_LEA = 9305.74;
	private static final double C_ESRD = 34259.48;
	private static final double C_BLI = 469.22;
	private static final double TC_CHD = 12082.36;
	private static final double TC_NEU = 0.0;
	private static final double TC_NPH = 33183.74;
	private static final double TC_RET = 0.0;
	private static final double TC_LEA = 11966.18;
	private static final double TC_ESRD = 3250.73;
	private static final double TC_BLI = 0.0;

	private static final double U_GENERAL_POP = 0.911400915;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final double DU_DNC = 0.0351;
	private static final double DU_CHD = (0.0409 + 0.0412) / 2;
	private static final double DU_NEU = 0.0244;
	private static final double DU_NPH = 0.0527;
	private static final double DU_RET = 0.0156;
	private static final double DU_LEA = (0.0379 + 0.0244) / 2;
	private static final double DU_ESRD = 0.0603;
	private static final double DU_BLI = 0.0498;

	/**
	 * @param baseCase
	 */
	public UnconsciousSecondOrderParams(boolean baseCase) {
		super(baseCase);
		final double[] paramsDNC_RET = betaParametersFromNormal(P_DNC_RET, sdFrom95CI(CI_DNC_RET));
		final double[] paramsDNC_NEU = betaParametersFromNormal(P_DNC_NEU, sdFrom95CI(CI_DNC_NEU));
		final double[] paramsDNC_NPH = betaParametersFromNormal(P_DNC_NPH, sdFrom95CI(CI_DNC_NPH));
		final double[] paramsDNC_CHD = betaParametersFromNormal(P_DNC_CHD, sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = betaParametersFromNormal(P_NEU_CHD, sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNEU_LEA = betaParametersFromNormal(P_NEU_LEA, sdFrom95CI(CI_NEU_LEA));
		final double[] paramsNEU_NPH = betaParametersFromNormal(P_NEU_NPH, sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_CHD = betaParametersFromNormal(P_NPH_CHD, sdFrom95CI(CI_NPH_CHD));
		final double[] paramsNPH_ESRD = betaParametersFromNormal(P_NPH_ESRD, sdFrom95CI(CI_NPH_ESRD));
		final double[] paramsRET_BLI = betaParametersFromNormal(P_RET_BLI, sdFrom95CI(CI_RET_BLI));
		final double[] paramsRET_CHD = betaParametersFromNormal(P_RET_CHD, sdFrom95CI(CI_RET_CHD));

		addProbParam(new SecondOrderParam(STR_P_DNC_RET, STR_P_DNC_RET, "", P_DNC_RET, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		addProbParam(new SecondOrderParam(STR_P_DNC_NEU, "Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", "DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1])));
		addProbParam(new SecondOrderParam(STR_P_DNC_NPH, STR_P_DNC_NPH, "", P_DNC_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NPH[0], paramsDNC_NPH[1])));
		addProbParam(new SecondOrderParam(STR_P_DNC_CHD, STR_P_DNC_CHD, "", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_CHD, STR_P_NEU_CHD, "", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_LEA, STR_P_NEU_LEA, "Klein et al. 2004", P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_NPH, STR_P_NEU_NPH, "", P_NEU_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1])));
		addProbParam(new SecondOrderParam(STR_P_NPH_CHD, STR_P_NPH_CHD, "", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NPH_ESRD, STR_P_NPH_ESRD, "", P_NPH_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_ESRD[0], paramsNPH_ESRD[1])));
		addProbParam(new SecondOrderParam(STR_P_RET_BLI, STR_P_RET_BLI, "", P_RET_BLI, RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		addProbParam(new SecondOrderParam(STR_P_RET_CHD, STR_P_RET_CHD, "", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));

		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", "Ly et al.", 0.234286582, RandomVariateFactory.getInstance("BetaVariate", 23.19437163, 75.80562837)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", "Ly et al.", 0.020895447, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", -3.868224010, 1.421931924))));

		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.CHD.name(), STR_RR_PREFIX + Complication.CHD.name(), "Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 1.15, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.15, 0.92, 1.43, 1)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NPH.name(), "%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, sdFrom95CI(new double[] {0.19, 0.32}))));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NEU.name(), "%risk reducion for combined groups for confirmed clinical neuropathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, sdFrom95CI(new double[] {0.18, 0.40}))));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, sdFrom95CI(new double[] {0.29, 0.41}))));
		addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
		
		addOtherParam(new SecondOrderParam(STR_IMR_DNC, "Increased mortaility risk due to T1 DM with no complications", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_RET, "Increased mortality risk due to retinopathy", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_NEU, "Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", "https://doi.org/10.2337/diacare.28.3.617", 1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_NPH, "Increased mortality risk due to severe proteinuria", "https://doi.org/10.2337/diacare.28.3.617", 2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_CHD, "Increased mortality risk due to macrovascular disease", "https://doi.org/10.2337/diacare.28.3.617", 1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_ESRD, "Increased mortality risk due to increased serum creatinine", "https://doi.org/10.2337/diacare.28.3.617", 4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_BLI, "Increased mortality risk due to blindness", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_LEA, "Increased mortality risk due to peripheral neuropathy (amputation)", "https://doi.org/10.2337/diacare.28.3.617", 3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));

		addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "https://doi.org/10.1007/s13300-017-0285-0", 2017, 716.82, getRandomVariateForCost(716.82)));
		addCostParam(new SecondOrderCostParam(STR_COST_DNC, "Cost of DNC", "", 2015, C_DNC, getRandomVariateForCost(C_DNC)));
		addCostParam(new SecondOrderCostParam(STR_COST_CHD, "Cost of CHD", "", 2015, C_CHD, getRandomVariateForCost(C_CHD)));
		addCostParam(new SecondOrderCostParam(STR_COST_NEU, "Cost of NEU", "", 2015, C_NEU, getRandomVariateForCost(C_NEU)));
		addCostParam(new SecondOrderCostParam(STR_COST_NPH, "Cost of NPH", "", 2015, C_NPH, getRandomVariateForCost(C_NPH)));
		addCostParam(new SecondOrderCostParam(STR_COST_RET, "Cost of RET", "", 2015, C_RET, getRandomVariateForCost(C_RET)));
		addCostParam(new SecondOrderCostParam(STR_COST_LEA, "Cost of LEA", "", 2015, C_LEA, getRandomVariateForCost(C_LEA)));
		addCostParam(new SecondOrderCostParam(STR_COST_ESRD, "Cost of ESRD", "", 2015, C_ESRD, getRandomVariateForCost(C_ESRD)));
		addCostParam(new SecondOrderCostParam(STR_COST_BLI, "Cost of BLI", "", 2015, C_BLI, getRandomVariateForCost(C_BLI)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_CHD, "Transition cost to CHD", "", 2015, TC_CHD, getRandomVariateForCost(TC_CHD)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_NEU, "Transition cost to NEU", "", 2015, TC_NEU, getRandomVariateForCost(TC_NEU)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_NPH, "Transition cost to NPH", "", 2015, TC_NPH, getRandomVariateForCost(TC_NPH)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_RET, "Transition cost to RET", "", 2015, TC_RET, getRandomVariateForCost(TC_RET)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_LEA, "Transition cost to LEA", "", 2015, TC_LEA, getRandomVariateForCost(TC_LEA)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_ESRD, "Transition cost to ESRD", "", 2015, TC_ESRD, getRandomVariateForCost(TC_ESRD)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_BLI, "Transition cost to BLI", "", 2015, TC_BLI, getRandomVariateForCost(TC_BLI)));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + "MI", "Cost of year 2+ Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 948, getRandomVariateForCost(948)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + "Angina", "Cost of year 2+ of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 532.01, getRandomVariateForCost(532.01)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + "Stroke", "Cost of year 2+ of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 2485.66, getRandomVariateForCost(2485.66)));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + "HF", "Cost of year 2+ of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 1054.42, getRandomVariateForCost(1054.42)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_PREFIX + "MI", "Cost of episode of Myocardial Infarction", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 23536-948, getRandomVariateForCost(23536-948)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_PREFIX + "Angina", "Cost of episode of Angina", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 2517.97-532.01, getRandomVariateForCost(2517.97-532.01)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_PREFIX + "Stroke", "Cost of episode of Stroke", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 6120.32-2485.66, getRandomVariateForCost(6120.32-2485.66)));
		addCostParam(new SecondOrderCostParam(STR_TRANS_PREFIX + "HF", "Cost of episode of Heart Failure", "https://doi.org/10.1016/j.endinu.2018.03.008", 2016, 5557.66-1054.42, getRandomVariateForCost(5557.66-1054.42)));
		addOtherParam(new SecondOrderParam(STR_P_MI, "Probability of a CHD complication being Myocardial Infarction", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.53, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.53)));
		addOtherParam(new SecondOrderParam(STR_P_STROKE, "Probability of a CHD complication being Stroke", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.07, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.07)));
		addOtherParam(new SecondOrderParam(STR_P_ANGINA, "Probability of a CHD complication being Angina", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.28, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.28)));
		addOtherParam(new SecondOrderParam(STR_P_HF, "Probability of a CHD complication being Heart Failure", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.12, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.12)));
		// FIXME: Costes de Canada (en dolares canadienses)
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + CSIIIntervention.NAME, "Cost of " + CSIIIntervention.NAME, "HTA Canada", 2018, 6817));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX +SAPIntervention.NAME, "Cost of " + SAPIntervention.NAME, "HTA Canada", 2018, 9211));

		addUtilParam(new SecondOrderParam(STR_U_GENERAL_POPULATION, "Utility of general population", "", U_GENERAL_POP));
		addUtilParam(new SecondOrderParam(STR_DU_HYPO_EVENT, "Disutility of severe hypoglycemic episode", "", DU_HYPO_EPISODE));
		addUtilParam(new SecondOrderParam(STR_DU_DNC, "Disutility of DNC", "", DU_DNC));
		addUtilParam(new SecondOrderParam(STR_DU_CHD, "Disutility of CHD", "", DU_CHD));
		addUtilParam(new SecondOrderParam(STR_DU_NEU, "Disutility of NEU", "", DU_NEU));
		addUtilParam(new SecondOrderParam(STR_DU_NPH, "Disutility of NPH", "", DU_NPH));
		addUtilParam(new SecondOrderParam(STR_DU_RET, "Disutility of RET", "", DU_RET));
		addUtilParam(new SecondOrderParam(STR_DU_LEA, "Disutility of LEA", "", DU_LEA));
		addUtilParam(new SecondOrderParam(STR_DU_ESRD, "Disutility of ESRD", "", DU_ESRD));
		addUtilParam(new SecondOrderParam(STR_DU_BLI, "Disutility of BLI", "", DU_BLI));
		
		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1016/j.endinu.2018.03.008", 0.5));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", 0.03));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_AGE, "Average baseline age", "https://doi.org/10.1016/j.endinu.2018.03.008",18.6));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_HBA1C, "Average baseline level of HBA1c", "https://doi.org/10.1016/j.endinu.2018.03.008", 7.5));
		addOtherParam(new SecondOrderParam(STR_AVG_HBA1C_AFTER + SAPIntervention.NAME, "Average level of HBA1c after " + SAPIntervention.NAME, "https://doi.org/10.1016/j.endinu.2018.03.008", 7.5));
		addOtherParam(new SecondOrderParam(STR_SENSOR_ADHERENCE_LOWER_LIMIT, "Lower limit for weekly sensor adherence", "", 5));
		addOtherParam(new SecondOrderParam(STR_SENSOR_ADHERENCE_UPPER_LIMIT, "Upper limit for weekly sensor adherence", "", 7));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_HBA1C).getValue());
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_AGE).getValue());
	}

	@Override
	public RandomVariate getWeeklySensorUsage() {
		return RandomVariateFactory.getInstance("UniformVariate", otherParams.get(STR_SENSOR_ADHERENCE_LOWER_LIMIT).getValue(), otherParams.get(STR_SENSOR_ADHERENCE_UPPER_LIMIT).getValue());
	}

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new CSIIIntervention(0, costParams.get(STR_COST_PREFIX + CSIIIntervention.NAME).getValue()),
				new SAPIntervention(1, costParams.get(STR_COST_PREFIX + SAPIntervention.NAME).getValue(), 
						otherParams.get(STR_AVG_HBA1C_AFTER + SAPIntervention.NAME).getValue(), YEARS_OF_EFFECT)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}

	@Override
	public ComplicationRR[] getComplicationRRs() {
		final double referenceHbA1c = otherParams.get(STR_REF_HBA1C).getValue();
		final ComplicationRR[] rr = new ComplicationRR[N_COMPLICATIONS];
		// First computes the relative risks for all the complications but CHD
		final EnumSet<Complication> allButCHD = EnumSet.complementOf(EnumSet.of(Complication.CHD));
		for (Complication comp : allButCHD) {
			final SecondOrderParam param = otherParams.get(STR_RR_PREFIX + comp.name());
			rr[comp.ordinal()] = (param == null) ? new StdComplicationRR(1.0) : new HbA1c10ReductionComplicationRR(param.getValue(), referenceHbA1c);
		}
		// Now CHD
		final SecondOrderParam param = otherParams.get(STR_RR_PREFIX + Complication.CHD.name());
		rr[Complication.CHD.ordinal()] = new HbA1c1PPComplicationRR((param == null) ? 1.0 : param.getValue(), referenceHbA1c);
		return rr;
	}
	
	@Override
	public ComplicationRR getHypoRR() {
		final double[] rrValues = new double[getNInterventions()];
		rrValues[0] = 1.0;
		final SecondOrderParam param = otherParams.get(STR_RR_HYPO);
		final double rr = (param == null) ? 1.0 : param.getValue();
		for (int i = 1; i < getNInterventions(); i++) {
			rrValues[i] = rr;
		}
		return new InterventionSpecificComplicationRR(rrValues);
	}

	public class CSIIIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "CSII";
		private final double annualCost;
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public CSIIIntervention(int id, double annualCost) {
			super(id, NAME, NAME, BasicConfigParams.MAX_AGE);
			this.annualCost = annualCost;
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c();
//			return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge()); // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3131116/
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}

	public class SAPIntervention extends T1DMMonitoringIntervention {
		public final static String NAME = "SAP";
		private final double annualCost;
		private final double hba1cAfterIntervention;
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public SAPIntervention(int id, double annualCost, double hba1cAfterIntervention, double yearsOfEffect) {
			super(id, NAME, NAME, yearsOfEffect);
			this.annualCost = annualCost;
			this.hba1cAfterIntervention = hba1cAfterIntervention;
		}

		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.isEffectActive() ? hba1cAfterIntervention : pat.getBaselineHBA1c();
//			return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
		}

		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return annualCost;
		}

	}
	
}
