/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CompositeBenchmarkTest {
	final private static int W_ACT = 32;
	final private static int W_ELEM = 32;
	final private static int W_ITER = 100;
	final private static int W_THREADS = 1;
	final private static BenchmarkModel.OverlappingType W_OVER = BenchmarkModel.OverlappingType.SAMETIME;

	static int nExp = 1;
	static boolean debug = false;
	static PrintStream out = System.out;

	private static BenchmarkModel[] testSimultaneousActivities() {
		boolean sequential = false;
		int []nThreads = {1,2,3};
		int []nElems = {32, 128, 256};
		int []nActs = {4, 8, 32};
		int []nIters = {5000};
		BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.PARALLEL;
		BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
		SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED3PHASE2};		
		ArrayList<BenchmarkModel> configs = new ArrayList<BenchmarkModel>();
		
		int counter = 0;
		for (int nIter : nIters) {
			for (int j = 0; j < nElems.length; j++) {
				for (int k = nActs.length - j - 1; k >= 0; k--) {
					if (sequential) {
						configs.add(new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL, modType, ovType, 1, nIter, nElems[j], nActs[k]));
						configs.add(new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL2, modType, ovType, 1, nIter, nElems[j], nActs[k]));
					}
					for (SimulationType simType : simTypes) {
						for (int nTh : nThreads)
							configs.add(new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k]));							
					}
				}
			}
		}
		return configs.toArray(new BenchmarkModel[0]);
	}
	
	private static BenchmarkModel[] testSimpleResourceWorkload() {
		boolean sequential = true;
		int []nThreads = {1,2,3};
		int []nElems = {512};
		int []nActs = {256, 512};
		int []nIters = {10000};
		long []workLoads = {0};
		BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.CONFLICT;
		BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
		SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED3PHASE2};		
		SimulationFactory.SimulationType []xsimTypes = {};		
		ArrayList<BenchmarkModel> configs = new ArrayList<BenchmarkModel>();
		
		int counter = 0;
		for (int nIter : nIters) {
			for (long wl : workLoads) {
				for (int j = 0; j < nElems.length; j++) {
					for (int k = 0; k < nActs.length && nActs[k] <= nElems[j]; k++) {
						if (sequential) {
							configs.add(new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL, modType, ovType, 1, nIter, nElems[j], nActs[k], 0, wl));
							configs.add(new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL2, modType, ovType, 1, nIter, nElems[j], nActs[k], 0, wl));
						}
						for (SimulationType simType : simTypes) {
							for (int nTh : nThreads)
								configs.add(new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k], 0, wl));							
						}
						for (SimulationType simType : xsimTypes) {
							configs.add(new BenchmarkModel(counter++, simType, modType, ovType, 0, nIter, nElems[j], nActs[k], 0, wl));							
							for (int nTh : nThreads)
								configs.add(new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k], 0, wl));							
						}
					}
				}
			}
		}
		return configs.toArray(new BenchmarkModel[0]);
	}
	
	private static BenchmarkModel[] getWarmUpTests(BenchmarkModel.ModelType modType) {
		BenchmarkModel[] configs = new BenchmarkModel[SimulationFactory.SimulationType.values().length];
		for (SimulationFactory.SimulationType simType : SimulationFactory.SimulationType.values())
			configs[simType.ordinal()] = new BenchmarkModel(simType.ordinal(), simType, modType, W_OVER, W_THREADS, W_ITER, W_ELEM, W_ACT);
		return configs;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Same Time", nExp) {
			@Override
			// We are not going to use this method
			public Simulation getSimulation(int ind) {
				return null;
			}

			@Override
			public void start() {
				PrintWriter buf = null;
				try {
					buf = new PrintWriter(new BufferedWriter(new FileWriter("c:\\res.txt")));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Simulation sim = null;
				BenchmarkModel[] configs = getWarmUpTests(BenchmarkModel.ModelType.PARALLEL);
				System.out.println("Init WARM UP...");
				for (BenchmarkModel conf : configs) {
					System.out.print("Testing... " + conf);
					sim = conf.getTestModel();
//					sim.addInfoReceiver(new ProgressListener(sim));
					sim.run();
					System.out.println("\tOK!");
				}
				System.out.println("End WARM UP...");
				configs = testSimpleResourceWorkload();
				buf.println(BenchmarkModel.getHeader() + "\tTime");
				for (int i = 0; i < nExp; i++) {
					for (BenchmarkModel conf : configs) {
						buf.print(conf + "\t");
						System.out.print(conf + "\t");
						sim = conf.getTestModel();
						if (debug)
							sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
						sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
//						sim.addInfoReceiver(new ProgressListener(sim));
						sim.run();
					}
				}
//				configs = testSimultaneousActivities();
//				for (int i = 0; i < nExp; i++) {
//					for (BenchmarkModel conf : configs) {
//						buf.print(conf + "\t");
//						System.out.print(conf + "\t");
//						sim = conf.getTestModel();
//						if (debug)
//							sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
//						sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
////						sim.addInfoReceiver(new ProgressListener(sim));
//						sim.run();
//					}
//				}
				buf.close();
			}
		}.start();
	}
	

}
