package es.ull.isaatc.simulation.inforeceiver;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.variable.Variable;

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
