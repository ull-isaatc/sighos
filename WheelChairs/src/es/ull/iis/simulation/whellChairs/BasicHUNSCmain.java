package es.ull.iis.simulation.whellChairs;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.whellChairs.BasicHUNSCsimulation.Density;

public class BasicHUNSCmain extends Experiment {
	final static private int N_JANITORS = 1; //número de bedeles que están en el modelo
	final static private int N_DOCTORS = 2;
	final static private int N_AUTO_CHAIRS = 1;
	final static private int N_MANUAL_CHAIRS = 20;
	final static private BasicHUNSCsimulation.Density[] SECTIONS = {Density.HIGH, Density.HIGH, Density.HIGH};
	public static boolean modeBatch = false; 

	/**
	 * @param nExperiments
	 */
	public BasicHUNSCmain() {
		super("Basic HUNSC Experiment", 1);
	}

	/**
	 * Crea un experimento en modo batch que lanzará nExperiments réplicas de la simulación
	 * @param nExperiments
	 */
	public BasicHUNSCmain(int nExperiments) {
		super("Basic HUNSC Experiment", nExperiments);
		modeBatch = true;
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new BasicHUNSCsimulation(ind, SECTIONS, N_JANITORS, N_DOCTORS, N_AUTO_CHAIRS, N_MANUAL_CHAIRS);
		if (!modeBatch)
			sim.addInfoReceiver(new StdInfoView());
		sim.addInfoReceiver(new TotalPatientTimeView());
		

		return sim;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Runs a single experiment
		
		new BasicHUNSCmain(1).start();
	}

}