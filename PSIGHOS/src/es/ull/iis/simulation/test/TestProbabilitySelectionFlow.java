package es.ull.iis.simulation.test;
import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ProbabilitySelectionFlow;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * 
 */
class ExperimentProbSel extends Experiment {
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;
	
	public ExperimentProbSel() {
		super("Banco", NEXP);
	}

	public Simulation getModel(int ind) {
		Simulation sim = null;
		TimeUnit unit = TimeUnit.MINUTE;
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "EjProbabilidades", TimeUnit.MINUTE, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		sim = factory.getSimulationEngine();

    	ActivityFlow act0 = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "10 %", 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
    	ActivityFlow act1 = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "30 %", 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
    	ActivityFlow act2 = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "60 %", 0, EnumSet.of(ActivityFlow.Modifier.NONPRESENTIAL));
        ResourceTypeEngine rt = factory.getResourceTypeInstance("Empleado");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceTypeEngine[] {rt}, new int[] {1});

        act0.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), 0, wg);
        act1.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), 0, wg);
        act2.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), 0, wg);
   
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(unit, 480, new SimulationTimeFunction(unit, "ConstantVariate", 1040), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 1040 * 7), 0, subc2);

        factory.getResourceInstance("Empleado1").addTimeTableEntry(c2, 420, rt);
        factory.getResourceInstance("Empleado2").addTimeTableEntry(c2, 420, rt);
        factory.getResourceInstance("Empleado3").addTimeTableEntry(c2, 420, rt);
        

        ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
        
        root.link(act0, 0.1);
        root.link(act1, 0.3);
        root.link(act2, 0.6);
        
        ElementType et = factory.getElementTypeInstance("Cliente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 1040), NDAYS);
        factory.getTimeDrivenGeneratorInstance(factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 100), et, root), cGen);        
		
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReceiver(debugView);
		return sim;
	}
	

}


public class TestProbabilitySelectionFlow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentProbSel().start();
	}

}
