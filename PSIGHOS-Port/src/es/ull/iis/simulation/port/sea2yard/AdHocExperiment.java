/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * An experiment intended to check whether the simulation is able to reproduce a set of solutions. The user sets the number of vehicles and how 
 * many solutions to test.  
 * @author Iván Castilla Rodríguez
 *
 */
public class AdHocExperiment {
	/** Number of vehicles to check with */
	private final int nVehicles;
	/** Number of solutions to check */
	private final int nSolutions;
	/** Set of available solutions */
	private final StowagePlan[] plans;
	/** Enables debug */
	private final boolean debug; 

	/**
	 * Creates an experiment to test nSolutions solutions with nVehicles vehicles
	 * @param plans Set of available solutions
	 * @param nSolutions Number of solutions to check
	 * @param nVehicles Number of vehicles to check with
	 * @param debug Enables debug
	 */
	public AdHocExperiment(StowagePlan[] plans, int nSolutions, int nVehicles, boolean debug) {
		this.nVehicles = nVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.debug = debug;
	}
	
	/**
	 * Creates an experiment to test a single solution with nVehicles vehicles
	 * @param plan Solution to test
	 * @param nVehicles Number of vehicles to check with
	 * @param debug Enables debug
	 */
	public AdHocExperiment(StowagePlan plan, int nVehicles, boolean debug) {
		this(new StowagePlan[] {plan}, 1, nVehicles, debug);
	}

	/**
	 * Launches the experiment
	 */
	public void start() {
		final TimeRepository times = new TimeRepository(plans[0], 0.0);
		for (int i = 0; i < nSolutions; i++) {
			final StowagePlan plan = plans[i];
			PortModel model = new PortModel(plan, i, nVehicles, times);
			System.out.println("Testing solution " + i);
			model.addInfoReceiver(new CheckSolutionListener(plan));
			if (debug) {
				System.out.println(plan.getOriginalSolution());
				System.out.println(plan);
				System.out.println(plan.getVessel());
				printSchedule(plan.getVessel());
				System.out.println("Dependencies:");
				for (int craneId = 0; craneId < plan.getNCranes(); craneId++) {
					for (int[] dep : plan.getWaits(craneId)) {
						System.out.println("Crane: " + craneId + "; Prev: " + dep[0] + "; Bay: " + dep[1] + "; Waitfor: " + dep[2]);
					}
				}
				model.addInfoReceiver(new ContainerTraceListener(TimeUnit.SECOND));
			}
			model.run();
		}
	}
	
	/**
	 * Prints the schedule
	 * @param vessel Information about the vessel
	 */
	private void printSchedule(Vessel vessel) {
		System.out.println("Task\tStart\tProc");
		for (int i = 0; i < vessel.getNContainers(); i++) {
			System.out.println(i + "\t" + vessel.getContainerOptStartTime(i) + "\t" + vessel.getContainerProcessingTime(i));
		}
	}
	
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
			final int nVehicles = args1.nVehicles;
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(args1.fileName);
	        if (args1.specific) {
		        if (plans.length < args1.nSolution)
		        	args1.nSolution = plans.length - 1;
		       	new AdHocExperiment(plans[args1.nSolution], nVehicles, args1.debug).start();
	        	
	        }
	        else {
		        if (plans.length < args1.nSolution)
		        	args1.nSolution = plans.length;
		       	new AdHocExperiment(plans, args1.nSolution, nVehicles, args1.debug).start();
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
		@Parameter(names ={"--debug", "-d"}, description = "Print debug messages", order = 4)
		private boolean debug;		
		@Parameter(names ={"--solution", "-s"}, description = "Number of QCSP solutions to process", order = 2)
		private int nSolution = 1;
		@Parameter(names ={"--specific", "-e"}, description = "Specific QCSP solution to process", order = 3)
		private boolean specific = false;
		@Parameter(names ={"--vehicles", "-v"}, description = "Number of delivery vehicles", required=true, order = 1)
		private int nVehicles;
	}
}
