/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.SimulationCycle;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class TimeDrivenGenerator extends ElementGenerator {
    /** Cycle that controls the generation of elements. */
    protected final SimulationCycle cycle;

	/**
	 * 
	 */
	public TimeDrivenGenerator(Model model, TimeFunction nElem, SimulationCycle cycle) {
		super(model, nElem);
		this.cycle = cycle;
	}

	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenGenerator(Model model, TimeFunction nElem, ElementType et, InitializerFlow flow, SimulationCycle cycle) {
		super(model, nElem, et, flow);
		this.cycle = cycle;
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public TimeDrivenGenerator(Model model, int nElem, SimulationCycle cycle) {
		this(model, TimeFunctionFactory.getInstance("ConstantVariate", nElem), cycle);
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenGenerator(Model model, int nElem, ElementType et, InitializerFlow flow, SimulationCycle cycle) {
		this(model, TimeFunctionFactory.getInstance("ConstantVariate", nElem), et, flow, cycle);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.ModelObject#getObjectTypeIdentifier()
	 */
	@Override
	public String getObjectTypeIdentifier() {
		return "GEN";
	}

	/**
	 * @return the cycle
	 */
	public SimulationCycle getCycle() {
		return cycle;
	}

	public void beforeCreate() {
	}
}
