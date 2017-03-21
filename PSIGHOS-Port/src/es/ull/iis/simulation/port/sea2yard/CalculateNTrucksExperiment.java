/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Random;

import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

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
	private static final int NEXP = 6;
	private static final double P_ERROR = 0.1; 
	private static final int NSIM = (P_ERROR == 0.0) ? 1 : 100;
	private static final int EXAMPLE = 1; 
	private final StowagePlan plan; 
	private int nTrucks;
	private final Listener[] experimentListeners;

	public CalculateNTrucksExperiment(StowagePlan plan) {
		super("PORTS", NEXP * NSIM);
		this.plan = plan;
		experimentListeners = new Listener[NEXP * NSIM];
		nTrucks = 0;
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
	static StowagePlan fillTestPlan1() {
		final Ship ship = new Ship(10, TimeUnit.MINUTE);
		ship.push(2, 1, 6);
		ship.push(1, 1, 18);
		ship.push(0, 1, 14);
		ship.push(3, 3, 10);
		ship.push(5, 4, 21);
		ship.push(4, 4, 17);
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

	static StowagePlan fillTestPlan2() {
		final Ship ship = new Ship(16, TimeUnit.MINUTE);
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
		if (ind % NSIM == 0)
			nTrucks++;
		final Simulation model = new PortModel(plan, ind, DESCRIPTION + " " + ind, nTrucks, P_ERROR);
		experimentListeners[ind] = new Sea2YardGeneralListener(plan, ind, TimeUnit.MINUTE);
//		model.addInfoReceiver(new StdInfoView());
		model.addInfoReceiver(experimentListeners[ind]);
//		model.addInfoReceiver(new ContainerTraceListener(model.getTimeUnit()));
//		model.addInfoReceiver(new ContainerTimeLineListener(plan));
		return model;
	}

	public static void main(String[] args) {
		final StowagePlan plan = (EXAMPLE == 0) ? fillTestPlan1() : fillTestPlan2();
		System.out.println("Ship: ");
		System.out.println(plan.getShip());
		System.out.println();
		System.out.println("Stowage plan:");
		System.out.println(plan);
		new CalculateNTrucksExperiment(plan).start();

//		Simulation model = new PortModel(plan, 0, DESCRIPTION + " " + 0, 5, P_ERROR, -792807212L); // 288 - 309
//		Simulation model = new PortModel(plan, 0, DESCRIPTION + " " + 0, 5, P_ERROR, 1601344126L);// 297 - 308
//		model.addInfoReceiver(new Sea2YardGeneralListener(plan, 0, TimeUnit.MINUTE));
//		model.start();
//		
//		RandomNumber rng = RandomNumberFactory.getInstance();
//		long seed = rng.getSeed();
//		rng.setSeed(seed);
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		rng = RandomNumberFactory.getInstance();
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		rng.setSeed(seed);
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		rng = RandomNumberFactory.getInstance(seed);
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
//		System.out.println("SEED: " + rng.getSeed() + "\tDRAW: " + rng.draw());
				
	}

}
