/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;

/**
 * @author Iván Castilla
 *
 */
public class DistanceTimeFunction extends TimeFunction {
	final long timeFromBerth2Block[][];

	/**
	 * 
	 */
	public DistanceTimeFunction(long timeFromBerth2Block[][]) {
		this.timeFromBerth2Block = timeFromBerth2Block;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(es.ull.iis.function.TimeFunctionParams)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		if (params instanceof Container) {
			final Container container = (Container)params;
			return timeFromBerth2Block[container.getBerth()][container.getBlock()];
		}
		else {
			return 0.0;
		}
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		// TODO Auto-generated method stub

	}

}
