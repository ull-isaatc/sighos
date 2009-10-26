package es.ull.isaatc.simulation.threaded.test;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.model.ModelPeriodicCycle;
import es.ull.isaatc.simulation.model.ModelTimeFunction;
import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationProbSel extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationProbSel(int id, int ndays) {
		super(id, "EjProbabilidades", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "10 %", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "30 %", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "60 %", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        new ResourceType(0, this, "Empleado");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);

        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(this, 480, new ModelTimeFunction(this, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(this, 0, new ModelTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Empleado1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Empleado2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Empleado3").addTimeTableEntry(c2, 420, getResourceType(0));
        

        ProbabilitySelectionFlow root = new ProbabilitySelectionFlow(this);
        SingleFlow sin0 = new SingleFlow(this, getActivity(0));
        SingleFlow sin1 = new SingleFlow(this, getActivity(1));
        SingleFlow sin2 = new SingleFlow(this, getActivity(2));
        
        root.link(sin0, 0.1);
        root.link(sin1, 0.3);
        root.link(sin2, 0.6);
        
        new ElementType(0, this, "Cliente");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 100), getElementType(0), root), cGen);        
    }
	
}
/**
 * 
 */
class ExperimentProbSel extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	
	public ExperimentProbSel() {
		super("Banco", NEXP);
	}

	public SimulationProbSel getSimulation(int ind) {
		SimulationProbSel sim = null;
		sim = new SimulationProbSel(ind, NDIAS);
		
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReciever(debugView);
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
