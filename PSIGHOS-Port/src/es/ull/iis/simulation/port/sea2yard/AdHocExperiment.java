/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class AdHocExperiment {
	private final int nVehicles;
	private final int nSolutions;
	private final StowagePlan[] plans;
	private final boolean debug; 

	public AdHocExperiment(StowagePlan[] plans, int nSolutions, int nVehicles, boolean debug) {
		this.nVehicles = nVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.debug = debug;
	}
	
	public AdHocExperiment(StowagePlan plan, int nVehicles, boolean debug) {
		this.nVehicles = nVehicles;
		this.nSolutions = 1;
		this.plans = new StowagePlan[] {plan};
		this.debug = debug;
	}
	
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
