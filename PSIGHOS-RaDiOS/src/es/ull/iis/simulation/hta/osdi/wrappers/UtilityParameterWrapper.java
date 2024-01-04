/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;

/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class UtilityParameterWrapper extends ParameterWrapper {
	/** The temporal behavior of the parameter: if true, applies one time; otherwise applies annually */
	private final boolean appliesOneTime;
	/** Whether this is a disutility */
	private final boolean disutility;
	
	/**
	 * Creates a wrapper for a utility defined in the ontology.
	 * @param wrap A wrapper for the ontology
	 * @param paramId The IRI of the utility instance in the ontology
	 * @throws MalformedOSDiModelException If the utility wrapper cannot be created due to incorrect definitions in the ontology
	 */
	public UtilityParameterWrapper(OSDiWrapper wrap, String paramId, String defaultDescription) throws MalformedOSDiModelException {
		super(wrap, paramId, defaultDescription);
		appliesOneTime = (OSDiDataProperties.APPLIES_ONE_TIME.getValue(paramId, "false") == "true");

		final Set<OSDiDataItemTypes> dataItems = getDataItemTypes();
		
		if (dataItems.contains(OSDiDataItemTypes.DI_DISUTILITY)) {
			disutility = true;			
		}
		else if (dataItems.contains(OSDiDataItemTypes.DI_UTILITY)) {
			disutility = false;			
		}
		else {
			throw new MalformedOSDiModelException(OSDiClasses.UTILITY, paramId, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Data item type for utility does not include utility or disutility");			
		}
	}
	
	/**
	 * Returns the temporal behavior of the parameter: if true, applies one time; otherwise applies annually
	 * @return the temporal behavior of the parameter: if true, applies one time; otherwise applies annually
	 */
	public boolean appliesOneTime() {
		return appliesOneTime;
	}
	
	/**
	 * Returns true it this is a disutility; false if it is a utility
	 * @return true it this is a disutility; false if it is a utility
	 */
	public boolean isDisutility() {
		return disutility;
	}

}
