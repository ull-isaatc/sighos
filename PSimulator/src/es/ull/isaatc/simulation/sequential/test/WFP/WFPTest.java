/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.TimeDrivenGenerator;

class NoSimulation extends WFPTestSimulation {

	public NoSimulation(int id) {
		super(id, "No valid simulation", false);
	}

	@Override
	protected void createModel() {
	}
	
}
class WFPTestExperiment extends Experiment {
	int wfp = -1;
	boolean detailed;
	
	public WFPTestExperiment(boolean detailed) {
		super("Testing WFPs...", WFPTest.simulations.size());
		this.detailed = detailed;
	}
	
	public WFPTestExperiment(int wfp, boolean detailed) {
		super("Testing WFPs...", 1);
		this.wfp = wfp;
		this.detailed = detailed;
	}

	private Simulation class2Simulation(Class<?> cl, int ind) {
		Simulation sim = null;
		try {
			if (cl == null)
				sim = new NoSimulation(ind);
			else {
				Constructor<?> c = cl.getConstructor(int.class, boolean.class);
				sim = (Simulation)c.newInstance(ind, detailed);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return sim;
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = null;
		if (wfp != -1) {
			sim = class2Simulation(WFPTest.simulations.get(wfp), ind);
		}
		else {
			sim = class2Simulation(WFPTest.simulations.pollFirstEntry().getValue(), ind);
		}
		return sim;
	}

	@Override
	public void start() {
		for (int i = 0; i < nExperiments; i++) {
			TimeDrivenGenerator.setElemCounter(0);
			WFPTestSimulation sim = (WFPTestSimulation)getSimulation(i);
//	        sim.addInfoReceiver(new CheckElementActionViewBuilder(sim));
//			sim.addInfoReceiver(new StdInfoView(sim));
			sim.run();
		}
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WFPTest {
	static public TreeMap <Integer, Class<?>> simulations = new TreeMap<Integer, Class<?>>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Creating the set of simulations
		simulations.put(1, WFP01Simulation.class);
		simulations.put(2, WFP02Simulation.class);
		simulations.put(3, WFP03Simulation.class);
		simulations.put(4, WFP04Simulation.class);
		simulations.put(5, WFP05Simulation.class);
		simulations.put(6, WFP06Simulation.class);
		simulations.put(7, WFP07Simulation.class);
		

		new WFPTestExperiment(3, true).start();
	}

}
