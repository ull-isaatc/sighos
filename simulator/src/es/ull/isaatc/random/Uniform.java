package es.ull.isaatc.random;

/************************************************************
 * The Uniform class is used to generate numbers uniformly 
 * distributed between the lowerBound and upperBound.  These
 * numbers can be either ints or doubles.
 * <br><br>
 * This distribution can be both discrete and continuous.
 * <br><br>
 * <b>Tests Performed</b><br>
 * 1000 samples were generated and the means and variances
 * were examined.  Subjectively, they seemed correct.
 * A goodness of fit test was performed with 100 samples
 * and 10 intervals.  It succeeded about 19/20 times.
 *
 * @version 1.96
 * @author Juraj Pivovarov
 ************************************************************/

public class Uniform extends RandomNumber {
    private int fLowerBound;
    private int fUpperBound;
    private double fLowerBoundDouble;
    private double fUpperBoundDouble;
    
    /************************************************************
     * The Uniform constructor requires the lower and upper 
     * bounds and these may be either integers or doubles.
     * 
     * @param lowerBound The minimum value that random variables
     * will take on.
     * @param upperBound The maximum value that random variables
     * will take on.
     ************************************************************/
    
    public Uniform ( int lowerBound, int upperBound) {
	init((double) lowerBound, (double) upperBound);
    }
    
    public Uniform (double lowerBound, double upperBound) {
	init(lowerBound, upperBound );
    }
    
    public Uniform (int lowerBound, double upperBound) {
	init((double) lowerBound, upperBound);
    }
    
    public Uniform (double lowerBound, int upperBound) {
	init(lowerBound, (double) upperBound);
    }

    private void init(double lower, double upper) {
	fLowerBound = (int) lower;
	fUpperBound = (int) upper;
	fLowerBoundDouble = lower;
	fUpperBoundDouble = upper;

	// We wont handle the case when there is a single point
	// in the distribution.  This should be allowed though,
	// maybe add support for it later.

	if( lower == upper )
	    throw new RuntimeException();
    }

    // Random number sampling functions

    /************************************************************
     * @return A uniformly distributed integer between the
     * lower and upper bounds. lower <= sampleInt() <= upper.
     *
     * @see Uniform#Uniform
     ************************************************************/
    
    public int sampleInt() {
	return (int)(fLowerBound + sample01() * 
		     (fUpperBound - fLowerBound + 1));
    }
    
    /************************************************************
     * @return A uniformly distributed double between the
     * lower and upper bounds. lower <= sampleInt() < upper.
     *
     * @see Uniform#Uniform 
     ************************************************************/
    
    public double sampleDouble() {
	return (fLowerBoundDouble + sample01() * 
		(fUpperBoundDouble - fLowerBoundDouble));
    }
}


