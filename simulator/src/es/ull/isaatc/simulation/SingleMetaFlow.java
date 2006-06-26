/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.random.RandomNumber;

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
	public SingleMetaFlow(int id, RandomNumber iterations, Activity act) {
		super(id, null, iterations);
		this.act = act;
	}
	
	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/**
	 * @param parent
	 */
	public SingleMetaFlow(int id, TypeBranchMetaFlow parent, RandomNumber iterations, Activity act) {
		super(id, parent, iterations);
		this.act = act;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MetaFlow#getFlow(es.ull.isaatc.simulation.Flow, es.ull.isaatc.simulation.Element)
	 */
	public boolean getFlow(Flow parentFlow, Element e) {
		int iter = iterations.sampleInt();
		SequenceFlow sec;
		
		if (iter == 0)
			return null;
		else if (iter == 1)
			return new SingleFlow((GroupFlow)parentFlow, e, act);
		else  {			
			if (parentFlow instanceof SequenceFlow)
				sec = (SequenceFlow)parentFlow;
			else
				sec = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			for (int i = 0; i < iter; i++)
				new SingleFlow(sec, e, act);
			return sec;
		}
	}

	protected void add(MetaFlow descendant) {
	}
}
