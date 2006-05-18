/**
 * 
 */
package es.ull.cyc.simulation.bind.validation;

import es.ull.cyc.simulation.bind.Flow;
import es.ull.cyc.simulation.bind.FlowType;

/**
 * @author Roberto Muñoz
 *
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
	
	protected boolean checkType(int id, FlowType type) {

		for (FlowType tp: FlowType.values())
			if (tp.equals(type))
				return false;
		error(id, this.TYPE_ID);
		return true;
	}
	
	protected boolean checkActivity(int id, int actId) {
	
		if (actVal.findId(actId))
			return false;
		error(id, this.ACT_ID + " " + actId);
		return true;
	}
	
	@Override
	public boolean validate(Object valObj) throws ModelException {
		Flow flow = (Flow)valObj;
		boolean hasError = false;
	
		hasError |= checkId(flow.getId());
		hasError |= checkType(flow.getId(), flow.getType());
		if (FlowType.SINGLE.equals(flow.getType()))
			hasError |= checkActivity(flow.getId(), flow.getActId());
		hasError |= has(flow.getId(), null);
		
		if (hasError) {
			throw new ModelException("Flow error");
		}
		add(flow.getId(), null);
		return !hasError;
	}


}
