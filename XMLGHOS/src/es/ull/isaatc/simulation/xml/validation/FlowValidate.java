/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.Flow;
import es.ull.isaatc.simulation.xml.SingleFlow;

/**
 * @author Roberto Muñoz
 */
public class FlowValidate extends Validate {
	
	/** Activity validation object */
	ActivityValidate actVal;
	
	/** Error codes */
	protected static final int ERR_TYPE = 5;
	
	protected static final int ERR_ACT_ID = 6;
	
	/** Messages */
	protected static final String TYPE_ID = "Unknown type";
	
	protected static final String ACT_ID = "Unknown activity";
	
	
	
	public FlowValidate(ActivityValidate actVal) {

		super();
		this.actVal = actVal;
	}
	
	
	protected boolean checkActivity(int id, int actId) {

		if (actVal.findId(actId))
			return false;
		error(id, this.ACT_ID + " " + actId);
		return true;
	}
	
	
	@Override
	public boolean validate(Object valObj) throws ModelException {

		Flow flow = (Flow) valObj;
		boolean hasError = false;
		
		hasError |= checkId(flow.getId());
		if (flow instanceof SingleFlow)
			hasError |= checkActivity(flow.getId(), ((SingleFlow) flow).getActId());
		hasError |= has(flow.getId(), null);
		
		if (hasError) {
			throw new ModelException("Flow error");
		}
		add(flow.getId(), null);
		return !hasError;
	}
	
}
