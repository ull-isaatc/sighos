/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams.CombinationMethod;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSecondOrderParams extends SecondOrderParams {
	private static final double P_MAN = 0.5;
	/** Duration of effect of the intervention */
	private static final double YEARS_OF_EFFECT = 1.0;
	private static final double DISCOUNT_RATE = 0.015; 
	
	private static final double P_DNC_RET = 0.0764;
	private static final double P_DNC_NEU = 0.0235;
	private static final double P_DNC_NPH = 0.0094;
	private static final double P_DNC_CHD = 0.0045;
	private static final double P_NEU_CHD = 0.02;
	private static final double P_NEU_LEA = 0.12;
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_CHD = 0.0224;
	private static final double P_NPH_ESRD = 0.072;
	private static final double P_RET_BLI = 0.0064;
	private static final double P_RET_CHD = 0.0155;
	
	private static final double RR_CHD = 0.761;
	private static final double RR_NPH = 0.742;
	private static final double RR_NEU = 0.624;
	private static final double RR_RET = 0.661;

	// FIXME: Nos on los datos canadienses de costes
	private static final double C_HYPO_EPISODE = 3755;
	private static final double C_DNC = 2174.11;
	private static final double C_CHD = 4072;
	private static final double C_NEU = 192;
	private static final double C_NPH = 13;
	private static final double C_RET = 52;
	private static final double C_LEA = 6024;
	private static final double C_ESRD = 12808;
	private static final double C_BLI = 2482;
	private static final double TC_CHD = 18682 - C_CHD;
	private static final double TC_NEU = 192 - C_NEU;
	private static final double TC_NPH = 80 - C_NPH;
	private static final double TC_RET = 492 - C_RET;
	private static final double TC_LEA = 43984 - C_LEA;
	private static final double TC_ESRD = 28221 - C_ESRD;
	private static final double TC_BLI = 3483 - C_BLI;

	private static final double U_GENERAL_POP = 1.0;
	private static final double DU_HYPO_EPISODE = 0.0206; // From Canada
	private static final double DU_DNC = U_GENERAL_POP - 0.814;
	private static final double DU_CHD = U_GENERAL_POP - DU_DNC - 0.685;
	private static final double DU_NEU = U_GENERAL_POP - DU_DNC - 0.624;
	private static final double DU_NPH = U_GENERAL_POP - DU_DNC - 0.575;
	private static final double DU_RET = U_GENERAL_POP - DU_DNC - 0.612;
	private static final double DU_LEA = U_GENERAL_POP - DU_DNC - 0.534;
	private static final double DU_ESRD = U_GENERAL_POP - DU_DNC - 0.49;
	private static final double DU_BLI = U_GENERAL_POP - DU_DNC - 0.569;


	/**
	 * @param baseCase
	 */
	public CanadaSecondOrderParams(boolean baseCase) {
		super(baseCase);
		setCanadaValidation();
		utilityCombinationMethod = CombinationMethod.MIN;
		interventions = new T1DMMonitoringIntervention[] {
				new T1DMMonitoringIntervention(0, "SMBG+MDI", "SMBG+MDI") {
					@Override
					public double getHBA1cLevel(T1DMPatient pat) {
						return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge());
					}
				},
				new T1DMMonitoringIntervention(1, "SAP", "SAP")	{
					private final static int MIN_WEEKLY_USAGE = 5;
					@Override
					public double getHBA1cLevel(T1DMPatient pat) {						
						return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
					}
				}			
		};
		addProbParam(new SecondOrderParam(STR_P_DNC_RET, STR_P_DNC_RET, "", P_DNC_RET, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_RET)));
		addProbParam(new SecondOrderParam(STR_P_DNC_NEU, STR_P_DNC_NEU, "", P_DNC_NEU, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_NEU)));
		addProbParam(new SecondOrderParam(STR_P_DNC_NPH, STR_P_DNC_NPH, "", P_DNC_NPH, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_NPH)));
		addProbParam(new SecondOrderParam(STR_P_DNC_CHD, STR_P_DNC_CHD, "", P_DNC_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_CHD)));
		addProbParam(new SecondOrderParam(STR_P_NEU_CHD, STR_P_NEU_CHD, "", P_NEU_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_CHD)));
		addProbParam(new SecondOrderParam(STR_P_NEU_LEA, STR_P_NEU_LEA, "", P_NEU_LEA, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_LEA)));
		addProbParam(new SecondOrderParam(STR_P_NEU_NPH, STR_P_NEU_NPH, "", P_NEU_NPH, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_NPH)));
		addProbParam(new SecondOrderParam(STR_P_NPH_CHD, STR_P_NPH_CHD, "", P_NPH_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_NPH_CHD)));
		addProbParam(new SecondOrderParam(STR_P_NPH_ESRD, STR_P_NPH_ESRD, "", P_NPH_ESRD, RandomVariateFactory.getInstance("ConstantVariate", P_NPH_ESRD)));
		addProbParam(new SecondOrderParam(STR_P_RET_BLI, STR_P_RET_BLI, "", P_RET_BLI, RandomVariateFactory.getInstance("ConstantVariate", P_RET_BLI)));
		addProbParam(new SecondOrderParam(STR_P_RET_CHD, STR_P_RET_CHD, "", P_RET_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_RET_CHD)));
		
		// Severe hypoglycemic episodes
		final double[] paramsHypo = betaParametersFromNormal(0.0982, sdFrom95CI(new double[]{0.0526, 0.1513}));
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode", "Canada", 0.0982, RandomVariateFactory.getInstance("BetaVariate", paramsHypo[0], paramsHypo[1])));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch", "Canada", 0.869, RandomVariateFactory.getInstance("RRFromLnCIVariate", 0.869, 0.476, 1.586, 1)));

		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.CHD.name(), STR_RR_PREFIX + Complication.CHD.name(), "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NPH.name(), STR_RR_PREFIX + Complication.NPH.name(), "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NEU.name(), STR_RR_PREFIX + Complication.NEU.name(), "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.RET.name(), STR_RR_PREFIX + Complication.RET.name(), "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));

		addCostParam(new SecondOrderCostParam(STR_COST_HYPO_EPISODE, "Cost of a severe hypoglycemic episode", "HTA Canada", 2018, C_HYPO_EPISODE));
		addCostParam(new SecondOrderCostParam(STR_COST_DNC, "Cost of DNC", "HTA Canada", 2018, C_DNC));
		addCostParam(new SecondOrderCostParam(STR_COST_CHD, "Cost of CHD", "HTA Canada", 2018, C_CHD));
		addCostParam(new SecondOrderCostParam(STR_COST_NEU, "Cost of NEU", "HTA Canada", 2018, C_NEU));
		addCostParam(new SecondOrderCostParam(STR_COST_NPH, "Cost of NPH", "HTA Canada", 2018, C_NPH));
		addCostParam(new SecondOrderCostParam(STR_COST_RET, "Cost of RET", "HTA Canada", 2018, C_RET));
		addCostParam(new SecondOrderCostParam(STR_COST_LEA, "Cost of LEA", "HTA Canada", 2018, C_LEA));
		addCostParam(new SecondOrderCostParam(STR_COST_ESRD, "Cost of ESRD", "HTA Canada", 2018, C_ESRD));
		addCostParam(new SecondOrderCostParam(STR_COST_BLI, "Cost of BLI", "HTA Canada", 2018, C_BLI));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_CHD, "Transition cost to CHD", "HTA Canada", 2018, TC_CHD));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_NEU, "Transition cost to NEU", "HTA Canada", 2018, TC_NEU));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_NPH, "Transition cost to NPH", "HTA Canada", 2018, TC_NPH));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_RET, "Transition cost to RET", "HTA Canada", 2018, TC_RET));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_LEA, "Transition cost to LEA", "HTA Canada", 2018, TC_LEA));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_ESRD, "Transition cost to ESRD", "HTA Canada", 2018, TC_ESRD));
		addCostParam(new SecondOrderCostParam(STR_TRANS_COST_BLI, "Transition cost to BLI", "HTA Canada", 2018, TC_BLI));
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

		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of havig sex = male", "Assumption", P_MAN));
		addOtherParam(new SecondOrderParam(STR_YEARS_OF_EFFECT, "Duration of effect in years", "", YEARS_OF_EFFECT));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", DISCOUNT_RATE));
		
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_AGE, "Average baseline age", "", 27));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_HBA1C, "Average baseline level of HBA1c", "", 8.8));

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
		return RandomVariateFactory.getInstance("ConstantVariate", 7);
	}

}
