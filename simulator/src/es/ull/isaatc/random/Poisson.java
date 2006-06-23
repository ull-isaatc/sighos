package es.ull.isaatc.random;

import java.lang.Math;

/************************************************************
 * The Poisson class is used to generate random variables 
 * from a Poisson distribution.
 * <br>
 * The Poisson distribution is often used to model the
 * total number of arrivals during a fixed time period.
 * <br><br>
 * This is a discrete distribution.
 * <br><br>
 * <b>Tests Performed</b><br>
 * 1000 samples were generated and the means and variances
 * were examined.  Subjectively, they seemed correct.
 * A goodness of fit test was performed with 100 samples
 * and 10 intervals.  It seems to fail about 16/20 times.
 * This might be due to the fact that it is a discrete
 * distribution and it wasn't possible to make the probabilies 
 * for the different intervals identical.  
 * 
 * @version 1.96
 * @author Juraj Pivovarov
 * @see Exponential
 ************************************************************/

public class Poisson extends RandomNumber {
    double fMean;
    
    /************************************************************
     * The Poisson constructor initializes the Poisson
     * distribution by setting the distribution's mean.
     * @param mean The mean of the poisson distribution.
     * The mean alone characterizes the poisson distribution.
     ************************************************************/

    public Poisson(double mean) {
	fMean = mean;
    }
    
    // Read only access to member data
    public double getMean() { 
	return fMean;  
    }
    
    // Random number sampling functions

    /************************************************************
     * Generate a random variable, an int, from the poisson
     * distribution.
     * @return The int representing a random draw from the
     * poisson distribution.
     ************************************************************/

    /*
    public int sampleInt() {
	int x = 0;
	double p, f, u;
	
	f = p = Math.exp( -fMean );
	for(u = sample01(); u>f; f+=p)
	    p *= (fMean/++x);
	return x;
    } 
    */

    // Algorithm from Knuth, Vol2.
    public int sampleInt() {

	int n = 0;
	double p = Math.exp( -fMean );
	
	double u = sample01();
	while( u >= p ) {
	    n++;
	    u *= sample01();
	}
	return n;
    } 

    public double sampleDouble() {
	return (double) sampleInt();
    }
}
