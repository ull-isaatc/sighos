/**
 * 
 */
package es.ull.isaatc.salford;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunctionFactory;
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
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.TransitionActivity;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

class SalfordIPSimulation extends Simulation {
	static final int NDAYS = 100;
	static final int NWARDS = 2;
	static final int NBEDS = 2;
	static final int NPAC = 10;
	static final String FILE_NAME = "C:\\Users\\Iván\\Documents\\Salford\\testTrans1.txt";
//	static final String FILE_NAME = "C:\\Users\\Iván\\Documents\\Salford\\test1.txt";
	
	public SalfordIPSimulation(int id) {
		super(id, "Salford Inpatient Model", SimulationTimeUnit.DAY, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, NDAYS));
	}

	protected void loadedModel() {
		BufferedReader br;
		int actCount = 0;
		ArrayList<String> specialities = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(FILE_NAME));
			String line = br.readLine();
		    String[] wardNames = line.split("\\t");
		    // Resource Types and WorkGroups
		    WorkGroup[] wgs = new WorkGroup[wardNames.length];
		    for (int i = 0; i < wardNames.length; i++) {
		    	ResourceType rt = new ResourceType(i, this, "W_" + wardNames[i]);
		    	wgs[i] = new WorkGroup(rt, 1);
		    }
	    	TransitionActivity[] acts = new TransitionActivity[3];
		    while ((line = br.readLine()) != null) {
		    	specialities.add(line);
		    	// Activities		    	
		    	acts[0] = new TransitionActivity(actCount++, this, "Emerg " + line);
		    	acts[1] = new TransitionActivity(actCount++, this, "Elect " + line);
		    	acts[2] = new TransitionActivity(actCount++, this, "Other " + line);
		    	
		    	for (int i = 0; i < 3; i++) {
			    	for (int j = 0; j < wardNames.length; j++)
			    		acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[j]);
		    		addTransitions(br, acts[i], wardNames.length);
		    	}
		    	
		    }
		    br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Create element types
		for (int i = 0; i < specialities.size(); i++) {
			new ElementType(i, this, "Emerg " + specialities.get(i));
			new ElementType(i + specialities.size(), this, "Elect " + specialities.get(i));
			new ElementType(i + specialities.size() * 2, this, "Other " + specialities.get(i));
		}
	}

	private void addTransitionsFrom(String[] percentages, TransitionActivity act, TimeDrivenActivity.ActivityWorkGroup fromTrans) {
		// I assume there are no transitions to gate
		for (int k = 1; k < percentages.length - 1; k++) {
			double p = new Double(percentages[k]);
			if (p > 0.0)
				act.addTransition(fromTrans, act.getWorkGroup(k - 1), p);
		}
		// Last transition
		double p = new Double(percentages[percentages.length - 1]);
		if (p > 0.0)
			act.addFinalTransition(fromTrans, p);		
	}
	
	private void addInitialTransitions(String[] percentages, TransitionActivity act) {
		// I assume there are no transitions to gate
		for (int k = 1; k < percentages.length - 1; k++) {
			double p = new Double(percentages[k]);
			if (p > 0.0)
				act.addInitialTransition(act.getWorkGroup(k - 1), p);
		}
		// Last transition
		double p = new Double(percentages[percentages.length - 1]);
		if (p > 0.0)
			act.addInitialFinalTransition(p);		
	}
	
	private void addTransitions(BufferedReader br, TransitionActivity act, int nWards) throws IOException {
		// Transitions from gate
		addInitialTransitions(br.readLine().split("\\t"), act);

		for (int j = 0; j < nWards; j++) {
			addTransitionsFrom(br.readLine().split("\\t"), act, act.getWorkGroup(j));
		}
		// I assume there are no transitions from discharge
		br.readLine();		
		br.readLine();		
	}
	
	protected void testModel() {
		// Defines the patient types
		new ElementType(0, this, "Patient");

		// Defines resource types
		for (int i = 0; i < NWARDS; i++) {
			new ResourceType(i, this, "W" + i);
		}

		// Defines activities
		TransitionActivity act = new TransitionActivity(0, this, "Stay in bed");

		// Defines Workgroups
		for (int i = 0; i < NWARDS; i++) {
			WorkGroup wg = new WorkGroup(getResourceType(i), 1);
			act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wg);
		}
		
		// Defines transitions
		act.addInitialTransition(act.getWorkGroup(0), 0.5);
		act.addInitialTransition(act.getWorkGroup(1), 0.5);
		act.addTransition(act.getWorkGroup(0), act.getWorkGroup(1), 1.0);
		act.addTransition(act.getWorkGroup(1), act.getWorkGroup(0), 0.2);
		act.addFinalTransition(act.getWorkGroup(1), 0.8);
		
		// Defines resources
		SimulationCycle c = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", endTs), 0);
//		for (int i = 0; i < NWARDS; i++) {
//			for(int j = 0; j < NBEDS; j++) {
//				new Resource(i * NBEDS + j, this, "BED" + j + "_W" + i).addTimeTableEntry(c, endTs, getResourceType(i));
//			}
//		}
		for(int j = 0; j < NBEDS * 2; j++) {
			new Resource(j, this, "BED" + j + "_W0").addTimeTableEntry(c, endTs, getResourceType(0));
		}
		for(int j = 0; j < NBEDS; j++) {
			new Resource((NBEDS * 2) + j, this, "BED" + j + "_W1").addTimeTableEntry(c, endTs, getResourceType(1));
		}

		// Defines flow
		SingleFlow f = new SingleFlow(this, act);
		SimulationCycle c1 = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", endTs), 0);
        new TimeDrivenGenerator(this, new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NPAC), getElementType(0), f), c1);        
		
	}
	
	@Override
	protected void createModel() {
		testModel();
	}
}

/**
 * Time unit is DAY.
 * @author Iván Castilla Rodríguez
 */
class SalfordIPExperiment extends PooledExperiment {
	static final int NEXP = 1;
	
	public SalfordIPExperiment() {
		super("Salford Inpatients", NEXP);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new SalfordIPSimulation(ind);
		sim.addInfoReceiver(new StdInfoView(sim));
//		sim.setOutput(new Output(true));
//		cont.addListener(new ResourceStdUsageListener(1));
//		cont.addListener(new ResourceUsageListener());
//		cont.addListener(new ActivityListener(PERIOD));
//		cont.addListener(new ActivityTimeListener(PERIOD));
		return sim;
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SalfordInPatients {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SalfordIPExperiment().start();
	}

}
