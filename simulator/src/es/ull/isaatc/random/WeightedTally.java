package es.ull.isaatc.random;

import java.lang.Math;
import java.util.*;
import java.io.*;

/*****************************************************************************
 * The WeightedTally class is for weighted tallying of values.  It extends
 * the Tally class.
 * <br>
 * Unfortunately, SimKit does not (yet) give the programmer access to
 * either the current event or the current simulation time.  This means
 * that there is no method of providing a time weighted tally where the
 * time weights are automatically calculated.
 *
 * @see Tally
 *
 * @version 1.96
 * @author Juraj Pivovarov
 *****************************************************************************/

public class WeightedTally extends Tally {
    /** sum of the weights in the tally */
    private double fSumWeights;	

    protected Vector fWeights = new Vector(128,128);
    
    public WeightedTally() {
	super();
	fSumWeights = 0.0;
    }

    /************************************************************
     * Initialize the object, give the Tally a name.
     * @param name The name of the Tally object.  This will
     * be used with the output functions.
     * @param storePoints Whether or not the Tally should record
     * all individiual data points.  Tally.STORE_POINTS means
     * the object should store data points, and Tally.SUMMARY_ONLY
     * means the object should only compute summary statistics.
     ************************************************************/

    public WeightedTally(String name, boolean storePoints) {
	super(name,storePoints);
	fSumWeights = 0.0;
    }

    public void reset() {
	super.reset();
	fSumWeights = 0.0;
	fWeights.removeAllElements();
    }

    // Functions to add new values to the Tally

    /************************************************************
     * Add a data point to the tally.  Assume that if no weight
     * is specified, a weight of 1 should be assigned.
     * @param x The value to add to the tally.
     ************************************************************/

    public void update(double x)
    {
	update(x,1.0);
    }

    /************************************************************
     * Updates the statistics with the weighted new value.
     * 
     * @param x The value to add to statistics
     * @param weight The weight to use for this value
     ************************************************************/

    public void update(double x, double weight) {
	// calculate min and max
	if (fCount == 0.0) {
	    fMin = x;
	    fMax = x;
	}
	else {
	    if (x < fMin) 
		fMin = x;
	    else if (x > fMax)
		fMax = x;
	}
	
	// update sums and number
	fSum += x * weight;
	fSumSq += x * x * weight;
	fSumWeights += weight;
	
	fCount += 1.0;  // for IEEE double overflow occurs in > 285 years at
	// one update per microsecond

	// update both lists of points
	if( isStoringPoints() ) {
	    fDataPoints.addElement( new Double(x) );
	    fWeights.addElement( new Double(weight) );
	}
    } 

    /************************************************************
     * Update Tally with batch data.  
     * <BR><BR>
     *
     * @param data The Tally whose data will be added to this
     * tally.  For now, this should be an instance of 
     * WeightedTally.  Later on, other types of Tallies may
     * be supported.
     * @exception RuntimeException A RuntimeException is thrown
     * if the Tally object data is not an instance of 
     * WeightedTally.
     ************************************************************/

    public void updateBatch(Tally data) {
	if( data instanceof WeightedTally )
	{
	    super.updateBatch(data);

	    WeightedTally wdata = (WeightedTally) data;
	    fSumWeights += wdata.fSumWeights;
		
	    // Now copy over the elements of the new vector.
	    if( isStoringPoints() ) {
		for(Enumeration e = wdata.fWeights.elements(); e.hasMoreElements();) 
		    fWeights.addElement( e.nextElement() );
	    }
	}
    }
    
    // Computed statistics
    
    /************************************************************
     * Report the weighted mean of the data points.
     * 
     * @return The weighted mean of the values in the tally.  If
     * there are no values in the tally then 0 is returned.
     ************************************************************/

    public double average() {
	if( fSumWeights == 0.0 )
	    return 0.0;
	else
	    return fSum / fSumWeights;
    } 

    /************************************************************
     * Report the variance of the data points.
     *
     * @return The sample variance of the values in the tally.  
     * If there are fewer than 2 values in the tally or the sum
     * of the weights is not positive, then 0 is returned.
     ************************************************************/

    public double variance() {
	double tmp;
	
	if (fCount < 2.0) 
	    return 0.0;
	if (fSumWeights < 0.0)
	    return 0.0;
	  

	// avoid some errors introduced by rounding
	tmp = (fSumSq - (fSum * fSum / fSumWeights)) / fSumWeights;
	if (tmp < 0.0) 
	    return 0.0;

	return tmp;
    }

    public double sumWeights() {
	return fSumWeights;
    }

    /************************************************************
     * Report the standard deviation of the data points.
     *
     * @return The standard deviation of the values in the tally.  
     * If there are fewer than 2 values in the tally or the
     * sum of the weights is not positive, then 0 is returned.
     ************************************************************/

    public double stdDev() {
	return Math.sqrt(variance());
    } 

    /************************************************************
     * Output all the data points stored in the Tally object.
     *
     * @param out The PrintStream to send the output to.
     * @param withHeader true to print a header, false to skip
     * the header.  Tally.WITH_HEADER and Tally.WO_HEADER can
     * be used instead of true and false.
     *
     * @exception RuntimeException A RuntimeException is thrown
     * if the Tally object is not currently storing data points.
     ************************************************************/

    public void outputPoints(PrintStream out, boolean withHeader) {
	if( !isStoringPoints() )
	    throw new RuntimeException();

	if( withHeader ) {
	    out.println("[" + fName + "] stats.  " + 
			fDataPoints.size() + " data points.");
	    out.println("Data Points, Weights:");
	    out.println("====================");
	}

	// Assertion: fDataPoints and fWeights have same number
	// of elements.  Internal error otherwise.
	if( fDataPoints.size() != fWeights.size() )
	    throw new RuntimeException();

	Enumeration wts=fWeights.elements();
	for(Enumeration pts=fDataPoints.elements();pts.hasMoreElements();) {
	    Double x = (Double) pts.nextElement();
	    Double w = (Double) wts.nextElement();
	    out.println(x.doubleValue() + seperator + w.doubleValue());
	}
    }

    /************************************************************
     * Output the summary statistics that were collected
     * for the WeightedTally.
     *
     * @param out The PrintStream to send the output to.
     * @param withHeader true to print a header, false to skip
     * the header.  Tally.WITH_HEADER and Tally.WO_HEADER can
     * be used instead of true and false.
     ************************************************************/

    public void outputSummary(PrintStream out, boolean withHeader) {
	if( withHeader ) {
	    out.println("[" + fName + "] Summary Statistics");
	    out.println("N, Min, Max, Mean, StdDev, Var, SumWeights");
	    out.println("========================================");
	}

	out.print(count() + seperator);
	out.print(min() + seperator);
	out.print(max() + seperator);
	out.print(average() + seperator);
	out.print(stdDev() + seperator);
	out.print(variance() + seperator);
	out.print(sumWeights());
	out.println();
    }
}
    


