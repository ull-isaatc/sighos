/**
 * 
 */
package es.ull.iis.simulation.examples.hospital;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

/**
 * Main class to execute the "hospital" tutorial example.
 * 
 * The "hospital" is simply a set of operating theatres and consultation rooms. 
 * Patients arrive at the hospital to be seen by a doctor. If the doctor decides so, a surgical team operates on the patient. 
 * After the surgical intervention, the doctor assesses the result of the intervention. 
 * In case the intervention failed, the surgical team reoperates on the patient, and further reassessments are performed.
 * 
 * The hospital has 6 doctors (3 of them are also surgeons), and 3 nurses. Doctors and nurses work from 8:00 to 15:00. 
 * Surgical interventions are scheduled from 11:00 to 14:00.
 * 
 * 5% of the patients that attend to an appointment require surgery, independently of the previous history of the patient.
 * 
 * Surgical teams require 2 surgeons and 1 nurse. In some cases, only 1 surgeon and 1 nurse may carry out the surgical 
 * intervention, but only by incrementing its duration.
 *  
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class BasicHospitalMain extends Experiment {

	/**
	 * @param nExperiments
	 */
	public BasicHospitalMain(int nExperiments) {
		super("Basic Hospital Experiment", nExperiments);
	}

	@Override
	public Simulation getSimulation(int ind) {
		// Creates a simulation based on minutes, which lasts for a week (7 days X 24 hours X 60 minutes)
		Simulation sim = new BasicHospitalSimulation(ind, "Hospital" + ind, TimeUnit.MINUTE, 0, 7 * 24 * 60);
		sim.addInfoReceiver(new StdInfoView(sim));

		return sim;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Runs a single experiment
		new BasicHospitalMain(1).start();
	}

}
