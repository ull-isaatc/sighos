/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author masbe_000
 * TODO Create class correctly
 */
public class TimeToE1CNVParam extends TimeToEventParam {

	/**
	 * @param baseCase
	 */
	public TimeToE1CNVParam(boolean baseCase) {
		super(baseCase);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.TimeToEventParam#getTimeToEvent(double, java.util.EnumSet, java.util.EnumSet)
	 */
	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		final double result;
		
		// Fellow eye has CNV
		if (fellowEye.contains(EyeState.AMD_CNV)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has GA
		else if (fellowEye.contains(EyeState.AMD_GA)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has EARM
		else if (fellowEye.contains(EyeState.EARM)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has no ARM
		else {
			// FIXME: Change when second order is implemented
			result = Double.MAX_VALUE;
		}
		return result;
	}

}
