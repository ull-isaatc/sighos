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
	public static final int INIT_AGE = 26;
	private static final double P_MAN = 0.5;
	/** Duration of effect of the intervention */
	private static final double YEARS_OF_EFFECT = Double.MAX_VALUE;

	private static final double P_DNC_RET = 0.0013;
	private static final double P_DNC_NEU = 0.035;
	private static final double P_DNC_NPH = 0.072;
	private static final double P_DNC_CHD = 0.031;
	private static final double P_NEU_CHD = 0.029;
	private static final double P_NEU_LEA = 0.0154;
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
	
	private static final double RR_CHD = 0.9655;
	private static final double RR_NPH = 0.9352099;
	private static final double RR_NEU = 0.9222518;
	private static final double RR_RET = 0.9092938;

	private static final double IMR_DNC = 1.0;
	private static final double IMR_RET = 1.0;
	private static final double IMR_NEU = 1.51;
	private static final double IMR_NPH = 2.23;
	private static final double IMR_CHD = 1.96;
	private static final double IMR_ESRD = 2.23 /* 101.59*/;
	private static final double IMR_BLI = 1.00;
	private static final double IMR_LEA = 3.98;


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

		probabilityParams.put(STR_P_DNC_RET, new DoubleParam(STR_P_DNC_RET, STR_P_DNC_RET, "", P_DNC_RET, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		probabilityParams.put(STR_P_DNC_NEU, new DoubleParam(STR_P_DNC_NEU, STR_P_DNC_NEU, "", P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1])));
		probabilityParams.put(STR_P_DNC_NPH, new DoubleParam(STR_P_DNC_NPH, STR_P_DNC_NPH, "", P_DNC_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NPH[0], paramsDNC_NPH[1])));
		probabilityParams.put(STR_P_DNC_CHD, new DoubleParam(STR_P_DNC_CHD, STR_P_DNC_CHD, "", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		probabilityParams.put(STR_P_NEU_CHD, new DoubleParam(STR_P_NEU_CHD, STR_P_NEU_CHD, "", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		probabilityParams.put(STR_P_NEU_LEA, new DoubleParam(STR_P_NEU_LEA, STR_P_NEU_LEA, "", P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1])));
		probabilityParams.put(STR_P_NEU_NPH, new DoubleParam(STR_P_NEU_NPH, STR_P_NEU_NPH, "", P_NEU_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1])));
		probabilityParams.put(STR_P_NPH_CHD, new DoubleParam(STR_P_NPH_CHD, STR_P_NPH_CHD, "", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		probabilityParams.put(STR_P_NPH_ESRD, new DoubleParam(STR_P_NPH_ESRD, STR_P_NPH_ESRD, "", P_NPH_ESRD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_ESRD[0], paramsNPH_ESRD[1])));
		probabilityParams.put(STR_P_RET_BLI, new DoubleParam(STR_P_RET_BLI, STR_P_RET_BLI, "", P_RET_BLI, RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		probabilityParams.put(STR_P_RET_CHD, new DoubleParam(STR_P_RET_CHD, STR_P_RET_CHD, "", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));
		
		probabilityParams.put(STR_RR_CHD, new DoubleParam(STR_RR_CHD, STR_RR_CHD, "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
		probabilityParams.put(STR_RR_NPH, new DoubleParam(STR_RR_NPH, STR_RR_NPH, "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
		probabilityParams.put(STR_RR_NEU, new DoubleParam(STR_RR_NEU, STR_RR_NEU, "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
		probabilityParams.put(STR_RR_RET, new DoubleParam(STR_RR_RET, STR_RR_RET, "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));
		
		probabilityParams.put(STR_IMR_DNC, new DoubleParam(STR_IMR_DNC, STR_IMR_DNC, "", IMR_DNC, RandomVariateFactory.getInstance("ConstantVariate", IMR_DNC)));
		probabilityParams.put(STR_IMR_RET, new DoubleParam(STR_IMR_RET, STR_IMR_RET, "", IMR_RET, RandomVariateFactory.getInstance("ConstantVariate", IMR_RET)));
		probabilityParams.put(STR_IMR_NEU, new DoubleParam(STR_IMR_NEU, STR_IMR_NEU, "", IMR_NEU, RandomVariateFactory.getInstance("ConstantVariate", IMR_NEU)));
		probabilityParams.put(STR_IMR_NPH, new DoubleParam(STR_IMR_NPH, STR_IMR_NPH, "", IMR_NPH, RandomVariateFactory.getInstance("ConstantVariate", IMR_NPH)));
		probabilityParams.put(STR_IMR_CHD, new DoubleParam(STR_IMR_CHD, STR_IMR_CHD, "", IMR_CHD, RandomVariateFactory.getInstance("ConstantVariate", IMR_CHD)));
		probabilityParams.put(STR_IMR_ESRD, new DoubleParam(STR_IMR_ESRD, STR_IMR_ESRD, "", IMR_ESRD, RandomVariateFactory.getInstance("ConstantVariate", IMR_ESRD)));
		probabilityParams.put(STR_IMR_BLI, new DoubleParam(STR_IMR_BLI, STR_IMR_BLI, "", IMR_BLI, RandomVariateFactory.getInstance("ConstantVariate", IMR_BLI)));
		probabilityParams.put(STR_IMR_LEA, new DoubleParam(STR_IMR_LEA, STR_IMR_LEA, "", IMR_LEA, RandomVariateFactory.getInstance("ConstantVariate", IMR_LEA)));
		
		probabilityParams.put(STR_P_MAN, new DoubleParam(STR_P_MAN, "Probability of havig sex = male", "Assumption", P_MAN));
		otherParams.put(STR_INIT_AGE, new DoubleParam(STR_INIT_AGE, "Initial age", "", INIT_AGE));
		otherParams.put(STR_YEARS_OF_EFFECT, new DoubleParam(STR_YEARS_OF_EFFECT, "Duration of effect in years", "", YEARS_OF_EFFECT));
	}

}
