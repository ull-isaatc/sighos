/**
 * 
 */
package es.ull.isaatc.simulation.proactive.test;

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StdInfoListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OverlappedSimulationProActive extends StandAloneLPSimulation {
	final static int NELEM = 3;
	final static int NRESOURCES = 2;
	final static int NEEDED = 1;
	int days;
    
	public OverlappedSimulationProActive() {		
	}
	
	public OverlappedSimulationProActive(IntWrapper id, IntWrapper days) {
		super(id.intValue(), "Sistema de análisis", 0.0, days.intValue() * 24 * 60.0);
		this.days = days.intValue();
		ListenerController cont = new ListenerController();
		setListenerController(cont);
		cont.addListener(new StdInfoListener());
    }
    
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
//    	Activity actDummy = new Activity(0, this, "Dummy");
        Activity actSangre = new Activity(0, this, "Análisis de sangre");
        Activity actOrina = new Activity(1, this, "Análisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "Máquina Análisis Sangre");
        ResourceType crOrina = new ResourceType(1, this, "Máquina Análisis Orina");
//        ResourceType crDummy = new ResourceType(2, this, "Dummy");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = new WorkGroup(1, this, ""); 
        wg1.add(crSangre, NEEDED);
        actSangre.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 20.0, 5.0), wg1);
//        wg1.add(crOrina, 1);
        WorkGroup wg2 = new WorkGroup(2, this, "");
        wg2.add(crOrina, 1);
        actOrina.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 20.0, 5.0), wg2);
//        WorkGroup wg3 = actDummy.getNewWorkGroup(0, new Normal(10.0, 2.0));
//        wg3.add(crDummy, 1);

//		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
//		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
//        for (int i = 0; i < NRESOURCES; i++) {
//        	Resource res = new Resource(i, this, "Máquina Análisis Sangre " + i);
//        	res.addTimeTableEntry(new PeriodicCycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al1);
//        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDummy);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new PeriodicCycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al2);

		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
		al2.add(crOrina);
		al2.add(crSangre);
        for (int i = 0; i < NRESOURCES; i++) {
			Resource poli1 = new Resource(i, this, "Máquina Polivalente 1");
			poli1.addTimeTableEntry(new PeriodicCycle(480, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al2);
        }
        
      SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1));
      new SingleMetaFlow(2, metaFlow, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
      new SingleMetaFlow(3, metaFlow, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
//		SingleMetaFlow metaFlow = new SingleMetaFlow(3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));     
		Cycle c = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), days);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), metaFlow), c);
	}	
}