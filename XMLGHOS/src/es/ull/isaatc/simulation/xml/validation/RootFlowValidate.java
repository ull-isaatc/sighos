/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.RootFlow;

/**
 * @author Roberto Muñoz
 */
public class RootFlowValidate extends Validate {
	
	/** Error codes */
	protected static final int ERR_RFLOW_ID = 6;
	
	/** Messages */
	protected static final String RFLOW_ID = "Unknown root flow";
	
	
	
	public RootFlowValidate() {

		super();
	}
	
	
	@Override
	public boolean validate(Object valObj) throws ModelException {

		RootFlow rf = (RootFlow) valObj;
		boolean hasError = false;
		
		hasError |= checkId(rf.getId());
		hasError |= checkDescription(rf.getId(), rf.getDescription());
		hasError |= has(rf.getId(), rf.getDescription());
		
		if (hasError) {
			throw new ModelException("Root flow error");
		}
		add(rf.getId(), rf.getDescription());
		return !hasError;
	}
}
