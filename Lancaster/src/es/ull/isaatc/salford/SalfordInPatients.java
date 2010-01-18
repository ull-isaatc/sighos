/**
 * 
 */
package es.ull.isaatc.salford;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.listener.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

class SalfordIPSimulation extends StandAloneLPSimulation {
	static final int NDAYS = 100;
	static final int NWARDS = 2;
	static final int NBEDS = 2;
	static final int NPAC = 100;
	static final String FILE_NAME = "C:\\Users\\Iván\\Documents\\Salford\\testTrans1.txt";
//	static final String FILE_NAME = "C:\\Users\\Iván\\Documents\\Salford\\test1.txt";
	
	public SalfordIPSimulation(int id) {
		super(id, "Salford Inpatient Model", 0.0, NDAYS);
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
		    for (int i = 0; i < wardNames.length; i++) {
		    	ResourceType rt = new ResourceType(i, this, "W_" + wardNames[i]);
		    	new WorkGroup(i, this, wardNames[i]).add(rt, 1);
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
			    		acts[i].addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 1), getWorkGroup(j));
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

	private void addTransitionsFrom(String[] percentages, TransitionActivity act, Activity.WorkGroup fromTrans) {
		// I assume there are no transitions to gate
		for (int k = 1; k < percentages.length - 1; k++) {
			double p = new Double(percentages[k]);
			if (p > 0.0)
				act.addTransition(fromTrans, act.getWorkGroup(k - 1), p);
		}
		// Last transition
		double p = new Double(percentages[percentages.length - 1]);
		if (p > 0.0)
			act.addTransition(fromTrans, act.finalTransition, p);		
	}
	
	private void addTransitions(BufferedReader br, TransitionActivity act, int nWards) throws IOException {
		// Transitions from gate
		addTransitionsFrom(br.readLine().split("\\t"), act, act.initialTransition);

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
			WorkGroup wg = new WorkGroup(i, this, "WG" + i);
			wg.add(getResourceType(i), 1);
			act.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 1), wg);
		}
		
		// Defines transitions
		act.addTransition(act.initialTransition, act.getWorkGroup(0), 0.5);
		act.addTransition(act.initialTransition, act.getWorkGroup(1), 0.5);
		act.addTransition(act.getWorkGroup(0), act.getWorkGroup(1), 1.0);
		act.addTransition(act.getWorkGroup(1), act.getWorkGroup(0), 0.2);
		act.addTransition(act.getWorkGroup(1), act.finalTransition, 0.8);
		
		// Defines resources
		Cycle c = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", endTs), 0);
		for (int i = 0; i < NWARDS; i++) {
			for(int j = 0; j < NBEDS; j++) {
				new Resource(i * NBEDS + j, this, "BED" + j + "_W" + i).addTimeTableEntry(c, endTs, getResourceType(i));
			}
		}

		// Defines flow
		TransitionSingleMetaFlow f = new TransitionSingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), act);
		Cycle c1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", endTs), 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NPAC), getElementType(0), f), c1);        
		
	}
	
	@Override
	protected void createModel() {
		loadedModel();
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
		ListenerController cont = new ListenerController() {
			@Override
			public void end() {
				super.end();
				for (String res : getListenerResults()) {
					System.out.println(res);
				}
			}
		};
		sim.setListenerController(cont);
//		sim.setOutput(new Output(true));
//		cont.addListener(new StdInfoListener());
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
