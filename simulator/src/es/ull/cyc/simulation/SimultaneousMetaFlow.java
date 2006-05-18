/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimultaneousMetaFlow extends GroupMetaFlow {

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, RandomNumber iterations) {
		super(id, null, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, OptionMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
	}

	/**
	 * @param parent
	 * @param iterations
	 */
	public SimultaneousMetaFlow(int id, GroupMetaFlow parent, RandomNumber iterations) {
		super(id, parent, iterations);
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.MetaFlow#getFlow(es.ull.cyc.simulation.Flow, es.ull.cyc.simulation.Element)
	 */
	public Flow getFlow(Flow parentFlow, Element e) {
		int iter = iterations.sampleInt();
		SimultaneousFlow flow = null;
		SequenceFlow sec = null;
		
		if (iter == 1) {
			if (parentFlow instanceof SimultaneousFlow)
				flow = (SimultaneousFlow)parentFlow;
			else
				flow = new SimultaneousFlow((SequenceFlow)parentFlow, e);
			getDescendantsFlows(flow, e);
			if (flow.list.isEmpty())
				return null;
			else
				return flow;
		}
		else if (iter > 1) {
			if ((parentFlow == null) || (parentFlow instanceof SimultaneousFlow))
				sec = new SequenceFlow((SimultaneousFlow)parentFlow, e);
			else
				sec = (SequenceFlow)parentFlow;
			for (int i = 0; i < iter; i++) {
				flow = new SimultaneousFlow(sec, e);
				getDescendantsFlows(flow, e);	
			}
			
			if (sec.list.isEmpty())
				return null;
			else
				return sec;
		}
		return null; 
	}

}
