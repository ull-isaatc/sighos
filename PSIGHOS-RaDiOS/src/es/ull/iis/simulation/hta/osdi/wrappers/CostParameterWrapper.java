/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;

/**
 * TODO Process currency
 * @author Iván Castilla Rodríguez
 *
 */
public class CostParameterWrapper extends ParameterWrapper {
	private final OSDiWrapper.TemporalBehavior temporalBehavior;
	/**
	 * @param wrap
	 * @param paramId
	 * @param defaultDetValue
	 * @throws MalformedOSDiModelException
	 */
	public CostParameterWrapper(OSDiWrapper wrap, String paramId, String defaultDescription, Set<ExpressionWrapper.SupportedType> supportedTypes)
			throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDescription, supportedTypes);
		temporalBehavior = OSDiWrapper.TemporalBehavior.valueOf(OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(paramId, OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.getShortName()));
	}
	
	/**
	 * @return the temporalBehavior
	 */
	public OSDiWrapper.TemporalBehavior getTemporalBehavior() {
		return temporalBehavior;
	}

}
