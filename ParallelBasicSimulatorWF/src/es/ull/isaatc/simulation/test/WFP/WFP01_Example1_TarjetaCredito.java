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
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

class SimulationWFP1E1 extends Simulation {
	int ndays;
	
	public SimulationWFP1E1(int id, int ndays) {
		super(id, "WFP1: Sequence. EjTarjetaCredito", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Verificar cuenta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Obtener detalles tarjeta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Cajero");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
   
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Cajero1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Cajero2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Cajero3").addTimeTableEntry(c2, 420, getResourceType(0));
        
        
        SingleFlow root = new SingleFlow(this, getActivity(0));
        SingleFlow sin1 = new SingleFlow(this, getActivity(1));
        
        root.link(sin1);

         
        new ElementType(0, this, "Cliente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}
/**
 * 
 */
class ExperimentWFP1E1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	public ExperimentWFP1E1() {
		super("Banco", NEXP);
	}

	public SimulationWFP1E1 getSimulation(int ind) {
		SimulationWFP1E1 sim = null;
		sim = new SimulationWFP1E1(ind, NDIAS);
		
		StdInfoView debugView = new StdInfoView(sim);
		sim.addInfoReciever(debugView);
		return sim;
	}
	

}


public class WFP01_Example1_TarjetaCredito {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExperimentWFP1E1().start();
	}

}
