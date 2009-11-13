package es.ull.isaatc.simulation.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.inforeceiver.statistics.CreatedElementsView;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.StandAloneLPSimulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

class SimulationWFP21E2For extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP21E2For(int id, int ndays) {
		super(id, "WFP21: Structured Loop. EjReveladoFotografico", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Revelar foto", false);

        new ResourceType(0, this, "Maquina revelado");
        
        WorkGroup wg = factory.getWorkGroupInstance(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 5.0), wg);

        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Maquina 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Maquina 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        ForLoopFlow root = new ForLoopFlow(this, sin1, TimeFunctionFactory.getInstance("ConstantVariate", 2));
        
        new ElementType(0, this, "Cliente");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
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
		sim.addInfoReceiver(debugView);
//		sim.addInfoReciever(exampleListener);
//		sim.addInfoReciever(exampleView);
		sim.addInfoReceiver(estView);
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
