/**
 * 
 */
package es.ull.isaatc.simulation.common.inforeceiver;

import es.ull.isaatc.simulation.common.info.AsynchronousInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SynchronousInfo;

/**
 * @author ycallero
 *
 */
public interface InfoHandler {

	public Number notifyInfo (SimulationInfo info);
	public void asynchronousInfoProcessing(AsynchronousInfo info);
	public Number synchronousInfoProcessing(SynchronousInfo info);
}
