/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class CalculateOptimumSpecificVehiclesExperiment {
	private final int minVehicles;
	private final int maxVehicles;
	private final int nSolutions;
	private final StowagePlan[] plans;
	private final boolean simulateAll;
	private int simCounter;

	public CalculateOptimumSpecificVehiclesExperiment(StowagePlan[] plans, int nSolutions, int minVehicles, int maxVehicles, boolean simulateAll) {
		this.minVehicles = minVehicles;
		this.maxVehicles = maxVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.simulateAll = simulateAll;
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

	private void simulate(int solution, int[]nVehicles) {
		System.out.print(simCounter);
		for (int i = 0; i < nVehicles.length; i++)
			System.out.print("\t" + nVehicles[i]);
		System.out.println();
//		final StowagePlan plan = plans[solution];
//		final PortModel model = new PortModel(plan, simCounter, nVehicles, 0.0);
//		final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, simCounter, TimeUnit.SECOND); 
//		model.addInfoReceiver(listener);
//		model.run();
//		printResults(simCounter, solution, listener, nVehicles);
		simCounter++;		
	}

	public void simulateNVehicles(int solution, int[]nVehicles, int level, int leftVehicles) {
		if (level == 0) {
			nVehicles[level] = leftVehicles;
			simulate(solution, nVehicles);
		}
		else {
			nVehicles[level] = leftVehicles;
			Arrays.fill(nVehicles, 0, level, 0);
			simulate(solution, nVehicles);
			for (int n = leftVehicles - 1; n >= 0; n--) {
				nVehicles[level] = n;
				simulateNVehicles(solution, nVehicles, level - 1, leftVehicles - n);
			}
		}
	}
	
	public void simulateNVehiclesAll(int solution, int[]nVehicles, int level, int leftVehicles) {
		final int maxLevel = nVehicles.length - 1;
		if (level == maxLevel) {
			nVehicles[level] = leftVehicles;
			simulate(solution, nVehicles);
		}
		else {
			nVehicles[level] = leftVehicles;
			Arrays.fill(nVehicles, level+1, maxLevel+1, 0);
			simulate(solution, nVehicles);
			for (int n = leftVehicles - 1; n >= 0; n--) {
				nVehicles[level] = n;
				simulateNVehicles(solution, nVehicles, level + 1, leftVehicles - n);
			}
		}
	}
	
	public void start() {
		// Print header
		printHeader();
		if (simulateAll) {
			for (int i = 0; i < nSolutions; i++) {
				final StowagePlan plan = plans[i];
				for (int nVehicles = minVehicles; nVehicles <= maxVehicles; nVehicles++) {
					simulateNVehicles(i, new int[plan.getNCranes() + 1], plan.getNCranes(), nVehicles);					
				}
			}
		}
		else {
			for (int i = 0; i < nSolutions; i++) {
				final StowagePlan plan = plans[i];
				for (int nVehicles = minVehicles; nVehicles <= maxVehicles; nVehicles++) {
					int nSpecificVehicles = 0;
					int [] arrayVehicles = computeNVehicles(plan.getNCranes(), nVehicles, nSpecificVehicles++);
					do {
						simulate(i, arrayVehicles);
						arrayVehicles = computeNVehicles(plan.getNCranes(), nVehicles, nSpecificVehicles++);
					} while (arrayVehicles != null);
				}
			}
		}
	}

	private void printHeader() {
		System.out.print("ID\tSOL\tVEHIC");
		for (int i = 1; i <= plans[0].getNCranes(); i++) {
			System.out.print("\tVEHIC" + i);
		}
		System.out.print("\tOBJ");
		for (int i = 1; i <= plans[0].getNCranes(); i++) {
			System.out.print("\tT_TOT" + i + "\tT_USE" + i + "\tT_OP" + i + "\tT_MOV" + i);
		}
		System.out.println();
	}

	private void printResults(final int ind, final int nPlan, final Sea2YardGeneralListener listener, final int[] nVehicles) {
		System.out.print("" + ind + "\t" + nPlan + "\t" + nVehicles[nVehicles.length - 1]);
		if (nVehicles.length > 1) {
			for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
				System.out.print("\t" + nVehicles[i]);
			}
		}
		else {
			for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
				System.out.print("\t0");
			}			
		}
		System.out.print("\t" + listener.getObjectiveValue());
		for (int i = 0; i < plans[nPlan].getNCranes(); i++) {
			System.out.print("\t" + listener.getObjTime()[i] + "\t" + listener.getUseTime()[i] + "\t" + listener.getOpTime()[i] + "\t" + listener.getMovTime()[i]);
		}
		System.out.println();		
	}
	
	public static void main(String[] args) {
//        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile("C:\\Users\\Iván Castilla\\Dropbox\\SimulationPorts\\for_simulation\\k40.sol");
//        int[] nVehicles = new int[] {2,2,2,6};
//		PortModel model = new PortModel(plans[0], 0, nVehicles, 0.0);
//		final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plans[0], 0, TimeUnit.SECOND); 
//		model.addInfoReceiver(listener);
//		model.run();
//		printResults(0, 0, listener, nVehicles);
//		simulateNVehicles(0, new int[4], 0, 10);
		if (args.length < 4) {
			System.err.println("Wrong argument number");
			System.exit(-1);
		}
		else {
			final String inputFile = args[0];
			int nSolutions = Integer.parseInt(args[1]);
			final int minVehicles = Integer.parseInt(args[2]);
			final int maxVehicles = Integer.parseInt(args[3]);
			boolean all = false;
			if (args.length > 4) {
				if (args[4].equalsIgnoreCase("-a") || args[4].equalsIgnoreCase("--all")) {
					all = true;
				}
			}
			if (minVehicles > maxVehicles) {
				System.err.println("Wrong argument format: maxVehicles (" + maxVehicles + ") must be >= than minVehicles (" + minVehicles + ")");
				System.exit(-1);
			}
	        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(inputFile);
	        if (plans.length < nSolutions)
	        	nSolutions = plans.length;
	       	new CalculateOptimumSpecificVehiclesExperiment(plans, nSolutions, minVehicles, maxVehicles, all).start();
		}
	}

}
