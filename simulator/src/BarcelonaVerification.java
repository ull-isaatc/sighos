/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

class SimActListener extends StatisticListener {
	int nTests = 0;
	int maxTests;
	FileWriter file;
	
	SimActListener(double period, int maxTests) {
		super(period);
		try {
			file = new FileWriter("C:\\VerSimact.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}						
		this.maxTests = maxTests;
	}
	
	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		try {
			for (Map.Entry<Integer,int[]> values : getActQueues().entrySet())
				for (int j = 0; j < values.getValue().length; j++) {
					file.write(values.getValue()[j] + " ");
					file.flush();
				}
			file.write("\r\n");
			file.flush();
			if (++nTests == maxTests)
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

	SimSimAct(String description, int ndays) {
		super(description, START, 24 * 60.0 * ndays);
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

		Cycle c = new PeriodicCycle(START_REC, new Fixed(1440), 0);
		for (int i = 0; i < NRES; i++) {
			Resource room = new Resource(i, this, "Room" + i);
			room.addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		}
		
		new ElementType(0, this, "PATIENT");
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(0, new Fixed(1));
        new SingleMetaFlow(2, sim, new Fixed(1), getActivity(1));
        new SingleMetaFlow(1, sim, new Fixed(1), getActivity(0));
        new SingleMetaFlow(3, sim, new Fixed(1), getActivity(2));
        Cycle c1 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        ElementCreator ec = new ElementCreator(new Fixed(NPACDAY), getElementType(0), sim);
        new TimeDrivenGenerator(this, ec, c1.iterator(startTs, endTs));
	}	
}

/**
 * Can be used for checking the influence of a RandomPrioritizedTableIterator against the traditional
 * PrioritizedTableIterator. Requires changing code in Activity and ActivityManager.
 * @author Iván Castilla Rodríguez
 *
 */
class SimPoolAct extends Simulation {
	static final int NPACDAY = 5;
	static final double START = 0.0;
	static final int DURAC_REC = 799;
	int ndays;

	SimPoolAct(String description, int ndays) {
		super(description, START, 24 * 60.0 * ndays);
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

		Cycle c = new PeriodicCycle(100, new Fixed(1440), 0);
		new Resource(0, this, "Nurse 1").addTimeTableEntry(c, DURAC_REC, getResourceType(0));

		Cycle c1 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACDAY), new ElementType(2, this, "PAT2"), new SingleMetaFlow(3, new Fixed(1), getActivity(2))), c1.iterator(startTs, endTs));
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACDAY), new ElementType(0, this, "PAT0"), new SingleMetaFlow(1, new Fixed(1), getActivity(0))), c1.iterator(startTs, endTs));
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACDAY), new ElementType(1, this, "PAT1"), new SingleMetaFlow(2, new Fixed(1), getActivity(1))), c1.iterator(startTs, endTs));
	}
}

class SimContinue extends Simulation {
	static final int NPACDAY = 25;
	static final int DURAC_REC = 480;
    int ndays;
    
    SimContinue(String description, double startTs, int ndays, Output out) {
		super(description, startTs, startTs + ndays * 24 * 60.0, out);
		this.ndays = ndays;
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

        Cycle c = new PeriodicCycle(480, new Fixed(1440.0), 0);
        new Resource(0, this, "Nurse #1").addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		new Resource(1, this, "Nurse #2").addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		new Resource(2, this, "Doctor #1").addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		new Resource(3, this, "Doctor #2").addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		new Resource(4, this, "Blood machine #1").addTimeTableEntry(c, DURAC_REC, getResourceType(0));
		new Resource(5, this, "X-Ray machine #1").addTimeTableEntry(c, DURAC_REC, getResourceType(1));

		int cont = 0;
        Cycle c1 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        SequenceMetaFlow sec = new SequenceMetaFlow(cont++, new Fixed(1));
        new SingleMetaFlow(cont++, sec, new Fixed(1), getActivity(0));
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(cont++, sec, new Fixed(1));
        new SingleMetaFlow(cont++, sim, new Fixed(1), getActivity(1));
        new SingleMetaFlow(cont++, sim, new Fixed(1), getActivity(2));
        new SingleMetaFlow(cont++, sec, new Uniform(0, 3), getActivity(3));        
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NPACDAY), new ElementType(0, this, "PATIENTS"), sec), c1.iterator(startTs, endTs));
	}
}

class ExpSimAct extends Experiment {
	final static int NEXP = 10;
	final static int NDAYS = 20;
	int expType = 0;
	SimActListener simListener;
	
	ExpSimAct(int expType) {
		super("Verifying", NEXP);
		this.expType = expType;
		if (expType == 0)
			simListener = new SimActListener(1440.0 * NDAYS, NEXP);
	}
	
	ExpSimAct(SimulationState previousState) {
		super("Testing continuation of simulations", NEXP);
		this.previousState = previousState;
		expType = 3;
	}
	
	public Simulation getSimulation(int ind) {
//		if (Double.compare(prevEnd, 0.0) != 0)
//			return new SimContinue(description + ind + "", NDAYS + (int)(prevEnd / (60 * 24)), new Output(Output.DebugLevel.NODEBUG));
		Simulation sim = null;
		if (expType == 0) {
			sim = new SimSimAct(description + ind + "", NDAYS);
			sim.addListener(simListener);
		}
		else if (expType == 1) {
			sim = new SimPoolAct(description + ind + "", NDAYS);			
			sim.addListener(new StatisticListener(1440.0));
			sim.addListener(new StdInfoListener());
		}
		else if (expType == 2) {
			sim = new SimContinue(description + ind + "", 0.0, NDAYS, new Output(Output.DebugLevel.NODEBUG));
			sim.addListener(new StdInfoListener());
		}
		else if (expType == 3) {
			sim = new SimContinue(description + ind + "", previousState.getEndTs(), NDAYS, new Output(Output.DebugLevel.NODEBUG));
			sim.addListener(new StdInfoListener());
		}
		return sim;
	}
	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BarcelonaVerification {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpSimAct(1).start();

	}

}
