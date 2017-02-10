package es.ull.iis.simulation.examples;
import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

/**
 * 
 */
class ExperimentFDAE1 extends Experiment {
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static final double PERIOD = 1040.0;
	static final TimeUnit unit = TimeUnit.MINUTE;
	static SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;
	
	public ExperimentFDAE1() {
		super("Bank", NEXP);
	}

	public Simulation<?> getSimulation(int ind) {
		Simulation<?> sim = null;
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "ExCreaditCard", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		sim = factory.getSimulation();
		
    	ActivityFlow<?,?> act0 = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "Verify account", 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
    	ActivityFlow<?,?> act1 = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "Get card details", 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
    	ActivityFlow<?,?> act2 = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "Process completed");
        ResourceType rt0 = factory.getResourceTypeInstance("Cashier");
        ResourceType rt1 = factory.getResourceTypeInstance("Director");
        
        WorkGroup wg0 = factory.getWorkGroupInstance(new ResourceType[] {rt0}, new int[] {1});
        WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {rt1}, new int[] {1});

        act0.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), 0, wg0);
        act1.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), 0, wg0);
   
        SimulationPeriodicCycle c2 = SimulationPeriodicCycle.newDailyCycle(unit);

        factory.getResourceInstance("Cashier1").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Cashier2").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Cashier3").addTimeTableEntry(c2, 420, rt0);
        factory.getResourceInstance("Director1").addTimeTableEntry(c2, 420, rt1);
        
        act0.link(act1);

        act2.addWorkGroup(act0, act1, wg1);
         
        ElementType et = factory.getElementTypeInstance("Cliente");
        SimulationPeriodicCycle cGen = SimulationPeriodicCycle.newDailyCycle(unit);
        factory.getTimeDrivenGeneratorInstance(factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 3), et, act2), cGen);        
		
		sim.addInfoReceiver(new StdInfoView(sim));

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
