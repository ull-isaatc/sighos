package es.ull.isaatc.simulation.bonn3Phase;
/**
 * 
 */

/**
 * An abstract basic class to represent the commons to all the barriers implemented in this
 * package. A barrier is a tool for synchronizing processes on a shared memory machine. 
 * No thread may pass the barrier until all other threads have arrived at it. A barrier 
 * has to know the parties (threads) which will use it; and the action, if there 
 * is any action, which will be executed when the barrier be tripped.
 * @author Patrick Peschlow
 * @author Martin Geuer
 * @author Ivan Castilla (adaptation)
 */
public abstract class TPCBarrier {
	/**
	 * Threads competing in the barrier.
	 */
	protected final Runnable[] parties_;
	
	/**
	 * The number of threads that must invoke await() before the barrier is tripped.
	 */
	protected final int numThreads_;

	/**
	 * The command to execute when the barrier is tripped, or null if there is no action.
	 */
	protected final Runnable barrierAction_;
	
	/**
	 * Creates a new <code>TPCBarrier</code> that will trip when the given number of parties 
	 * (threads) are waiting upon it, and which will execute the given barrier action when the 
	 * barrier is tripped, performed by the last thread entering the barrier.
	 * @param parties the threads that must invoke {@link #await()} before the barrier is tripped
	 * @param barrierAction the command to execute when the barrier is tripped, or null if 
	 * there is no action
	 * @throws IllegalArgumentException if parties is less than 1
	 */
	public TPCBarrier(Runnable[] parties, Runnable barrierAction) {
		parties_ = parties.clone();
		numThreads_ = parties.length;
        if (numThreads_ == 0) throw new IllegalArgumentException();
		barrierAction_ = barrierAction;
	}

	/**
	 * Waits until all parties have invoked <code>await</code> on this barrier. 
	 */
	public abstract void await();

	/**
	 * Returns the number of parties required to trip this barrier. 
	 * @return the number of parties required to trip this barrier
	 */
	public int getParties() {
		return numThreads_;
	}	
}
