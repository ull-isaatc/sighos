/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.sequential.BasicElement;
import es.ull.iis.simulation.sequential.BasicElementCreator;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.ElementGenerator;
import es.ull.iis.simulation.sequential.WorkThread;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ContainerCreator implements BasicElementCreator {
	private final ArrivalPlanning plan;
	private final PortSimulation simul;
	private final ElementType et;
	private final InitializerFlow flow;
	
	/**
	 * @param sim
	 * @param nElem
	 * @param et
	 * @param flow
	 */
	public ContainerCreator(PortSimulation simul, ArrivalPlanning plan, ElementType et, InitializerFlow flow) {
		this.simul = simul;
		this.plan = plan;
		this.flow = flow;
		this.et = et;
	}

	@Override
	public void create(ElementGenerator gen) {
		final double arrivalTime = gen.getTime();		
		final int[] containers = plan.getDestinationBlocks(arrivalTime);
		for (int i = 0; i < containers.length; i++) {
			final Container container = new Container(simul, et, flow, plan.getBerth(), containers[i]);
			final BasicElement.DiscreteEvent ev = container.getStartEvent(simul.getTs());
			container.addEvent(ev);
		}
	}

}
