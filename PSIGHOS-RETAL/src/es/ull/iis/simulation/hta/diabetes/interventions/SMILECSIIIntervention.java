package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * An intervention that represents the usual pump treatment used in Spain. Assumes no changes in the HbA1c level of the 
 * patient during the simulation, and no disutility
 * @author Iván Castilla Rodríguez
 *
 */
public class SMILECSIIIntervention extends SecondOrderDiabetesIntervention {
	public final static String NAME = "CSII";
	/** Annual cost of the treatment with CSII. Based on microcosts from Medtronic */
	private static final double C_CSII = 377.38;
	/** The duration (in years) of the effect of the intervention */
	final private double yearsOfEffect;

	/**
	 * Creates the intervention
	 * @param yearsOfEffect Years of effect of the intervention
	 */
	public SMILECSIIIntervention(final double yearsOfEffect) {
		super(NAME, NAME);
		this.yearsOfEffect = yearsOfEffect;
	}

	/**
	 * Creates the intervention, and supposes lifetime effect.
	 */
	public SMILECSIIIntervention() {
		this(BasicConfigParams.DEF_MAX_AGE);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + SMILECSIIIntervention.NAME, "Annual cost of CSII", 
				"Own calculations from data provided by medtronic (see Parametros.xls)", 2018, C_CSII, SecondOrderParamsRepository.getRandomVariateForCost(C_CSII)));
	}
	
	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new Instance(id, secParams);
	}
	
	public class Instance extends DiabetesIntervention {
		/** Annual cost of the intervention */
		private final double annualCost;
		/**
		 * 
		 * @param id Unique identifier of the intervention
		 */
		public Instance(int id, SecondOrderParamsRepository secParams) {
			super(id, yearsOfEffect);
			final double auxCost = secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + SMILECSIIIntervention.NAME);
			annualCost = Double.isNaN(auxCost) ? 0.0 : auxCost;
		}
		
		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return pat.getBaselineHBA1c();
//				return 2.206 + (0.744*pat.getBaselineHBA1c()) - (0.003*pat.getAge()); // https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3131116/
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return annualCost;
		}
		
	}

}