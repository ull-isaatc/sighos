package es.ull.cyc.random;

import java.lang.Math;
import java.util.*;
import java.io.*;

/************************************************************
 * 		      Statistic Collection Objects
 * <br>
 * The Tally class is used for collecting simple statistics
 * for a set of numbers.  It provides functions to compute min, max,
 * variance, standard deviation, sum, and sum of squares.
 * All values are represented using doubles.
 * <br>
 * Note that the original source code for these classes was
 * written by Zhonge Xiao in C++.  The C++ code was ported
 * over to Java and some minor design changes were made.
 *
 * @version 1.96
 * @author Juraj Pivovarov
 ************************************************************/

public class Tally {
    /** Count of values in the tally. */
    protected double fCount;	

    /** Sum of the values in the tally. */
    protected double fSum;	

    /** Sum of the squares in the tally. */
    protected double fSumSq;	

    /** Minimum value in the tally. */
    protected double fMin;	

    /** Maximum value in the tally. */
    protected double fMax;	

    /** The name of the tally. */
    protected String fName;	

    /** Indicates whether or not the tally object stores all
     * the individual data points. */

    private boolean fStorePoints;
    public static final boolean STORE_POINTS = true;
    public static final boolean SUMMARY_ONLY = false;

    protected Vector fDataPoints = new Vector(128,128);

    public static final boolean WITH_HEADER = true;
    public static final boolean WO_HEADER = false;

    /**
     * Represents the string used for seperating consecutive numerical 
     * values.  The user should feel free to change this to whatever 
     * he deems appropriate. */

    public static String seperator = ", ";

    // Common initialization code for all constructors.
    // "init();" should be the first line of code in each constructor.

    private void init() {
	reset();

	fName = "";
	fStorePoints = false;
    }
    
    /************************************************************
     * Initialize the object.  With this constructor, no data points
     * will be collected; only the summary statistics will be
     * computed.  Also, the tally will have no name.
     ************************************************************/

    public Tally() {
	init();
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

    public Tally(String name, boolean storePoints) {
	init();

	fName = name;
	fStorePoints = storePoints;
    } 

    /************************************************************
     * Remove all data points from the Tally.
     ************************************************************/

    public void reset() {
	fCount = 0.0;
	fSum = 0.0;
	fSumSq = 0.0;
	fMin = 0.0;
	fMax = 0.0;
	
	// clear the list of data points as well.
	fDataPoints.removeAllElements();
    } 
    
    // Adding data to the tally

    /************************************************************
     * Updates the statistics with the data item x.
     *
     * @param x The value to record in the tally.
     ************************************************************/

    public void update(double x)
    {
	// calculate min and max
	if (fCount == 0.0) {
	    fMin = x;
	    fMax = x;
	}
	else {
	    if (fMin > x) {
		fMin = x;
	    }
	    else if (fMax < x) {
		fMax = x;
	    }
	}
	
	// update sums and count
	fSum += x;
	fSumSq += x*x;
	fCount += 1.0;  // for IEEE double overflow occurs in > 285 years at
			// one update per microsecond

	// update the data point list as well.
	if( fStorePoints ) 
	    fDataPoints.addElement( new Double(x) );
    } 
    
    /************************************************************
     * Update Tally with batch data.
     * <BR><BR>
     *
     * @param data The tally whose data will be added to this
     * tally.
     * @exception RuntimeException A RuntimeException is thrown
     * if the Tally object data is not storing data points
     * but the current Tally object is.  There is no problem
     * however if we are not storing points but the data object
     * is.
     ************************************************************/

    public void updateBatch(Tally data) {
	if (data.count() != 0) {
	    if( isStoringPoints() && !data.isStoringPoints() )
		throw new RuntimeException();
	    if (fCount == 0.0) {
		fMin = data.min();
		fMax = data.max();
	    }
	    else {
		if (fMin > data.min())
		    fMin = data.min();
		if (fMax < data.max())
		    fMax = data.max();
	    }
	    
	    fCount += data.count();
	    fSum += data.sum();
	    fSumSq += data.sumSq();

	    // Now copy over the elements of the new vector.
	    if( isStoringPoints() ) {
		for(Enumeration e = data.fDataPoints.elements(); e.hasMoreElements();) 
		    fDataPoints.addElement( e.nextElement() );
	    }
	}
    }
    
    // raw statistic access

    /************************************************************
     * Report how many data points are in the tally.
     *
     * @return The count of points in the tally.
     ************************************************************/

    public double count() {
	return fCount;
    } 
    
    /************************************************************
     * Report the sum of the data points.
     *
     * @return The sum of the values in the tally.  If there are 
     * no values in the tally then 0 is returned.
     ************************************************************/

    public double sum() {
	return fSum;
    } 
    
    /************************************************************
     * Report the sum of the squares of the data points.
     *
     * @return The sum of the squares of the values in the tally.  
     * If there are no values in the tally then 0 is returned.
     ************************************************************/

    public double sumSq() {
	return fSumSq;
    } 
    
    /************************************************************
     * Report the minimum data point.
     *
     * @return The minimum value in the Tally.   If there are no 
     * values in the tally then 0 is returned.
     ************************************************************/

    public double min() {
	return fMin;
    } 
    
    /************************************************************
     * Report the maximum data point.
     *
     * @return The maximum value in the Tally.  If there are no 
     * values in the tally then 0 is returned.
     ************************************************************/

    public double max() {
	return fMax;
    } 
    
    // computed statistics

    /************************************************************
     * Report the mean data point.
     *
     * @return The average of the values in the tally.  If there 
     * are no values in the tally then 0 is returned.
     ************************************************************/

    public double average() {
	if (fCount == 0.0) {
	    return 0.0;
	}
	return fSum / fCount;
    } 
    
    /************************************************************
     * Report the standard deviation of the data points.
     *
     * @return The standard deviation of the values in the tally.  
     * If there are fewer than 2 values in the tally then 0 
     * is returned.
     ************************************************************/

    public double stdDev() {
	return Math.sqrt(variance());
    } 

    /************************************************************
     * Report the variance of the data points.
     *
     * @return The sample variance of the values in the tally.  
     * If there are fewer than 2 values in the tally then 0 
     * is returned.
     ************************************************************/

    public double variance() {
	double tmp;
	
	if (fCount < 2.0) {
	    return 0.0;
	}

	// avoid some errors introduced by rounding
	tmp = (fSumSq - fSum * fSum / fCount) / (fCount - 1);
	if (tmp < 0.0) 
	    tmp = 0.0;

	return tmp;
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
	    out.println("Data Points:");
	    out.println("====================");
	}

	for(Enumeration e=fDataPoints.elements();e.hasMoreElements();) {
	    Double d = (Double) e.nextElement();
	    out.println(d.doubleValue());
	}
    }

    /************************************************************
     * Output the summary statistics that were collected
     * for the Tally.
     *
     * @param out The PrintStream to send the output to.
     * @param withHeader true to print a header, false to skip
     * the header.  Tally.WITH_HEADER and Tally.WO_HEADER can
     * be used instead of true and false.
     ************************************************************/

    public void outputSummary(PrintStream out, boolean withHeader) {
	if( withHeader ) {
	    out.println("[" + fName + "] Summary Statistics");
	    out.println("N Min, Max, Mean, StdDev, Var");
	    out.println("========================================");
	}

	out.print(count() + seperator);
	out.print(min() + seperator);
	out.print(max() + seperator);
	out.print(average() + seperator);
	out.print(stdDev() + seperator);
	out.print(variance());
	out.println();
    }

    /************************************************************
     * Report whether or not this Tally object is storing
     * the actual data points.
     *
     * @return true if this Tally object is recording individual
     * data points, false if it is only collecting summary 
     * statistics.
     ************************************************************/

    public boolean isStoringPoints() {
	return fStorePoints;
    }
}

