/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import java.util.HashMap;

import es.ull.isaatc.random.Uniform;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;
import net.sourceforge.jFuzzyLogic.rule.Variable;

class FuzzyController {

	/** Tasks QoS */
	private static HashMap<String, Double> qos = new HashMap<String, Double>();

	private int nSamples;
	
	private int nExp;

	private FIS fis;
	
	private HashMap<String, Integer> taskPerfomed;

	public FuzzyController(int nExp, int nSamples, String fileName) {
		this.nExp = nExp;
		this.nSamples = nSamples;

		// define the QoS
		qos.put("task1", 1.0);
		qos.put("task2", 0.8);
		qos.put("task3", 0.7);

		taskPerfomed = new HashMap<String, Integer>();
		taskPerfomed.put("task1", 0);
		taskPerfomed.put("task2", 0);
		taskPerfomed.put("task3", 0);
		
		// load the FCL file
		fis = FIS.load(fileName, false);
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}
	}

	public void startExperiments(int queueSize) {
		System.out.println("QUEUE SIZE :\t" + queueSize);
		System.out.println("TASK\tFUZZY\tQOS\tProb\tThrow\tis performed");
		for (int i = 0; i < nExp; i++)
			run(queueSize);
	}
	public void run(int queueSize) {
		if (fis == null) {
			return;
		}
		
		for (String varStr : taskPerfomed.keySet()) {
			taskPerfomed.put(varStr, 0);
		}

		FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
//		fuzzyRuleSet.chart();
		
		fuzzyRuleSet.setVariable("queue", queueSize);
		
		double controllerResult;
		double taskQOS;
		double prob;
		int isPerformed;
		
		double acc = 0;
		Uniform uniform = new Uniform(0, 1);
		
		for (int i = 0; i < nSamples; i++) {
			fuzzyRuleSet.evaluate();
	
			for (String varStr : qos.keySet()) {
				Variable var = fuzzyRuleSet.getVariable(varStr);
				controllerResult = var.defuzzify();
				taskQOS = qos.get(varStr);
				prob = 1 - ((1 - taskQOS) * controllerResult);
				
				double t1 = uniform.sampleDouble();
				acc = acc + t1;
				if (t1 <= prob) {
					isPerformed = 1;
					taskPerfomed.put(varStr, taskPerfomed.get(varStr) + 1);
				}
				else
					isPerformed = 0;
					
				System.out.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%d\n", varStr, controllerResult, taskQOS, prob, t1, isPerformed);
			}
		}
		System.out.println(acc / (nSamples * 3));
		
		for (String varStr : taskPerfomed.keySet()) {
			System.out.println(varStr + " :\t" + (double)taskPerfomed.get(varStr) / nSamples);
		}
	}
}

/**
 * Test fuzzy controller FCL
 * 
 * @author rmglez
 * 
 */
public class TestController {

	static int NSAMPLES = 100;
	
	static int NEXP = 1;
	
	static int QSIZE = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FuzzyController controller = new FuzzyController(NEXP, NSAMPLES, "controller.fcl");
		controller.startExperiments(QSIZE);
	}
}