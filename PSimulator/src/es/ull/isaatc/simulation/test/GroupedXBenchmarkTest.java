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
public class GroupedXBenchmarkTest {
	static boolean debug = false;
	static PrintStream out = System.out;
	static int [][]grain = {{1,1},{1,0},{2,1},{4,1}};

	private static BenchmarkModel[] testGroupedX() {
		int []nThreads = {2,4,8,16};
		int []nElems = {512};
		int []nActs = {128,512};
		int []nIters = {10000};
		long []workLoads = {0};
		int []rtXact = {4,8};
		int []rtXres = {1,2};
		BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.CONFLICT;
		BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
		ArrayList<BenchmarkModel> configs = new ArrayList<BenchmarkModel>();
		
		int counter = 0;
		BenchmarkModel bm = null;
		for (int rtA : rtXact) {
			for (int rtR : rtXres) {
				for (int nIter : nIters) {
					for (long wl : workLoads) {
						for (int j = 0; j < nElems.length; j++) {
							for (int k = 0; k < nActs.length && nActs[k] <= nElems[j]; k++) {
								for (int nTh : nThreads) {
									bm = new BenchmarkModel(counter++, SimulationType.GROUPEDX, modType, ovType, nTh, nIter, nElems[j], nActs[k], 0, wl);
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
		return configs.toArray(new BenchmarkModel[0]);
	}
	
	private static BenchmarkModel[] getWarmUpTests(BenchmarkModel.ModelType modType) {
		final int W_ACT = 32;
		final int W_ELEM = 32;
		final int W_ITER = 100;
		final int W_THREADS = 1;
		final BenchmarkModel.OverlappingType W_OVER = BenchmarkModel.OverlappingType.SAMETIME;
		final SimulationFactory.SimulationType []simTypes = {SimulationType.SEQUENTIAL, SimulationType.SEQ3PHASE2,SimulationType.GROUPEDX,SimulationType.GROUPED3PHASEX};
		
		BenchmarkModel[] configs = new BenchmarkModel[simTypes.length];
		for (int i = 0; i < simTypes.length; i++) {
			SimulationType simType = simTypes[i]; 
			configs[i] = new BenchmarkModel(simType.ordinal(), simType, modType, W_OVER, W_THREADS, W_ITER, W_ELEM, W_ACT);
		}
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
				BenchmarkModel[] configs = testGroupedX();
				buf.println(configs[0].getHeader() + "\tTime");
				for (BenchmarkModel conf : configs) {
					for (int []g: grain) {
						buf.print(conf + "\t" + g[0] + "\t" + g[1]);
						System.out.print(conf + "\t" + g[0] + "\t" + g[1]);
						for (int i = 0; i < nExp; i++) {
							sim = conf.getTestModel();
							((es.ull.isaatc.simulation.groupedExtraThreaded.Simulation)sim).grain = g[0];
							((es.ull.isaatc.simulation.groupedExtraThreaded.Simulation)sim).rest = g[1];
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
					
				buf.close();
			}
		}.start();
	}
	

}
