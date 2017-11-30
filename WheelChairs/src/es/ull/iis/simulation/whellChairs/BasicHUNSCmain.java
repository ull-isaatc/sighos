package es.ull.iis.simulation.whellChairs;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.whellChairs.BasicHUNSCsimulation.Density;
import es.ull.iis.simulation.whellChairs.listener.WheelchairListener;

public class BasicHUNSCmain extends Experiment {
	final static private int DEF_N_JANITORS = 1; //número de bedeles que están en el modelo
	final static private int DEF_N_DOCTORS = 2;
	final static private int DEF_N_CHAIRS = 6;
	final static private int DEF_N_PATIENTS_PER_ARRIVAL = 2;
	final static private int DEF_MINUTES_BETWEEN_ARRIVALS = 1030;
	final static private int N_EXPERIMENTS = 1;
	final static private BasicHUNSCsimulation.Density[] DEF_DENSITY = {Density.HIGH, Density.HIGH, Density.HIGH};
	static int maxAutoChairs;
	static int maxManualChairs; 
	private final boolean debug; 
	private final int nJanitors;
	private final int nDoctors;
	private final int nAutoChairs;
	private final int nManualChairs;
	private final int patientsPerArrival;
	private final int minutesBetweenArrivals;
	private final BasicHUNSCsimulation.Density[] density;

	/**
	 * Crea un experimento en modo batch que lanzará nExperiments réplicas de la simulación
	 * @param nExperiments
	 */
	public BasicHUNSCmain(int nExperiments, int nJanitors, int nDoctors, int nAutoChairs, int nManualChairs, int patientsPerArrival, int minutesBetweenArrivals, Density[] density, boolean debug) {
		super("Basic HUNSC Experiment", nExperiments);
		this.debug = debug;
		this.nJanitors = nJanitors;
		this.nDoctors = nDoctors;
		this.nAutoChairs = nAutoChairs;
		this.nManualChairs = nManualChairs;
		this.patientsPerArrival = patientsPerArrival;
		this.minutesBetweenArrivals = minutesBetweenArrivals;
		this.density = density;
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new BasicHUNSCsimulation(ind, density, nJanitors, nDoctors, nAutoChairs, nManualChairs, patientsPerArrival, minutesBetweenArrivals);
//		if (debug)
			sim.addInfoReceiver(new StdInfoView());
		final WheelchairListener listener = new WheelchairListener(TimeUnit.MINUTE, nJanitors, nDoctors, nAutoChairs, nManualChairs, nJanitors, nDoctors, maxAutoChairs, maxManualChairs, patientsPerArrival, minutesBetweenArrivals, density, debug);
		sim.addInfoReceiver(listener);
		return sim;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Arguments args1 = new Arguments();
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
			
			switch(args1.nChairs.size()) {
			case 2:
				// Runs a single experiment			
				WheelchairListener.printHeader(density, args1.nJanitors, args1.nDoctors, args1.nChairs.get(0), args1.nChairs.get(1));
				maxAutoChairs = args1.nChairs.get(0);
				maxManualChairs = args1.nChairs.get(1);
				new BasicHUNSCmain(args1.nSims, args1.nJanitors, args1.nDoctors, args1.nChairs.get(0), args1.nChairs.get(1), args1.nPatients, args1.minutesBetweenArrivals, density, args1.debug).start();
				WheelchairListener.printLegend();
				break;
			case 1:
				WheelchairListener.printHeader(density, args1.nJanitors, args1.nDoctors, args1.nChairs.get(0), args1.nChairs.get(0));
				maxAutoChairs = args1.nChairs.get(0);
				maxManualChairs = args1.nChairs.get(0);
				for (int i = 0; i <= args1.nChairs.get(0); i++) {
					new BasicHUNSCmain(args1.nSims, args1.nJanitors, args1.nDoctors, i, args1.nChairs.get(0) - i, args1.nPatients, args1.minutesBetweenArrivals, density, args1.debug).start();
				}
				WheelchairListener.printLegend();
				break;
			case 0:
				WheelchairListener.printHeader(density, args1.nJanitors, args1.nDoctors, DEF_N_CHAIRS, DEF_N_CHAIRS);
				maxAutoChairs = DEF_N_CHAIRS;
				maxManualChairs = DEF_N_CHAIRS;
				for (int i = 0; i <= DEF_N_CHAIRS; i++) {
					new BasicHUNSCmain(args1.nSims, args1.nJanitors, args1.nDoctors, i, DEF_N_CHAIRS - i, args1.nPatients, args1.minutesBetweenArrivals, density, args1.debug).start();
				}
				WheelchairListener.printLegend();
				break;
			default:
				throw new ParameterException("Number of chairs must be unique, or a pair <automated, manual>");								
			}
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			ex.usage();
			System.exit(-1);
		}
	}

	private static class Arguments {
		@Parameter(names ={"--janitors", "-nj"}, description = "Number of janitors", order = 2)
		private int nJanitors = DEF_N_JANITORS;
		@Parameter(names ={"--doctors", "-nd"}, description = "Number of doctors", order = 3)
		private int nDoctors = DEF_N_DOCTORS;
		@Parameter(names ={"--wheelchairs", "-nw"}, variableArity = true, description = "Number of wheelchairs. Can be two numbers (automated, manual), or only one (it tests from 0 to N automated wheelchairs, keeping the rest manual)", order = 6)
		private List<Integer> nChairs = new ArrayList<>();;
		@Parameter(names ={"--patients", "-np"}, description = "Patiens per arrival (as defined by the -t parameter)", order = 7)
		private int nPatients = DEF_N_PATIENTS_PER_ARRIVAL;
		@Parameter(names ={"--minutes", "-m"}, description = "Minutes between arrival of patients", order = 8)
		private int minutesBetweenArrivals = DEF_MINUTES_BETWEEN_ARRIVALS;
		@Parameter(names ={"--density", "-sd"}, description = "Density of each of the 3 sections: can be any of {h,m,n,l}, e.g. \"hhh\", \"lmh\", etc., where h=high, m=medium-high, n=medium-low, and l=low", order = 9)
		private String density = null;
		@Parameter(names ={"--replications", "-r"}, description = "Number of replications", order = 10)
		private int nSims = N_EXPERIMENTS;
		@Parameter(names ={"--debug", "-d"}, description = "Enables debug mode", order = 11)
		private boolean debug = false;

//		@Parameter(names ={"--parallel", "-p"}, description = "Enables parallel execution using the maximum available processors", order = 7)
//		private boolean parallel = false;
//		@Parameter(names ={"--batch", "-b"}, description = "A plain text file with file names must be provided as input file. Each file name is a set of QSCP solutions"
//				+ " and they are processed in batch mode.", order = 9)
//		private boolean batch = false;
	}
	
	
}