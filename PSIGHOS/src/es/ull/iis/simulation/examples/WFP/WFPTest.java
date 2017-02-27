/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.TimeUnit;

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

	private Model class2Model(Class<?> cl, int ind) {
		Model model = null;
		try {
			if (cl == null) {
				model = new Model(ind, "No valid simulation", TimeUnit.MINUTE, 0, 0);
			}
			else {
				Constructor<?> c = cl.getConstructor(SimulationType.class, int.class, boolean.class);
				model = ((WFPTestSimulationFactory)c.newInstance(type, ind, detailed)).getModel();
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
		return model;
	}
	
	@Override
	public Model getModel(int ind) {
		Model model = null;
		if (wfp != -1) {
			model = class2Model(WFPTest.simulations.get(wfp), ind);
		}
		else {
			model = class2Model(WFPTest.simulations.pollFirstEntry().getValue(), ind);
		}
//        sim.addInfoReceiver(new CheckElementActionViewBuilder(sim));
		model.addInfoReceiver(new StdInfoView(model));
		// FIXME: Fix when implementing parallel
//		model.setNThreads(nThreads);
		return model;
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

		new WFPTestExperiment(SimulationType.SEQUENTIAL, 211, 4, false).start();
	}

}
