/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * The state of a sequence flow.
 * @author Iván Castilla Rodríguez
 *
 */
public class SequenceFlowState extends GroupFlowState {

	public SequenceFlowState(int finished) {
		super(finished);
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("SEQ FLOW (Finished " + finished + ") {");
		for (FlowState fs : descendants) 
			str.append(fs + "\t");
		str.append("}\r\n");
		return str.toString();
	}
}
