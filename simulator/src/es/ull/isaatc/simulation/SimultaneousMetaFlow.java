/**
 * 
 */
package es.ull.isaatc.simulation;

import simkit.random.RandomVariate;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimultaneousMetaFlow extends GroupMetaFlow {

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, RandomVariate iterations) {
		super(id, null, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, TypeBranchMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations) {
		super(id, parent, iterations);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		int iter = (int)iterations.generate();
		SimultaneousFlow flow = null;
		SequenceFlow sec = null;
		
		if (iter == 1) {
			if (parentFlow instanceof SimultaneousFlow)
				flow = (SimultaneousFlow)parentFlow;
			else
				flow = new SimultaneousFlow((SequenceFlow)parentFlow, e);
			if (parentFlow == null)
				e.setFlow(flow);
			return getDescendantsFlows(flow, e);
		}
		else if (iter > 1) {
			if ((parentFlow == null) || (parentFlow instanceof SimultaneousFlow))
				sec = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			else
				sec = (SequenceFlow)parentFlow;
			if (parentFlow == null)
				e.setFlow(sec);
			boolean exit = false;
			for (int i = 0; (i < iter) && !exit; i++) {
				flow = new SimultaneousFlow(sec, e);
				exit = getDescendantsFlows(flow, e);	
			}
			return !exit;
		}
		return true;
	}

}
