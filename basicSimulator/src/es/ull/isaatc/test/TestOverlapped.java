package es.ull.isaatc.test;

import java.io.FileWriter;
import java.util.ArrayList;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SimultaneousMetaFlow;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;

/**
 * 
 */

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
class OverlappedSimulation extends StandAloneLPSimulation {
	final static int NELEM = 3;
	final static int NRESOURCES = 2;
	final static int NEEDED = 1;
	int days;
    
	OverlappedSimulation(int id, int days) {
		super(id, "Sistema de an�lisis", SimulationTimeUnit.MINUTE, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTime(SimulationTimeUnit.DAY, days));
		this.days = days;
    }
    
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
//    	Activity actDummy = new Activity(0, this, "Dummy");
        Activity actSangre = new Activity(0, this, "An�lisis de sangre");
        Activity actOrina = new Activity(1, this, "An�lisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "M�quina An�lisis Sangre");
        ResourceType crOrina = new ResourceType(1, this, "M�quina An�lisis Orina");
//        ResourceType crDummy = new ResourceType(2, this, "Dummy");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = new WorkGroup(1, this, ""); 
        wg1.add(crSangre, NEEDED);
        actSangre.addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 20.0, 5.0), wg1);
//        wg1.add(crOrina, 1);
        WorkGroup wg2 = new WorkGroup(2, this, "");
        wg2.add(crOrina, 1);
        actOrina.addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 20.0, 5.0), wg2);
//        WorkGroup wg3 = actDummy.getNewWorkGroup(0, new Normal(10.0, 2.0));
//        wg3.add(crDummy, 1);

//		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
//		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
//        for (int i = 0; i < NRESOURCES; i++) {
//        	Resource res = new Resource(i, this, "M�quina An�lisis Sangre " + i);
//        	res.addTimeTableEntry(new PeriodicCycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al1);
//        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDummy);
//		Resource orina1 = new Resource(1, this, "M�quina An�lisis Orina 1");
//		orina1.addTimeTableEntry(new PeriodicCycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, al2);

		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
		al2.add(crOrina);
		al2.add(crSangre);
        for (int i = 0; i < NRESOURCES; i++) {
			Resource poli1 = new Resource(i, this, "M�quina Polivalente 1");
			poli1.addTimeTableEntry(new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0), new SimulationTime(SimulationTimeUnit.MINUTE, 480), al2);
        }
        
      SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1));
      new SingleMetaFlow(2, metaFlow, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
      new SingleMetaFlow(3, metaFlow, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
//		SingleMetaFlow metaFlow = new SingleMetaFlow(3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));     
		SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), days);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(0, this, "ET0"), metaFlow), c);
	}	
}

class OverlappedListener implements SimulationListener {
	long execTime[];
	int nTest = 0;
	
	OverlappedListener(int nTests) {
		execTime = new long[nTests];
	}

	public void infoEmited(SimulationObjectInfo info) {
	}

	public void infoEmited(SimulationStartInfo info) {
		execTime[nTest] = -info.getIniT();
	}

	public void infoEmited(SimulationEndInfo info) {
		execTime[nTest++] += info.getEndT();
		if (nTest == execTime.length) {
			try {
				FileWriter file = new FileWriter("c:\\out20_15(7R)_1.txt"/*"c:\\mNS" + NDIAS + ".txt"*/);
				for (long val : execTime) {
					file.write("" + val + "\r\n");
					file.flush();
				}
				file.close();
			} catch(Exception ee) {
				ee.printStackTrace();
			}				
		}
	}

	public void infoEmited(TimeChangeInfo info) {
		
	}

}

class ExpOverlapped extends PooledExperiment {
    static final int NDIAS = 1;
    static final int NPRUEBAS = 2;
    OverlappedListener oListener = new OverlappedListener(NPRUEBAS);

	public ExpOverlapped(String description) {
		super(description, NPRUEBAS);
	}

	public Simulation getSimulation(int ind) {
		OverlappedSimulation sim = new OverlappedSimulation(ind, NDIAS);
		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
//		cont.setOutput(new Output(true/*, new OutputStreamWriter(System.out), new OutputStreamWriter(System.out)*/));
//		cont.addListener(oListener);
		cont.addListener(new StdInfoListener());
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
