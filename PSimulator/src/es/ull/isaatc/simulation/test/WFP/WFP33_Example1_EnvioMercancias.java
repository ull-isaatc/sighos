package es.ull.isaatc.simulation.test.WFP;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.StandAloneLPSimulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.SynchronizationFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

class SimulationWFP33 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP33(int id, int ndays) {
		super(id, "WFP33: Generalized AND-Join. EjEnvioMercacias", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Generacion de factura", false);
    	new TimeDrivenActivity(1, this, "Comprobacion de factura", false);
    	new TimeDrivenActivity(2, this, "Envio de mercancias", false);
        
        new ResourceType(0, this, "Comercial");
        
        WorkGroup wg = factory.getWorkGroupInstance(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(2)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

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
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
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
