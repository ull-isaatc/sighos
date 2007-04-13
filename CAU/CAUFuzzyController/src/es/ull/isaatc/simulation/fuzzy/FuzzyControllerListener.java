/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import java.util.ArrayList;

import es.ull.isaatc.random.Uniform;
import es.ull.isaatc.simulation.Generator;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.XMLSimulation;
import es.ull.isaatc.simulation.fuzzy.ProgrammedTaskList.ProgrammedTaskListEntry;
import es.ull.isaatc.simulation.info.ActivityQueueListener;
import es.ull.isaatc.simulation.info.CompassListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;
import es.ull.isaatc.util.TableCycle;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyControllerListener extends CompassListener {
	/** Programed tasks list */
	private ProgrammedTaskList programmedTaskList = new ProgrammedTaskList();
	/** Fuzzy engine */
	private FIS fis;
	/** Queue listener */
	private ActivityQueueListener actListener;
	/** Activity identifier */
	private int actQueueId;
	
	/**
	 * 
	 * @param period sample rate
	 * @param fileName fuzzy file path
	 */
	public FuzzyControllerListener(Simulation simul, CycleIterator cycleIterator, double period, int actQueueId, String fileName) {
		super(cycleIterator);

		this.actQueueId = actQueueId;
		actListener = new ActivityQueueListener(period);
		simul.addListener(actListener);
		// load the FCL file
		fis = FIS.load(fileName, false);
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}
	}

	public void addTask(String description, int et, int mf, Cycle c, double qos) {
		programmedTaskList.add(description, et,	mf, c, qos);
		
	}
	
	public void infoEmited(SimulationStartInfo info) {
		programmedTaskList.initIterator(info.getSimulation().getStartTs(), info.getSimulation().getEndTs());
	}

	
	public void takeSample(Generator gen) {
		FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
//		int queue = actListener.getActivityQueue(6) + actListener.getActivityQueue(7) + actListener.getActivityQueue(8); 
		int queue = actListener.getActivityQueue(actQueueId);
		fuzzyRuleSet.setVariable("queue", queue);
		double prob;
		Uniform uniform = new Uniform(0, 1);

		for (ProgrammedTaskListEntry entry : programmedTaskList.getTaskList()) {
			// repeat the test while the task period is in this sample
			ArrayList<Double> taskCycle = new ArrayList<Double>();
			for ( ; entry.getNextTs() < nextTs; entry.next()) {
				fuzzyRuleSet.evaluate();
				Variable var = fuzzyRuleSet.getVariable(entry.getDescription());
				prob = 1 - ((1 - entry.getQos()) * var.defuzzify());
				double pValue = uniform.sampleDouble();
				if (pValue <= prob) {
					taskCycle.add(entry.getNextTs());
				}
			}
			double taskCycleArray[] = new double[taskCycle.size()];
			int i = 0;
			for (double value : taskCycle) {
				taskCycleArray[i++] = value;
			}
			Cycle cycle = new TableCycle(taskCycleArray);
			new TimeDrivenGenerator(gen.getSimul(), entry.getElementCreator((XMLSimulation) gen.getSimul()), cycle);
		}
	}

	public void infoEmited(SimulationObjectInfo arg0) {
		
	}

	public void infoEmited(SimulationEndInfo arg0) {
		
	}

	public void infoEmited(TimeChangeInfo arg0) {
	
	}	
}