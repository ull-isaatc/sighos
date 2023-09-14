/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;

/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class UtilityParameterWrapper extends ParameterWrapper {
	private final OSDiWrapper.TemporalBehavior temporalBehavior;
	private final OSDiWrapper.UtilityType type;
	/**
	 * @param wrap
	 * @param paramId
	 * @param defaultDetValue
	 * @throws MalformedOSDiModelException
	 */
	public UtilityParameterWrapper(OSDiWrapper wrap, String paramId)
			throws MalformedOSDiModelException {
		super(wrap, paramId, Double.NaN);
		temporalBehavior = OSDiWrapper.TemporalBehavior.valueOf(OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(wrap, paramId, OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.getShortName()));
		
		switch(getDataItemType()) {
		case DI_DISUTILITY:
			type = OSDiWrapper.UtilityType.DISUTILITY;
			break;			
		case DI_UNDEFINED:
			wrap.printWarning(paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type not specified fot utility. Assuming that it is a utility");
		case DI_UTILITY:
			type = OSDiWrapper.UtilityType.UTILITY;
			break;
		default:
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.UTILITY, paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Using something else as utility: " + getDataItemType().getShortName());
		}
		// Fix deterministic value in case it was not specified
		if (Double.isNaN(getDeterministicValue())) {
			setDeterministicValue(OSDiWrapper.UtilityType.UTILITY.equals(type) ? 1.0 : 0.0);
			wrap.printWarning(paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Fixing the default value of the utility. Using " + getDeterministicValue());
		}
	}
	
	/**
	 * @return the temporalBehavior
	 */
	public OSDiWrapper.TemporalBehavior getTemporalBehavior() {
		return temporalBehavior;
	}
	
	/**
	 * @return the type
	 */
	public OSDiWrapper.UtilityType getType() {
		return type;
	}

}
