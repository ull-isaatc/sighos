/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ContainerCreator extends TimeDrivenElementGenerator {
	private ArrivalPlanning plan;
	private ElementType et;
	private InitializerFlow flow;
	
	/**
	 * @param sim
	 * @param nElem
	 * @param et
	 * @param flow
	 */
	public ContainerCreator(PortModel model, ArrivalPlanning plan, ElementType et, InitializerFlow flow) {
		super(model, plan, et, flow, plan);
		this.plan = plan;
		this.et = et;
		this.flow = flow;
	}
	
	@Override
	public EventSource[] create(long ts) {
		int n = getSampleNElem();
		n = beforeCreateElements(n);
		final EventSource[] elems = new EventSource[n];
		final int[] containers = plan.getDestinationBlocks(ts);
		for (int i = 0; i < containers.length; i++) {
			elems[i] = new Container(simul, et, flow, plan.getBerth(), containers[i]);
            final DiscreteEvent e = elems[i].onCreate(simul.getSimulationEngine().getTs());
            simul.addEvent(e);
		}
        afterCreateElements();
		return elems;
	}
	
}
