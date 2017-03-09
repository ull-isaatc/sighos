/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class CalculateNTrucksExperiment extends Experiment {
	final private static String DESCRIPTION = "Port Simulation";
	private static final int NSIM = 1;
	private static final TimeUnit PORT_TIME_UNIT = TimeUnit.MINUTE;
	private static final long START_TS = 0;
	private static final long END_TS = 24 * 60;
	private static final int EXAMPLE = 0; 
	private final int example; 

	public CalculateNTrucksExperiment(int example) {
		super("PORTS", NSIM);
		this.example = example;
	}
	
	/**
	 * Creates a ship with 10 bays
	 * 		0
	 * 		1			4				8
	 * 		2		3	5		6	7	9
	 * ---------------------------------------
	 * 	0	1	2	3	4	5	6	7	8	9
	 * Creates a stowage plan for two cranes:
	 * - Crane 0 unloads 0, 1, 2, 5, 7
	 * - Crane 1 unloads 3, 4, 6, 8, 9
	 * @return A stowage plane for two cranes
	 */
	StowagePlan fillTestPlan1() {
		final Ship ship = new Ship(10);
		ship.push(2, 1, 6);
		ship.push(1, 1, 18);
		ship.push(0, 1, 14);
		ship.push(3, 3, 10);
		ship.push(5, 4, 17);
		ship.push(4, 4, 21);
		ship.push(6, 6, 10);
		ship.push(7, 7, 9);
		ship.push(9, 8, 19);
		ship.push(8, 8, 7);
		final StowagePlan plan = new StowagePlan(ship, 2, 1);
		plan.addAll(0, new int[]{0, 1, 2, 5, 7});
		plan.addAll(1, new int[]{3, 4, 6, 8, 9});
		plan.setInitialPosition(0, 2);
		plan.setInitialPosition(1, 6);
		return plan;
	}

	StowagePlan fillTestPlan2() {
		final Ship ship = new Ship(16);
		ship.push(0, 1, 33);
		ship.push(1, 2, 2);
		ship.push(3, 4, 59);
		ship.push(2, 4, 44);
		ship.push(5, 9, 6);
		ship.push(4, 9, 60);
		ship.push(7, 10, 52);
		ship.push(6, 10, 60);
		ship.push(9, 11, 41);
		ship.push(8, 11, 56);
		ship.push(11, 14, 22);
		ship.push(10, 14, 38);
		ship.push(14, 15, 34);
		ship.push(13, 15, 16);
		ship.push(12, 15, 54);
		final StowagePlan plan = new StowagePlan(ship, 2, 0);
		plan.addAll(0, new int[]{0, 1, 2, 3, 7, 8, 9});
		plan.addAll(1, new int[]{4, 5, 6, 10, 11, 12, 13, 14});
		plan.setInitialPosition(0, 1);
		plan.setInitialPosition(1, 8);
		return plan;
	}

	@Override
	public Simulation getSimulation(int ind) {
		final Simulation model = new PortModel((example == 0) ? fillTestPlan1() : fillTestPlan2(), ind, DESCRIPTION + " " + ind, PORT_TIME_UNIT, START_TS, END_TS, ind + 1);
		model.addInfoReceiver(new StdInfoView(model));
		model.addInfoReceiver(new Sea2YardGeneralListener(model));
//		model.addInfoReceiver(new ContainerTraceListener(model));
		model.addInfoReceiver(new ContainerTimeLineListener(model));
		return model;
	}

	public static void main(String[] args) {
		new CalculateNTrucksExperiment(EXAMPLE).start();
	}

}
