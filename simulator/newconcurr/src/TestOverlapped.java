import java.io.*;
import java.util.ArrayList;
import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.PeriodicActivityQueueListener;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.*;

/**
 * 
 */

/**
 * @author Iván Castilla Rodríguez
 *
 */
class OverlappedSimulation extends Simulation {
	final static int NELEM = 3;
	final static int NRESOURCESA = 3;
	final static int NRESOURCESB = 1;
	final static int NEEDED = 1;
	int days;
    
	OverlappedSimulation(double startTs, double endTs, int days, Output out) {
		super("Sistema de análisis", startTs, endTs, out);
		this.days = days;
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
        WorkGroup wg1 = actSangre.getNewWorkGroup(0, new Fixed(480.0));
        wg1.add(crSangre, NEEDED);
//        wg1.add(crOrina, 1);
        WorkGroup wg2 = actOrina.getNewWorkGroup(0, new Fixed(90.0));
        wg2.add(crOrina, NEEDED);
//        WorkGroup wg3 = actDummy.getNewWorkGroup(0, new Normal(10.0, 2.0));
//        wg3.add(crDummy, 1);

        for (int i = 0; i < NRESOURCESA; i++) {
        	Resource res = new Resource(i, this, "Máquina Análisis Sangre " + i);
        	res.addTimeTableEntry(new PeriodicCycle(480, new Fixed(1440.0), 0), 480, getResourceType(0));
        }
        for (int i = 0; i < NRESOURCESB; i++) {
            Resource res = getResourceList().get(i);
        	res.addTimeTableEntry(new PeriodicCycle(14 * 60.0, new Fixed(1440.0), 0), 120.0, getResourceType(1));
        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDummy);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new PeriodicCycle(480, new Fixed(1440.0), 0), 480, al2);

//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crSangre);
//        for (int i = 0; i < NRESOURCES; i++) {
//			Resource poli1 = new Resource(i, this, "Máquina Polivalente 1");
//			poli1.addTimeTableEntry(new PeriodicCycle(480, new Fixed(1440.0), 0), 480, al2);
//        }
        
    	SingleMetaFlow sf0 = new SingleMetaFlow(0, new Fixed(1), getActivity(0));
    	SingleMetaFlow sf1 = new SingleMetaFlow(1, new Fixed(1), getActivity(1));
		Cycle subC1 = new PeriodicCycle(0.0, new Fixed(60.0), 480);
		Cycle c1 = new PeriodicCycle(480, new Fixed(1440.0), days, subC1);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(0, this, "ET0"), sf0), c1.iterator(startTs, endTs));
		
		Cycle c = new PeriodicCycle(10 * 24 * 60.0, new Fixed(1440.0), 10);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), getElementType(0), sf0), c.iterator(startTs, endTs));

		Cycle c2 = new PeriodicCycle(14 * 60.0, new Fixed(1440.0), 0);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NRESOURCESB), new ElementType(1, this, "ET1"), sf1), c2.iterator(startTs, endTs));
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
		// TODO Auto-generated method stub
		
	}

}

class ExpOverlapped extends Experiment {
    static final int NDIAS = 10;
    static final int NPRUEBAS = 1;
    OverlappedListener oListener = new OverlappedListener(NPRUEBAS);

	public ExpOverlapped(String description) {
		super(description, NPRUEBAS);
	}

	public Simulation getSimulation(int ind) {
		OverlappedSimulation sim = new OverlappedSimulation(0.0, NDIAS * 24 * 60.0, NDIAS, new Output(false/*, new OutputStreamWriter(System.out), new OutputStreamWriter(System.out)*/));
//		sim.addListener(oListener);
		sim.addListener(new StdInfoListener());
//		sim.addListener(new PeriodicActivityQueueListener(1440.0));
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
