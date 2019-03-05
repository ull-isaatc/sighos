/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.Generator;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * A time-driven generator for {@link MovableElement movable elements}. Can create different proportions of elements that appear at different locations.
 * @author Ivan Castilla Rodriguez
 *
 */
public class TimeDrivenMovableElementGenerator extends TimeDrivenGenerator<TimeDrivenMovableElementGenerator.GenerationInfo> {

	/**
	 * Creates a generator of of movable elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenMovableElementGenerator(Simulation model, TimeFunction nElem, SimulationCycle cycle) {
		super(model, nElem, cycle);
	}

	/**
	 * Creates a generator of of movable elements.
	 * @param model The simulation model this generator belongs to
	 * @param nElem Number of objects created each time this generator is invoked
	 * @param cycle A function to determine when are created the elements
	 */
	public TimeDrivenMovableElementGenerator(Simulation model, int nElem, SimulationCycle cycle) {
		super(model, nElem, cycle);
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
	public TimeDrivenMovableElementGenerator(Simulation model, TimeFunction nElem, ElementType et, InitializerFlow flow, TimeFunction size, Location initLocation, SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new GenerationInfo(et, flow, size, initLocation, 1.0));
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
	public TimeDrivenMovableElementGenerator(Simulation model, int nElem, ElementType et, InitializerFlow flow, TimeFunction size, Location initLocation, SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new GenerationInfo(et, flow, size, initLocation, 1.0));
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
	public TimeDrivenMovableElementGenerator(Simulation model, int nElem, ElementType et, InitializerFlow flow, int size, Location initLocation, SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new GenerationInfo(et, flow, TimeFunctionFactory.getInstance("ConstantVariate", size), initLocation, 1.0));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Only creates the element if the initial location has enough available capacity
	 * @param ind Index of the element created
	 * @param info Information required to create the element  
	 */
	public EventSource createEventSource(int ind, GenerationInfo info) {
		final int size = (int)info.getSize().getValue(this);
		final Location initLocation = info.getInitLocation();
		if (initLocation.getAvailableCapacity() >= size) {
			final Element elem = new Element(simul, info.getElementType(), info.getFlow(), size);
			initLocation.move(elem);
			elem.initializeElementVars(info.getElementType().getElementValues());
			return elem;			
		}
		error("Could not create element. Not enough space in location " + initLocation + " (available: " + initLocation.getAvailableCapacity() + " - required: " + size + ")");
		return null;
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
	}

	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationInfo extends Generator.GenerationInfo {
		/** Type of the created elements. */
		protected final ElementType et;
		/** Description of the flow that the elements carry out. */
		protected final InitializerFlow flow;
		/** Function to determine the size of the elements created */ 
		protected final TimeFunction size;
		/** The initial {@link Location} where the elements appear */
		protected final Location initLocation;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param flow Description of the activity flow that the elements carry out.
		 * @param size A function to determine the size of the generated elements 
		 * @param initLocation The initial {@link Location} where the elements appear
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		protected GenerationInfo(ElementType et, InitializerFlow flow, TimeFunction size, Location initLocation, double prop) {
			super(prop);
			this.et = et;
			this.flow = flow;
			this.size = size;
			this.initLocation = initLocation;
		}
		
		/**
		 * Returns the element type.
		 * @return Returns the element type.
		 */
		public ElementType getElementType() {
			return et;
		}
		
		/**
		 * Returns the flow.
		 * @return the flow
		 */
		public InitializerFlow getFlow() {
			return flow;
		}

		/**
		 * Returns the function that determines the size of the generated elements 
		 * @return the function that determines the size of the generated elements
		 */
		public TimeFunction getSize() {
			return size;
		}

		/**
		 * Returns the initial {@link Location} where the elements appear
		 * @return The initial {@link Location} where the elements appear
		 */
		public Location getInitLocation() {
			return initLocation;
		}
	}

	
}
