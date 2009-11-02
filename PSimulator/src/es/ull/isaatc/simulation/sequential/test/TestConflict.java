package es.ull.isaatc.simulation.sequential.test;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.sequential.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelCycle;
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
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.util.Output;

/**
 * Define un modelo:
 * - A0 {RT0:1, RT1:1}; A1 {RT3:1, RT2:1}
 * - R0 {RT0, RT2}; R1 {RT3, RT1}
 * - E0 {A0}; E1 {A1} 
 */
class SimConflict1 extends StandAloneLPSimulation {
	final static int NRT = 4;
	final static int NACTS = 2;
	final static int NELEM = 1;
	
	SimConflict1(int id, Time startTs, Time endTs) {
		super(id, "Testing conflicts", TimeUnit.MINUTE, startTs, endTs);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = new WorkGroup();
		wgs[1] = new WorkGroup();
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		for (int i = 0; i < NACTS; i++)
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 40), wgs[i]);
		
		ModelCycle c = new ModelPeriodicCycle(unit, new Time(TimeUnit.MINUTE, 0.0), new ModelTimeFunction(unit, "ConstantVariate", 1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		r0.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(0));
		r0.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(2));
		r1.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(3));
		r1.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(1));

		ModelCycle c1 = new ModelPeriodicCycle(this, new Time(TimeUnit.MINUTE, 1.0), new ModelTimeFunction(this, "ConstantVariate", 1440.0), new Time(TimeUnit.MINUTE, 480.0));
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), new SingleFlow(this, getActivity(0))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(1, this, "ET1"), new SingleFlow(this, getActivity(1))), c1);

	}
}

/**
 * Define un modelo:
 * - A0 {RT0:1, RT1:1, RT4:1}; A1 {RT3:1, RT2:1}; A2 {RT5:1}
 * - R0 {RT0, RT2}; R1 {RT3, RT1}; R2 {RT5, RT4}
 * - E0 {A0}; E1 {A1}; E2 {A2} 
 */
class SimConflict2 extends StandAloneLPSimulation {
	final static int NRT = 6;
	final static int NACTS = 3;
	final static int NELEM = 1;
	
	public SimConflict2(int id, Time startTs, Time endTs) {
		super(id, "Testing conflicts", TimeUnit.MINUTE, startTs, endTs);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = new WorkGroup();
		wgs[1] = new WorkGroup();
		wgs[2] = new WorkGroup();
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[0].add(getResourceType(4), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		wgs[2].add(getResourceType(5), 1);
		for (int i = 0; i < NACTS; i++)
			new TimeDrivenActivity(i, this, "ACT" + i).addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", 40), wgs[i]);

		ModelCycle c = new ModelPeriodicCycle(this, new Time(TimeUnit.MINUTE, 0.0), new ModelTimeFunction(this, "ConstantVariate", 1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		Resource r2 = new Resource(2, this, "Res2");
		r0.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(0));
		r0.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(2));
		r1.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(3));
		r1.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(1));
		r2.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(4));
		r2.addTimeTableEntry(c, new Time(TimeUnit.MINUTE, 480.0), getResourceType(5));

		ModelCycle c1 = new ModelPeriodicCycle(this, new Time(TimeUnit.MINUTE, 1.0), new ModelTimeFunction(this, "ConstantVariate", 1440.0), new Time(TimeUnit.MINUTE, 480.0));
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), new SingleFlow(this, getActivity(0))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(1, this, "ET1"), new SingleFlow(this, getActivity(1))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(2, this, "ET2"), new SingleFlow(this, getActivity(2))), c1);
	}
}

class ExpConflict extends PooledExperiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;
    
    ExpConflict() {
    	super("CHECKING CONFLICTS", NTESTS);
    }
    
	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new SimConflict2(ind, new Time(TimeUnit.MINUTE, 0.0), new Time(TimeUnit.DAY, NDAYS));
		sim.setOutput(new Output(true));
		return sim;
	}	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestConflict {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpConflict().start();
	}

}
