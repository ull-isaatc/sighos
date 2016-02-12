/**
 * 
 */
package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SynchronousInfo;

/**
 * @author ycallero
 *
 */
public interface InfoHandler {

	public Number notifyInfo (SimulationInfo info);
	public void asynchronousInfoProcessing(AsynchronousInfo info);
	public Number synchronousInfoProcessing(SynchronousInfo info);
}
