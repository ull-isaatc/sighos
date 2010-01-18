/**
 * 
 */
package es.ull.isaatc.simulation;

import simkit.random.RandomVariate;

/**
 * @author Iván
 *
 */
public class TransitionSingleMetaFlow extends SingleMetaFlow {

	/**
	 * @param id
	 * @param parent
	 * @param iterations
	 * @param act
	 */
	public TransitionSingleMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations, TransitionActivity act) {
		super(id, parent, iterations, act);
	}

	/**
	 * @param id
	 * @param parent
	 * @param iterations
	 * @param act
	 */
	public TransitionSingleMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations, TransitionActivity act) {
		super(id, parent, iterations, act);
	}

	/**
	 * @param id
	 * @param iterations
	 * @param act
	 */
	public TransitionSingleMetaFlow(int id, RandomVariate iterations, TransitionActivity act) {
		super(id, iterations, act);
	}

	/**
	 * @param id
	 * @param parent
	 * @param iterations
	 * @param act
	 */
	public TransitionSingleMetaFlow(int id, TypeBranchMetaFlow parent, RandomVariate iterations, TransitionActivity act) {
		super(id, parent, iterations, act);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		Flow flow;
		int iter = (int)iterations.generate();

		if (iter == 0)
			return true;
		else if (iter == 1)
			flow = new TransitionSingleFlow((GroupFlow)parentFlow, e, (TransitionActivity)act);
		else  {
			if (parentFlow instanceof SequenceFlow)
				flow = (SequenceFlow)parentFlow;
			else
				flow = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			for (int i = 0; i < iter; i++)
				new TransitionSingleFlow((GroupFlow)flow, e, (TransitionActivity)act);
		}
		if (parentFlow == null)
			e.setFlow(flow);
		return true;
	}
	
}
