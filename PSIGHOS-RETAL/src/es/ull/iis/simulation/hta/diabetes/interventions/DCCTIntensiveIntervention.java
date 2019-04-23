/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author icasrod
 *
 */
public class DCCTIntensiveIntervention extends SecondOrderDiabetesIntervention {
	public final static String NAME = "Intensive";
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;

	/**
	 */
	public DCCTIntensiveIntervention() {
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
			super(id, YEARS_OF_EFFECT);
			rnd = RandomVariateFactory.getInstance("NormalVariate", 1.5, 1.1);
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
