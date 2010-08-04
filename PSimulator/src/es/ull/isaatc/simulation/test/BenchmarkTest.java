/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintStream;

import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.inforeceiver.CpuTimeView;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.util.Output;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class BenchmarkTest {
	private static final int MINARGS = 9;
	static int nThreads = 1;
	static int nElem = 16;
	static int nAct = 8;
	static int nIter = 100000;
	static int nExp = 1;
	static int rtXact = 4;
	static int rtXres = 1;
	static int mixFactor = 2;
	static long workLoad = 0;
	static BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
	static BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.CONFLICT;
	static boolean debug = true;
	static PrintStream out = System.out;
	static SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int argCounter = 0;
		if (args.length >= MINARGS) {
			simType = SimulationType.valueOf(args[argCounter++]);
			modType = BenchmarkModel.ModelType.valueOf(args[argCounter++]);
			ovType = BenchmarkModel.OverlappingType.valueOf(args[argCounter++]);
			nAct = Integer.parseInt(args[argCounter++]);
			nElem = Integer.parseInt(args[argCounter++]);
			nIter = Integer.parseInt(args[argCounter++]);
			nThreads = Integer.parseInt(args[argCounter++]);
			nExp = Integer.parseInt(args[argCounter++]);
			workLoad = Long.parseLong(args[argCounter++]);
			if (args.length > argCounter) {
				if (ovType == BenchmarkModel.OverlappingType.MIXED) {
					mixFactor = Integer.parseInt(args[argCounter++]);
				}
				// Debug is always the last parameter
				if (args.length > argCounter)
					debug = "D".equals(args[args.length - 1]);
			}
		} else if (args.length > 0) { 
			System.err.println("Wrong number of arguments.\n Arguments expected: " + MINARGS);
			System.exit(0);
		} 
		
		new PooledExperiment("Same Time", nExp/*, Executors.newFixedThreadPool(nExp)*/) {
			long t1;

			@Override
			public void start() {
				t1 = System.nanoTime();
				super.start();
			}
			
			@Override
			protected void end() {
				super.end();
				System.out.println("TOTAL EXPERIMENT: " + ((System.nanoTime() - t1) / 1000000) + " miliseconds");
			}
			
			@Override
			public Simulation getSimulation(int ind) {
				BenchmarkModel config = new BenchmarkModel(ind, simType, modType, ovType, nThreads, nIter, nElem, nAct, mixFactor, workLoad);
				config.setRtXact(rtXact);
				config.setRtXres(rtXres);
				System.out.println(config);
				Simulation sim = config.getTestModel(); 
				
				if (debug)
					sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
//				sim.addInfoReceiver(new CpuTimeView(sim));
//				sim.addInfoReceiver(new ProgressListener(sim));
//				sim.addInfoReceiver(new StdInfoView(sim));
//				sim.setOutput(new Output(true));
				return sim;
			}
			
		}.start();
		
//		new Experiment("Same Time", nExp) {
//			long t1;
//			@Override
//			public Simulation getSimulation(int ind) {
//				return getTestOcurrenceSimN2(simType, ind);
//			}
//
//			@Override
//			public void start() {
//				t1 = System.nanoTime();
//				for (int i = 0; i < nExperiments; i++) {
//					Simulation sim = getSimulation(i);
//					sim.setNThreads(nThreads);
//					
////					sim.addInfoReciever(new StdInfoView(sim));
//					if (debug)
//						sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
//					sim.addInfoReceiver(new CpuTimeView(sim));
//					sim.addInfoReceiver(new ProgressListener(sim));
//					Thread th = new Thread(sim);
//					th.start();
//					try {
//						th.join();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				end();		
//			}
//			protected void end() {
//				System.out.println("" + (System.nanoTime() - t1));
//			}			
//		}.start();
	}
	
}
