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
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.WorkGroup;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.flow.SynchronizationFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

class SimulationWFP33 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP33(int id, int ndays) {
		super(id, "WFP33: Generalized AND-Join. EjEnvioMercacias", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Generacion de factura", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Comprobacion de factura", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(2, this, "Envio de mercancias", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Comercial");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
   
        SimulationPeriodicCycle subc2 = new SimulationPeriodicCycle(this, 480, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), 5);
        SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Comercial1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Comercial2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Comercial3").addTimeTableEntry(c2, 420, getResourceType(0));
        
        ParallelFlow root = new ParallelFlow(this);
        SynchronizationFlow synchro1 = new SynchronizationFlow(this, false);
        SingleFlow sin1 = new SingleFlow(this, (TimeDrivenActivity)getActivity(0));
        SingleFlow sin2 = new SingleFlow(this, (TimeDrivenActivity)getActivity(1));
        SingleFlow sin3 = new SingleFlow(this, (TimeDrivenActivity)getActivity(2));
        
        root.link(sin1);
        root.link(sin2);
        sin1.link(synchro1);
        sin2.link(synchro1);
        synchro1.link(sin3);
        
        new ElementType(0, this, "Cliente");
        SimulationPeriodicCycle cGen = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
	
}

public class WFP33_Example1_EnvioMercancias {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp33", NEXP) {
			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP33 sim = new SimulationWFP33(ind, NDIAS);
				sim.addInfoReceiver(new StdInfoView(sim));
				return sim;
			}
		}.start();
	}
}
