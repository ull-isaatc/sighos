/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AttributeValueWrapper extends ValuableWrapper {
	private final String attributeId;
	/**
	 * @throws MalformedOSDiModelException 
	 * 
	 */
	public AttributeValueWrapper(OSDiWrapper wrap, String attributeValueId, double defaultDetValue) throws MalformedOSDiModelException {
		super(wrap, attributeValueId, defaultDetValue);
		attributeId = OSDiWrapper.ObjectProperty.IS_VALUE_OF_ATTRIBUTE.getValue(wrap, attributeValueId);
		
	}
	
	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}
}
