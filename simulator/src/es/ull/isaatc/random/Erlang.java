package es.ull.isaatc.random;

import java.lang.Math;

/************************************************************
 * Erlang is used to generate random variables from
 * the Erlang distribution.
 * <br><br>
 * This is a continuous distribution.
 * <br><br>
 * <b>Tests Performed</b><br>
 * 1000 samples were generated and the means and variances
 * were examined.  Subjectively, they seemed correct.
 * Goodness of fit tests where not performed.
 * 
 * @version 1.96
 * @author Juraj Pivovarov
 ************************************************************/

public class Erlang extends RandomNumber {
    double fMean;
    double fVariance;
    double fStdDev;
    
    /************************************************************
     * Erlang constructor.  Initialize the parameters of the
     * Erlang distribution.
     * <br>
     * @param mean The mean of the distribution
     * @param stdDev The standard deviation of the distribution
     ************************************************************/
    
    public Erlang(double mean, double stdDev) {
	fMean = mean;
	fStdDev = stdDev;
	
	// Calculate the variance.
	fVariance = fStdDev*fStdDev;
    }
    
    // Read only access to member data
    public double getMean() { 
	return fMean;  
    }

    public double getVariance() { 
	return fVariance; 
    }

    public double getStdDev() { 
	return fStdDev; 
    }
    
    // Random number sampling functions

    /************************************************************
     * The sampleDouble function returns a random variable
     * that is chosen from a Erlang distribution with parameters
     * as set in the constructor.
     * @see Erlang#Erlang 
     ************************************************************/

    public double sampleDouble() {
	if (fStdDev > fMean)
	    throw new RuntimeException();
	
	int i,k;
	double z;
	
	z = fMean/fStdDev;
	k = (int) (z*z);
	z = 1.0;
	for(i=0;i<k;i++)
	    z *= sample01();
	return ( -(fMean/k)*Math.log(z) );
    }
    
    /************************************************************
     * The sampleInt function should not be called for
     * this continuous distribution.
     ************************************************************/

    public int sampleInt() {
	throw new RuntimeException();
    } 
}
