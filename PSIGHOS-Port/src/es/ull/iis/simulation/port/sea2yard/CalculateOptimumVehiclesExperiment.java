/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.List;

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
public class CalculateOptimumVehiclesExperiment {
	private final int minVehicles;
	private final int maxVehicles;
	private final int nSolutions;
	private final StowagePlan[] plans;

	public CalculateOptimumVehiclesExperiment(StowagePlan[] plans, int nSolutions, int minVehicles, int maxVehicles) {
		this.minVehicles = minVehicles;
		this.maxVehicles = maxVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
	}
	
	public void start() {
		PortModel model = null;
		int simCounter = 0;
		long lastObjValue = Long.MAX_VALUE;
		// Print header
		printHeader();
		for (int i = 0; i < nSolutions; i++) {
			final StowagePlan plan = plans[i];
			final long objValue = plan.getObjectiveValue() * 60;
			int nVehicles = minVehicles;
			do {
				model = new PortModel(plan, simCounter, nVehicles, 0.0);
				final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, simCounter, TimeUnit.SECOND); 
				model.addInfoReceiver(listener);
				model.run();
				printResults(simCounter, i, listener, nVehicles);
				lastObjValue = listener.getObjectiveValue();
				nVehicles++;
				simCounter++;
			} while ((nVehicles <= maxVehicles) && (objValue != lastObjValue));
		}
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
			final int minVehicles = args1.minMax.get(0);
			final int maxVehicles = args1.minMax.get(1);
			if (minVehicles > maxVehicles) {
				ParameterException ex = new ParameterException("Wrong argument format: maxVehicles (" + maxVehicles + ") must be >= than minVehicles (" + minVehicles + ")");
				ex.setJCommander(jc);
				throw ex;
			}
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(args1.fileName);
	        if (plans.length < args1.nSolutions)
	        	args1.nSolutions = plans.length;
	       	new CalculateOptimumVehiclesExperiment(plans, args1.nSolutions, minVehicles, maxVehicles).start();
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}		
	}

	private static class Arguments {
		@Parameter(names ={"--input", "-i"}, description = "Input \"sol\" file with QCSP solutions", required = true, order = 0)
		private String fileName;
		
		@Parameter(names ={"--solutions", "-s"}, description = "Number of QCSP solutions to process", order = 2)
		private int nSolutions = 1;
		@Parameter(names ={"--minmax", "-mm"}, arity = 2, description = "Min and max number of delivery vehicles", required=true, order = 1)
		private List<Integer> minMax;
	}
}
