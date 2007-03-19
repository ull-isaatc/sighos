package es.ull.isaatc.random;

/*****************************************************************************
 * The TimeSeriesTally class is for calculating weighted time series 
 * statistics for a bunch of points.
 * <br>
 * Unfortunately, SimKit does not (yet) give the programmer access to
 * either the current event or the current simulation time.  This means
 * that there is no method of providing a time weighted tally where the
 * time weights are automatically calculated.
 * @see Tally
 * @see WeightedTally
 *
 * @version 1.96
 * @author Juraj Pivovarov
 *****************************************************************************/

public class TimeSeriesTally extends WeightedTally {

    private double fPrevTime;	// Last point in a usually 0.0-origined, usually
				// increasing series at which an update was done.

    public TimeSeriesTally() {
	super();
	fPrevTime = 0.0;
    }

    public void reset() {
	super.reset();
	fPrevTime = 0.0;
    }

    /************************************************************
     * Add a data point to the tally.  Assume that if no time is
     * provided, that the new time is 1 unit greater than the
     * previous time.
     * @param x The value to add to the tally.
     ************************************************************/

    public void update(double x)
    {
	super.update(x,1.0);
	fPrevTime += 1.0;
    }

    /************************************************************
     * Updates the statistics with the data item x with the weight calculated
     * as the difference between the given time and the last given
     * time point. If there is no last given point, then 0.0 is used.  This
     * function is useful when doing a time weighted series.  The point is
     * just the simulation time at which the update is done.  
     * <br>
     * @param x The value to add to statistics
     * @param currTime The current simulation time or some other value
     * that can be interpreted as time (from an increasing sequence ...)
     ************************************************************/
    
    public void update(double x, double currTime) {
	// It should never happen that the currTime is less than
	// the previous time.  Time doesn't flow backwards.
	//
	// This assertion may need to be taken out when the system
	// runs in parallel, since the laws of time are not adhered
	// to in a parallel world.

	if( currTime < fPrevTime )
	    throw new RuntimeException();

	super.update(x, currTime - fPrevTime);
	fPrevTime = currTime;
    } 

    /************************************************************
     * This would be the automatic time weighted tally function
     *
     * void update(double x);
     *
     * Updates the statistics with the data item x.  The weight is the
     * elapsed simulated time since the last update, or if no updates have
     * been done then the current simulation time, converted to a
     * double
     *
     * @param x The value to add to statistics
     ***********************************************************/
}



