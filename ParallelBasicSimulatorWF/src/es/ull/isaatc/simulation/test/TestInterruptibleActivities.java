/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.ParallelFlow;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

class InterruptibleActivitiesSimulation extends Simulation {
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;
	public InterruptibleActivitiesSimulation(int id) {
		super(id, "Testing interruptible activities", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.MINUTE, 400));
		addInfoReciever(new StdInfoView(this));
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(rt, 1);
		for (int i = 0; i < NACT; i++)
			new TimeDrivenActivity(i, this, "ACT" + i, i / 2, EnumSet.of(TimeDrivenActivity.Modifier.INTERRUPTIBLE)).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 101), 0, wg);
		SimulationPeriodicCycle c1 = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 200.0), 0);
		SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 20.0, new SimulationTimeFunction(this, "ConstantVariate", 100.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "RES" + i).addTimeTableEntry(c2, 40, rt);
		ParallelFlow meta = new ParallelFlow(this);
		for (int i = 0; i < NACT; i++)
			new SingleFlow(this, getActivity(i));
		for (int i = 0; i < NELEMT; i++)
			new TimeDrivenGenerator(this, 
					new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(i, this, "ET" + i, i), meta), 
					c1);
	}
	
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestInterruptibleActivities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Testing interruptible activities", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				return new InterruptibleActivitiesSimulation(ind);
			}
			
		}.start();
	}

}
