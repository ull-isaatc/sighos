package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.*;
import es.ull.isaatc.util.OrderedList;

/**
 * This class validates each model's element described in XML
 * 
 * @author Roberto Muñoz
 */
public class ModelValidator {
   
    /** validator for resource types */
    private ResourceTypeValidate resTypeVal;

    /** validator for work groups */
    private WorkGroupValidate wgVal;

    /** validator for resources */
    private ResourceValidate resVal;

    /** validator for activites */
    private ActivityValidate actVal;

    /** validator for element types */
    private ElementTypeValidate elemTypeVal;

    /** validator for root flows */
    private RootFlowValidate rootFlowVal;

    /**
     * 
     */
    public ModelValidator(OrderedList<ModelMappingTable> modelList) {
		resTypeVal = new ResourceTypeValidate(modelList);
		wgVal = new WorkGroupValidate(resTypeVal, modelList);
		resVal = new ResourceValidate(resTypeVal, modelList);
		actVal = new ActivityValidate(wgVal, modelList);
		elemTypeVal = new ElementTypeValidate(modelList);
		rootFlowVal = new RootFlowValidate(actVal, elemTypeVal, modelList);
    }

    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(Resource valObj) throws ModelException {

    	return resVal.validate(valObj);
    }

    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(ResourceType valObj) throws ModelException {

    	return resTypeVal.validate(valObj);
    }

    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(WorkGroup valObj) throws ModelException {

    	return wgVal.validate(valObj);
    }
    
    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(Activity valObj) throws ModelException {

    	return actVal.validate(valObj);
    }

    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(ElementType valObj) throws ModelException {

    	return elemTypeVal.validate(valObj);
    }

    /**
     * Validates an object
     * 
     * @param valObj object to validate
     * @return true if its OK
     */
    public boolean validate(RootFlow valObj) throws ModelException {

    	return rootFlowVal.validate(valObj);
    }

    /**
     * @return the hasErrors
     */
    public boolean hasErrors() {

		int sz = 0;
		sz += resVal.errorList.size();
		sz += resTypeVal.errorList.size();
		sz += wgVal.errorList.size();
		sz += actVal.errorList.size();
		sz += elemTypeVal.errorList.size();
		sz += rootFlowVal.errorList.size();
		return (sz > 0) ? true : false;
    }

    /**
     * Print to STDERR the erros found in the model.
     */
    public void showErrors() {
		showResTypeErrors();
		showWGErrors();
		showResourceErrors();
		showActivityErrors();
		showElementTypeErrors();
		showRootFlowErrors();
    }
    
    public void showResTypeErrors() {

    	resTypeVal.showErrors("RESTYPE");
    }

    public void showWGErrors() {

    	resTypeVal.showErrors("WG");
    }

    public void showResourceErrors() {

    	resVal.showErrors("RES");
    }

    public void showActivityErrors() {

    	actVal.showErrors("ACT");
    }

    public void showElementTypeErrors() {

    	elemTypeVal.showErrors("ELEMTYPE");
    }

    public void showRootFlowErrors() {

    	rootFlowVal.showErrors("FLOW");
    }
}
