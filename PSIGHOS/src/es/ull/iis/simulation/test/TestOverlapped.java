package es.ull.iis.simulation.test;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ModelPeriodicCycle;
import es.ull.iis.simulation.model.ModelTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * 
 */
class ExpOverlapped extends Experiment {
	final static SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;
	final static TimeUnit unit = TimeUnit.MINUTE;
    static final int NDAYS = 1;
    static final int NTESTS = 2;
	final static int NELEM = 3;
	final static int NRESOURCES = 2;
	final static int NEEDED = 1;

	public ExpOverlapped(String description) {
		super(description, NTESTS);
	}

	public Simulation getSimulation(int ind) {
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Sistema de an�lisis", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.DAY, NDAYS));
		Simulation sim = factory.getSimulation();

        // PASO 1: Inicializo las Activityes de las que se compone
//    	Activity actDummy = factory.getActivityInstance("Dummy");
    	ActivityFlow<?,?> actSangre = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "An�lisis de sangre");
    	ActivityFlow<?,?> actOrina = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "An�lisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = factory.getResourceTypeInstance("M�quina An�lisis Sangre");
        ResourceType crOrina = factory.getResourceTypeInstance("M�quina An�lisis Orina");
//        ResourceType crDummy = factory.getResourceTypeInstance("Dummy");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = factory.getWorkGroupInstance(new ResourceType[] {crSangre}, new int[] {NEEDED});
        actSangre.addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 20, 5), 0, wg1);
//        wg1.add(crOrina, 1);
        WorkGroup wg2 = factory.getWorkGroupInstance(new ResourceType[] {crOrina}, new int[] {1});
        actOrina.addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 20, 5), 0, wg2);
//        WorkGroup wg3 = factory.getWorkGroupInstance(new ResourceType[] {crDummy}, new int[] {1});
//        actDummy.addWorkGroup(new ModelTimeFunction(unit, "NormalVariate", 10, 2), wg3);

//		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
//		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
//        for (int i = 0; i < NRESOURCES; i++) {
//        	Resource res = factory.getResourceInstance(i, "M�quina An�lisis Sangre " + i);
//        	res.addTimeTableEntry(new ModelPeriodicCycle(unit, 480, RandomVariateFactory.getInstance("ConstantVariate", 1440), 0), 480, al1);
//        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDummy);
//		Resource orina1 = factory.getResourceInstance("M�quina An�lisis Orina 1");
//		orina1.addTimeTableEntry(new ModelPeriodicCycle(unit, 480, RandomVariateFactory.getInstance("ConstantVariate", 1440), 0), 480, al2);

		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
		al2.add(crOrina);
		al2.add(crSangre);
        for (int i = 0; i < NRESOURCES; i++) {
			Resource poli1 = factory.getResourceInstance("M�quina Polivalente 1");
			poli1.addTimeTableEntry(new ModelPeriodicCycle(unit, 480, new ModelTimeFunction(unit, "ConstantVariate", 1440), 0), 480, al2);
        }
        
		ParallelFlow metaFlow = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
		metaFlow.link(actOrina);
		metaFlow.link(actSangre);
		
//		SingleFlow metaFlow = (SingleFlow)factory.getFlowInstance(RandomVariateFactory.getInstance("ConstantVariate", 1), actDummy);     
		ModelPeriodicCycle c = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 1440), NDAYS);
		factory.getTimeDrivenGeneratorInstance(
				factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
						factory.getElementTypeInstance("ET0"), metaFlow), c);
		
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
