/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ForLoopFlow;

/**
 * @author Iván Castilla
 *
 */
public class PortModel extends Simulation {
	private static final int N_TRUCKS = 4;
	private static final int N_CONTAINERS = 10;
	protected static final String QUAY_CRANE = "Quay Crane";
	private static final String TRUCK = "Truck";
	protected static final String CONTAINER = "Container";
	private static final String ACT_UNLOAD = "Unload";
	private final ResourceType[] rtContainers;
	private static final long T_UNLOAD = 3L;
	private static final long T_TRANSPORT = 10L;
	
	/**
	 * @param id
	 * @param description
	 * @param unit
	 * @param startTs
	 * @param endTs
	 */
	public PortModel(int id, String description, TimeUnit unit, long startTs, long endTs) {
		super(id, description, unit, startTs, endTs);
		// Creates the main element type representing quay cranes
		final ElementType et = new ElementType(this, QUAY_CRANE);
		final ResourceType rtTrucks = new ResourceType(this, TRUCK);
		rtTrucks.addGenericResources(N_TRUCKS);
		rtContainers = new ResourceType[N_CONTAINERS];
		final Resource[] resContainers = new Resource[N_CONTAINERS];
		final WorkGroup[] wgContainers = new WorkGroup[N_CONTAINERS];
		for (int i = 0; i < N_CONTAINERS; i++) {
			rtContainers[i] = new ResourceType(this, CONTAINER + i);
			wgContainers[i] = new WorkGroup(this, new ResourceType[] {rtTrucks, rtContainers[i]}, new int[] {1, 1});
		}
		
		// Set the containers which are available from the beginning
		resContainers[0] = new Resource(this, CONTAINER + 0);
		resContainers[0].addTimeTableEntry(rtContainers[0]);
		resContainers[3] = new Resource(this, CONTAINER + 3);
		resContainers[3].addTimeTableEntry(rtContainers[3]);
		resContainers[4] = new Resource(this, CONTAINER + 4);
		resContainers[4].addTimeTableEntry(rtContainers[4]);
		resContainers[6] = new Resource(this, CONTAINER + 6);
		resContainers[6].addTimeTableEntry(rtContainers[6]);
		resContainers[7] = new Resource(this, CONTAINER + 7);
		resContainers[7].addTimeTableEntry(rtContainers[7]);
		resContainers[8] = new Resource(this, CONTAINER + 8);
		resContainers[8].addTimeTableEntry(rtContainers[8]);
		
		// Activities
		final ActivityFlow aUnload0 = new ActivityFlow(this, ACT_UNLOAD + "0") {
			@Override
			public void afterFinalize(FlowExecutor fe) {
				resContainers[1] = new Resource(model, PortModel.CONTAINER + 1);
				resContainers[1].addTimeTableEntry(rtContainers[1]);
			}
		};
		aUnload0.addWorkGroup(0, wgContainers[0], T_UNLOAD);
		aUnload0.addResourceCancellation(rtTrucks, T_TRANSPORT);

		final ActivityFlow aUnload1 = new ActivityFlow(this, ACT_UNLOAD + "1") {
			@Override
			public void afterFinalize(FlowExecutor fe) {
				resContainers[2] = new Resource(model, PortModel.CONTAINER + 2);
				resContainers[2].addTimeTableEntry(rtContainers[2]);
			}
		};
		aUnload1.addWorkGroup(0, wgContainers[1], T_UNLOAD);
		aUnload1.addResourceCancellation(rtTrucks, T_TRANSPORT);

		final ActivityFlow aUnload2 = new ActivityFlow(this, ACT_UNLOAD + "2");
		aUnload2.addWorkGroup(0, wgContainers[2], T_UNLOAD);
		aUnload2.addResourceCancellation(rtTrucks, T_TRANSPORT);

		final ActivityFlow aUnload3 = new ActivityFlow(this, ACT_UNLOAD + "3");
		aUnload3.addWorkGroup(0, wgContainers[3], T_UNLOAD);
		aUnload3.addResourceCancellation(rtTrucks, T_TRANSPORT);

		new TimeDrivenElementGenerator(this, 1, et, aUnload3, SimulationPeriodicCycle.newDailyCycle(unit));
		new TimeDrivenElementGenerator(this, 1, et, aUnload3, SimulationPeriodicCycle.newDailyCycle(unit));
	}
	
	Ship fillTestShip1() {
		final Ship ship = new Ship(10);
		ship.push(1, 1);
		ship.push(1, 2);
		ship.push(1, 3);
		ship.push(3, 4);
		ship.push(4, 5);
		ship.push(4, 6);
		ship.push(6, 7);
		ship.push(7, 8);
		ship.push(8, 9);
		ship.push(8, 10);
		return ship;
	}
	
	void genericModel() {
		// Creates the main element type representing quay cranes
		final ElementType et = new ElementType(this, QUAY_CRANE);
		final ResourceType rtTrucks = new ResourceType(this, TRUCK);
		rtTrucks.addGenericResources(N_TRUCKS);
		
		// Defines the needs of the activities in terms of resources
		final WorkGroup wgTruck = new WorkGroup(this, rtTrucks, 1);
		
		// Activities
		final ActivityFlow aUnload = new ActivityFlow(this, ACT_UNLOAD);
		aUnload.addWorkGroup(0, wgTruck, T_UNLOAD);
		aUnload.addResourceCancellation(rtTrucks, T_TRANSPORT);

		// Defines the flow for the former activities
		final ForLoopFlow fLoop = new ForLoopFlow(this, aUnload, 100);
		new TimeDrivenElementGenerator(this, 1, et, fLoop, SimulationPeriodicCycle.newDailyCycle(getTimeUnit()));
	}
}
