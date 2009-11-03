package es.ull.isaatc.simulation.common.inforeceiver;

import es.ull.isaatc.simulation.common.Model;
import es.ull.isaatc.simulation.variable.Variable;

public abstract class VarView extends View implements Variable {

	String varName;
	
	public VarView(Model simul, String varName) {
		super(simul, "VARVIEW " + varName);
		this.varName = varName;
	}

	public String getVarName() {
		return varName;
	}
	
}
