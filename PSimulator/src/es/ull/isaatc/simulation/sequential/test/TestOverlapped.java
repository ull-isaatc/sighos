package es.ull.isaatc.simulation.sequential.test;

import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.model.ElementType;
import es.ull.isaatc.simulation.model.WorkGroup;
import es.ull.isaatc.simulation.sequential.ElementCreator;
import es.ull.isaatc.simulation.sequential.Resource;
import es.ull.isaatc.simulation.sequential.ResourceType;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenActivity;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;
import es.ull.isaatc.simulation.sequential.flow.ParallelFlow;
import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.sequential.inforeceiver.StdInfoView;

/**
 * 
 */

/**
 * @author Iván Castilla Rodríguez
 *
 */
class OverlappedSimulation extends StandAloneLPSimulation {
	final static int NELEM = 3;
	final static int NRESOURCES = 2;
	final static int NEEDED = 1;
	int days;
    
	OverlappedSimulation(int id, int days) {
		super(id, "Sistema de análisis", TimeUnit.MINUTE, 0.0, days * 24 * 60.0);
		this.days = days;
    }
    
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
//    	TimeDrivenActivity actDummy = new TimeDrivenActivity(0, this, "Dummy");
    	TimeDrivenActivity actSangre = new TimeDrivenActivity(0, this, "Análisis de sangre");
    	TimeDrivenActivity actOrina = new TimeDrivenActivity(1, this, "Análisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "Máquina Análisis Sangre");
        ResourceType crOrina = new ResourceType(1, this, "Máquina Análisis Orina");
//        ResourceType crDummy = new ResourceType(2, this, "Dummy");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = new WorkGroup(crSangre, NEEDED); 
        actSangre.addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 20.0, 5.0), wg1);
//        wg1.add(crOrina, 1);
        WorkGroup wg2 = new WorkGroup(crOrina, 1);
        actOrina.addWorkGroup(new ModelTimeFunction(this, "NormalVariate", 20.0, 5.0), wg2);
//        WorkGroup wg3 = actDummy.getNewWorkGroup(0, new Normal(10.0, 2.0));
//        wg3.add(crDummy, 1);

//		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
//		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
//        for (int i = 0; i < NRESOURCES; i++) {
//        	Resource res = new Resource(i, this, "Máquina Análisis Sangre " + i);
//        	res.addTimeTableEntry(new SimulationPeriodicCycle(this, 480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al1);
//        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDummy);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new SimulationPeriodicCycle(this, 480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al2);

		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
		al2.add(crOrina);
		al2.add(crSangre);
        for (int i = 0; i < NRESOURCES; i++) {
			Resource poli1 = new Resource(i, this, "Máquina Polivalente 1");
			poli1.addTimeTableEntry(new ModelPeriodicCycle(this, 480, new ModelTimeFunction(this, "ConstantVariate", 1440.0), 0), 480, al2);
        }
        
      ParallelFlow metaFlow = new ParallelFlow(this);
      new SingleFlow(this, getActivity(1));
      new SingleFlow(this, getActivity(0));
//		SingleMetaFlow metaFlow = new SingleMetaFlow(3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));     
		ModelPeriodicCycle c = new ModelPeriodicCycle(this, 0.0, new ModelTimeFunction(this, "ConstantVariate", 1440.0), days);
		new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), metaFlow), c);
	}	
}

class ExpOverlapped extends PooledExperiment {
    static final int NDIAS = 1;
    static final int NPRUEBAS = 2;

	public ExpOverlapped(String description) {
		super(description, NPRUEBAS);
	}

	public Simulation getSimulation(int ind) {
		OverlappedSimulation sim = new OverlappedSimulation(ind, NDIAS);
		sim.addInfoReceiver(new StdInfoView(sim));
		return sim;
	}
}

public class TestOverlapped {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpOverlapped("Solapados").start();
	}

}
