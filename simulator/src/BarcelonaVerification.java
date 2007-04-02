/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import simkit.random.RandomVariateFactory;

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

class SimSimAct extends StandAloneLPSimulation {
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
		WorkGroup wg0 = new WorkGroup(0, this, "");
		wg0.add(rt3, 1);
		
		act0.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0, wg0));
		act1.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0, wg0));
		act2.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0, wg0));

		Cycle c = new PeriodicCycle(START_REC, RandomVariateFactory.getInstance("ConstantVariate", 1440), 0);
		for (int i = 0; i < NRES; i++) {
			Resource room = new Resource(i, this, "Room" + i);
			room.addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		}
		
		new ElementType(0, this, "PATIENT");
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1));
        new SingleMetaFlow(2, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
        new SingleMetaFlow(1, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
        new SingleMetaFlow(3, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2));
        Cycle c1 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        ElementCreator ec = new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", NPACDAY), getElementType(0), sim);
        new TimeDrivenGenerator(this, ec, c1);
	}	
}

/**
 * Can be used for checking the influence of a RandomPrioritizedTableIterator against the traditional
 * PrioritizedTableIterator. Requires changing code in Activity and ActivityManager.
 * @author Iv�n Castilla Rodr�guez
 *
 */
class SimPoolAct extends StandAloneLPSimulation {
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
		WorkGroup wg0 = new WorkGroup(0, this, "");
		wg0.add(rt0, 1);
		act0.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0), wg0);
		act1.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0), wg0);
		act2.addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 100.0), wg0);

		Cycle c = new PeriodicCycle(100, RandomVariateFactory.getInstance("ConstantVariate", 1440), 0);
		new Resource(0, this, "Nurse 1").addTimeTableEntry(c, DURAC_REC, getResourceType(0));

		Cycle c1 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
		new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", NPACDAY), new ElementType(2, this, "PAT2"), new SingleMetaFlow(3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", NPACDAY), new ElementType(0, this, "PAT0"), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c1);
		new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", NPACDAY), new ElementType(1, this, "PAT1"), new SingleMetaFlow(2, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))), c1);
	}
}

class SimContinue extends StandAloneLPSimulation {
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

        WorkGroup wg0 = new WorkGroup(0, this, ""); 
        wg0.add(crDoctor, 1);
        actFOA.addWorkGroup(RandomVariateFactory.getInstance("NormalVariate", 15.0, 2.0), wg0);
        actSOA.addWorkGroup(RandomVariateFactory.getInstance("NormalVariate", 18.0, 2.0), wg0);
        WorkGroup wg1 = new WorkGroup(1, this, "");
        wg1.add(crBlood, 1);
        wg1.add(crNurse, 1);
        actBS.addWorkGroup(RandomVariateFactory.getInstance("NormalVariate", 10.0, 2.0), wg1);
        WorkGroup wg2 = new WorkGroup(2, this, "");
        wg2.add(crXRay, 1);
        wg2.add(crNurse, 1);       
        actXR.addWorkGroup(RandomVariateFactory.getInstance("NormalVariate", 18.0, 5.0), wg2);

        Cycle c = new PeriodicCycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new Resource(0, this, "Nurse #1").addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		new Resource(1, this, "Nurse #2").addTimeTableEntry(c, DURAC_REC, getResourceType(2));
		new Resource(2, this, "Doctor #1").addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		new Resource(3, this, "Doctor #2").addTimeTableEntry(c, DURAC_REC, getResourceType(3));
		new Resource(4, this, "Blood machine #1").addTimeTableEntry(c, DURAC_REC, getResourceType(0));
		new Resource(5, this, "X-Ray machine #1").addTimeTableEntry(c, DURAC_REC, getResourceType(1));

		int cont = 0;
        Cycle c1 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        SequenceMetaFlow sec = new SequenceMetaFlow(cont++, RandomVariateFactory.getInstance("ConstantVariate", 1));
        new SingleMetaFlow(cont++, sec, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(cont++, sec, RandomVariateFactory.getInstance("ConstantVariate", 1));
        new SingleMetaFlow(cont++, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
        new SingleMetaFlow(cont++, sim, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2));
        new SingleMetaFlow(cont++, sec, RandomVariateFactory.getInstance("UniformVariate", 0, 3), getActivity(3));        
		new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", NPACDAY), new ElementType(0, this, "PATIENTS"), sec), c1);
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
			sim = new SimContinue(description + ind + "", 0.0, NDAYS, new Output());
			sim.addListener(new StdInfoListener());
		}
		else if (expType == 3) {
			sim = new SimContinue(description + ind + "", previousState.getEndTs(), NDAYS, new Output());
			sim.addListener(new StdInfoListener());
		}
		return sim;
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
		new ExpSimAct(1).start();

	}

}