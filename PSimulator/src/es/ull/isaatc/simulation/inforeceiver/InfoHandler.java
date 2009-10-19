/**
 * 
 */
package es.ull.isaatc.simulation.inforeceiver;

import es.ull.isaatc.simulation.info.AsynchronousInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SynchronousInfo;

/**
 * @author ycallero
 *
 */
public interface InfoHandler {

	public Number notifyInfo (SimulationInfo info);
	public void asynchronousInfoProcessing(AsynchronousInfo info);
	public Number synchronousInfoProcessing(SynchronousInfo info);
}
