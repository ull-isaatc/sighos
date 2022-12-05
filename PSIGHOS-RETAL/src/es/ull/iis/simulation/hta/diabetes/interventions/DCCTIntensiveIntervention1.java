/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author icasrod
 *
 */
public class DCCTIntensiveIntervention1 extends SecondOrderDiabetesIntervention {
	public final static String NAME = "Intensive";

	/**
	 */
	public DCCTIntensiveIntervention1() {
		super(NAME, NAME);
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
	}

	@Override
	public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
		return new IntensiveTherapy(id, secParams);
	}

	public class IntensiveTherapy extends DiabetesIntervention {
		private final RandomVariate rnd; 
		/**
		 * @param id
		 */
		public IntensiveTherapy(int id, SecondOrderParamsRepository secParams) {
			super(id, BasicConfigParams.DEF_MAX_AGE);
			rnd = RandomVariateFactory.getInstance("ConstantVariate", 1.5);
		}

		@Override
		public double getHBA1cLevel(DiabetesPatient pat) {
			if (pat.isEffectActive()) {
				return pat.getBaselineHBA1c() - rnd.generate();
				
			}
			return pat.getBaselineHBA1c();
		}

		@Override
		public double getAnnualCost(DiabetesPatient pat) {
			return 0.0;
		}
	}
}
