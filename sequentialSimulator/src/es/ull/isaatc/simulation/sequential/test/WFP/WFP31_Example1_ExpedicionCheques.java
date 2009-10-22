package es.ull.isaatc.simulation.sequential.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.ElementType;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.PartialJoinFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

class SimulationWFP31E1 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP31E1(int id, int ndays) {
		super(id, "WFP31: Blocking Partial Join. EjExpedicionCheques", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "AprobarCuenta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "ExpedirCheque", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	
        new ResourceType(0, this, "Director");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 5.0), wg);

        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Director 1").addTimeTableEntry(c2, 420, getResourceType(0));        
        new Resource(1, this, "Director 2").addTimeTableEntry(c2, 420, getResourceType(0));
        
        ParallelFlow root = new ParallelFlow(this);        
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        PartialJoinFlow part1 = new PartialJoinFlow(this, 2);
        SingleFlow sin4 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        
        root.link(sin1);
        root.link(sin2);
        root.link(sin3);
        sin1.link(part1);
        sin2.link(part1);
        sin3.link(part1);
        part1.link(sin4);
        
        new ElementType(0, this, "Cliente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP31E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP31E1() {
		super("Banco", NEXP);
	}

	public SimulationWFP31E1 getSimulation(int ind) {
		SimulationWFP31E1 sim = new SimulationWFP31E1(ind, NDIAS);
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReceiver(debugView);
//		try {
//			sim.setOutput(new Output(true, new FileWriter("c:\\test.txt")));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}		
		return sim;
	}
}

public class WFP31_Example1_ExpedicionCheques {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExperimentWFP31E1().start();
	}

}
