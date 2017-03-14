/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.port.inforeceiver.ContainerTimeListener;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class PortMain extends Experiment {
	final private static String DESCRIPTION = "Port Simulation";
	private static final int NSIM = 1;
	private static final TimeUnit PORT_TIME_UNIT = TimeUnit.MINUTE;
	private static final long START_TS = 0;
	private static final long END_TS = 7 * 24 * 60;
	protected static final String CONS_VAR = "ConstantVariate";

	public PortMain() {
		super("PORTS", NSIM);
	}
	
	public static void main(String[] args) {
		new PortMain().start();
	}

	@Override
	public Simulation getSimulation(int ind) {
		final Simulation model = new PortModel(ind, DESCRIPTION + " " + ind, PORT_TIME_UNIT, START_TS, END_TS);
		model.addInfoReceiver(new StdInfoView());
		model.addInfoReceiver(new ContainerTimeListener());
		return model;
	}
}
