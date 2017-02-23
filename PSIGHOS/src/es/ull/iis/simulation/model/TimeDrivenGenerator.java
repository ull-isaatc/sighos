/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class TimeDrivenGenerator extends ElementGenerator {
    /** Cycle that controls the generation of elements. */
    protected final ModelCycle cycle;
    /** The iterator which moves through the defined cycle */
    private DiscreteCycleIterator cycleIter;

	/**
	 * 
	 */
	public TimeDrivenGenerator(Model model, TimeFunction nElem, ModelCycle cycle) {
		super(model, nElem);
		this.cycle = cycle;
	}

	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenGenerator(Model model, TimeFunction nElem, ElementType et, InitializerFlow flow, ModelCycle cycle) {
		super(model, nElem, et, flow);
		this.cycle = cycle;
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public TimeDrivenGenerator(Model model, int nElem, ModelCycle cycle) {
		this(model, TimeFunctionFactory.getInstance("ConstantVariate", nElem), cycle);
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public TimeDrivenGenerator(Model model, int nElem, ElementType et, InitializerFlow flow, ModelCycle cycle) {
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
	public ModelCycle getCycle() {
		return cycle;
	}

	@Override
	public double getTime() {
		return model.getSimulationEngine().getTs();
	}

	@Override
	public long nextEvent() {
		return cycleIter.next();
	}

	@Override
	public DiscreteEvent onCreate(long ts) {
    	final long newTs = nextEvent();
    	if (newTs == -1)
            return onDestroy();
        else {
            return new GenerateEvent(newTs);
        }
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		cycleIter = cycle.getCycle().iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
	}

}
