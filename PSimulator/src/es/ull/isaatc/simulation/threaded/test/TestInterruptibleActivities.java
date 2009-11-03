/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ParallelFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class InterruptibleActivitiesSimulation extends StandAloneLPSimulation {
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;
	public InterruptibleActivitiesSimulation(int id) {
		super(id, "Testing interruptible activities", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.MINUTE, 400));
		addInfoReciever(new StdInfoView(this));
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(rt, 1);
		for (int i = 0; i < NACT; i++)
			new TimeDrivenActivity(i, this, "ACT" + i, i / 2, EnumSet.of(TimeDrivenActivity.Modifier.INTERRUPTIBLE)).addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 101), 0, wg);
		ModelPeriodicCycle c1 = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 200.0), 0);
		ModelPeriodicCycle c2 = new ModelPeriodicCycle(this, 20.0, new ModelTimeFunction(this, "ConstantVariate", 100.0), 0);
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
