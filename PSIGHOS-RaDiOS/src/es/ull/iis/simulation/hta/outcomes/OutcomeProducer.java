/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

/**
 * Generates outcomes for a patient during a specified period of time
 * @author Iván Castilla
 *
 */
public interface OutcomeProducer {
	/**
	 * Returns an array with as many items as natural years are in the interval (initT, endT). The first and last item are initialized 
	 * with the proportional part of the natural year corresponding to initT and endT, respectively; while the rest of intervals are initialized to 1. 
	 * For example, if initT = 3.1 and endT = 5.2, this method should return [0.9, 1.0, 0.2].  
	 * @param initT Starting time of the period (in years)
	 * @param endT Ending time of the period
	 * @return an array with as many items as natural years are in the interval (initT, endT)
	 */
	public static double[] getIntervalsForPeriod(double initT, double endT) {
		int naturalYear = (int) initT;
		final int nIntervals = (int) endT - naturalYear + (int) Math.ceil(endT - (int) endT);
		if (nIntervals == 1)
			return new double[] {endT - initT};
		double[] result = new double[nIntervals];
		result[0] = naturalYear + 1 - initT;
		// Process the intermediate intervals, corresponding to full years 
		for (int i = 1; i < nIntervals - 1; i++) {
			result[i] = 1.0;
		}
		// Process the last interval
		result[nIntervals - 1] = endT - Math.ceil(endT) + 1;		
		return result;
	}

}
