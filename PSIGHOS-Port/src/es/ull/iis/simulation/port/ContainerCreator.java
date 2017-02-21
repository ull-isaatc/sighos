/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ContainerCreator extends TimeDrivenGenerator {
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
	public Element[] createElements(int n, long ts) {
		final Element[] elems = new Element[n];
		final int[] containers = plan.getDestinationBlocks(ts);
		for (int i = 0; i < containers.length; i++) {
			elems[i] = new Container(model, et, flow, plan.getBerth(), containers[i]);
		}
		return elems;
	}
	
}
