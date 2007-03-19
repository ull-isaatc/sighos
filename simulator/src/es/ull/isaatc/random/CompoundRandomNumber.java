/**
 * 
 */
package es.ull.isaatc.random;

/**
 * Represents the composition of several random distributions.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class CompoundRandomNumber extends RandomNumber {
	protected RandomNumber r1;
	protected RandomNumber r2;
	
	public CompoundRandomNumber(RandomNumber r1, RandomNumber r2) {
		super();
		this.r1 = r1;
		this.r2 = r2;
	}

	protected abstract double operateDouble(double sample1, double sample2);
	protected abstract int operateInt(int sample1, int sample2);
	
    public double sampleDouble() {
        return operateDouble(r1.sampleDouble(), r2.sampleDouble());
    }
    
    public int sampleInt() {
        return operateInt(r1.sampleInt(), r2.sampleInt());
    }     
}
