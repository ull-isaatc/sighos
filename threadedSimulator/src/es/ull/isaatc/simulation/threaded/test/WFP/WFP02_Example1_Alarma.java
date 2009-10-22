package es.ull.isaatc.simulation.threaded.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ParallelFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class SimulationWFP2E2 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP2E2(int id, int ndays) {
		super(id, "WFP2: Parallel Split. EjAlarma", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Detecci�n alarma");
    	new TimeDrivenActivity(1, this, "Mandar patrulla", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Generar informe", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Operador");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
   
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Operador1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Operador2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Operador3").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(3, this, "Operador4").addTimeTableEntry(c2, 420, getResourceType(0));
        
        
        SingleFlow root = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        ParallelFlow par1 = new ParallelFlow(this);
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        
        root.link(par1);
        par1.link(sin2);
        par1.link(sin3);
         
        new ElementType(0, this, "Activaciones de alarma");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

/**
 * 
 */
class ExperimentWFP2E2 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP2E2() {
		super("Comisaria", NEXP);
	}

	public SimulationWFP2E2 getSimulation(int ind) {
		SimulationWFP2E2 sim = new SimulationWFP2E2(ind, NDIAS);
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

public class WFP02_Example1_Alarma {
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP2E2().start();
	}

}
