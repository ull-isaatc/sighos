/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public class CanadaIntervention1 extends SecondOrderDiabetesIntervention {
	public final static String NAME = "SMBG plus multiple daily injections";

	/**
	 */
	public CanadaIntervention1() {
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
			super(id, BasicConfigParams.DEF_MAX_AGE);
		}

		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return 8.3;
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 3677;
		}
	}
}
