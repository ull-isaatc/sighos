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
	/**
	 * The temporal behavior for this utility
	 */
	private final OSDiWrapper.TemporalBehavior temporalBehavior;
	/**
	 * The type (UTILITY, DISUTILITY) of this utility
	 */
	private final OSDiWrapper.UtilityType type;
	
	/**
	 * Creates a wrapper for a utility defined in the ontology.
	 * @param wrap A wrapper for the ontology
	 * @param paramId The IRI of the utility instance in the ontology
	 * @throws MalformedOSDiModelException If the utility wrapper cannot be created due to incorrect definitions in the ontology
	 */
	public UtilityParameterWrapper(OSDiWrapper wrap, String paramId) throws MalformedOSDiModelException {
		super(wrap, paramId, Double.NaN);
		temporalBehavior = OSDiWrapper.TemporalBehavior.valueOf(OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(paramId, OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.getShortName()));
		
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
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.UTILITY, paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Using something else as utility: " + getDataItemType().getInstanceName());
		}
		// Fix deterministic value in case it was not specified
		if (Double.isNaN(getDeterministicValue())) {
			setDeterministicValue(OSDiWrapper.UtilityType.UTILITY.equals(type) ? 1.0 : 0.0);
			wrap.printWarning(paramId, OSDiWrapper.DataProperty.HAS_EXPRESSION, "Fixing the default value of the utility. Using " + getDeterministicValue());
		}
	}
	
	/**
	 * Returns the temporal behavior for this utility. By default, it is set to NOT_SPECIFIED if it was not specified by the ontology 
	 * @return the temporal behavior for this utility.
	 */
	public OSDiWrapper.TemporalBehavior getTemporalBehavior() {
		return temporalBehavior;
	}
	
	/**
	 * Returns the type (UTILITY, DISUTILITY) of this utility
	 * @return the type (UTILITY, DISUTILITY) of this utility
	 */
	public OSDiWrapper.UtilityType getType() {
		return type;
	}

}
