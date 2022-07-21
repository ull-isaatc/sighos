/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class HealthTechnology implements PartOfStrategy {
	private final String name;
	private final String description;
	protected final SecondOrderParamsRepository secParams;
	private final Guideline guide;
	/**
	 * 
	 */
	public HealthTechnology(SecondOrderParamsRepository secParams, String name, String description, Guideline guide) {
		this.name = name;
		this.description = description;
		this.secParams = secParams;
		this.guide = guide;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public double getUnitCost(Patient pat) {
		return secParams.getCostParam(getUnitCostParameterString(false), pat.getSimulation());
	}

	/**
	 * @return the guide
	 */
	public Guideline getGuide() {
		return guide;
	}

}
