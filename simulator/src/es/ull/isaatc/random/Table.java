/**
 * 
 */
package es.ull.isaatc.random;

/**
 * A generator that returns a pre-established sequence of values. It works cyclically,
 * so every time the last value is reached, it starts again for the first value.
 * @author Iván Castilla Rodríguez
 */
public class Table extends RandomNumber {
	/** The sequence of values which this generator returns */
	double []values;
	/** The current value */
	private int count = 0;

	/**
	 * Creates a new sequence generator
	 * @param values The sequence of values
	 */
	public Table(double []values) {
		super();
		this.values = values;
	}
	

    @Override
    public double sampleDouble() {
        double val = values[count];
        count = (count + 1) % values.length;
        return val;
    }
    
    @Override
    public int sampleInt() {
        double val = values[count];
        count = (count + 1) % values.length;
        return (int) val;
    }     	

}
