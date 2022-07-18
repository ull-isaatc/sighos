/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;

/**
 * A generator of event sources which is activated every time a condition is met
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ConditionDrivenGenerator<INF extends Generator.GenerationInfo> extends Generator<INF> {
	/** The condition that must be met to generate the event sources */
	protected final Condition<ElementInstance> cond;

	/**
	 * Creates a condition-driven generator
	 * @param model The simulation model this generator belong to
	 * @param nElem A function to characterize the number of entities to create every time the generator is invoked 
	 * @param cond The condition that must be met to generate the event sources
	 */
	public ConditionDrivenGenerator(final Simulation model, final TimeFunction nElem, final Condition<ElementInstance> cond) {
		super(model, model.getConditionDrivenGeneratorList().size(), nElem);
		this.cond = cond;
		model.add(this);
	}

	/**
	 * Creates a condition-driven generator
	 * @param model The simulation model this generator belong to
	 * @param nElem A fixed number of entities to create every time the generator is invoked 
	 * @param cond The condition that must be met to generate the event sources
	 */
	public ConditionDrivenGenerator(final Simulation model, final int nElem, final Condition<ElementInstance> cond) {
		super(model, model.getConditionDrivenGeneratorList().size(), nElem);
		this.cond = cond;
		model.add(this);
	}

	/**
	 * Returns the condition that fires the generator
	 * @return the condition that fires the generator
	 */
	public Condition<ElementInstance> getCondition() {
		return cond;
	}

	
}
