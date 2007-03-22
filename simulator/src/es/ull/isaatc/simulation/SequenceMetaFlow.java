/**
 * 
 */
package es.ull.isaatc.simulation;

import simkit.random.RandomVariate;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SequenceMetaFlow extends GroupMetaFlow {

	/**
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, RandomVariate iterations) {
		super(id, null, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, TypeBranchMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SequenceMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		int iter = (int)iterations.generate();
		SequenceFlow flow;
		
		if (iter == 0)
			return true;
		if (parentFlow instanceof SequenceFlow)
			flow = (SequenceFlow)parentFlow;
		else
			flow = new SequenceFlow((SimultaneousFlow)parentFlow, e);
		if (parentFlow == null)
			e.setFlow(flow);
		boolean exit = false;
		for (int i = 0; (i < iter) && !exit; i++)
			exit = !getDescendantsFlows(flow, e);
		return !exit;
	}
}
