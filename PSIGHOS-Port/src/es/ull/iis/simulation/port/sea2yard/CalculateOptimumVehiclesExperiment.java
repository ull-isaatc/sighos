/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * An experiment to compute the optimum number of vehicles to fulfill a stowage plan schedule. 
 * @author Iván Castilla Rodríguez
 */
public class CalculateOptimumVehiclesExperiment {
	/** Default maximum number of vehicles to test with */
	private static final int LIMIT_VEHICLES = 100;
	/** Default number of vehicle configurations to test when optimum is found. Optimum +- this value configurations are tested */ 
	private static final int NUMBER = 5;
	/** Number of solutions to find optimum for */
	private final int nSolutions;
	/** Set of available solutions */
	private final StowagePlan[] plans;
	/** Enables debug */
	private final boolean debug;
	/** Maximum number of vehicles to test with */
	private final int limitVehicles;
	/** Number of vehicle configurations to test when optimum is found. Optimum +- this value configurations are tested */ 
	private final int plusMinus;

	/**
	 * Creates an experiment to test a single solution
	 * @param plan Solution to test
	 * @param limitVehicles Maximum number of vehicles to test with
	 * @param plusMinus Number of vehicle configurations to test when optimum is found. Optimum +- this value configurations are tested
	 * @param debug Enables debug
	 */
	public CalculateOptimumVehiclesExperiment(StowagePlan plan, int limitVehicles, int plusMinus, boolean debug) {
		this(new StowagePlan[] {plan}, 1, limitVehicles, plusMinus, debug);
	}
	
	/**
	 * Creates an experiment to test nSolutions from plans.
	 * @param plans Set of available solutions
	 * @param nSolutions Set of available solutions
	 * @param limitVehicles Maximum number of vehicles to test with
	 * @param plusMinus Number of vehicle configurations to test when optimum is found. Optimum +- this value configurations are tested
	 * @param debug Enables debug
	 */
	public CalculateOptimumVehiclesExperiment(StowagePlan[] plans, int nSolutions, int limitVehicles, int plusMinus, boolean debug) {
		this.limitVehicles = limitVehicles;
		this.nSolutions = nSolutions;
		this.plusMinus = plusMinus;
		this.plans = plans;
		this.debug = debug;
	}
	
	/**
	 * Searches the optimum number of vehicles required for a specific plan
	 * @param id Solution id
	 * @param plan Stowage plan
	 * @return the optimum number of vehicles required for a specific plan
	 */
	private int getOptimum(int id, StowagePlan plan) {
		PortModel model = null;
		int simCounter = 0;
		long lastObjValue = Long.MAX_VALUE;
		if (debug) {
			System.out.println("Searching optimum for solution " + id + "...");
			// Print header
			printHeader();
		}
		final long objValue = plan.getObjectiveValue() * PortModel.T_OPERATION;
		int nVehicles = (int) (plan.getNCranes() * ((PortModel.T_OPERATION + PortModel.T_TRANSPORT) / PortModel.T_OPERATION));
		int minVehicles = plan.getNCranes();
		int maxVehicles = limitVehicles;
		boolean found = false;
		boolean desist = false;
		final TimeRepository times = new TimeRepository(plan, 0.0);
		while (!found && !desist) {
			model = new PortModel(plan, simCounter, nVehicles, times);
			final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
			model.addInfoReceiver(listener);
			model.run();
			if (debug)
				printResults(simCounter, id, listener, nVehicles);
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
	
	/**
	 * Launches this experiment.
	 */
	public void start() {
		// Print header
		printHeader();
		for (int i = 0; i < nSolutions; i++) {
			final StowagePlan plan = plans[i];
			final TimeRepository times = new TimeRepository(plan, 0.0);
			int opt = getOptimum(i, plan);
			if (debug)
				System.out.println("Solution " + i + ". OPT: " + opt);
			int simCounter = 0;
			for (int nVehicles = Math.max(0, opt - plusMinus); nVehicles <= opt; nVehicles++) {
				PortModel model = new PortModel(plan, simCounter, nVehicles, times);
				final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, TimeUnit.SECOND); 
				model.addInfoReceiver(listener);
				model.run();
				printResults(simCounter, i, listener, nVehicles);
				simCounter++;				
			}
		}
//		PortModel model = null;
//		int simCounter = 0;
//		long lastObjValue = Long.MAX_VALUE;
//		// Print header
//		printHeader();
//		for (int i = 0; i < nSolutions; i++) {
//			final StowagePlan plan = plans[i];
//			final long objValue = plan.getObjectiveValue() * 60;
//			int nVehicles = minVehicles;
//			do {
//				model = new PortModel(plan, simCounter, nVehicles, 0.0);
//				final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, simCounter, TimeUnit.SECOND); 
//				model.addInfoReceiver(listener);
//				model.run();
//				printResults(simCounter, i, listener, nVehicles);
//				lastObjValue = listener.getObjectiveValue();
//				nVehicles++;
//				simCounter++;
//			} while ((nVehicles <= maxVehicles) && (objValue != lastObjValue));
//		}
	}

	private void printHeader() {
		System.out.print("ID\tSOL\tVEHIC\tOBJ");
		for (int i = 1; i <= plans[0].getNCranes(); i++) {
			System.out.print("\tT_TOT" + i + "\tT_USE" + i + "\tT_OP" + i + "\tT_MOV" + i);
		}
		System.out.println();
	}

	private void printResults(final int ind, final int nPlan, final Sea2YardGeneralListener listener, final int nVehicles) {
		System.out.print("" + ind + "\t" + nPlan + "\t" + nVehicles + "\t" + listener.getObjectiveValue());
		for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
			System.out.print("\t" + listener.getObjTime()[i] + "\t" + listener.getUseTime()[i] + "\t" + listener.getOpTime()[i] + "\t" + listener.getMovTime()[i]);
		}
		System.out.println();		
	}
	
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(args1.fileName);
	        if (plans.length < args1.nSolutions)
	        	args1.nSolutions = plans.length;
	        if (args1.specific) {
	        	if (plans.length < args1.nSolutions) {
	        		throw new ParameterException("Trying to simulate solution #" + args1.nSolutions + " when only " + plans.length + " solutions available");
	        	}
	        	else {
	    	       	new CalculateOptimumVehiclesExperiment(plans[args1.nSolutions], args1.limit, args1.number, args1.debug).start();	        		
	        	}
	        }
	        else {
	        	new CalculateOptimumVehiclesExperiment(plans, args1.nSolutions, args1.limit, args1.number, args1.debug).start();
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
		
		@Parameter(names ={"--solutions", "-s"}, description = "Number of QCSP solutions to process", order = 1)
		private int nSolutions = 1;
		@Parameter(names ={"--limit", "-l"}, description = "Maximum number of vehicles to test with", order = 2)
		private int limit = LIMIT_VEHICLES;
		@Parameter(names ={"--number", "-n"}, description = "Number of configurations to test when optimum is found", order = 3)
		private int number = NUMBER;
		@Parameter(names ={"--specific", "-f"}, description = "Changes the way the 'solutions' parameter in interpreted. Now indicates the specific #solution to execute", order = 4)
		private boolean specific = false;
		@Parameter(names ={"--debug", "-d"}, description = "Enables debug mode", order = 5)
		private boolean debug = false;
	}
}
