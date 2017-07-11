/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.Arrays;
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
public class CalculateOptimumSpecificVehiclesExperiment {
	private final int minVehicles;
	private final int maxVehicles;
	private final int nSolutions;
	private final StowagePlan[] plans;
	private final boolean simulateAll;
	private final boolean test;
	private int simCounter;

	public CalculateOptimumSpecificVehiclesExperiment(StowagePlan[] plans, int nSolutions, int minVehicles, int maxVehicles, boolean simulateAll, boolean test) {
		this.minVehicles = minVehicles;
		this.maxVehicles = maxVehicles;
		this.nSolutions = nSolutions;
		this.plans = plans;
		this.simulateAll = simulateAll;
		this.test = test;
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
		if (test) {
			System.out.print(simCounter);
			for (int i = 0; i < nVehicles.length; i++)
				System.out.print("\t" + nVehicles[i]);
			System.out.println();
		}
		else {
			final StowagePlan plan = plans[solution];
			final PortModel model = new PortModel(plan, simCounter, nVehicles, 0.0);
			final Sea2YardGeneralListener listener = new Sea2YardGeneralListener(plan, simCounter, TimeUnit.SECOND); 
			model.addInfoReceiver(listener);
			model.run();
			printResults(simCounter, solution, listener, nVehicles);
		}
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
	
	public void simulateNVehiclesNonZero(int solution, int[]nVehicles, int level, int leftVehicles) {
		if (level == 0) {
			nVehicles[level] = leftVehicles;
			simulate(solution, nVehicles);
		}
		else {
			nVehicles[level] = leftVehicles - level;
			Arrays.fill(nVehicles, 0, level, 1);
			simulate(solution, nVehicles);
			for (int n = leftVehicles - level - 1; n >= 1; n--) {
				nVehicles[level] = n;
				simulateNVehiclesNonZero(solution, nVehicles, level - 1, leftVehicles - n);
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
					for (int n = nVehicles; n > 0; n--) {
						final int[] arrayVehicles = new int[plan.getNCranes() + 1];
						arrayVehicles[plan.getNCranes()] = n;
						simulateNVehicles(i, arrayVehicles, plan.getNCranes() - 1, nVehicles - n);					
					}
					simulateNVehiclesNonZero(i, new int[plan.getNCranes() + 1], plan.getNCranes() - 1, nVehicles);					
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
	       	new CalculateOptimumSpecificVehiclesExperiment(plans, args1.nSolutions, minVehicles, maxVehicles, args1.all, args1.test).start();
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
		@Parameter(names={"--all", "-a"}, description = "Tests all the possible combinations of delivery vehicles")
		private boolean all = false;
		@Parameter(names ={"--test", "-t"}, description = "No simulation: just check")
		private boolean test;
	}
}
