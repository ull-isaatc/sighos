/**
 * 
 */
package es.ull.iis.simulation.port;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
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
	private static final TimeStamp START_TS = TimeStamp.getZero();
	private static final TimeStamp END_TS = TimeStamp.getWeek();
	protected static final String CONS_VAR = "ConstantVariate";
	private final Model model;

	public PortMain() {
		super("PORTS", NSIM);
		model = new PortModel(PORT_TIME_UNIT);
	}
	
	public static void main(String[] args) {
		new PortMain().start();
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new Simulation(ind, DESCRIPTION + " " + ind, model, START_TS, END_TS);
		sim.addInfoReceiver(new StdInfoView(sim));
		sim.addInfoReceiver(new ContainerTimeListener(sim));

		return sim;
	}
}
