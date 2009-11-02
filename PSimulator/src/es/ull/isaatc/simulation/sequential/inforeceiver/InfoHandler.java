/**
 * 
 */
package es.ull.isaatc.simulation.sequential.inforeceiver;

import es.ull.isaatc.simulation.sequential.info.AsynchronousInfo;
import es.ull.isaatc.simulation.sequential.info.SimulationInfo;
import es.ull.isaatc.simulation.sequential.info.SynchronousInfo;

/**
 * @author ycallero
 *
 */
public interface InfoHandler {

	public Number notifyInfo (SimulationInfo info);
	public void asynchronousInfoProcessing(AsynchronousInfo info);
	public Number synchronousInfoProcessing(SynchronousInfo info);
}
