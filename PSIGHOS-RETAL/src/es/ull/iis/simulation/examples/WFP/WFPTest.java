/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

class WFPTestExperiment extends Experiment {
	int wfp = -1;
	boolean detailed;
	SimulationType type;
	int nThreads = 1;
	
	public WFPTestExperiment(SimulationType type, int nThreads, boolean detailed) {
		super("Testing WFPs...", WFPTest.simulations.size());
		this.detailed = detailed;
		this.type = type;
		this.nThreads = nThreads;
	}
	
	public WFPTestExperiment(SimulationType type, int wfp, int nThreads, boolean detailed) {
		super("Testing WFPs...", 1);
		this.wfp = wfp;
		this.detailed = detailed;
		this.type = type;
		this.nThreads = nThreads;
	}

	private Simulation class2Simulation(Class<?> cl, int ind) {
		Simulation sim = null;
		try {
			if (cl == null)
				sim = SimulationFactory.getInstance(type, ind, "No valid simulation", TimeUnit.MINUTE, 0, 0).getSimulation();
			else {
				Constructor<?> c = cl.getConstructor(SimulationType.class, int.class, boolean.class);
				sim = ((WFPTestSimulationFactory)c.newInstance(type, ind, detailed)).getSimulation();
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
//        sim.addInfoReceiver(new CheckElementActionViewBuilder(sim));
		sim.addInfoReceiver(new StdInfoView(sim));
		sim.setNThreads(nThreads);
		return sim;
	}
}

/**
 * @author Iv�n Castilla Rodr�guez
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
		simulations.put(8, WFP08Simulation.class);
		simulations.put(9, WFP09Simulation.class);
		simulations.put(10, WFP10Simulation.class);
		simulations.put(12, WFP12Simulation.class);
		simulations.put(13, WFP13Simulation.class);
		simulations.put(17, WFP17Simulation.class);
		simulations.put(19, WFP19Simulation.class);
		simulations.put(21, WFP21Simulation_For.class);
		simulations.put(211, WFP21Simulation_WhileDo.class);
		simulations.put(212, WFP21Simulation_DoWhile.class);
		simulations.put(28, WFP28Simulation.class);
		simulations.put(30, WFP30Simulation.class);
		simulations.put(40, WFP40Simulation.class);

		new WFPTestExperiment(SimulationType.PARALLEL, 7, 4, false).start();
	}

}
