/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

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
		if (args.length < 4) {
			System.err.println("Wrong argument number");
			System.exit(-1);
		}
		else {
			final String inputFile = args[0];
			int nSolutions = Integer.parseInt(args[1]);
			final int minVehicles = Integer.parseInt(args[2]);
			final int maxVehicles = Integer.parseInt(args[3]);
			if (minVehicles > maxVehicles) {
				System.err.println("Wrong argument format: maxVehicles (" + maxVehicles + ") must be >= than minVehicles (" + minVehicles + ")");
				System.exit(-1);
			}
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(inputFile);
	        if (plans.length < nSolutions)
	        	nSolutions = plans.length;
	       	new CalculateOptimumVehiclesExperiment(plans, nSolutions, minVehicles, maxVehicles).start();
		}
	}

}
