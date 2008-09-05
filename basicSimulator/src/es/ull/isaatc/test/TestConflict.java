package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
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
	
	SimConflict1(int id, SimulationTime startTs, SimulationTime endTs) {
		super(id, "Testing conflicts", SimulationTimeUnit.MINUTE, startTs, endTs);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = new WorkGroup(0, this, "");
		wgs[1] = new WorkGroup(1, this, "");
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		for (int i = 0; i < NACTS; i++)
			new Activity(i, this, "ACT" + i).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 40), wgs[i]);
		
		SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		r0.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(0));
		r0.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(2));
		r1.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(3));
		r1.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(1));

		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 1.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), new SimulationTime(SimulationTimeUnit.MINUTE, 480.0));
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(1, this, "ET1"), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))), c1);
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
	
	public SimConflict2(int id, SimulationTime startTs, SimulationTime endTs) {
		super(id, "Testing conflicts", SimulationTimeUnit.MINUTE, startTs, endTs);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = new WorkGroup(0, this, "");
		wgs[1] = new WorkGroup(1, this, "");
		wgs[2] = new WorkGroup(2, this, "");
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[0].add(getResourceType(4), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		wgs[2].add(getResourceType(5), 1);
		for (int i = 0; i < NACTS; i++)
			new Activity(i, this, "ACT" + i).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 40), wgs[i]);

		SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		Resource r2 = new Resource(2, this, "Res2");
		r0.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(0));
		r0.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(2));
		r1.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(3));
		r1.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(1));
		r2.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(4));
		r2.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(5));

		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 1.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), new SimulationTime(SimulationTimeUnit.MINUTE, 480.0));
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(1, this, "ET1"), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(2, this, "ET2"), new SingleMetaFlow(2, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2))), c1);
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
		Simulation sim = new SimConflict2(ind, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTime(SimulationTimeUnit.DAY, NDAYS));
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
