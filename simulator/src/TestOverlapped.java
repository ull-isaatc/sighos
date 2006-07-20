import java.io.*;
import java.util.ArrayList;
import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.InfoListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.util.*;

/**
 * 
 */

/**
 * @author Iván Castilla Rodríguez
 *
 */
class OverlappedSimulation extends Simulation {
	final static int NELEM = 15;
	final static int NRESOURCES = 7;
	final static int NEEDED = 1;
	int days;
    
	OverlappedSimulation(double startTs, double endTs, int days, Output out) {
		super("Sistema de análisis", startTs, endTs, out);
		this.days = days;
    }
    
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
    	Activity actDummy = new Activity(0, this, "Dummy");
        Activity actSangre = new Activity(1, this, "Análisis de sangre");
//        Activity actOrina = new Activity(2, this, "Análisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "Máquina Análisis Sangre");
//        ResourceType crOrina = new ResourceType(1, this, "Máquina Análisis Orina");
        ResourceType crDummy = new ResourceType(2, this, "Dummy");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = actSangre.getNewWorkGroup(0, new Normal(20.0, 5.0));
        wg1.add(crSangre, NEEDED);
//      wg1.add(crOrina, 1);
//        WorkGroup wg2 = actOrina.getNewWorkGroup(0, new Normal(20.0, 5.0));
//      wg2.add(crOrina, 1);
        WorkGroup wg3 = actDummy.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg3.add(crDummy, 1);

		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
        for (int i = 0; i < NRESOURCES; i++) {
        	Resource res = new Resource(i, this, "Máquina Análisis Sangre " + i);
        	res.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al1);
        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDumb);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al2);

//      SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(1, new Fixed(1));
//      new SingleMetaFlow(2, metaFlow, new Fixed(1), getActivity(2));
//      new SingleMetaFlow(3, metaFlow, new Fixed(1), getActivity(1));
		SingleMetaFlow metaFlow = new SingleMetaFlow(3, new Fixed(1), getActivity(1));     
		Cycle c = new Cycle(0.0, new Fixed(1440.0), days);
		CycleIterator it = c.iterator(startTs, endTs);
		new ElementGenerator(this, new Fixed(NELEM), it, new ElementType(0, this, "ET0"), metaFlow);
	}	
}

class OverlappedListener implements InfoListener {
	long execTime[];
	int nTest = 0;
	
	OverlappedListener(int nTests) {
		execTime = new long[nTests];
	}

	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo)
			execTime[nTest] = -((SimulationStartInfo)info).getIniT();
		else if (info instanceof SimulationEndInfo) {
			execTime[nTest++] += ((SimulationEndInfo)info).getEndT();
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
	}	
}

class ExpOverlapped extends Experiment {
    static final int NDIAS = 20;
    static final int NPRUEBAS = 100;
    OverlappedListener oListener = new OverlappedListener(NPRUEBAS);

	public ExpOverlapped(String description) {
		super(description, NPRUEBAS);
	}

	public Simulation getSimulation(int ind) {
		OverlappedSimulation sim = new OverlappedSimulation(0.0, NDIAS * 24 * 60.0, NDIAS, new Output(Output.DebugLevel.NODEBUG));
		sim.addListener(oListener);
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
