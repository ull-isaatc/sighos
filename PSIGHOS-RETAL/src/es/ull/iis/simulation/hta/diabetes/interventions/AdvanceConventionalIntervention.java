/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public class AdvanceConventionalIntervention extends SecondOrderDiabetesIntervention {
	public final static String NAME = "Conventional";

	/**
	 */
	public AdvanceConventionalIntervention() {
		super(NAME, NAME);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
	}

	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new ConventionalTherapy(id, secParams);
	}
	
	public class ConventionalTherapy extends DiabetesIntervention {
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public ConventionalTherapy(int id, SecondOrderParamsRepository secParams) {
			super(id, BasicConfigParams.DEF_MAX_AGE);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			return pat.getBaselineHBA1c();
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 0.0;
		}
	}
}
