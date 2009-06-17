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
import es.ull.isaatc.simulation.flow.DiscriminatorFlow;
import es.ull.isaatc.simulation.flow.ParallelFlow;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

class SimulationWFP28E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP28E1(int id, int ndays) {
		super(id, "WFP28: Blocking Discriminator. EjComprobacionCredenciales", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Confirmar llegada delegacion", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Chequeo de seguridad", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Preparacion para nueva delegacion", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));

        new ResourceType(0, this, "Asistente");
        new ResourceType(1, this, "Personal Seguridad");
        
        WorkGroup wg0 = new WorkGroup(getResourceType(0), 1);
        WorkGroup wg1 = new WorkGroup(getResourceType(1), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 18.0, 1.0), wg0);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 20.0, 10.0), wg1);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 5.0), wg0);
        
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Asistente 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Asistente 2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Segurita 1").addTimeTableEntry(c2, 420, getResourceType(1));
        new Resource(3, this, "Segurita 2").addTimeTableEntry(c2, 420, getResourceType(1));
        
        ParallelFlow root = new ParallelFlow(this);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        DiscriminatorFlow dis1 = new DiscriminatorFlow(this);
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(dis1);
        sin2.link(dis1);
        dis1.link(sin3);
        
        new ElementType(0, this, "Asistente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP28E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP28E1() {
		super("Tienda Revelado", NEXP);
	}

	public SimulationWFP28E1 getSimulation(int ind) {
		SimulationWFP28E1 sim = new SimulationWFP28E1(ind, NDIAS);
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

public class WFP28_Example1_ComprobacionCredenciales {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP28E1().start();
	}

}
