package es.ull.isaatc.simulation.sequential.test.WFP;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.model.ElementType;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

class SimulationWFP19 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP19(int id, int ndays) {
		super(id, "WFP19: Cancel Task. EjTarjetaCredito", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Verificar cuenta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
    	new TimeDrivenActivity(1, this, "Obtener detalles tarjeta", EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
        
        new ResourceType(0, this, "Cajero");
        
        WorkGroup wg = new WorkGroup(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(this, 480, new ModelTimeFunction(this, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(this, 0, new ModelTimeFunction(this, "ConstantVariate", 1040.0 * 7), 0, subc2);

        new Resource(0, this, "Cajero1").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(1, this, "Cajero2").addTimeTableEntry(c2, 420, getResourceType(0));
        new Resource(2, this, "Cajero3").addTimeTableEntry(c2, 420, getResourceType(0));
        
        
        SingleFlow root = new SingleFlow(this, getActivity(0)) {
        	boolean pass = false;
	        @Override
	        public boolean beforeRequest(Element e) {
	        	pass = !pass;
	        	return pass && super.beforeRequest(e);
	        }
        };
        SingleFlow sin1 = new SingleFlow(this, getActivity(1));
        
        root.link(sin1);

         
        new ElementType(0, this, "Cliente");
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1040.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), getElementType(0), root), cGen);        
    }
}

public class WFP19_Example1_TarjetaCredito {
	static final int NEXP = 1;
    static final int NDIAS = 1;
	static final double PERIOD = 1040.0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Exp19", NEXP) {
			@Override
			public Simulation getSimulation(int ind) {
				SimulationWFP19 sim = new SimulationWFP19(ind, NDIAS);				
				sim.addInfoReceiver(new StdInfoView(sim));
				return sim;
			}
		}.start();
	}

}