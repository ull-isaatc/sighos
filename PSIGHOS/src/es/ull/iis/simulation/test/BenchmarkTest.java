/**
 * 
 */
package es.ull.iis.simulation.test;

import java.io.PrintStream;

import es.ull.iis.simulation.factory.SimulationType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkTest {
	private static final int MINARGS = 9;
	static int nThreads = 1;
	static int nElem = 512;
	static int nAct = 512;
	static int nIter = 100;
	static int nExp = 1;
	static int rtXact = 4;
	static int rtXres = 1;
	static int mixFactor = 2;
	static long workLoad = 0;
	static BenchmarkModel.OverlappingType ovType = BenchmarkModel.OverlappingType.SAMETIME;
	static BenchmarkModel.ModelType modType = BenchmarkModel.ModelType.CONFLICT;
	static boolean debug = true;
	static PrintStream out = System.out;
	static SimulationType simType = SimulationType.SEQUENTIAL;

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
		
		new Experiment("Same Time", nExp) {
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
					sim.addInfoReceiver(new BenchmarkListener(System.out));
				return sim;
			}
			
		}.start();
		
	}
	
}
