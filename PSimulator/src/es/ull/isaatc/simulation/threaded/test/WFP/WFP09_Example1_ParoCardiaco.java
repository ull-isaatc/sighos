package es.ull.isaatc.simulation.threaded.test.WFP;

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
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.flow.StructuredDiscriminatorFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationWFP9E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP9E1(int id, int ndays) {
		super(id, "WFP9: Structured Discriminator. EjParoCardiaco", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Comprobar respiracion", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Comprobar pulso", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Masaje cardiaco", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

        new ResourceType(0, this, "Doctor");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);

        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(this, 480, new ModelTimeFunction(this, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(this, 0, new ModelTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Doctor 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Doctor 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        StructuredDiscriminatorFlow root = new StructuredDiscriminatorFlow(this);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
              
        root.addBranch(sin1);
        root.addBranch(sin2);
        root.link(sin3);
        
        new ElementType(0, this, "Paciente");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP9E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP9E1() {
		super("Hospital", NEXP);
	}

	public SimulationWFP9E1 getSimulation(int ind) {
		SimulationWFP9E1 sim = new SimulationWFP9E1(ind, NDIAS);
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReciever(debugView);
//		try {
//			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
		return sim;
	}
}

public class WFP09_Example1_ParoCardiaco {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentWFP9E1().start();
	}

}
