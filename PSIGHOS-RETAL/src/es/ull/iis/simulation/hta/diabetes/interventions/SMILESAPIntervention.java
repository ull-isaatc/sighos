package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * An intervention with SAP with predictive low-glucose management. For this population, it has no effect on the 
 * HbA1c level.
 * @author Iván Castilla Rodríguez
 *
 */
public class SMILESAPIntervention extends SecondOrderDiabetesIntervention {
	public final static String NAME = "SAP";
	/** Annual cost of the treatment with SAP. Based on microcosts from Medtronic. */
	private static final double C_SAP = 3484.56;
	/** The duration (in years) of the effect of the intervention */
	final private double yearsOfEffect;

	/**
	 * Creates the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public SMILESAPIntervention(double yearsOfEffect) {
		super(NAME, NAME);
		this.yearsOfEffect = yearsOfEffect;
	}

	/**
	 * Creates the intervention, and supposes lifetime effect.
	 */
	public SMILESAPIntervention() {
		this(BasicConfigParams.DEF_MAX_AGE);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX +SMILESAPIntervention.NAME, "Annual cost of SAP",  
				"Own calculations from data provided by medtronic (see Parametros.xls", 2018, C_SAP, SecondOrderParamsRepository.getRandomVariateForCost(C_SAP)));
		
	}
	
	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new Instance(id, secParams);
	}

	public class Instance extends DiabetesIntervention {
		/** Annual cost of the intervention */
		private final double annualCost;

		public Instance(int id, SecondOrderParamsRepository secParams) {
			super(id, yearsOfEffect);
			final double auxCost = secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + SMILESAPIntervention.NAME);
			annualCost = Double.isNaN(auxCost) ? 0.0 : auxCost;
		}
		
		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return pat.getBaselineHBA1c();
//				return 2.206 + 1.491 + (0.618*pat.getBaselineHBA1c()) - (0.150 * Math.max(0, pat.getWeeklySensorUsage() - MIN_WEEKLY_USAGE)) - (0.005*pat.getAge());
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return annualCost;
		}
	}

}