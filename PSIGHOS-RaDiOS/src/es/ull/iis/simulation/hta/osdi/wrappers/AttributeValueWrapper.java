/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AttributeValueWrapper extends ParameterWrapper {
	private final String attributeId;
	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public AttributeValueWrapper(OSDiWrapper wrap, String attributeValueId) throws MalformedOSDiModelException {
		super(wrap, attributeValueId);
		attributeId = OSDiObjectProperties.IS_VALUE_OF_ATTRIBUTE.getValue(attributeValueId);
		
	}
	
	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}
	
}
