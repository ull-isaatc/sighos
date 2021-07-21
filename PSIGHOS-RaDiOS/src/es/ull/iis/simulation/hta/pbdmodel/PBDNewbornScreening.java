/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.Modification;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDNewbornScreening extends ScreeningStrategy {
	private final static double C_TEST = 0.89;
	private final Disease disease;

	/**
	 * @param secParams
	 */
	public PBDNewbornScreening(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_InterventionScreening", "Basic screening", 1.0, 0.999935);
		this.disease = disease;
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addCostParam(this, "Cost of screening", "", 2013, C_TEST, RandomVariateFactory.getInstance("UniformVariate", 0.5, 2.5));
		for (Manifestation manif : secParams.getRegisteredManifestations())
			secParams.addModificationParam(this, Modification.Type.SET, disease.getAsymptomaticManifestation(), manif, "", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
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
