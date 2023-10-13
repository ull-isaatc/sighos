/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

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
	public AttributeValueWrapper(OSDiWrapper wrap, String attributeValueId, Set<ExpressionWrapper.SupportedType> supportedTypes) throws MalformedOSDiModelException {
		super(wrap, attributeValueId, supportedTypes);
		attributeId = OSDiWrapper.ObjectProperty.IS_VALUE_OF_ATTRIBUTE.getValue(attributeValueId);
		
	}
	
	/**
	 * @return the attributeId
	 */
	public String getAttributeId() {
		return attributeId;
	}
	
}
