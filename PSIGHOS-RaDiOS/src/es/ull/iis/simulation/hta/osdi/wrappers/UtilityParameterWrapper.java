/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

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
	public UtilityParameterWrapper(OSDiWrapper wrap, String paramId, String defaultDescription) throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDescription);
		temporalBehavior = OSDiWrapper.TemporalBehavior.valueOf(OSDiWrapper.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(paramId, OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.getShortName()));

		final Set<OSDiWrapper.DataItemType> dataItems = getDataItemTypes();
		
		if (dataItems.contains(OSDiWrapper.DataItemType.DI_DISUTILITY)) {
			type = OSDiWrapper.UtilityType.DISUTILITY;			
		}
		else if (dataItems.contains(OSDiWrapper.DataItemType.DI_UTILITY)) {
			type = OSDiWrapper.UtilityType.UTILITY;			
		}
		else {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.UTILITY, paramId, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item type for utility does not include utility or disutility");			
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
