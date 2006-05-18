import java.io.*;
import java.util.ArrayList;
import es.ull.cyc.random.*;
import es.ull.cyc.simulation.*;
import es.ull.cyc.simulation.results.*;
import es.ull.cyc.util.*;

/**
 * 
 */

/**
 * @author Iván Castilla Rodríguez
 *
 */
class OverlappedSimulation extends Simulation {
	final int NELEM = 5;
	int days;
    
	OverlappedSimulation(double startTs, double endTs, int days, Output out) {
		super("Sistema de análisis", startTs, endTs, out);
		this.days = days;
    }
    
	protected void createGenerators() {
//		createMetaFlow0();
	}
	
    protected void createModel() {
        // PASO 1: Inicializo las Activityes de las que se compone
    	Activity actDumb = new Activity(0, this, "Dumb");
        Activity actSangre = new Activity(1, this, "Análisis de sangre");
//        Activity actOrina = new Activity(2, this, "Análisis de orina");
 
        // PASO 2: Inicializo las clases de recursos
        ResourceType crSangre = new ResourceType(0, this, "Máquina Análisis Sangre");
//        ResourceType crOrina = new ResourceType(1, this, "Máquina Análisis Orina");
        ResourceType crDumb = new ResourceType(2, this, "Dumb");

        // PASO 3: Creo las tablas de clases de recursos
        WorkGroup wg1 = actSangre.getNewWorkGroup(0, new Normal(20.0, 5.0));
        wg1.add(crSangre, 1);
//        WorkGroup wg2 = actOrina.getNewWorkGroup(0, new Normal(20.0, 5.0));
//        wg2.add(crOrina, 1);
        WorkGroup wg3 = actDumb.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg3.add(crDumb, 1);
       
		ArrayList al1 = new ArrayList();
		al1.add(crSangre);
		al1.add(crDumb);
		Resource sangre1 = new Resource(0, this, "Máquina Análisis Sangre 1");
		sangre1.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al1);
//		ArrayList al2 = new ArrayList();
//		al2.add(crOrina);
//		al2.add(crDumb);
//		Resource orina1 = new Resource(1, this, "Máquina Análisis Orina 1");
//		orina1.addTimeTableEntry(new Cycle(480, new Fixed(1440.0), 0), 480, al2);
    }
    
    protected void createMetaFlow0() {
        SimultaneousMetaFlow metaFlow = new SimultaneousMetaFlow(1, new Fixed(1));
        new SingleMetaFlow(2, metaFlow, new Fixed(1), getActivity(2));
        new SingleMetaFlow(3, metaFlow, new Fixed(1), getActivity(1));
        
        Cycle c = new Cycle(0.0, new Fixed(1440.0), days);
        Generation gen = new Generation(new Fixed(NELEM));
        gen.add(metaFlow, 1.0);
        gen.createGenerators(this, c);
//        addElementType(0, this.getDefaultLogicalProcess(), new Fixed(NELEM), 0.0, days, new Fixed(1440.0), metaFlow);
    }

}

class OverlappedResultProcessor implements ResultProcessor {
	private FileWriter file;
	
	public OverlappedResultProcessor() {
		try {
			this.file = new FileWriter("c:\\out.txt"/*"c:\\mNS" + NDIAS + ".txt"*/);
			this.file.write("Solapados\r\n");
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
    static final int NDIAS = 15;
	
	public ExpOverlapped(String description, int experiments, Output out) {
		super(description, experiments, new StdResultProcessor(1440.0), out);
	}

	public Simulation getSimulation(int ind) {
		return new OverlappedSimulation(0.0, NDIAS * 24 * 60.0, NDIAS, out);
	}
}

public class TestOverlapped {
    static final int NPRUEBAS = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpOverlapped("Solapados", NPRUEBAS, new Output(Output.DebugLevel.DEBUG)).start();		
	}

}
