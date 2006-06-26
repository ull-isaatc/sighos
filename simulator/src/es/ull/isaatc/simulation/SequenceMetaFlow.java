/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SequenceMetaFlow extends GroupMetaFlow {

	/**
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, TypeBranchMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		int iter = iterations.sampleInt();
		SequenceFlow flow = null;
		
		if (iter == 0)
			return null;
		if (parentFlow instanceof SequenceFlow)
			flow = (SequenceFlow)parentFlow;
		else
			flow = new SequenceFlow((SimultaneousFlow)parentFlow, e);		
		for (int i = 0; i < iter; i++)
			getDescendantsFlows(flow, e);
		if (flow.list.isEmpty())
			return null;
		else
			return flow;
	}
}
