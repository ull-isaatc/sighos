/**
 * 
 */
package es.ull.isaatc.simulation.model;

import es.ull.isaatc.simulation.common.ModelCycle;


/**
 * A generator which creates elements following a temporal pattern. 
 * @author Ivan Castilla Rodríguez
 */
public class TimeDrivenGenerator extends VariableStoreModelObject {
    /** Cycle that controls the generation of elements. */
    protected final ModelCycle cycle;
    /** Specifies the way the elements are created. */
    protected final ElementCreator creator;
    /** Generator's counter */
    private static int counter = 0;

    /**
     * Creates a generator driven by a time cycle.
     * @param simul Simulation which uses this generator
     * @param creator The way the elements are created every "tic" of the cycle 
     * @param cycle Control of the time between generations 
     */
	public TimeDrivenGenerator(Model model, ElementCreator creator, ModelCycle cycle) {
		super(counter++, model);
		this.creator = creator;
		this.cycle = cycle;
	}

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

	/**
	 * @return the creator
	 */
	public ElementCreator getCreator() {
		return creator;
	}
}
