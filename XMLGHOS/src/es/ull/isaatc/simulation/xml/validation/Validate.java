/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a validation class for elements in a model
 * 
 * @author Roberto Muñoz
 */
public abstract class Validate {

    /** Errors list */
    protected ArrayList<ErrorElement> errors;

    /** Element table */
    protected List<Integer> elementsId;

    protected List<String> elementsDesc;

    /** Error code */
    protected static final int ERR_ID = 1;

    protected static final int ERR_DESC = 2;

    protected static final int ERR_UNDEF_ID = 3;

    protected static final int ERR_UNDEF_DESC = 4;

    /** Messages */
    protected static final String DUPLICATE_ID = "Duplicate id";

    protected static final String DUPLICATE_DESC = "Duplicate description";

    protected static final String UNDEF_ID = "Undefined id";

    protected static final String UNDEF_DESC = "Undefined description";

    protected ArrayList<String> errMsg;

    /**
         * Creates a validation object
         */
    public Validate() {

	super();
	errMsg = new ArrayList<String>();
	errMsg.add("");
	errMsg.add(DUPLICATE_ID);
	errMsg.add(DUPLICATE_DESC);
	errMsg.add(UNDEF_ID);
	errMsg.add(UNDEF_DESC);
	elementsId = new LinkedList<Integer>();
	elementsDesc = new LinkedList<String>();
	errors = new ArrayList<ErrorElement>();
    }

    /**
         * Adds a model element into the list
         * 
         * @param id
         * @param description
         */
    public void add(int id, String description) {

	if (id != -1) {
	    int index = Collections.binarySearch(elementsId, id);
	    // Add the element to the list
	    if (index < 0)
		elementsId.add(-index - 1, id);
	}

	if (description != null) {
	    int index = Collections.binarySearch(elementsDesc, description);
	    // Add the element to the list
	    if (index < 0)
		elementsDesc.add(-index - 1, description);
	}
    }

    /**
         * Returns true if the table has an elemnt with the same id or the same
         * description
         * 
         * @param id
         * @param description
         */
    public boolean has(int id, String description) {

	int idIndex = 0;
	int descIndex = 0;
	boolean hasError = false;

	if (id != -1)
	    idIndex = Collections.binarySearch(elementsId, id);
	if (description != null)
	    descIndex = Collections.binarySearch(elementsDesc, description);

	if ((id != -1) && (idIndex >= 0)) {
	    error(id, errMsg.get(ERR_ID));
	    hasError = true;
	}
	if ((description != null) && (descIndex >= 0)) {
	    error(id, errMsg.get(ERR_ID));
	    hasError = true;
	}
	return hasError;
    }

    /**
         * Checks if the id is it correct
         * 
         * @param id
         */
    public boolean checkId(int id) {

	boolean hasError = false;
	if (id < 1) {
	    error(id, errMsg.get(ERR_UNDEF_ID));
	    hasError = true;
	}
	return hasError;
    }

    /**
         * Checks if the description is it correct
         * 
         * @param description
         */
    public boolean checkDescription(int id, String description) {

	boolean hasError = false;
	if ((description == null) || (description.length() == 0)) {
	    error(id, errMsg.get(ERR_UNDEF_DESC));
	    hasError = true;
	}
	return hasError;
    }

    /**
         * Search for the description in the elementsDesc list
         * 
         * @param description
         */
    public boolean findDescription(String description) {

	if (Collections.binarySearch(elementsDesc, description) >= 0)
	    return true;
	return false;
    }

    /**
         * Search for the id in the elementsId list
         * 
         * @param description
         */
    public boolean findId(int id) {

	if (Collections.binarySearch(elementsId, id) >= 0)
	    return true;
	return false;
    }

    public void error(int id, String msg) {

	errors.add(new ErrorElement(id, msg));
    }

    /**
         * Print the errors
         */
    public void showErrors(String header) {

	Iterator<ErrorElement> errorsIt = errors.iterator();
	while (errorsIt.hasNext()) {
	    ErrorElement err = errorsIt.next();
	    System.err.println(header + "[" + err.getId() + "] : "
		    + err.getError());
	}
    }

    /**
         * Returns true if the object is valid
         */
    public abstract boolean validate(Object valObj) throws ModelException;

    /**
         * @author Roberto Muñoz
         */
    public final class ErrorElement {

	/** Element id */
	protected final int id;

	/** Element error */
	protected final String error;

	/**
         * @param id
         * @param error
         */
	public ErrorElement(int id, String error) {

	    super();
	    this.id = id;
	    this.error = error;
	}

	/**
         * @return the error
         */
	public String getError() {

	    return error;
	}

	/**
         * @return the id
         */
	public Integer getId() {

	    return id;
	}
    }
}
