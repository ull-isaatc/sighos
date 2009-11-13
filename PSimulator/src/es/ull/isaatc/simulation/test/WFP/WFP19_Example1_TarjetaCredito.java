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
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.StandAloneLPSimulation;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeDrivenGenerator;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

class SimulationWFP19 extends StandAloneLPSimulation {
	int ndays;
	
	public SimulationWFP19(int id, int ndays) {
		super(id, "WFP19: Cancel Task. EjTarjetaCredito", TimeUnit.MINUTE, Time.getZero(), new Time(TimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
   	
    	new TimeDrivenActivity(0, this, "Verificar cuenta", false);
    	new TimeDrivenActivity(1, this, "Obtener detalles tarjeta", false);
        
        new ResourceType(0, this, "Cajero");
        
        WorkGroup wg = factory.getWorkGroupInstance(getResourceType(0), 1);
        ((TimeDrivenActivity)getActivity(0)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg);
        ((TimeDrivenActivity)getActivity(1)).addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 15.0, 2.0), wg);
   
        ModelPeriodicCycle subc2 = new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), 5);
        ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0 * 7), 0, subc2);

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
        ModelPeriodicCycle cGen = new ModelPeriodicCycle(unit, 0.0, new ModelTimeFunction(unit, "ConstantVariate", 1040.0), ndays);
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
