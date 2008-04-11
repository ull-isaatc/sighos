/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.TreeMap;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.DescComponent;
import es.ull.isaatc.simulation.xml.ModelMappingTable;

/**
 * @author Roberto Muñoz
 *
 */
public abstract class DescComponentValidate extends BaseComponentValidate {
  
    public DescComponentValidate(TreeMap<Integer, ModelMappingTable> modelList) {
	super(modelList);
    }

    public boolean checkComponent(DescComponent component) {
		boolean hasError = false;
		
		// check the component definition
		if (component.getId() < 1) {
		    error(component, UNDEF_ID);
		    hasError = true;
		}
		
		if ((component.getDescription() == null)  || (component.getDescription().length() < 1)) {
		    error(component, UNDEF_DESCRIPTION);
		    hasError = true;
		}
	
		return hasError;
    }
    
    /* (non-Javadoc)
     * @see es.ull.isaatc.simulation.xml.validation.BaseComponentValidate#validate(es.ull.isaatc.simulation.xml.BaseComponent)
     */
    @Override
    public boolean validate(BaseComponent component) throws ModelException {
	DescComponent dc = (DescComponent) component;
	boolean hasError = false;

	hasError |= checkComponent(dc);

	return !hasError;
    }
}
