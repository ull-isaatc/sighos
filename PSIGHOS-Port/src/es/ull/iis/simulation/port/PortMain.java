/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class PortMain {
	private static final int NSIM = 1;
	private static final TimeUnit PORT_TIME_UNIT = TimeUnit.MINUTE;
	private static final TimeStamp START_TS = TimeStamp.getZero();
	private static final TimeStamp END_TS = TimeStamp.getWeek();
	protected static final String CONS_VAR = "ConstantVariate";

	public static void main(String[] args) {
		for (int id = 0; id < NSIM; id++) {
			final Simulation sim = new PortSimulation(id, PORT_TIME_UNIT, START_TS, END_TS);
			sim.run();
		}
	}
}
