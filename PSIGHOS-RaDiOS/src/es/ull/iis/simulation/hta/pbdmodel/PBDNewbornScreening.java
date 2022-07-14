/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDNewbornScreening extends ScreeningStrategy {
	private final static double C_TEST = 0.89;

	/**
	 * @param secParams
	 */
	public PBDNewbornScreening(SecondOrderParamsRepository secParams) {
		super(secParams, "#PBD_InterventionScreening", "Basic screening", 1.0, 0.999935);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(new SecondOrderCostParam(secParams, SecondOrderParamsRepository.STR_COST_PREFIX + this, 
				"Cost of screening", "", 2013, C_TEST, RandomVariateFactory.getInstance("UniformVariate", 0.5, 2.5)));
		for (Manifestation manif : secParams.getRegisteredManifestations())
			secParams.addModificationParam(this, Modification.Type.SET, SecondOrderParamsRepository.getProbString(manif), "", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return 0.0;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return secParams.getCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + this.name(), pat.getSimulation());
	}

}
