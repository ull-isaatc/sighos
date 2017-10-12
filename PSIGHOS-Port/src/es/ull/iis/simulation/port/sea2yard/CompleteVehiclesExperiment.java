/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * An experiment to compute the optimum number of vehicles to fulfill a stowage plan schedule. 
 * @author Iván Castilla
 *
 */
public class CompleteVehiclesExperiment {
	private static final int LIMIT_VEHICLES = 100;
	private static final int NUMBER = 5;
	private static final int GAP = 50;
	private final int nSolutions;
	private final StowagePlan[] plans;
	private final boolean debug;
	private final int limitVehicles;
	private final int number;
	private final int nSims;
	private final double pError;
	private final boolean parallel;
	private final String outputFileName;
	private final TimeRepository deterministicTimes;
	private final int[] optimumVehicles;
	private final PrintProgress progress;

	public CompleteVehiclesExperiment(StowagePlan plan, String outputFileName, int limitVehicles, int number, int nSims, double pError, boolean parallel, boolean debug) {
		this(new StowagePlan[] {plan}, outputFileName, 1, limitVehicles, number, nSims, pError, parallel, debug);
	}
	
	public CompleteVehiclesExperiment(StowagePlan[] plans, String outputFileName, int nSolutions, int limitVehicles, int number, int nSims, double pError, boolean parallel, boolean debug) {
		this.limitVehicles = limitVehicles;
		this.nSolutions = nSolutions;
		this.number = number;
		this.plans = plans;
		this.debug = debug;
		this.pError = pError;
		this.nSims = nSims;
		this.parallel = parallel;
		this.outputFileName = outputFileName;
		this.deterministicTimes = new TimeRepository(plans[0], 0.0);
		this.optimumVehicles = new int[nSolutions];
		this.progress = new PrintProgress(GAP);
	}
	
	private int getOptimum(int id, StowagePlan plan) {
		PortModel model = null;
		int simCounter = 0;
		long lastObjValue = Long.MAX_VALUE;
		if (debug) {
			System.out.println("Searching optimum for solution " + id + "...");
		}
		final long objValue = plan.getObjectiveValue() * PortModel.T_OPERATION;
		int nVehicles = (int) (plan.getNCranes() * ((PortModel.T_OPERATION + PortModel.T_TRANSPORT) / PortModel.T_OPERATION));
		int minVehicles = plan.getNCranes();
		int maxVehicles = limitVehicles;
		boolean found = false;
		boolean desist = false;
		while (!found && !desist) {
			model = new PortModel(plan, simCounter, nVehicles, deterministicTimes);
			final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
			model.addInfoReceiver(listener);
			model.run();
			lastObjValue = listener.getObjectiveValue();
			simCounter++;
			if (lastObjValue > objValue) {
				if (nVehicles == limitVehicles) {
					desist = true;
				}
				else {
					if (nVehicles + 1 == maxVehicles) {
						nVehicles = maxVehicles;
						minVehicles = nVehicles;
					}
					else {
						minVehicles = nVehicles;
						nVehicles = (minVehicles + maxVehicles) / 2;
					}
				}
			}
			else if (lastObjValue == objValue) {
				if (nVehicles == minVehicles) {
					found = true;
				}
				else {
					maxVehicles = nVehicles;
					nVehicles = (nVehicles + minVehicles) / 2;
				}
			}
			else {
				System.out.println("WTF?? Best than optimum with " + nVehicles + "vehicles?? " + lastObjValue);
			}
		}
		return nVehicles * (desist ? -1:1);
	}
	
	public void start() {
		long t = System.currentTimeMillis(); 
		System.out.print("Computing optimum number of vehicles");
		if (debug) {
			System.out.println();
		}
		for (int sol = 0; sol < nSolutions; sol ++) {
			final StowagePlan plan = plans[sol];
			optimumVehicles[sol] = getOptimum(sol, plan);
			if (debug) {
				System.out.println("Solution " + sol + ". OPT: " + optimumVehicles[sol]);
			}
			else {
				System.out.print(".");
			}
		}
		if (!debug) {
			System.out.println();
		}
		System.out.println("FINISHED: Computing optimum number of vehicles.");
		if (parallel) {
			final int maxThreads = Runtime.getRuntime().availableProcessors();
			final PrintWriter []files = new PrintWriter[maxThreads]; 
			if (outputFileName == null) {
				for (int i = 0; i < maxThreads; i++) {
					files[i] = new PrintWriter(System.out);
				}
			}
			else {
				final int pos = outputFileName.lastIndexOf('.');
				final String nameAux;
				final String ext; 
				if (pos > 0) {
					nameAux = outputFileName.substring(0, pos);
					ext = outputFileName.substring(pos);
				}
				else {
					nameAux = outputFileName;
					ext = "";
				}
				for (int i = 0; i < maxThreads; i++) {
					try {
						files[i] = new PrintWriter(new BufferedWriter(new FileWriter(nameAux + "_" + i + ext)));
					} catch (IOException e) {
						e.printStackTrace();
						files[i] = new PrintWriter(System.out);
					}
				}
			}
			try {
				final Thread[] workers = new Thread[maxThreads];
				for (int i = 0; i < maxThreads; i++) {
					workers[i] = new Thread(new ProblemExecutor(files[i], i, maxThreads));
					workers[i].start();
				}
				for (int i = 0; i < maxThreads; i++) {
					workers[i].join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			PrintWriter out;
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
			new ProblemExecutor(out, 0, 1).run();
		}
		System.out.println("Execution time: " + ((System.currentTimeMillis() - t) / 1000) + " sec");
	}
	
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
			// If we have to process input files in batch mode...
			if (args1.batch) {
			    BufferedReader br = null;
			    String solFile = null;
				try {
					br = new BufferedReader(new InputStreamReader(new FileInputStream(args1.fileName)));
					solFile = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (br != null) {
					while (solFile != null) {
					    try {
							System.out.println("PROCESSING: " + solFile);
						    final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(solFile);
						    if (plans.length < args1.nSolutions)
						    	args1.nSolutions = plans.length;
							final int pos = solFile.lastIndexOf('.');
						   	new CompleteVehiclesExperiment(plans, solFile.substring(0, pos) + ".out", args1.nSolutions, args1.limit, args1.number, args1.nSims, args1.pError, args1.parallel, args1.debug).start();
						} catch (Exception e) {
							System.err.println("Error processing experiment: " + solFile);
						}
					    // Read next solutions file
						try {
							solFile = br.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else {
		        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(args1.fileName);
		        if (args1.outputFileName == null) {
					final int pos = args1.fileName.lastIndexOf('.');
					args1.outputFileName = args1.fileName.substring(0, pos) + ".out";
		        }
		        if (args1.specific) {
		        	if (plans.length < args1.nSolutions) {
		        		throw new ParameterException("Trying to simulate solution #" + args1.nSolutions + " when only " + plans.length + " solutions available");
		        	}
		        	else {
				       	new CompleteVehiclesExperiment(plans[args1.nSolutions], args1.outputFileName, args1.limit, args1.number, args1.nSims, args1.pError, args1.parallel, args1.debug).start();
		        	}
		        }
		        else {
			        if (plans.length < args1.nSolutions)
			        	args1.nSolutions = plans.length;
			       	new CompleteVehiclesExperiment(plans, args1.outputFileName, args1.nSolutions, args1.limit, args1.number, args1.nSims, args1.pError, args1.parallel, args1.debug).start();
		        }
			}
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
		@Parameter(names ={"--limit", "-l"}, description = "Maximum number of vehicles to test with", order = 3)
		private int limit = LIMIT_VEHICLES;
		@Parameter(names ={"--number", "-n"}, description = "Number of configurations to test when optimum is found", order = 4)
		private int number = NUMBER;
		@Parameter(names ={"--error", "-e"}, description = "Probability of error", order = 6)
		private double pError = 0.25;
		@Parameter(names ={"--replications", "-r"}, description = "Number of replications per solution", order = 5)
		private int nSims = 2;
		@Parameter(names ={"--debug", "-d"}, description = "Enables debug mode", order = 8)
		private boolean debug = false;
		@Parameter(names ={"--specific", "-f"}, description = "Changes the way the 'solutions' parameter in interpreted. Now indicates the specific #solution to execute", order = 10)
		private boolean specific = false;
		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution using the maximum available processors", order = 7)
		private boolean parallel = false;
		@Parameter(names ={"--batch", "-b"}, description = "A plain text file with file names must be provided as input file. Each file name is a set of QSCP solutions"
				+ " and they are processed in batch mode.", order = 9)
		private boolean batch = false;
	}
	
	private class ProblemExecutor implements Runnable {
		final private PrintWriter out;
		final private int id;
		final private int maxThreads;
	
		public ProblemExecutor(PrintWriter out, int id, int maxThreads) {
			this.out = out;
			this.id = id;
			this.maxThreads = maxThreads;
		}

		@Override
		public void run() {
			System.out.println("Launched thread " + id);
			// Print header
			printHeader();
			// Main executor launches deterministic simulations
			if (id == 0) {
				System.out.println("Computing deterministic solutions");
				for (int sol = 0; sol < nSolutions; sol ++) {
					final StowagePlan plan = plans[sol];
					int minVehicles = Math.max(0, optimumVehicles[sol] - number);
					int simCounter = 0;
					Sea2YardGeneralListener listener = null;
					for (int nVehicles = minVehicles; nVehicles <= optimumVehicles[sol]; nVehicles++) {
						final PortModel model = new PortModel(plan, simCounter, nVehicles, deterministicTimes);
						listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
						model.addInfoReceiver(listener);
						model.run();
						printResults(0, sol, listener, nVehicles, 0.0, plan.getOverlap());
						simCounter++;				
						System.out.print(".");
					}
					// Fill the results up to max vehicles
					for (int nVehicles = optimumVehicles[sol] + 1; nVehicles <= optimumVehicles[sol] + number; nVehicles++) {
						printResults(0, sol, listener, nVehicles, 0.0, plan.getOverlap());
					}
				}				
				System.out.println();
				System.out.println("FINISHED: Computing deterministic solutions");
			}
			System.out.println("Computing probabilistic solutions");
			for (int sim = id; sim < nSims; sim += maxThreads) {
				final TimeRepository times = new TimeRepository(plans[0], pError);
				for (int sol = 0; sol < nSolutions; sol ++) {
					final StowagePlan plan = plans[sol];
					int minVehicles = Math.max(0, optimumVehicles[sol] - number);
					int maxVehicles = optimumVehicles[sol] + number;
					PortModel model = new PortModel(plan, sim + 1, minVehicles, times);
					Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
					model.addInfoReceiver(listener);
					model.run();
					printResults(sim + 1, sol, listener, minVehicles, pError, plan.getOverlap());
					long lastObjValue = listener.getObjectiveValue();
					int nVehicles = minVehicles + 1;
					for (; nVehicles <= maxVehicles; nVehicles++) {
						model = new PortModel(plan, sim + 1, nVehicles, times);
						listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
						model.addInfoReceiver(listener);
						model.run();
						printResults(sim + 1, sol, listener, nVehicles, pError, plan.getOverlap());
						// Do not continue simulating if no further improvement is achieved
						if (lastObjValue == listener.getObjectiveValue())
							break;
						lastObjValue = listener.getObjectiveValue();
					}
					nVehicles++;
					// Fill the results up to max vehicles
					for (; nVehicles <= maxVehicles; nVehicles++) {
						printResults(sim + 1, sol, listener, nVehicles, pError, plan.getOverlap());
					}
					progress.print();
				}
			}
			System.out.println();
			System.out.println("FINISHED: Computing probabilistic solutions");
			out.flush();
			out.close();
		}

		private void printHeader() {
			out.print("ID\tSOL\tOVERLAP\tVEHIC\tERROR\tOBJ");
			for (int i = 1; i <= plans[0].getNCranes(); i++) {
				out.print("\tT_TOT" + i + "\tT_USE" + i + "\tT_OP" + i + "\tT_MOV" + i);
			}
			out.println();
		}

		private void printResults(final int ind, final int nPlan, final Sea2YardGeneralListener listener, final int nVehicles, final double pError, final double overlap) {
			out.print("" + ind + "\t" + nPlan + "\t" + overlap + "\t" + nVehicles + "\t" + pError + "\t" + listener.getObjectiveValue());
			for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
				out.print("\t" + listener.getObjTime()[i] + "\t" + listener.getUseTime()[i] + "\t" + listener.getOpTime()[i] + "\t" + listener.getMovTime()[i]);
			}
			out.println();		
		}
	}
	
	private class PrintProgress {
		final private int totalSim;
		final private int gap;
		private AtomicInteger counter;
		
		public PrintProgress(int gap) {
			this.totalSim = nSolutions * nSims;
			this.gap = gap;
			this.counter = new AtomicInteger();
		}
		
		public void print() {
			if (counter.incrementAndGet() % gap == 0)
				System.out.println("" + (counter.get() * 100 / totalSim) + "% finished");
		}
	}
}
