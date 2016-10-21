/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.sequential.BasicElementCreator;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.Generator;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.flow.InitializerFlow;

/**
 * @author Iván Castilla
 *
 */
public class ContainerCreator implements BasicElementCreator, es.ull.iis.simulation.core.ElementCreator {

	/**
	 * @param sim
	 * @param nElem
	 * @param et
	 * @param flow
	 */
	public ContainerCreator(Simulation sim, TimeFunction nElem, ElementType et, InitializerFlow flow) {
		super(sim, nElem, et, flow);
	}

	@Override
	public void create(Generator gen) {
		final int nContainers = nelem.
		for (int i = 0; i < )
		super.create(gen);
	}
}
