package es.ull.iis.simulation.test;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.util.Output;

class ExpConflict extends Experiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;
	static SimulationType simType = SimulationType.PARALLEL;
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
    private void createSimulation1(SimulationFactory factory) {
    	final int NRT = 4;
    	final int NACTS = 2;
    	final int NELEM = 1;
    	
    	ResourceType [] rts = new ResourceType[NRT];
		for (int i = 0; i < NRT; i++)
			rts[i] = factory.getResourceTypeInstance("RT" + i);
		
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = factory.getWorkGroupInstance(new ResourceType[] {rts[0], rts[1]}, new int[] {1, 1});
		wgs[1] = factory.getWorkGroupInstance(new ResourceType[] {rts[3], rts[2]}, new int[] {1, 1});
		
		ActivityFlow acts[] = new ActivityFlow[NACTS];
		for (int i = 0; i < NACTS; i++) {
			acts[i] = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "ACT" + i);
			acts[i].addWorkGroup(0, wgs[i], new SimulationTimeFunction(unit, "ConstantVariate", 40));
		}
		
		SimulationCycle c = SimulationPeriodicCycle.newDailyCycle(unit);
		
		Resource r0 = factory.getResourceInstance("Res0");
		Resource r1 = factory.getResourceInstance("Res1");
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[0]);
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[2]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[3]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[1]);

		SimulationCycle c1 = new SimulationPeriodicCycle(unit, new TimeStamp(TimeUnit.MINUTE, 1), new SimulationTimeFunction(unit, "ConstantVariate", 1440), new TimeStamp(TimeUnit.MINUTE, 480));
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET0"), (InitializerFlow)factory.getFlowInstance("SingleFlow", acts[0]), c1);
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET1"), (InitializerFlow)factory.getFlowInstance("SingleFlow", acts[1]), c1);
    }
    
    /**
     * Defines a model:
     * - A0 {RT0:1, RT1:1, RT4:1}; A1 {RT3:1, RT2:1}; A2 {RT5:1}
     * - R0 {RT0, RT2}; R1 {RT3, RT1}; R2 {RT5, RT4}
     * - E0 {A0}; E1 {A1}; E2 {A2} 
     */
    @SuppressWarnings("unused")
	private void createSimulation2(SimulationFactory factory) {
    	final int NRT = 6;
    	final int NACTS = 3;
    	final int NELEM = 1;
    	
    	ResourceType [] rts = new ResourceType[NRT];
		for (int i = 0; i < NRT; i++)
			rts[i] = factory.getResourceTypeInstance("RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		wgs[0] = factory.getWorkGroupInstance(new ResourceType[] {rts[0], rts[1], rts[4]}, new int[] {1, 1, 1});
		wgs[1] = factory.getWorkGroupInstance(new ResourceType[] {rts[3], rts[2]}, new int[] {1, 1});
		wgs[2] = factory.getWorkGroupInstance(new ResourceType[] {rts[5]}, new int[] {1});

		ActivityFlow acts[] = new ActivityFlow[NACTS];
		for (int i = 0; i < NACTS; i++) {
			acts[i] = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "ACT" + i);
			acts[i].addWorkGroup(0, wgs[i], new SimulationTimeFunction(unit, "ConstantVariate", 40));
		}

		SimulationCycle c = SimulationPeriodicCycle.newDailyCycle(unit);
		
		Resource r0 = factory.getResourceInstance("Res0");
		Resource r1 = factory.getResourceInstance("Res1");
		Resource r2 = factory.getResourceInstance("Res1");
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[0]);
		r0.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[2]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[3]);
		r1.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[1]);
		r2.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[4]);
		r2.addTimeTableEntry(c, new TimeStamp(TimeUnit.MINUTE, 480), rts[5]);

		SimulationCycle c1 = new SimulationPeriodicCycle(unit, new TimeStamp(TimeUnit.MINUTE, 1), new SimulationTimeFunction(unit, "ConstantVariate", 1440), new TimeStamp(TimeUnit.MINUTE, 480));
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET0"), (InitializerFlow)factory.getFlowInstance("SingleFlow", acts[0]), c1);
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET1"), (InitializerFlow)factory.getFlowInstance("SingleFlow", acts[1]), c1);
		factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET2"), (InitializerFlow)factory.getFlowInstance("SingleFlow", acts[2]), c1);
    }
    
	@Override
	public Simulation getSimulation(int ind) {
		SimulationFactory factory = new SimulationFactory(ind, "TestConflicts", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();
		createSimulation1(factory);
		Simulation.setOutput(new Output(true));
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
