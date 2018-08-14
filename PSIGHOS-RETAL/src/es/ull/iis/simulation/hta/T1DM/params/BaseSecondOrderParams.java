/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BaseSecondOrderParams extends SecondOrderParams {

	private static final double P_DNC_RET = 0.0013;
	private static final double P_DNC_NEU = 0.0354; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
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
	
	private static final double PERCENT_POINT_REDUCTION = 0.887480916;

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
	public BaseSecondOrderParams(boolean baseCase) {
		super(baseCase);
		interventions = new Intervention[] {
				new T1DMMonitoringIntervention(0, "CSII", "CSII"),
				new T1DMMonitoringIntervention(1, "SAP", "SAP")				
		};
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
		addProbParam(new SecondOrderParam(STR_P_DNC_NEU, STR_P_DNC_NEU, "Klein et al. 2004", P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1])));
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
		final double[] paramsHypo = betaParametersFromNormal(0.0982, sdFrom95CI(new double[]{0.0526, 0.1513}));
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode", "Canada", 0.0982, RandomVariateFactory.getInstance("BetaVariate", paramsHypo[0], paramsHypo[1])));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch", "Canada", 0.869, RandomVariateFactory.getInstance("RRFromLnCIVariate", 0.869, 0.476, 1.586, 1)));

		addOtherParam(new SecondOrderParam(STR_ORR_CHD, STR_ORR_CHD, "", 0.15/*, RandomVariateFactory.getInstance("NormalVariate", 0.15, 0.13010)*/));
		addOtherParam(new SecondOrderParam(STR_ORR_NPH, STR_ORR_NPH, "", 0.25/*, RandomVariateFactory.getInstance("NormalVariate", 0.25, 0.03316)*/));
		addOtherParam(new SecondOrderParam(STR_ORR_NEU, STR_ORR_NEU, "", 0.3/*, RandomVariateFactory.getInstance("NormalVariate", 0.3, 0.07398)*/));
		addOtherParam(new SecondOrderParam(STR_ORR_RET, STR_ORR_RET, "", 0.35/*, RandomVariateFactory.getInstance("NormalVariate", 0.35, 0.03571)*/));
		addOtherParam(new SecondOrderParam(STR_EFF_PREFIX + interventions[1].getShortName(), STR_EFF_PREFIX + interventions[1].getShortName(), "", 0.23, RandomVariateFactory.getInstance("NormalVariate", 0.23, 0.12244898)));
		addOtherParam(new SecondOrderParam(STR_PERCENT_POINT_REDUCTION, STR_PERCENT_POINT_REDUCTION, "", PERCENT_POINT_REDUCTION));
		
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
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + interventions[0].getShortName(), "Cost of " + interventions[0].getDescription(), "HTA Canada", 2018, 3677));
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + interventions[1].getShortName(), "Cost of " + interventions[1].getDescription(), "HTA Canada", 2018, 11811));

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
		addOtherParam(new SecondOrderParam(STR_INIT_AGE, "Initial age", "https://doi.org/10.1016/j.endinu.2018.03.008", 18.6));
		addOtherParam(new SecondOrderParam(STR_YEARS_OF_EFFECT, "Duration of effect in years", "Assumption (as in the Canada model)", 1.0));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", 0.03));
	}

}
