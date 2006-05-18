/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.random.RandomNumber;

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

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.MetaFlow#getFlow(es.ull.cyc.simulation.Flow, es.ull.cyc.simulation.Element)
	 */
	public Flow getFlow(Flow parentFlow, Element e) {
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
