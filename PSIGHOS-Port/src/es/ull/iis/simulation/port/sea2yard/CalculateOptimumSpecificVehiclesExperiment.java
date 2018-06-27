/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * An experiment to compute the optimum with different vehicle assignment policies.  
 * @author Iván Castilla Rodríguez
 *
 */
public class CalculateOptimumSpecificVehiclesExperiment {
	private final int minVehicles;
	private final int maxVehicles;
	private final int nSolutions;
	private final double pError;
	private final int nSims;	
	private final StowagePlan[] plans;
	private final boolean simulateAll;
	private final boolean stopAtFirst;
	private final boolean test;
	private int simCounter;
	private PrintWriter out;
	private PrintWriter system_out = new PrintWriter(System.out);

	public CalculateOptimumSpecificVehiclesExperiment(String outputFileName, int nSims, StowagePlan[] plans, int nSolutions, int minVehicles, int maxVehicles, double pError, boolean simulateAll, boolean stopAtFirst, boolean test) {
		this.minVehicles = minVehicles;
		this.maxVehicles = maxVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.simulateAll = simulateAll;
		this.stopAtFirst = stopAtFirst;
		this.test = test;
		this.pError = pError;
		this.nSims = nSims;
		if (outputFileName == null) {
			out = new PrintWriter(System.out);
		}
		else {
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
			} catch (IOException e) {
				e.printStackTrace();
				out = new PrintWriter(System.out);
			}
		}
		simCounter = 0;
	}
	
	private int[] computeNVehicles(int nCranes, int nVehicles, int nSpecificVehicles) {
		if (nSpecificVehicles == 0) {
			return new int[] {nVehicles};
		}
		if (nCranes * nSpecificVehicles <= nVehicles) {
			final int[]nVehic = new int[nCranes + 1];
			Arrays.fill(nVehic, 0, nCranes, nSpecificVehicles);
			nVehic[nCranes] = nVehicles - nCranes * nSpecificVehicles;
			return nVehic; 
		}
		return null;
	}

	/**
	 * 
	 * @param sim
	 * @param solution
	 * @param nVehicles
	 * @param times
	 * @return True if "stop at first" is activated, and found an optimum solution
	 */
	private boolean simulate(final int sim, final int solution, final int[]nVehicles, final TimeRepository times) {
		if (test) {
			out.print(simCounter);
			for (int i = 0; i < nVehicles.length; i++)
				out.print("\t" + nVehicles[i]);
			out.println();
		}
		else {
			final StowagePlan plan = plans[solution];
			final PortModel model = (stopAtFirst ?
					new PortModel(plan, simCounter, nVehicles, times, (plan.getObjectiveValue() + 1) * PortModel.T_OPERATION) :
					new PortModel(plan, simCounter, nVehicles, times));
			final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
			model.addInfoReceiver(listener);
			model.run();
			printResults(system_out, sim, solution, listener, nVehicles, pError);
			if (stopAtFirst) {
				if (listener.isScheduleCompleted()) {
					printResults(out, sim, solution, listener, nVehicles, pError);					
					out.flush();
					return true;
				}
			}
			else {
				printResults(out, sim, solution, listener, nVehicles, pError);
			}
		}
		simCounter++;
		if (simCounter % 100 == 1) {
			system_out.flush();
			out.flush();
		}
		return false;
	}

	public boolean simulateNVehicles(final int sim, final int solution, final int[]nVehicles, final int level, final int leftVehicles, final TimeRepository times, final boolean leftToRight) {
		final int lastLevel = leftToRight ? 0 :  nVehicles.length - 2;
		if (level == lastLevel) {
			nVehicles[level] = leftVehicles;
			if (simulate(sim, solution, nVehicles, times))
				return true;
		}
		else {
			nVehicles[level] = leftVehicles;
			if (leftToRight)
				Arrays.fill(nVehicles, 0, level, 0);
			else
				Arrays.fill(nVehicles, level + 1, nVehicles.length - 1, 0);
			if (simulate(sim, solution, nVehicles, times))
				return true;
			for (int n = leftVehicles - 1; n >= 0; n--) {
				nVehicles[level] = n;
				if (simulateNVehicles(sim, solution, nVehicles, level + (leftToRight ? -1 : 1), leftVehicles - n, times, leftToRight))
					return true;
			}
		}
		return false;
	}
	
	public boolean simulateNVehiclesNonZero(final int sim, final int solution, final int[]nVehicles, final int level, final int leftVehicles, final TimeRepository times, final boolean leftToRight) {
		final int lastLevel = leftToRight ? 0 :  nVehicles.length - 2;
		if (level == lastLevel) {
			nVehicles[level] = leftVehicles;
			if (simulate(sim, solution, nVehicles, times))
				return true;
		}
		else {
			if (leftToRight) {
				nVehicles[level] = leftVehicles - level;
				Arrays.fill(nVehicles, 0, level, 1);
			}
			else {
				nVehicles[level] = leftVehicles - lastLevel + level;
				Arrays.fill(nVehicles, level + 1, nVehicles.length - 1, 1);
			}
			if (simulate(sim, solution, nVehicles, times))
				return true;
			for (int n = leftVehicles - (leftToRight ? level : (lastLevel - level)) - 1; n >= 1; n--) {
				nVehicles[level] = n;
				if (simulateNVehiclesNonZero(sim, solution, nVehicles, level + (leftToRight ? -1 : 1), leftVehicles - n, times, leftToRight))
					return true;
			}
		}
		return false;
	}
	
	public void start() {
		// Print header
		printHeader(system_out);
		printHeader(out);
		for (int sim = 0; sim < nSims; sim++) {
			final TimeRepository times = new TimeRepository(plans[0], pError);
			if (simulateAll) {
				for (int sol = 0; sol < nSolutions; sol++) {
					final StowagePlan plan = plans[sol];
					// Checks whether is leftToRight or rightToLeft to optimize the search, by starting assigning vehicles to the most critical crane in the first place
					// i.e. the leftmost crane in a rightToLeft plan; the rightmost crane in a leftToRight plan.
					final boolean leftToRight = false; //plan.isLeftToRight();
					boolean stop = false;
					for (int nVehicles = maxVehicles; nVehicles >= minVehicles && !stop; nVehicles--) {
						for (int n = nVehicles; n > 0 && !stop; n--) {
							final int[] arrayVehicles = new int[plan.getNCranes() + 1];
							arrayVehicles[plan.getNCranes()] = n;
							stop = simulateNVehicles(sim, sol, arrayVehicles, leftToRight ? (plan.getNCranes() - 1) : 0, nVehicles - n, times, leftToRight);					
						}
						if (!stop) {
							stop = simulateNVehiclesNonZero(sim, sol, new int[plan.getNCranes() + 1], leftToRight ? (plan.getNCranes() - 1) : 0, nVehicles, times, leftToRight);
						}
						// If "stop at first" is activated and no solution is found for that level of vehicles, the simulations must stop.
						// Conversely, if a solution is found, the simulations must continue
						if (stopAtFirst)
							stop = !stop;
					}
				}
			}
			else {
				for (int sol = 0; sol < nSolutions; sol++) {
					final StowagePlan plan = plans[sol];
					for (int nVehicles = minVehicles; nVehicles <= maxVehicles; nVehicles++) {
						int nSpecificVehicles = 0;
						int [] arrayVehicles = computeNVehicles(plan.getNCranes(), nVehicles, nSpecificVehicles++);
						do {
							simulate(sim, sol, arrayVehicles, times);
							arrayVehicles = computeNVehicles(plan.getNCranes(), nVehicles, nSpecificVehicles++);
						} while (arrayVehicles != null);
					}
				}
			}
		}
		out.flush();
		out.close();

	}

	private void printHeader(PrintWriter out) {
		out.print("ID\tSOL\tVEHIC");
		for (int i = 1; i <= plans[0].getNCranes(); i++) {
			out.print("\tVEHIC" + i);
		}
		out.print("\tERROR\tOBJ");
		for (int i = 1; i <= plans[0].getNCranes(); i++) {
			out.print("\tT_TOT" + i + "\tT_USE" + i + "\tT_OP" + i + "\tT_MOV" + i);
		}
		if (stopAtFirst)
			out.print("\tLEFT");
		out.println();
	}

	private void printResults(PrintWriter out, final int ind, final int nPlan, final Sea2YardGeneralListener listener, final int[] nVehicles, final double pError) {
		out.print("" + ind + "\t" + nPlan + "\t" + nVehicles[nVehicles.length - 1]);
		if (nVehicles.length > 1) {
			for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
				out.print("\t" + nVehicles[i]);
			}
		}
		else {
			for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
				out.print("\t0");
			}			
		}
		out.print("\t" + pError + "\t" + listener.getObjectiveValue());
		for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
			out.print("\t" + listener.getObjTime()[i] + "\t" + listener.getUseTime()[i] + "\t" + listener.getOpTime()[i] + "\t" + listener.getMovTime()[i]);
		}
		if (stopAtFirst)
			out.print("\t" + listener.tasksLeft());
		out.println();		
	}
	
	public static void main(String[] args) {
//        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(System.getProperty("user.home") + "/Dropbox/SimulationPorts/for_validation/k41.sol");
//        int[] nVehicles = new int[] {0,0,0,12};
//		final TimeRepository times = new TimeRepository(plans[0], 0.0);        
//		PortModel model = new PortModel(plans[0], 0, nVehicles, times, 11760);
//		model.addInfoReceiver(new ContainerTraceListener(TimeUnit.SECOND));
//		final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plans[0], TimeUnit.SECOND); 
//		model.addInfoReceiver(listener);
////		model.addInfoReceiver(new StdInfoView());
//		model.run();
//		System.out.println("OPT: " + plans[0].getObjectiveValue() * PortModel.T_OPERATION + "\tGET: " + listener.getObjectiveValue()); 
//		System.out.println(listener.isScheduleCompleted() ? "FINISHED" : "NOT FINISHED");
//		simulateNVehicles(0, new int[4], 0, 10);
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
			final int minVehicles = args1.minMax.get(0);
			final int maxVehicles = args1.minMax.get(1);
			if (minVehicles > maxVehicles) {
				ParameterException ex = new ParameterException("Wrong argument format: maxVehicles (" + maxVehicles + ") must be >= than minVehicles (" + minVehicles + ")");
				ex.setJCommander(jc);
				throw ex;
			}
	        if (args1.pError == 0.0 && args1.nSims > 1) {
				ParameterException ex = new ParameterException("Wrong argument format: replications (" + args1.nSims + ") must be 1 when error is 0.0");
				ex.setJCommander(jc);
				throw ex;
			}
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(args1.fileName);
	        if (args1.outputFileName == null) {
				final int pos = args1.fileName.lastIndexOf('.');
				args1.outputFileName = args1.fileName.substring(0, pos) + ".out";
	        }
	        if (plans.length < args1.nSolutions)
	        	args1.nSolutions = plans.length;
	       	new CalculateOptimumSpecificVehiclesExperiment(args1.outputFileName, args1.nSims, plans, args1.nSolutions, minVehicles, maxVehicles, args1.pError, args1.all, args1.first, args1.test).start();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}		
	}

	private static class Arguments {
		@Parameter(names ={"--input", "-i"}, description = "Input \"sol\" file with QCSP solutions", required = true, order = 0)
		private String fileName;
		@Parameter(names ={"--output", "-o"}, description = "Output file name", order = 1)
		private String outputFileName = null;
		
		@Parameter(names ={"--solutions", "-s"}, description = "Number of QCSP solutions to process", order = 2)
		private int nSolutions = 1;
		@Parameter(names ={"--minmax", "-mm"}, arity = 2, description = "Min and max number of delivery vehicles", required=true, order = 1)
		private List<Integer> minMax;
		@Parameter(names ={"--error", "-e"}, description = "Probability of error", order = 3)
		private double pError = 0.0;
		@Parameter(names ={"--replications", "-r"}, description = "Number of replications per solution", order = 4)
		private int nSims = 1;
		@Parameter(names={"--all", "-a"}, description = "Tests all the possible combinations of delivery vehicles")
		private boolean all = false;
		@Parameter(names={"--first", "-f"}, description = "For each number of delivery vehicles, stops when it finds the first solution that reach the objective value")
		private boolean first = false;
		@Parameter(names ={"--test", "-t"}, description = "No simulation: just check")
		private boolean test;
	}
}
