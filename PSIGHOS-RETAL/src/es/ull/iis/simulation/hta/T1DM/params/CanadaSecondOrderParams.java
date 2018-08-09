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
public class CanadaSecondOrderParams extends SecondOrderParams {
	public static final int INIT_AGE = 27;
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


	/**
	 * @param baseCase
	 */
	public CanadaSecondOrderParams(boolean baseCase) {
		super(baseCase);
		setCanadaValidation();
		interventions = new Intervention[] {
				new T1DMMonitoringIntervention(0, "SMBG+MDI", "SMBG+MDI"),
				new T1DMMonitoringIntervention(1, "SAP", "SAP")				
		};
		probabilityParams.put(STR_P_DNC_RET, new DoubleParam(STR_P_DNC_RET, STR_P_DNC_RET, "", P_DNC_RET, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_RET)));
		probabilityParams.put(STR_P_DNC_NEU, new DoubleParam(STR_P_DNC_NEU, STR_P_DNC_NEU, "", P_DNC_NEU, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_NEU)));
		probabilityParams.put(STR_P_DNC_NPH, new DoubleParam(STR_P_DNC_NPH, STR_P_DNC_NPH, "", P_DNC_NPH, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_NPH)));
		probabilityParams.put(STR_P_DNC_CHD, new DoubleParam(STR_P_DNC_CHD, STR_P_DNC_CHD, "", P_DNC_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_DNC_CHD)));
		probabilityParams.put(STR_P_NEU_CHD, new DoubleParam(STR_P_NEU_CHD, STR_P_NEU_CHD, "", P_NEU_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_CHD)));
		probabilityParams.put(STR_P_NEU_LEA, new DoubleParam(STR_P_NEU_LEA, STR_P_NEU_LEA, "", P_NEU_LEA, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_LEA)));
		probabilityParams.put(STR_P_NEU_NPH, new DoubleParam(STR_P_NEU_NPH, STR_P_NEU_NPH, "", P_NEU_NPH, RandomVariateFactory.getInstance("ConstantVariate", P_NEU_NPH)));
		probabilityParams.put(STR_P_NPH_CHD, new DoubleParam(STR_P_NPH_CHD, STR_P_NPH_CHD, "", P_NPH_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_NPH_CHD)));
		probabilityParams.put(STR_P_NPH_ESRD, new DoubleParam(STR_P_NPH_ESRD, STR_P_NPH_ESRD, "", P_NPH_ESRD, RandomVariateFactory.getInstance("ConstantVariate", P_NPH_ESRD)));
		probabilityParams.put(STR_P_RET_BLI, new DoubleParam(STR_P_RET_BLI, STR_P_RET_BLI, "", P_RET_BLI, RandomVariateFactory.getInstance("ConstantVariate", P_RET_BLI)));
		probabilityParams.put(STR_P_RET_CHD, new DoubleParam(STR_P_RET_CHD, STR_P_RET_CHD, "", P_RET_CHD, RandomVariateFactory.getInstance("ConstantVariate", P_RET_CHD)));
		
		probabilityParams.put(STR_RR_CHD, new DoubleParam(STR_RR_CHD, STR_RR_CHD, "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
		probabilityParams.put(STR_RR_NPH, new DoubleParam(STR_RR_NPH, STR_RR_NPH, "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
		probabilityParams.put(STR_RR_NEU, new DoubleParam(STR_RR_NEU, STR_RR_NEU, "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
		probabilityParams.put(STR_RR_RET, new DoubleParam(STR_RR_RET, STR_RR_RET, "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));

		probabilityParams.put(STR_P_MAN, new DoubleParam(STR_P_MAN, "Probability of havig sex = male", "Assumption", P_MAN));
		otherParams.put(STR_INIT_AGE, new DoubleParam(STR_INIT_AGE, "Initial age", "", INIT_AGE));
		otherParams.put(STR_YEARS_OF_EFFECT, new DoubleParam(STR_YEARS_OF_EFFECT, "Duration of effect in years", "", YEARS_OF_EFFECT));
		otherParams.put(STR_DISCOUNT_RATE, new DoubleParam(STR_DISCOUNT_RATE, "Discount rate", "Spanish guidelines", DISCOUNT_RATE));
	}

}
