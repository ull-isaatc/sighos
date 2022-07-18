package es.ull.iis.simulation.condition;

/**
 * Default {@link Condition} used to define not conditional branches and situations 
 * without uncertainty. This {@link Condition} always returns false. 
 * @author Iván Castilla Rodríguez
 *
 */
public final class FalseCondition<E> extends Condition<E> {
	
	/**
	 * Creates a new TrueCondition
	 */
	public FalseCondition(){
	}

	@Override
	public boolean check(E fe) {
		return false;
	}
}
