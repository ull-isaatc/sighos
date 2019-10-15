/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.interventions;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author icasrod
 *
 */
public class AdvanceIntensiveIntervention extends SecondOrderDiabetesIntervention {
	public final static String NAME = "Intensive";
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;

	/**
	 */
	public AdvanceIntensiveIntervention() {
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
			final double sd = Statistics.sdFrom95CI(new double[] {0.65, 0.75});
			rnd = RandomVariateFactory.getInstance("NormalVariate", 0.7, sd);
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
