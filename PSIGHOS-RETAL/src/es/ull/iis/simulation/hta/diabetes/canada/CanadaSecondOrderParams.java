/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;

/**
 * To validate the model, must be launched with discount rate = 0.015 (1.5%)
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaSecondOrderParams extends SecondOrderParamsRepository {
	public static final double DISCOUNT_RATE = 0.015; 
	
	private static final double C_DNC = 2262;

	protected static final double U_DNC = 0.814;


	/**
	 * @param nPatients Number of patients to create
	 */
	public CanadaSecondOrderParams(int nPatients) {
		super(nPatients, new CanadaPopulation());
		BasicConfigParams.DEF_U_GENERAL_POP = 1.0;
		
		registerComplication(new CanadaCHDSubmodel());
		registerComplication(new CanadaNEUSubmodel());
		registerComplication(new CanadaNPHSubmodel());
		registerComplication(new CanadaRETSubmodel());

		registerComplication(new CanadaSevereHypoglycemiaEvent());
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.CHD.name(), STR_RR_PREFIX + MainChronicComplications.CHD.name(), "", RR_CHD, RandomVariateFactory.getInstance("ConstantVariate", RR_CHD)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.NPH.name(), STR_RR_PREFIX + MainChronicComplications.NPH.name(), "", RR_NPH, RandomVariateFactory.getInstance("ConstantVariate", RR_NPH)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.NEU.name(), STR_RR_PREFIX + MainChronicComplications.NEU.name(), "", RR_NEU, RandomVariateFactory.getInstance("ConstantVariate", RR_NEU)));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.RET.name(), STR_RR_PREFIX + MainChronicComplications.RET.name(), "", RR_RET, RandomVariateFactory.getInstance("ConstantVariate", RR_RET)));

		registerIntervention(new CanadaIntervention1());
		registerIntervention(new CanadaIntervention2());
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of DNC", "HTA Canada", 2018, C_DNC));

		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", BasicConfigParams.DEF_U_GENERAL_POP - U_DNC));
		
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		return new CanadaDeathSubmodel(this);
	}

	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new CanadaUtilityCalculator(duDNC, BasicConfigParams.DEF_U_GENERAL_POP, acuteSubmodels[DiabetesAcuteComplications.SHE.ordinal()].getDisutility(null));
	}
	
}
