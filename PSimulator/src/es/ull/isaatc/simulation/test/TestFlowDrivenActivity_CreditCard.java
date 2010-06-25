package es.ull.isaatc.simulation.test;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.util.Output;

/**
 * 
 */
class ExperimentFDAE1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static final double PERIOD = 1040.0;
	static final TimeUnit unit = TimeUnit.MINUTE;
	static SimulationFactory.SimulationType simType = SimulationType.GROUPED3PHASE;
	
	public ExperimentFDAE1() {
		super("Bank", NEXP);
	}

	public Simulation getSimulation(int ind) {
		Simulation sim = null;
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "ExCreaditCard", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		sim = factory.getSimulation();
		
    	TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance("Verify account", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	TimeDrivenActivity act1 = factory.getTimeDrivenActivityInstance("Get card details", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	FlowDrivenActivity act2 = factory.getFlowDrivenActivityInstance("Process completed");
        ResourceType rt0 = factory.getResourceTypeInstance("Cashier");
        ResourceType rt1 = factory.getResourceTypeInstance("Director");
        
        WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});

        act0.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), wg0);
        act1.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), wg0);
   
        SimulationPeriodicCycle c2 = SimulationPeriodicCycle.newDailyCycle(unit);

        factory.getResourceInstance("Cashier1").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Cashier2").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Cashier3").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Director1").addTimeTableEntry(c2, 420, rt1);
        
        
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        
        root.link(sin1);

        act2.addWorkGroup(root, sin1, wg1);
        SingleFlow whole = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
         
        ElementType et = factory.getElementTypeInstance("Cliente");
        SimulationPeriodicCycle cGen = SimulationPeriodicCycle.newDailyCycle(unit);
        factory.getTimeDrivenGeneratorInstance(factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 3), et, whole), cGen);        
		
		sim.addInfoReceiver(new StdInfoView(sim));
//		try {
//			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return sim;
	}
	

}


public class TestFlowDrivenActivity_CreditCard {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentFDAE1().start();
	}

}
