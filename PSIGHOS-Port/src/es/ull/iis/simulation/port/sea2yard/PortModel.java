/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;

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
	private static final int N_CRANES = 2;
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
		
		final Ship ship = fillTestShip1();
		final ResourceType rtTrucks = new ResourceType(this, TRUCK);
		rtTrucks.addGenericResources(N_TRUCKS);
		rtContainers = new ResourceType[N_CONTAINERS];
		final Resource[] resContainers = new Resource[N_CONTAINERS];
		final WorkGroup[] wgContainers = new WorkGroup[N_CONTAINERS];
		for (int i = 0; i < N_CONTAINERS; i++) {
			rtContainers[i] = new ResourceType(this, CONTAINER + i);
			wgContainers[i] = new WorkGroup(this, new ResourceType[] {rtTrucks, rtContainers[i]}, new int[] {1, 1});
		}
		
		// Set the containers which are available from the beginning and creates the activities
		final ActivityFlow[] actUnloads = new ActivityFlow[N_CONTAINERS];
		for (int bayId = 0; bayId < ship.getNBays(); bayId++) {
			final ArrayList<Integer> bay = ship.getBay(bayId);
			if (!bay.isEmpty()) {
				for (int i = 0; i < bay.size() - 1; i++) {
					final int containerId = bay.get(i);
					actUnloads[containerId] = new ActivityFlow(this, ACT_UNLOAD + containerId) {
						@Override
						public void afterFinalize(FlowExecutor fe) {
							resContainers[containerId] = new Resource(model, PortModel.CONTAINER + containerId);
							resContainers[containerId].addTimeTableEntry(rtContainers[containerId]);
						}
					};
					actUnloads[containerId].addWorkGroup(0, wgContainers[containerId], T_UNLOAD);
					actUnloads[containerId].addResourceCancellation(rtTrucks, T_TRANSPORT);
				}
				final int containerId = ship.peek(bayId);
				resContainers[containerId] = new Resource(this, CONTAINER + containerId);
				resContainers[containerId].addTimeTableEntry(rtContainers[containerId]);
				actUnloads[containerId] = new ActivityFlow(this, ACT_UNLOAD + containerId);
				actUnloads[containerId].addWorkGroup(0, wgContainers[containerId], T_UNLOAD);
				actUnloads[containerId].addResourceCancellation(rtTrucks, T_TRANSPORT);
			}
		}
		
//		resContainers[0] = new Resource(this, CONTAINER + 0);
//		resContainers[0].addTimeTableEntry(rtContainers[0]);
//		resContainers[3] = new Resource(this, CONTAINER + 3);
//		resContainers[3].addTimeTableEntry(rtContainers[3]);
//		resContainers[4] = new Resource(this, CONTAINER + 4);
//		resContainers[4].addTimeTableEntry(rtContainers[4]);
//		resContainers[6] = new Resource(this, CONTAINER + 6);
//		resContainers[6].addTimeTableEntry(rtContainers[6]);
//		resContainers[7] = new Resource(this, CONTAINER + 7);
//		resContainers[7].addTimeTableEntry(rtContainers[7]);
//		resContainers[8] = new Resource(this, CONTAINER + 8);
//		resContainers[8].addTimeTableEntry(rtContainers[8]);
		
//		// Activities
//		final ActivityFlow aUnload0 = new ActivityFlow(this, ACT_UNLOAD + "0") {
//			@Override
//			public void afterFinalize(FlowExecutor fe) {
//				resContainers[1] = new Resource(model, PortModel.CONTAINER + 1);
//				resContainers[1].addTimeTableEntry(rtContainers[1]);
//			}
//		};
//		aUnload0.addWorkGroup(0, wgContainers[0], T_UNLOAD);
//		aUnload0.addResourceCancellation(rtTrucks, T_TRANSPORT);
//
//		final ActivityFlow aUnload1 = new ActivityFlow(this, ACT_UNLOAD + "1") {
//			@Override
//			public void afterFinalize(FlowExecutor fe) {
//				resContainers[2] = new Resource(model, PortModel.CONTAINER + 2);
//				resContainers[2].addTimeTableEntry(rtContainers[2]);
//			}
//		};
//		aUnload1.addWorkGroup(0, wgContainers[1], T_UNLOAD);
//		aUnload1.addResourceCancellation(rtTrucks, T_TRANSPORT);
//
//		final ActivityFlow aUnload2 = new ActivityFlow(this, ACT_UNLOAD + "2");
//		aUnload2.addWorkGroup(0, wgContainers[2], T_UNLOAD);
//		aUnload2.addResourceCancellation(rtTrucks, T_TRANSPORT);
//
//		final ActivityFlow aUnload3 = new ActivityFlow(this, ACT_UNLOAD + "3");
//		aUnload3.addWorkGroup(0, wgContainers[3], T_UNLOAD);
//		aUnload3.addResourceCancellation(rtTrucks, T_TRANSPORT);

		final StowagePlan plan = fillTestPlan1();
		for (int craneId = 0; craneId < N_CRANES; craneId++) {
			final ArrayList<Integer> cranePlan = plan.get(craneId);
			ActivityFlow lastAct = actUnloads[cranePlan.get(0)];
			for (int i = 1; i < cranePlan.size(); i++) {
				lastAct.link(actUnloads[cranePlan.get(i)]);
				lastAct = actUnloads[cranePlan.get(i)];
			}
		}

		// Creates the main element type representing quay cranes
		final ElementType[] ets = new ElementType[N_CRANES]; 
		for (int i = 0; i < N_CRANES; i++) {
			ets[i] = new ElementType(this, QUAY_CRANE + i);
			new TimeDrivenElementGenerator(this, 1, ets[i], actUnloads[plan.get(i).get(0)], SimulationPeriodicCycle.newDailyCycle(unit));
		}
	}
	
	Ship fillTestShip1() {
		final Ship ship = new Ship(10);
		ship.push(1, 0);
		ship.push(1, 1);
		ship.push(1, 2);
		ship.push(3, 3);
		ship.push(4, 4);
		ship.push(4, 5);
		ship.push(6, 6);
		ship.push(7, 7);
		ship.push(8, 8);
		ship.push(8, 9);
		return ship;
	}
	
	StowagePlan fillTestPlan1() {
		final StowagePlan plan = new StowagePlan(2);
		plan.addAll(0, new int[]{0, 1, 2, 5, 7});
		plan.addAll(1, new int[]{3, 4, 6, 8, 9});
		return plan;
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
