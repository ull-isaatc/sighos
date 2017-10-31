package es.ull.iis.simulation.whellChairs;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;

public class BasicHUNSCmainAuto extends Experiment {

	/**
	 * @param nExperiments
	 */
	public BasicHUNSCmainAuto(int nExperiments) {
		super("Basic HUNSC Experiment", nExperiments);
	}

	@Override
	public Simulation getSimulation(int ind) {
		// Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes)
		Simulation sim = new BasicHUNSCsimulationAuto(ind, "HUNSC Auto" + ind, TimeUnit.MINUTE, 0, 7 * 24 * 60);
		sim.addInfoReceiver(new StdInfoView());
		sim.addInfoReceiver(new TotalPatientTimeViewAuto());
		sim.addInfoReceiver(new TotalResourceTimeViewAuto());

		return sim;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Runs a single experiment
		
		new BasicHUNSCmainAuto(1).start();
	}

}