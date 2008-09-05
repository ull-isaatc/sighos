/**
 * 
 */
package es.ull.isaatc.simulation;

import simkit.random.RandomVariate;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleMetaFlow extends MetaFlow {
    /** The activity to perform */
    protected Activity act;
    
	/**
	 * @param id Unique identifier
	 * @param iterations Number of iterations this metaflow should be done
	 * @param act Activity to perform
	 */
	public SingleMetaFlow(int id, RandomVariate iterations, Activity act) {
		super(id, null, iterations);
		this.act = act;
	}
	
	/**
	 * @param id Unique identifier
	 * @param parent Parent metaflow
	 * @param iterations Number of iterations this flow should be done
	 * @param act Activity to perform
	 */
	public SingleMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param id Unique identifier
	 * @param parent Parent metaflow
	 * @param iterations Number of iterations this flow should be done
	 * @param act Activity to perform
	 */
	public SingleMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param id Unique identifier
	 * @param parent Parent metaflow
	 * @param iterations Number of iterations this flow should be done
	 * @param act Activity to perform
	 */
	public SingleMetaFlow(int id, TypeBranchMetaFlow parent, RandomVariate iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
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
			flow = new SingleFlow((GroupFlow)parentFlow, e, act);
		else  {
			if (parentFlow instanceof SequenceFlow)
				flow = (SequenceFlow)parentFlow;
			else
				flow = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			for (int i = 0; i < iter; i++)
				new SingleFlow((GroupFlow)flow, e, act);
		}
		if (parentFlow == null)
			e.setFlow(flow);
		return true;
	}

	protected void add(MetaFlow descendant) {
	}
}
