package es.ull.isaatc.simulation.common.inforeceiver;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.variable.Variable;

public abstract class VarView extends View implements Variable {

	String varName;
	
	public VarView(Simulation simul, String varName) {
		super(simul, "VARVIEW " + varName);
		this.varName = varName;
	}

	public String getVarName() {
		return varName;
	}
	
}
