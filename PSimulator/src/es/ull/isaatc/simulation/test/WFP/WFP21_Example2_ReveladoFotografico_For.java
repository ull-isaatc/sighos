package es.ull.isaatc.simulation.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.ForLoopFlow;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.inforeceiver.statistics.CreatedElementsView;

class SimulationWFP21E2For extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP21E2For(int id, int ndays) {
		super(id, "WFP21: Structured Loop. EjReveladoFotografico", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Revelar foto", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

        new ResourceType(0, this, "Maquina revelado");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);

        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Maquina 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Maquina 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        ForLoopFlow root = new ForLoopFlow(this, sin1, TimeFunctionFactory.getInstance("ConstantVariate", 2));
        
        new ElementType(0, this, "Cliente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP21E2For extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP21E2For() {
		super("Tienda Revelado", NEXP);
	}

	public SimulationWFP21E2For getSimulation(int ind) {
		SimulationWFP21E2For sim = new SimulationWFP21E2For(ind, NDIAS);;
		StdInfoView debugView = new StdInfoView(sim);
//		ListenerExample exampleListener = new ListenerExample(sim);
//		NewInfoView exampleView = new NewInfoView(sim);
		CreatedElementsView estView = new CreatedElementsView(sim);
		sim.addInfoReciever(debugView);
//		sim.addInfoReciever(exampleListener);
//		sim.addInfoReciever(exampleView);
		sim.addInfoReciever(estView);
//		try {
//			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return sim;
	}
}

public class WFP21_Example2_ReveladoFotografico_For {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP21E2For().start();
	}

}
