/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * The state of a simulataneous flow.
 * @author Iv�n Castilla Rodr�guez
 */
public class SimultaneousFlowState extends GroupFlowState {
	private static final long serialVersionUID = 8758311863142631234L;

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
