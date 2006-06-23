import java.io.*;
import java.util.ArrayList;
import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.results.*;
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

    }
    
	@Override
	protected ArrayList<Resource> createResources() {
		ArrayList<Resource> list = new ArrayList<Resource>();
		ArrayList<ResourceType> al1 = new ArrayList<ResourceType>();
		al1.add(getResourceType(0));
//		al1.add(getResourceType(2));
        for (int i = 0; i < NRESOURCES; i++) {
        	Resource res = new Resource(i, this, "Máquina Análisis Sangre " + i);
        	res.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al1);
			list.add(res);
        }
//		ArrayList<ResourceType> al2 = new ArrayList<ResourceType>();
//		al2.add(crOrina);
//		al2.add(crDumb);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al2);
		return list;
	}

	protected ArrayList<Generator> createGenerators() {
//      SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(1, new Fixed(1));
//      new SingleMetaFlow(2, metaFlow, new Fixed(1), getActivity(2));
//      new SingleMetaFlow(3, metaFlow, new Fixed(1), getActivity(1));
		SingleMetaFlow metaFlow = new SingleMetaFlow(3, new Fixed(1), getActivity(1));     
		Cycle c = new Cycle(0.0, new Fixed(1440.0), days);
		CycleIterator it = c.iterator(startTs, endTs);
		ArrayList<Generator> genList = new ArrayList<Generator>();
		genList.add(new ElementGenerator(this, new Fixed(NELEM), it, metaFlow));
		return genList;
	}	
}

class OverlappedResultProcessor implements ResultProcessor {
	private FileWriter file;
	
	public OverlappedResultProcessor() {
		try {
			this.file = new FileWriter("c:\\out20_15(7R)_1.txt"/*"c:\\mNS" + NDIAS + ".txt"*/);
			this.file.flush();
		} catch(Exception ee) {
			ee.printStackTrace();
		}
	}
	
	public void processStatistics(SimulationResults[] results) {
		try {
			for (int i = 0; i < results.length; i++)
				file.write("" + (results[i].getEndT() - results[i].getIniT()) + "\r\n");
				file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

class ExpOverlapped extends Experiment {
    static final int NDIAS = 20;
    static final int NPRUEBAS = 100;

	public ExpOverlapped(String description, Output out) {
		super(description, NPRUEBAS, new OverlappedResultProcessor(), out);
//		super(description, NPRUEBAS, new StdResultProcessor(1440.0), out);
	}

	public Simulation getSimulation(int ind) {
		return new OverlappedSimulation(0.0, NDIAS * 24 * 60.0, NDIAS, out);
	}
}

public class TestOverlapped {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpOverlapped("Solapados", new Output(Output.DebugLevel.NODEBUG)).start();
	}

}
