package es.ull.isaatc.random;

/************************************************************
 * Geometric is used to generate random variables from the
 * geometric distribution.  The random variable represents 
 * the position of the the first success in an infinite 
 * sequence of Bernoulli trials.  The probability of success
 * of each trial is identical and is given by fProbSuccess.  
 * This value is passed 
 * to the constructor to initialize the object.
 * <br>
 * This algorithm is kind of slow if p is very small since we have to 
 * keep running trials until we observe a success.  If p = 0.0001, this
 * could take quite a while ...
 * <br>
 * See Knuth Vol 2 for a faster algorithm using logarithms.
 * <br><br>
 * This is a discrete distribution.
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

public class Geometric extends RandomNumber {
    double fProbSuccess;
    
    public Geometric(double probSuccess) {

	// If the probSuccess is 0.0, when sampleInt is called
	// it will run forever.  It will be impossible for a 
	// fa

	if( probSuccess == 0.0 )
	    throw new RuntimeException();
	fProbSuccess = probSuccess;
    }
    
    // Random number sampling functions

    /************************************************************
     * Generate a geometrically distributed random variable.
     *
     * @return The index of the first observed success in an
     * infinite  sequence of bernoulli trials.
     * @see Geometric#Geometric 
     ************************************************************/

    public int sampleInt() {
	int k = 1;
	while( sample01() > fProbSuccess )
	{
	    k++;
	}
	return k;
    }

    public double sampleDouble() {
	return (double) sampleInt();
    } 
  
}
