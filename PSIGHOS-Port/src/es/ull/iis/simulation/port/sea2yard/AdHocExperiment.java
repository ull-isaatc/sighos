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
	private final PortModel.SafetyType safety;
	private final boolean debug; 

	public AdHocExperiment(StowagePlan[] plans, int nSolutions, int nVehicles, PortModel.SafetyType safety, boolean debug) {
		this.nVehicles = nVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.safety = safety;
		this.debug = debug;
	}
	
	public void start() {
		for (int i = 0; i < nSolutions; i++) {
			final StowagePlan plan = plans[i];
			PortModel model = new PortModel(safety, plan, i, nVehicles, 0.0);
			model.addInfoReceiver(new CheckSolutionListener(plan));
			if (debug) {
				model.addInfoReceiver(new ContainerTraceListener(TimeUnit.SECOND));
			}
			model.run();
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
	        if (plans.length < args1.nSolutions)
	        	args1.nSolutions = plans.length;
	        final PortModel.SafetyType safety = (args1.safety == 'f') ? PortModel.SafetyType.FULL : PortModel.SafetyType.OPERATION_ONLY;
	       	new AdHocExperiment(plans, args1.nSolutions, nVehicles, safety, args1.debug).start();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}		
	}

	private static class Arguments {
		@Parameter(names ={"--input", "-i"}, description = "Input \"sol\" file with QCSP solutions", required = true, order = 0)
		private String fileName;
		@Parameter(names ={"--safety", "-f"}, description = "Way of applying safety distance: f for full (default); o for operation only", order = 3)
		private char safety = 'f';
		@Parameter(names ={"--debug", "-d"}, description = "Print debug messages", order = 4)
		private boolean debug;		
		@Parameter(names ={"--solutions", "-s"}, description = "Number of QCSP solutions to process", order = 2)
		private int nSolutions = 1;
		@Parameter(names ={"--vehicles", "-v"}, description = "Number of delivery vehicles", required=true, order = 1)
		private int nVehicles;
	}
}
