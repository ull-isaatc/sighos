/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import es.ull.cyc.simulation.*;
import es.ull.cyc.simulation.results.*;
import es.ull.cyc.util.Cycle;
import es.ull.cyc.util.Output;
import es.ull.cyc.random.*;

class SimActProcessor implements ResultProcessor {

	public void processStatistics(SimulationResults[] results) {
		try {
			FileWriter file = new FileWriter("C:\\ValSimact.txt");
			for (int i = 0; i < results.length; i++) {
				int queues[] = results[i].computeQueueSizes();
				for (int j = 0; j < queues.length; j++) {
					file.write(queues[j] + " ");
					file.flush();
				}
				file.write("\r\n");
				file.flush();
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
class SimSimAct extends Simulation {
	static final int NPACDAY = 5;
	static final double START = 0.0;
	static final double DURAC_REC = 899.0;
	static final int START_REC = 0;
	static final int NRES = 1;
	int ndays;

	SimSimAct(String description, int ndays, Output out) {
		super(description, START, 24 * 60.0 * ndays, out);
		this.ndays = ndays;
	}
	
	protected void createModel() {
		Activity act0 = new Activity(0, this, "X-Ray");
		Activity act1 = new Activity(1, this, "Blood sample");
		Activity act2 = new Activity(2, this, "MRI scan");
		ResourceType rt3 = new ResourceType(3, this, "Room");
		WorkGroup wg0 = act0.getNewWorkGroup(0, new Fixed(100.0));
		wg0.add(rt3, 1);
		WorkGroup wg1 = act1.getNewWorkGroup(0, new Fixed(100.0));
		wg1.add(rt3, 1);
		WorkGroup wg2 = act2.getNewWorkGroup(0, new Fixed(100.0));
		wg2.add(rt3, 1);
	}

	protected ArrayList<Generator> createGenerators() {
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(0, new Fixed(1));
        new SingleMetaFlow(2, sim, new Fixed(1), getActivity(1));
        new SingleMetaFlow(1, sim, new Fixed(1), getActivity(0));
        new SingleMetaFlow(3, sim, new Fixed(1), getActivity(2));
        Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
        ArrayList<Generator> genList = new ArrayList<Generator>();
        genList.add(new ElementGenerator(this, new Fixed(NPACDAY), c.iterator(startTs, endTs), sim));
        return genList;
	}

	@Override
	protected ArrayList<Resource> createResources() {
		ArrayList<Resource> list = new ArrayList<Resource>();
		Cycle c = new Cycle(START_REC, new Fixed(1440), 0);
		for (int i = 0; i < NRES; i++) {
			Resource room = new Resource(i, this, "Room" + i);
			room.addTimeTableEntry(c, DURAC_REC, getResourceType(3));
			list.add(room);
		}
		return list;
	}
	
}

class SimPoolActResultProcessor implements ResultProcessor {
	protected double period;

	/**
	 * @param period
	 */
	public SimPoolActResultProcessor(double period) {
		this.period = period;
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.results.ResultProcessor#processStatistics()
	 */
	public void processStatistics(SimulationResults []results) {
		for (int i = 0; i < results.length; i++) {
			processSimulationTimeStatistics(results[i]);
			processElementStatistics(results[i]);
			processActivityStatistics(results[i], period);
		}
	}

	public static void processSimulationTimeStatistics(SimulationResults res) {
		System.out.println("INICIO SIMULACI�N:\t" + res.getSimStart());
		System.out.println("FIN SIMULACI�N:\t" + res.getSimEnd());
		System.out.println("FIN REAL SIMULACI�N:\t" + res.getSimRealEnd());
		System.out.println("TIEMPO EJECUCI�N SIMULACI�N:\t" + (res.getEndT() - res.getIniT()));		
	}

	public static void processElementStatistics(SimulationResults res) {
		System.out.println("Element Statistics");
		System.out.println("Created: " + res.createdElements());
		for (int i = 0; i < res.getElementStatistics().size(); i++) {
			ElementStatistics es = (ElementStatistics) res.getElementStatistics().get(i);
			if (es.getType() == ElementStatistics.STAACT) {
				System.out.println("[" + es.getElemId() + "]\t" + es.getTs() + "\tSTARTS ACTIVITY\t" + es.getValue());
			}
			else if (es.getType() == ElementStatistics.ENDACT) {
				System.out.println("[" + es.getElemId() + "]\t" + es.getTs() + "\tENDS ACTIVITY\t" + es.getValue());
			}
		}
	}

	public static void processActivityStatistics(SimulationResults res, double period) {
		System.out.println("Activity Queues(PERIOD: " + period + ")");
		int[][]queues= res.computeQueueSizes(period);
		for (int i = 0; i < queues.length; i++) {
			System.out.print("A" + res.getActIds()[i][0] + ":");
			for (int j = 0; j < queues[i].length; j++) {
				System.out.print("\t" + queues[i][j]);
			}
			System.out.println("");
		}
	}

}

/**
 * Can be used for checking the influence of a RandomPrioritizedTableIterator against the traditional
 * PrioritizedTableIterator. Requires changing code in Activity and ActivityManager.
 * @author Iv�n Castilla Rodr�guez
 *
 */
class SimPoolAct extends Simulation {
	static final int NPACDAY = 5;
	static final double START = 0.0;
	static final int DURAC_REC = 799;
	int ndays;

	SimPoolAct(String description, int ndays, Output out) {
		super(description, START, 24 * 60.0 * ndays, out);
		this.ndays = ndays;
	}
	
	protected void createModel() {
		Activity act0 = new Activity(0, this, "X-Ray");
		Activity act1 = new Activity(1, this, "Blood sample");
		Activity act2 = new Activity(2, this, "MRI scan");
		ResourceType rt0 = new ResourceType(0, this, "NurseT0");
		act0.getNewWorkGroup(0, new Fixed(100.0)).add(rt0, 1);
		act1.getNewWorkGroup(0, new Fixed(100.0)).add(rt0, 1);
		act2.getNewWorkGroup(0, new Fixed(100.0)).add(rt0, 1);
	}

	protected ArrayList<Generator> createGenerators() {        
        Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
		ArrayList<Generator> genList = new ArrayList<Generator>();
		genList.add(new ElementGenerator(this, new Fixed(NPACDAY), c.iterator(startTs, endTs), new SingleMetaFlow(3, new Fixed(1), getActivity(2))));
		genList.add(new ElementGenerator(this, new Fixed(NPACDAY), c.iterator(startTs, endTs), new SingleMetaFlow(1, new Fixed(1), getActivity(0))));
		genList.add(new ElementGenerator(this, new Fixed(NPACDAY), c.iterator(startTs, endTs), new SingleMetaFlow(2, new Fixed(1), getActivity(1))));
		return genList;
	}

	@Override
	protected ArrayList<Resource> createResources() {
		Cycle c = new Cycle(100, new Fixed(1440), 0);
		Resource nurse1 = new Resource(0, this, "Nurse 1");
		nurse1.addTimeTableEntry(c, DURAC_REC, getResourceType(0));
		ArrayList<Resource> list = new ArrayList<Resource>();
		list.add(nurse1);
		return list;
	}
}

class SimContinue extends Simulation {
	static final int NPACDAY = 25;
	static final int DURAC_REC = 480;
    int ndays;
    
    SimContinue(String description, double startTs, int ndays, Output out) {
		super(description, startTs, ndays * 24 * 60.0, out);
		this.ndays = ndays;
    }
    
    SimContinue(String description, int lastday, Output out, SimulationResults res) {
		super(description, lastday * 24 * 60.0, out, res);
		this.ndays = lastday;
    }
    
	protected void createModel() {
        Activity actFOA = new Activity(0, this, "First Outpatient Appointment");
        Activity actBS = new Activity(1, this, "Blood sample");
        Activity actXR = new Activity(2, this, "X-Ray");
        Activity actSOA = new Activity(3, this, "Subsequent Outpatient Appointment");
 
        ResourceType crBlood = new ResourceType(0, this, "Sample Machine");
        ResourceType crXRay = new ResourceType(1, this, "X-Ray machine");
        ResourceType crNurse = new ResourceType(2, this, "Nurse");
        ResourceType crDoctor = new ResourceType(3, this, "Doctor");

        WorkGroup wg1 = actFOA.getNewWorkGroup(0, new Normal(15.0, 2.0));
        wg1.add(crDoctor, 1);
        WorkGroup wg2 = actSOA.getNewWorkGroup(0, new Normal(18.0, 2.0));
        wg2.add(crDoctor, 1);
        WorkGroup wg3 = actBS.getNewWorkGroup(0, new Normal(10.0, 2.0));
        wg3.add(crBlood, 1);
        wg3.add(crNurse, 1);
        WorkGroup wg4 = actXR.getNewWorkGroup(0, new Normal(18.0, 5.0));
        wg4.add(crXRay, 1);
        wg4.add(crNurse, 1);       
	}

	protected ArrayList<Generator> createGenerators() {
		int cont = 0;
        Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
        SequenceMetaFlow sec = new SequenceMetaFlow(cont++, new Fixed(1));
        new SingleMetaFlow(cont++, sec, new Fixed(1), getActivity(0));
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(cont++, sec, new Fixed(1));
        new SingleMetaFlow(cont++, sim, new Fixed(1), getActivity(1));
        new SingleMetaFlow(cont++, sim, new Fixed(1), getActivity(2));
        new SingleMetaFlow(cont++, sec, new Uniform(0, 3), getActivity(3));        
		ArrayList<Generator> genList = new ArrayList<Generator>();
		genList.add(new ElementGenerator(this, new Fixed(NPACDAY), c.iterator(startTs, endTs), sec));
		return genList;
	}

	@Override
	protected ArrayList<Resource> createResources() {
        Cycle c = new Cycle(480, new Fixed(1440.0), 0);
        ArrayList<Resource> list = new ArrayList<Resource>();
		Resource r;
		r = new Resource(0, this, "Nurse #1");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		list.add(r);
		r = new Resource(1, this, "Nurse #2");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		list.add(r);
		r = new Resource(2, this, "Doctor #1");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		list.add(r);		
		r = new Resource(3, this, "Doctor #2");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		list.add(r);
		r = new Resource(4, this, "Blood machine #1");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(0));
		list.add(r);
		r = new Resource(5, this, "X-Ray machine #1");
		r.addTimeTableEntry(c, DURAC_REC, getResourceType(1));
		list.add(r);
		return list;
	}
	
}

class ExpSimAct extends Experiment {
	final static int NEXP = 1;
	final static int NDAYS = 10;
	double prevStart = 0.0, prevEnd = 0.0;
	
	ExpSimAct() {
//		super("Testing activity pool", NEXP, new SimActProcessor(), new Output(Output.NODEBUG));
		super("Testing simultaneously requested activities", NEXP, new SimActProcessor(), new Output(Output.DebugLevel.NODEBUG));
	}
	
	ExpSimAct(double prevStart, double prevEnd) {
		super("Testing continuation of simulations", NEXP, new StdResultProcessor(1440.0)/*new RecoverableResultProcessor("c:\\")*/, new Output(Output.DebugLevel.NODEBUG));
		this.prevStart = prevStart;
		this.prevEnd = prevEnd;
	}
	
	public Simulation getSimulation(int ind) {
		if (Double.compare(prevEnd, 0.0) != 0)
			return new SimContinue(description + ind + "", NDAYS + (int)(prevEnd / (60 * 24)), out, new PreviousSimulationResults(prevStart, prevEnd, ind, "c:\\"));
		return new SimContinue(description + ind + "", 0.0, NDAYS, out);
//		return new SimPoolAct(description + ind + "", NDAYS, out);
//		return new SimSimAct(description + ind + "", NDAYS, out);
	}
	
}
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class BarcelonaVerification {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpSimAct(0.0, 0.0).start();

	}

}
