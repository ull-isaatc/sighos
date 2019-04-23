/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public class CanadaIntervention2 extends SecondOrderDiabetesIntervention {
	public final static String NAME = "Sensor-augmented pump";
	/** Duration of effect of the intervention */
	private static final double YEARS_OF_EFFECT = 1.0;

	/**
	 */
	public CanadaIntervention2() {
		super(NAME, NAME);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention#addSecondOrderParams(es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository)
	 */
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {

	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention#getInstance(int, es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository)
	 */
	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new Instance(id);
	}

	public class Instance extends DiabetesIntervention {
		/**
		 * @param id
		 */
		public Instance(int id) {
			super(id, YEARS_OF_EFFECT);
		}

		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return pat.isEffectActive() ? 7.3 : 8.3;
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 9211;
		}
	}
}
