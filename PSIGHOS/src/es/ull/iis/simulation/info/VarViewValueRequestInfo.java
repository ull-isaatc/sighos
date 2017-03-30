package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public class VarViewValueRequestInfo extends SynchronousInfo {

	private final String varName;
	private final Object requestObject;
	private Object[] params;
	
	public VarViewValueRequestInfo(Simulation model, String varName, Object requestObject, Object[] params, long ts) {
		super(model, ts);
		this.varName = varName;
		this.requestObject = requestObject;
		this.params = params;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public String getVarName() {
		return varName;
	}

	public Object getRequestObject() {
		return requestObject;
	}

	@Override
	public String toString() {
		String message = new String();
		message += simul.long2SimulationTime(ts) + "\tVARVIEWVALUEREQUEST:\t" + varName + "\tREQOBJ: " + requestObject.toString() + "\t" + simul.toString() + "\n";
		for (int i = 0; i < params.length; i++)
			message += "\tPARAM" + (i+1) + ": " + params[i].toString();
		return message;
	}

}
