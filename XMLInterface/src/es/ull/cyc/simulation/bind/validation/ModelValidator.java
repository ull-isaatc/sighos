
package es.ull.cyc.simulation.bind.validation;

import es.ull.cyc.simulation.bind.*;

/**
 * This class validates each model's element described in XML
 * @author Roberto Muñoz
 */
public class ModelValidator {
	/** validator for resource types */
	ResourceTypeValidate resTypeVal = new ResourceTypeValidate();
	/** validator for resources */
	ResourceValidate resVal = new ResourceValidate(resTypeVal);
	/** validator for activites */
	ActivityValidate actVal = new ActivityValidate();
	/** validator for flows */
	FlowValidate flowVal = new FlowValidate(actVal);
	
	/**
	 * 
	 */
	public ModelValidator() {
		super();
	}
	
	/**
	 * Validates an object
	 * @param valObj object to validate
	 * @return true if its OK
	 */
	public boolean validate(Resource valObj) throws ModelException {
		return resVal.validate(valObj);
	}

	/**
	 * Validates an object
	 * @param valObj object to validate
	 * @return true if its OK
	 */
	public boolean validate(ResourceType valObj) throws ModelException {
		return resTypeVal.validate(valObj);
	}
	
	/**
	 * Validates an object
	 * @param valObj object to validate
	 * @return true if its OK
	 */
	public boolean validate(Activity valObj) throws ModelException {
		return actVal.validate(valObj);
	}
	
	/**
	 * Validates an object
	 * @param valObj object to validate
	 * @return true if its OK
	 */
	public boolean validate(Flow valObj) throws ModelException {
		return flowVal.validate(valObj);
	}
	
	/**
	 * @return the hasErrors
	 */
	public boolean hasErrors() {
		int sz = 0;
		sz += resVal.errors.size();
		sz += resTypeVal.errors.size();
		sz += actVal.errors.size();
		sz += flowVal.errors.size();
		return (sz > 0) ? true : false;
	}
	
	/**
	 * Print to STDOUT the errors
	 */
	public void showResTypeErrors() {
		resTypeVal.showErrors("RESTYPE");
	}

	public void showResourceErrors() {
		resVal.showErrors("RES");
	}

	public void showActivityErrors() {
		actVal.showErrors("ACT");	
	}	

	public void showFlowErrors() {
		flowVal.showErrors("FLOW");	
	}

}
