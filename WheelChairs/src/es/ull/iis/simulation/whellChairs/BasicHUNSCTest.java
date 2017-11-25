package es.ull.iis.simulation.whellChairs;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.whellChairs.BasicHUNSCsimulation.Density;
import es.ull.iis.simulation.whellChairs.listener.WheelchairListener;

public class BasicHUNSCTest {
	final static private int DEF_N_JANITORS = 1; //número de bedeles que están en el modelo
	final static private int DEF_N_DOCTORS = 2;
	final static private int DEF_N_CHAIRS = 6;
	final static private int DEF_N_PATIENTS_PER_ARRIVAL = 4;
	final static private int DEF_MINUTES_BETWEEN_ARRIVALS = 30;
	final static private int N_EXPERIMENTS = 5;
	final static private BasicHUNSCsimulation.Density[] DEF_DENSITY = {Density.HIGH, Density.HIGH, Density.HIGH};

	/**
	 * Crea un experimento en modo batch que lanzará nExperiments réplicas de la simulación
	 * @param nExperiments
	 */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WheelchairListener.printHeader(DEF_DENSITY, DEF_N_JANITORS, DEF_N_DOCTORS, DEF_N_CHAIRS, 0);
		for (int i = 0; i < N_EXPERIMENTS; i++) {
			for (int j = 0; j < 2; j++) {
				Simulation sim = new BasicHUNSCsimulation(i, DEF_DENSITY, DEF_N_JANITORS, DEF_N_DOCTORS, DEF_N_CHAIRS + j, 0, DEF_N_PATIENTS_PER_ARRIVAL, DEF_MINUTES_BETWEEN_ARRIVALS);
	//			if (debug)
	//				sim.addInfoReceiver(new StdInfoView());
				final WheelchairListener listener = new WheelchairListener(TimeUnit.MINUTE, DEF_N_JANITORS, DEF_N_DOCTORS, DEF_N_CHAIRS + j, 0, DEF_N_JANITORS, DEF_N_DOCTORS, DEF_N_CHAIRS + j, 0, DEF_N_PATIENTS_PER_ARRIVAL, DEF_MINUTES_BETWEEN_ARRIVALS, DEF_DENSITY, false);
				sim.addInfoReceiver(listener);
				sim.run();
			}
			BasicHUNSCsimulation.resetTimeFunctions();
		}
		WheelchairListener.printLegend();
	}

	
}