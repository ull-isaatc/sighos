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
    /** Actividad que conforma el flujo */
    protected Activity act;
    
	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, RandomVariate iterations, Activity act) {
		super(id, null, iterations);
		this.act = act;
	}
	
	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, GroupMetaFlow parent, RandomVariate iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, OptionMetaFlow parent, RandomVariate iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param parent
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
