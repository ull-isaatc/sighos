/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;

/**
 * TODO Process currency
 * @author Iván Castilla Rodríguez
 *
 */
public class CostParameterWrapper extends ParameterWrapper {
	/** The temporal behavior of the parameter: if true, applies one time; otherwise applies annually */
	private final boolean appliesOneTime;
	/**
	 * @param wrap
	 * @param paramId
	 * @param defaultDetValue
	 * @throws MalformedOSDiModelException
	 */
	public CostParameterWrapper(OSDiWrapper wrap, String paramId, String defaultDescription)
			throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDescription);
		appliesOneTime = (OSDiDataProperties.APPLIES_ONE_TIME.getValue(paramId, "false").equals("true"));
	}
	
	/**
	 * Returns the temporal behavior of the parameter: if true, applies one time; otherwise applies annually
	 * @return the temporal behavior of the parameter: if true, applies one time; otherwise applies annually
	 */
	public boolean appliesOneTime() {
		return appliesOneTime;
	}

	@Override
	protected RandomVariate getRandomVariateFromAvgAndSD(double avg, double sd) {
		final double[] gammaParams = Statistics.gammaParametersFromNormal(avg, sd);
		// By default, uncertainty in costs is modeled as a gamma distribution
		return super.getRandomVariateFromAvgAndSD(gammaParams[0], gammaParams[1]);
	}
}
