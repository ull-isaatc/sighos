/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.ModelMappingTable;

/**
 * This class represents a validation class for elements in a model
 * 
 * @author Roberto Muñoz
 */
public abstract class BaseComponentValidate {

    /** Mapping table */
    protected TreeMap<Integer, ModelMappingTable> modelList;
    
    /** Errors list */
    protected ArrayList<ErrorElement> errorList;

    /** Error messages */
    protected static final String UNDEF_ID = "Undefined id";
    protected static final String UNDEF_DESCRIPTION = "Undefined description";

    /**
     * Creates a validation object
     */
    public BaseComponentValidate(TreeMap<Integer, ModelMappingTable> modelList) {
	this.modelList = modelList;
	errorList = new ArrayList<ErrorElement>();
    }

    /**
     * Checks the component
     * 
     * @param component the model component to check
     */
    public boolean checkComponent(BaseComponent component) {
	boolean hasError = false;
	
	// check the component definition
	if (component.getId() < 1) {
	    error(component, UNDEF_ID);
	    hasError = true;
	}
	
	return hasError;
    }

    /**
     * Inserts an error in the error list.
     * @param component the component with the error
     * @param errMsg the error message
     */
    public void error(BaseComponent component, String errMsg) {
	errorList.add(new ErrorElement(component, errMsg));
    }

    /**
     * Print the errors in STDERR
     */
    public void showErrors(String header) {

	for (ErrorElement errElem : errorList) {
	    System.err.println(header + "[" + errElem.getComponent().getId() + "] : " + errElem.getError());
	}
    }

    /**
     * Returns true if the object is valid
     */
    public abstract boolean checkReference(ComponentRef ref);

    
    /**
     * Returns true if the object is valid
     */
    public abstract boolean validate(BaseComponent component) throws ModelException;

    /**
     * Encapsulates an error in the model
     * @author Roberto Muñoz
     */
    public final class ErrorElement {

	/** Component with an error */
	BaseComponent component;

	/** Error description */
	protected final String error;

	/**
	 * 
	 * @param component
	 * @param error
	 */
	public ErrorElement(BaseComponent component, String error) {

	    this.component = component;
	    this.error = error;
	}

	/**
	 * @return the error
	 */
	public String getError() {

	    return error;
	}

	/**
	 * @return the component
	 */
	public BaseComponent getComponent() {

	    return component;
	}
    }
}
