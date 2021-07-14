/**
 * 
 */
package es.ull.iis.simulation.hta.diab;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.manifestations.EndStageRenalDisease;
import es.ull.iis.simulation.hta.diab.manifestations.Macroalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.Microalbuminuria;
import es.ull.iis.simulation.hta.diab.manifestations.SevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.Transition;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class T1DMDisease extends Disease {
	private static final String STR_HBA1C = "HbA1c";
	private static final double P_DNC_ALB1 = 0.0436;
	private static final double[] CI_DNC_ALB1 = {0.0136, 0.0736}; // Assumption
	private static final double BETA_ALB1 = 3.25;
	final private Manifestation she;
	final private Manifestation alb1;
	final private Manifestation alb2;
	final private Manifestation esrd;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public T1DMDisease(SecondOrderParamsRepository secParams) {
		super(secParams, "T1DM", "Type I Diabetes Mellitus");
		she = addManifestation(new SevereHypoglycemiaEvent(secParams, this));
		// Register and configure Nephropathy-related manifestations
		alb1 = addManifestation(new Microalbuminuria(secParams, this));
		alb2 = addManifestation(new Macroalbuminuria(secParams, this));
		esrd = addManifestation(new EndStageRenalDisease(secParams, this));
		final Transition toAlb1 = addTransition(new Transition(secParams, getNullManifestation(), alb1, true));
		toAlb1.setCalculator(toAlb1.new AnnualRiskBasedTimeToEventCalculator(new SheffieldComplicationRR(getNullManifestation(), alb1)));
	}

	@Override
	public void registerSecondOrderParameters() {
		final double[] paramsDNC_ALB1 = Statistics.betaParametersFromNormal(P_DNC_ALB1, Statistics.sdFrom95CI(CI_DNC_ALB1));
		secParams.addProbParam(getNullManifestation(), alb1, 
				"https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 
				P_DNC_ALB1, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_ALB1[0], paramsDNC_ALB1[1]));
		secParams.addRRParam(getNullManifestation(), alb1, 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", BETA_ALB1); 
	}

	@Override
	public DiseaseProgression getProgression(Patient pat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDiagnosisCost(Patient pat) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Computes the RR according to the Sheffiled's method
	 * 
	 * They assume a probability for HbA1c level = 10% (p_10), so that p_h = p_10 X (h/10)^beta, where "h" is the new HbA1c level.
	 * As a consequence, RR = p_h/p_10 = (h/10)^beta
	 *   
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class SheffieldComplicationRR implements es.ull.iis.simulation.hta.params.RRCalculator {
		final private Manifestation srcManifestation;
		final private Manifestation destManifestation;
		/**
		 * Creates a relative risk computed as described in the Sheffield's T1DM model
		 */
		public SheffieldComplicationRR(Manifestation srcManifestation, Manifestation destManifestation) {
			this.srcManifestation = srcManifestation;
			this.destManifestation = destManifestation;
		}

		@Override
		public double getRR(Patient pat) {
			final double beta = secParams.getRR(srcManifestation, destManifestation, pat.getSimulation());
			return Math.pow(pat.getProfile().getDoubleProperty(STR_HBA1C)/10.0, beta);
		}
	}
	
}
