package es.ull.isaatc.simulation.test;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

/**
 * 
 */
class ExperimentProbSel extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDAYS = 1;
	static SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	
	public ExperimentProbSel() {
		super("Banco", NEXP);
	}

	public Simulation getSimulation(int ind) {
		Simulation sim = null;
		TimeUnit unit = TimeUnit.MINUTE;
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "EjProbabilidades", TimeUnit.MINUTE, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		sim = factory.getSimulation();

    	TimeDrivenActivity act0 = factory.getTimeDrivenActivityInstance("10 %", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	TimeDrivenActivity act1 = factory.getTimeDrivenActivityInstance("30 %", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	TimeDrivenActivity act2 = factory.getTimeDrivenActivityInstance("60 %", 0, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        ResourceType rt = factory.getResourceTypeInstance("Empleado");
        
        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});

        act0.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), wg);
        act1.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), wg);
        act2.addWorkGroup(new SimulationTimeFunction(unit, "NormalVariate", 15, 2), wg);
   
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(unit, 480, new SimulationTimeFunction(unit, "ConstantVariate", 1040), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 1040 * 7), 0, subc2);

        factory.getResourceInstance("Empleado1").addTimeTableEntry(c2, 420, rt);
        factory.getResourceInstance("Empleado2").addTimeTableEntry(c2, 420, rt);
        factory.getResourceInstance("Empleado3").addTimeTableEntry(c2, 420, rt);
        

        ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
        SingleFlow sin0 = (SingleFlow)factory.getFlowInstance("SingleFlow", act0);
        SingleFlow sin1 = (SingleFlow)factory.getFlowInstance("SingleFlow", act1);
        SingleFlow sin2 = (SingleFlow)factory.getFlowInstance("SingleFlow", act2);
        
        root.link(sin0, 0.1);
        root.link(sin1, 0.3);
        root.link(sin2, 0.6);
        
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
