package es.ull.iis.simulation.whellChairs;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

//Archivo main para el caso MANUAL

public class BasicHUNSCmainManual extends Experiment {
	public static boolean modeBatch = false; 

	/**
	 * @param nExperiments
	 */
	public BasicHUNSCmainManual() {
		super("Basic HUNSC Experiment", 1);
	}

	/**
	 * Crea un experimento en modo batch que lanzará nExperiments réplicas de la simulación
	 * @param nExperiments
	 */
	public BasicHUNSCmainManual(int nExperiments) {
		super("Basic HUNSC Experiment", nExperiments);
		modeBatch = true;
	}

	@Override
	public Simulation getSimulation(int ind) {
		// Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes)
		Simulation sim = new BasicHUNSCsimulationManual(ind, "HUNSC" + ind, TimeUnit.SECOND, 0, 7 * 24 * 60 * 60);
		if (!modeBatch)
			sim.addInfoReceiver(new StdInfoView());
		sim.addInfoReceiver(new TotalPatientTimeViewManual());
		sim.addInfoReceiver(new TotalResourceTimeViewManual());
		

		return sim;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Runs a single experiment
		
		new BasicHUNSCmainManual(1).start();
	}

}