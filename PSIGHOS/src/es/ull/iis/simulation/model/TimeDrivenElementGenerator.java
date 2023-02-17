/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Location;

/**
 * A time-driven generator for {@link Element elements}. Can create different proportions of elements that appear at different locations.
 * @author Ivan Castilla Rodriguez
 *
 */
public class TimeDrivenElementGenerator extends TimeDrivenGenerator<StandardElementGenerationInfo> {
	
	/**
	 * Creates a creator of elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final int nElem, final SimulationCycle cycle) {
		super(model, nElem, cycle);
	}

	/**
	 * Creates a generator of elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final TimeFunction nElem, final SimulationCycle cycle) {
		super(model, nElem, cycle);
	}

	/**
	 * Creates a creator of a single type of elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final int nElem, final ElementType et, final InitializerFlow flow, final SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new StandardElementGenerationInfo(et, flow, 0, null, 1.0));
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final TimeFunction nElem, final ElementType et, final InitializerFlow flow, final SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new StandardElementGenerationInfo(et, flow, 0, null, 1.0));
	}
	
	/**
	 * Creates a generator of a single type of movable elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param et The type of the elements to be created
	 * @param flow The first step in the flow of the elements to be created
	 * @param size A function to determine the size of the generated elements 
	 * @param initLocation The initial {@link Location} where the elements appear
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final TimeFunction nElem, final ElementType et, final InitializerFlow flow, final TimeFunction size, final Location initLocation, final SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new StandardElementGenerationInfo(et, flow, size, initLocation, 1.0));
	}
	
	/**
	 * Creates a generator of a single type of movable elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param et The type of the elements to be created
	 * @param flow The first step in the flow of the elements to be created
	 * @param size A function to determine the size of the generated elements 
	 * @param initLocation The initial {@link Location} where the elements appear
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final int nElem, final ElementType et, final InitializerFlow flow, final TimeFunction size, final Location initLocation, final SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new StandardElementGenerationInfo(et, flow, size, initLocation, 1.0));
	}
	
	/**
	 * Creates a generator of a single type of movable elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param et The type of the elements to be created
	 * @param flow The first step in the flow of the elements to be created
	 * @param size The size of the generated elements 
	 * @param initLocation The initial {@link Location} where the elements appear
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenElementGenerator(final Simulation model, final int nElem, final ElementType et, final InitializerFlow flow, final int size, final Location initLocation, final SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new StandardElementGenerationInfo(et, flow, size, initLocation, 1.0));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Only creates the element if the initial location has enough available capacity
	 * @param ind Index of the element created
	 * @param info Information required to create the element  
	 */
	public EventSource createEventSource(final int ind, final StandardElementGenerationInfo info) {
		return new Element(simul, info);
	}

	
}
