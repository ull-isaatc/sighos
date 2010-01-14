package es.ull.isaatc.simulation.test;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.util.Output;

class ExpConflict extends PooledExperiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;
	static SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	static final TimeUnit unit = TimeUnit.MINUTE;
    
    ExpConflict() {
    	super("CHECKING CONFLICTS", NTESTS);
    }
    
    /**
     * Defines a model:
     * - A0 {RT0:1, RT1:1}; A1 {RT3:1, RT2:1}
     * - R0 {RT0, RT2}; R1 {RT3, RT1}
     * - E0 {A0}; E1 {A1} 
     */
    private void createSimulation1(SimulationObjectFactory factory) {
    	final int NRT = 4;
    	final int NACTS = 2;
    	final int NELEM = 1;
    	
    	ResourceType [] rts = new ResourceType[NRT];
		for (int i = 0; i < NRT; i++)
			rts[i] = factory.getResourceTypeInstance(i, "RT" + i);
		
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = factory.getWorkGroupInstance(0, new ResourceType[] {rts[0], rts[1]}, new int[] {1, 1});
		wgs[1] = factory.getWorkGroupInstance(1, new ResourceType[] {rts[3], rts[2]}, new int[] {1, 1});
		
		TimeDrivenActivity acts[] = new TimeDrivenActivity[NACTS];
		for (int i = 0; i < NACTS; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "ACT" + i);
			acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 40), wgs[i]);
		}
		
		SimulationCycle c = SimulationPeriodicCycle.newDailyCycle(unit);
		
		Resource r0 = factory.getResourceInstance(0, "Res0");
		Resource r1 = factory.getResourceInstance(1, "Res1");
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[0]);
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[2]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[3]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[1]);

		SimulationCycle c1 = new SimulationPeriodicCycle(unit, new TimeStamp(TimeUnit.MINUTE, 1), new SimulationTimeFunction(unit, "ConstantVariate", 1440), new TimeStamp(TimeUnit.MINUTE, 480));
		factory.getTimeDrivenGeneratorInstance(0, 
				factory.getElementCreatorInstance(0, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance(0, "ET0"), (InitializerFlow)factory.getFlowInstance(0, "SingleFlow", acts[0])), c1);
		factory.getTimeDrivenGeneratorInstance(1, 
				factory.getElementCreatorInstance(1, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance(1, "ET1"), (InitializerFlow)factory.getFlowInstance(1, "SingleFlow", acts[1])), c1);
    }
    
    /**
     * Defines a model:
     * - A0 {RT0:1, RT1:1, RT4:1}; A1 {RT3:1, RT2:1}; A2 {RT5:1}
     * - R0 {RT0, RT2}; R1 {RT3, RT1}; R2 {RT5, RT4}
     * - E0 {A0}; E1 {A1}; E2 {A2} 
     */
    private void createSimulation2(SimulationObjectFactory factory) {
    	final int NRT = 6;
    	final int NACTS = 3;
    	final int NELEM = 1;
    	
    	ResourceType [] rts = new ResourceType[NRT];
		for (int i = 0; i < NRT; i++)
			rts[i] = factory.getResourceTypeInstance(i, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = factory.getWorkGroupInstance(0, new ResourceType[] {rts[0], rts[1], rts[4]}, new int[] {1, 1, 1});
		wgs[1] = factory.getWorkGroupInstance(1, new ResourceType[] {rts[3], rts[2]}, new int[] {1, 1});
		wgs[2] = factory.getWorkGroupInstance(2, new ResourceType[] {rts[5]}, new int[] {1});

		TimeDrivenActivity acts[] = new TimeDrivenActivity[NACTS];
		for (int i = 0; i < NACTS; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "ACT" + i);
			acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 40), wgs[i]);
		}

		SimulationCycle c = SimulationPeriodicCycle.newDailyCycle(unit);
		
		Resource r0 = factory.getResourceInstance(0, "Res0");
		Resource r1 = factory.getResourceInstance(1, "Res1");
		Resource r2 = factory.getResourceInstance(2, "Res1");
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[0]);
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[2]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[3]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[1]);
		r2.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[4]);
		r2.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[5]);

		SimulationCycle c1 = new SimulationPeriodicCycle(unit, new TimeStamp(TimeUnit.MINUTE, 1), new SimulationTimeFunction(unit, "ConstantVariate", 1440), new TimeStamp(TimeUnit.MINUTE, 480));
		factory.getTimeDrivenGeneratorInstance(0, 
				factory.getElementCreatorInstance(0, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance(0, "ET0"), (InitializerFlow)factory.getFlowInstance(0, "SingleFlow", acts[0])), c1);
		factory.getTimeDrivenGeneratorInstance(1, 
				factory.getElementCreatorInstance(1, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance(1, "ET1"), (InitializerFlow)factory.getFlowInstance(1, "SingleFlow", acts[1])), c1);
		factory.getTimeDrivenGeneratorInstance(2, 
				factory.getElementCreatorInstance(2, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance(2, "ET2"), (InitializerFlow)factory.getFlowInstance(2, "SingleFlow", acts[2])), c1);
    }
    
	@Override
	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "TestConflicts", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();
		createSimulation1(factory);
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
