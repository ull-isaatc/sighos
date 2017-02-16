/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * A generator driven by simulation events. Generates the elements when
 * certain condition/s is/are triggered.<p>
 * The user of this class should add the following code every time the condition/s is/are triggered:
 * <code>
 * addEvent(new GenerateEvent(ts));
 * </code>
 * @author Iván Castilla Rodríguez
 */
public abstract class ConditionDrivenGenerator extends ElementGenerator {

	/**
	 * Creates a generator driven by simulation events.
	 * @param simul Simulation which uses this generator
	 * @param creator The way the elements are created every time the condition is triggered
	 */
	public ConditionDrivenGenerator(Simulation simul, es.ull.iis.simulation.model.ElementGenerator creator) {
		super(simul, creator);
	}

}
