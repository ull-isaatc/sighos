/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class TimeDrivenElementGenerator extends TimeDrivenGenerator<TimeDrivenElementGenerator.GenerationTrio> {

	/**
	 * 
	 */
	public TimeDrivenElementGenerator(Simulation model, TimeFunction nElem, SimulationCycle cycle) {
		super(model, nElem, cycle);
	}

	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenElementGenerator(Simulation model, TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public TimeDrivenElementGenerator(Simulation model, int nElem, SimulationCycle cycle) {
		super(model, nElem, cycle);
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenElementGenerator(Simulation model, int nElem, ElementType et, InitializerFlow flow, SimulationCycle cycle) {
		super(model, nElem, cycle);
		add(new GenerationTrio(et, flow, 1.0));
	}
	
	@Override
	public EventSource createEventSource(int ind, GenerationTrio info) {
		Element elem = new Element(simul, info.getElementType(), info.getFlow());
		elem.initializeElementVars(info.getElementType().getElementValues());
		return elem;
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
	}

	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationTrio extends Generator.GenerationInfo {
		/** Type of the created elements. */
		protected final ElementType et;
		/** Description of the activity flow that the elements carry out. */
		protected final InitializerFlow flow;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param flow Description of the activity flow that the elements carry out.
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		protected GenerationTrio(ElementType et, InitializerFlow flow, double prop) {
			super(prop);
			this.et = et;
			this.flow = flow;
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
	}

	
}
