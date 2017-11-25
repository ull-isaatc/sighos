package es.ull.iis.simulation.whellChairs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.EnumSet;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.whellChairs.BasicHUNSCsimulation.Density;
import es.ull.iis.simulation.whellChairs.listener.WheelchairListener;

public class CompleteExperiment extends Experiment {
	private enum HospitalSize {
		SMALL(new int[] {1,2}, new int[] {4,5,6,7}, new int[] {2}, new int[] {4,5,6}),
//		MEDIUM(new int[] {3,4,5}, new int[] {10,11,12,13,14}, new int[] {4}, new int[] {8,10,12}),
		MEDIUM(new int[] {3,4}, new int[] {8,9,10,11,12}, new int[] {4}, new int[] {8,9,10}),
		BIG(new int[] {6,8,10}, new int[] {18,20,22,24,26}, new int[] {8}, new int[] {16,20,24});
		
		int []nJanitors;
		int []nChairs;
		int []nDoctors;
		int []patientsXArrival;
		
		private HospitalSize(int[] nJanitors, int[] nChairs, int[] nDoctors, int[] patientsXArrival) {
			this.nJanitors = nJanitors;
			this.nChairs = nChairs;
			this.nDoctors = nDoctors;
			this.patientsXArrival = patientsXArrival;
		}
		
		public int getMaxJanitor() {
			return nJanitors[nJanitors.length - 1];
		}
		public int getMaxChairs() {
			return nChairs[nChairs.length - 1];
		}
		public int getMaxDoctors() {
			return nDoctors[nDoctors.length - 1];
		}
	}
	final static private int DEF_MINUTES_BETWEEN_ARRIVALS = 30;
	final static private int N_EXPERIMENTS = 100;
	final static private String DEF_SIZE = "a";
	final static private BasicHUNSCsimulation.Density[] DEF_DENSITY = {Density.HIGH, Density.HIGH, Density.HIGH};
	private final boolean debug; 
	private final int minutesBetweenArrivals;
	private final EnumSet<HospitalSize> sizes;
	private final BasicHUNSCsimulation.Density[] density;

	/**
	 * Crea un experimento en modo batch que lanzará nExperiments réplicas de la simulación
	 * @param nExperiments
	 */
	public CompleteExperiment(EnumSet<HospitalSize> sizes, int nExperiments, int minutesBetweenArrivals, Density[] density, boolean debug) {
		super("Basic HUNSC Experiment", nExperiments);
		this.debug = debug;
		this.minutesBetweenArrivals = minutesBetweenArrivals;
		this.density = density;
		this.sizes = sizes;
	}

	@Override
	public Simulation getSimulation(int ind) {
		return null;
	}
	
	@Override
	public void start() {
		int ind = 0;
		int maxJanitors = HospitalSize.SMALL.getMaxJanitor();
		int maxDoctors = HospitalSize.SMALL.getMaxDoctors();
		int maxChairs = HospitalSize.SMALL.getMaxChairs();
		if (sizes.contains(HospitalSize.BIG)) {
			maxJanitors = HospitalSize.BIG.getMaxJanitor();
			maxDoctors = HospitalSize.BIG.getMaxDoctors();
			maxChairs = HospitalSize.BIG.getMaxChairs();
		}
		else if (sizes.contains(HospitalSize.MEDIUM)) {
			maxJanitors = HospitalSize.MEDIUM.getMaxJanitor();
			maxDoctors = HospitalSize.MEDIUM.getMaxDoctors();
			maxChairs = HospitalSize.MEDIUM.getMaxChairs();
		}
		WheelchairListener.printHeader(density, maxJanitors, maxDoctors, maxChairs, maxChairs);
		for (HospitalSize size : sizes) {
			System.out.println("Testing " + size);
			for (int n = 0; n < nExperiments; n++) {
				for (int patientsPerArrival : size.patientsXArrival) {
					for (int nDoctors : size.nDoctors) {
						for (int nJanitors : size.nJanitors) {
							for (int nChairs : size.nChairs) {
								for (int nAutoChairs = 0; nAutoChairs <= nChairs; nAutoChairs++) {
									final Simulation sim = new BasicHUNSCsimulation(ind++, density, nJanitors, nDoctors, nAutoChairs, nChairs - nAutoChairs, patientsPerArrival, minutesBetweenArrivals);
									final WheelchairListener listener = new WheelchairListener(TimeUnit.MINUTE, nJanitors, nDoctors, nAutoChairs, nChairs - nAutoChairs, maxJanitors, maxDoctors, maxChairs, maxChairs, patientsPerArrival, minutesBetweenArrivals, density, debug);
									sim.addInfoReceiver(listener);
									sim.run();
								}
							}
						}
					}
				}
				BasicHUNSCsimulation.resetTimeFunctions();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
		PrintStream out = null;
		try {
			JCommander jc = JCommander.newBuilder()
			  .addObject(args1)
			  .build();
			jc.parse(args);
			BasicHUNSCsimulation.Density[] density = DEF_DENSITY;
			if (args1.density != null) {
				if (args1.density.length() != 3) {
					throw new ParameterException("Density must have three components, one per section");
				}
				density = new BasicHUNSCsimulation.Density[3];
				if (!args1.density.matches("[hmnl]{3}")) {
					throw new ParameterException("Density must be expressed as either h (high), m (medium-high), n (medium-low) or l (low)");					
				}
				for (int i = 0; i< 3; i++)
					switch(args1.density.substring(i, i + 1)) {
					case "h": 
						density[i] = Density.HIGH;
						break;
					case "m":
						density[i] = Density.MEDIUM_HIGH;
						break;
					case "n":
						density[i] = Density.MEDIUM_LOW;
						break;
					case "l":
						density[i] = Density.LOW;
						break;
					}
			}
			if (args1.fileName != null) {
				out = new PrintStream(new FileOutputStream(args1.fileName));
				WheelchairListener.setOut(out);
			}
			switch(args1.size) {
			case "a":
				new CompleteExperiment(EnumSet.allOf(HospitalSize.class), args1.nSims, args1.minutesBetweenArrivals, density, args1.debug).start();
				break;
			case "b":
				new CompleteExperiment(EnumSet.of(HospitalSize.BIG), args1.nSims, args1.minutesBetweenArrivals, density, args1.debug).start();
				break;
			case "m":
				new CompleteExperiment(EnumSet.of(HospitalSize.MEDIUM), args1.nSims, args1.minutesBetweenArrivals, density, args1.debug).start();
				break;
			case "s":
				new CompleteExperiment(EnumSet.of(HospitalSize.SMALL), args1.nSims, args1.minutesBetweenArrivals, density, args1.debug).start();
				break;
			default:
				throw new ParameterException("Hospital size must be either s (small), m (medium), b (big) or a (for testing all)");					
			}
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.exit(-1);
		} finally {
			if (args1.fileName != null) {
				out.close();
			}			
		}
	}

	private static class Arguments {
		@Parameter(names ={"--size", "-s"}, description = "Hospital size to test (s: small, m: medium, b: big, a: all)", order = 1)
		private String size = DEF_SIZE;
		@Parameter(names ={"--output", "-o"}, description = "Output file (default: stdout)", order = 2)
		private String fileName = null;
		@Parameter(names ={"--minutes", "-m"}, description = "Minutes between arrival of patients", order = 8)
		private int minutesBetweenArrivals = DEF_MINUTES_BETWEEN_ARRIVALS;
		@Parameter(names ={"--density", "-sd"}, description = "Density of each of the 3 sections: can be any of {h,m,n,l}, e.g. \"hhh\", \"lmh\", etc., where h=high, m=medium-high, n=medium-low, and l=low", order = 9)
		private String density = null;
		@Parameter(names ={"--replications", "-r"}, description = "Number of replications", order = 10)
		private int nSims = N_EXPERIMENTS;
		@Parameter(names ={"--debug", "-d"}, description = "Enables debug mode", order = 11)
		private boolean debug = false;
	}

	
}