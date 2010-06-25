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
	static boolean debug = false;
	static PrintStream out = System.out;

	private static BenchmarkModel[] testParallel() {
		boolean sequential = true;
		int []nThreads = {1,2,3};
		int []xnThreads = {0,1,2,3};
		int []nElems = {32, 128, 256};
		int []nActs = {4, 8, 32};
		int []nIters = {5000};
		BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.PARALLEL;
		BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
		SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED3PHASE};		
		SimulationFactory.SimulationType []xsimTypes = {SimulationType.PASIVE,SimulationType.GROUPEDX,SimulationType.BONNGROUPEDX, SimulationType.GROUPED3PHASEX};		
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
					for (SimulationType simType : xsimTypes) {
						for (int nTh : xnThreads)
							configs.add(new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k]));							
					}
				}
			}
		}
		return configs.toArray(new BenchmarkModel[0]);
	}
	
	private static BenchmarkModel[] testConflict() {
		boolean sequential = true;
		int []nThreads = {1,2,3};
		int []xnThreads = {3,4};
		int []nElems = {512};
		int []nActs = {512};
		int []nIters = {10000};
		long []workLoads = {0};
		int []rtXact = {4,8};
		int []rtXres = {1,2};
		BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.CONFLICT;
		BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
		SimulationFactory.SimulationType []simTypes = {};		
		SimulationFactory.SimulationType []xsimTypes = {SimulationType.GROUPEDX,SimulationType.BONNGROUPEDX, SimulationType.GROUPED3PHASEX};		
		ArrayList<BenchmarkModel> configs = new ArrayList<BenchmarkModel>();
		
		int counter = 0;
		BenchmarkModel bm = null;
		for (int rtA : rtXact) {
			for (int rtR : rtXres) {
				for (int nIter : nIters) {
					for (long wl : workLoads) {
						for (int j = 0; j < nElems.length; j++) {
							for (int k = 0; k < nActs.length && nActs[k] <= nElems[j]; k++) {
								if (sequential) {
									bm = new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL, modType, ovType, 1, nIter, nElems[j], nActs[k], 0, wl);
									bm.setRtXact(rtA);
									bm.setRtXres(rtR);
									configs.add(bm);
									bm = new BenchmarkModel(counter++, SimulationFactory.SimulationType.SEQUENTIAL2, modType, ovType, 1, nIter, nElems[j], nActs[k], 0, wl);
									bm.setRtXact(rtA);
									bm.setRtXres(rtR);
									configs.add(bm);
								}
								for (SimulationType simType : simTypes) {
									for (int nTh : nThreads) {
										bm = new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k], 0, wl);
										bm.setRtXact(rtA);
										bm.setRtXres(rtR);
										configs.add(bm);
									}
								}
								for (SimulationType simType : xsimTypes) {
									for (int nTh : xnThreads) {
										bm = new BenchmarkModel(counter++, simType, modType, ovType, nTh, nIter, nElems[j], nActs[k], 0, wl);
										bm.setRtXact(rtA);
										bm.setRtXres(rtR);
										configs.add(bm);
									}
								}
							}
						}
					}
				}				
			}
		}
		return configs.toArray(new BenchmarkModel[0]);
	}
	
	private static BenchmarkModel[] getWarmUpTests(BenchmarkModel.ModelType modType) {
		final int W_ACT = 32;
		final int W_ELEM = 32;
		final int W_ITER = 100;
		final int W_THREADS = 1;
		final BenchmarkModel.OverlappingType W_OVER = BenchmarkModel.OverlappingType.SAMETIME;
		BenchmarkModel[] configs = new BenchmarkModel[SimulationFactory.SimulationType.values().length];
		for (SimulationFactory.SimulationType simType : SimulationFactory.SimulationType.values())
			configs[simType.ordinal()] = new BenchmarkModel(simType.ordinal(), simType, modType, W_OVER, W_THREADS, W_ITER, W_ELEM, W_ACT);
		return configs;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Wrong arg number: you must indicate a destination file, number of experiments and problems to perform.");
			System.exit(-1);
		}
		final String fileName = args[0];
		final int nExp = Integer.parseInt(args[1]);
		final String problemType = args[2].toLowerCase();
		
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
					buf = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Simulation sim = null;
				if (problemType.indexOf('w') != -1) {
					BenchmarkModel[] configs = getWarmUpTests(BenchmarkModel.ModelType.PARALLEL);
					System.out.println("Init WARM UP...");
					for (BenchmarkModel conf : configs) {
						System.out.print("Testing... " + conf);
						sim = conf.getTestModel();
//						sim.addInfoReceiver(new ProgressListener(sim));
						sim.run();
						System.out.println("\tOK!");
					}
					System.out.println("End WARM UP...");					
				}
				if (problemType.indexOf('c') != -1) {
					BenchmarkModel[] configs = testConflict();
					buf.println(configs[0].getHeader() + "\tTime");
					for (BenchmarkModel conf : configs) {
						buf.print(conf);
						System.out.print(conf);
						for (int i = 0; i < nExp; i++) {
							sim = conf.getTestModel();
							if (debug)
								sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
							sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
//							sim.addInfoReceiver(new ProgressListener(sim));
							sim.run();
						}
						System.out.println();
						buf.println();
					}
					
				}
				if (problemType.indexOf('p') != -1) {
					BenchmarkModel[] configs = testParallel();
					buf.println(configs[0].getHeader() + "\tTime");
					for (BenchmarkModel conf : configs) {
						buf.print(conf);
						System.out.print(conf);
						for (int i = 0; i < nExp; i++) {
							sim = conf.getTestModel();
							if (debug)
								sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
							sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
	//						sim.addInfoReceiver(new ProgressListener(sim));
							sim.run();
						}
						System.out.println();
						buf.println();
					}
				}
				buf.close();
			}
		}.start();
	}
	

}
