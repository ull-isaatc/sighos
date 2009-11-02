/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.sequential.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

class InterruptibleActivitiesSimulation extends StandAloneLPSimulation {
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;
	public InterruptibleActivitiesSimulation(int id) {
		super(id, "Testing interruptible activities", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.MINUTE, 400));
		addInfoReceiver(new StdInfoView(this));
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(rt, 1);
		for (int i = 0; i < NACT; i++)
			new TimeDrivenActivity(i, this, "ACT" + i, i / 2, EnumSet.of(TimeDrivenActivity.Modifier.INTERRUPTIBLE)).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 101), 0, wg);
		ModelPeriodicCycle c1 = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 200.0), 0);
		ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 20.0, new ModelTimeFunction(unit, "ConstantVariate", 100.0), 0);
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
 * @author Iván Castilla Rodríguez
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
