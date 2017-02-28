/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ConditionDrivenGenerator<INF extends Generator.GenerationInfo> extends Generator<INF> {
	protected final Condition cond;

	public ConditionDrivenGenerator(Model model, TimeFunction nElem, Condition cond) {
		super(model, model.getConditionDrivenGeneratorList().size(), nElem);
		this.cond = cond;
		model.add(this);
	}

	public ConditionDrivenGenerator(Model model, int nElem, Condition cond) {
		super(model, model.getConditionDrivenGeneratorList().size(), nElem);
		this.cond = cond;
		model.add(this);
	}

	/**
	 * Returns the condition that fires the generator
	 * @return the condition that fires the generator
	 */
	public Condition getCondition() {
		return cond;
	}

	
}
