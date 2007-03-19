/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * The state of a simulataneous flow.
 * @author Iván Castilla Rodríguez
 */
public class SimultaneousFlowState extends GroupFlowState {

	public SimultaneousFlowState(int finished) {
		super(finished);
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("SIM FLOW (Finished " + finished + ") {");
		for (FlowState fs : descendants) 
			str.append(fs + "\t");
		str.append("}\r\n");
		return str.toString();
	}
}
