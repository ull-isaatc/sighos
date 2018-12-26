/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.ReseteableParam;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * An abstract class to represent acute complications. Acute complications are characterized by an annual
 * probability of developing the complication, a relative risk of development depending on characteristics
 * of the patient or the intervention, and a probability of dying from the complication.
 * A patient can suffer several acute events during his/her life. Hence, this class stores all the predictions. 
 * An event can be cancelled for any reason, so the random number used to generate the event is reused.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class AcuteComplicationSubmodel extends ComplicationSubmodel implements ReseteableParam<AcuteComplicationSubmodel.Progression> {
	/**
	 * The progression of an acute event, i.e., when it's predicted to happen and whether it will be mortal.
	 * @author Iván Castilla Rodríguez
	 */
	public static class Progression {
		/** Time to development of the acute complication */
		public final long timeToEvent;
		/** If true, the event will produce the death of the patient */
		public final boolean causesDeath;
		
		public Progression(long timeToEvent, boolean causesDeath) {
			this.timeToEvent = timeToEvent;
			this.causesDeath = causesDeath;
		}
	}
	/**	Random numbers used to estimate the time to events */
	private final RandomNumber rng;
	/** The list of generated random numbers for each patient. Each entry is a pair &lt;random number for time to event, random number for mortality&gt; */
	private final ArrayList<ArrayList<double[]>> generated;

	/** Annual probability of developing the acute complication */
	private final double annualProb;
	/** Relative risk of developing the acute complication, depending on characteristics of the patient or the intervention */
	private final ComplicationRR rr;
	/** Probability of dying when an acute event appears */
	private final double pDeath;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;

	/**
	 * Creates a submodel for an acute complication. 
	 * @param nPatients Number of patient that will be created
	 * @param annualProb Annual probability of developing the acute complication
	 * @param rr Relative risk of developing the acute complication, depending on characteristics of the patient or the intervention
	 * @param pDeath Probability of dying when an acute event appears
	 */
	public AcuteComplicationSubmodel(int nPatients, double annualProb, ComplicationRR rr, double pDeath) {
		this.rng = RandomNumberFactory.getInstance();
		generated = new ArrayList<>(nPatients);
		for (int i = 0; i < nPatients; i++) {
			generated.add(new ArrayList<double[]>());
		}
		eventCounter = new int[nPatients];
		Arrays.fill(eventCounter, 0);
		this.annualProb = annualProb;
		this.rr = rr;
		this.pDeath = pDeath;
	}

	/**
	 * Returns the time to a new acute event and whether it causes causes the death of the patient.
	 * @param pat A patient 
	 * @return the time to a new acute event and whether it causes causes the death of the patient.
	 */
	@Override
	public Progression getValue(T1DMPatient pat) {
		// New event for the patient
		if (eventCounter[pat.getIdentifier()] == generated.get(pat.getIdentifier()).size()) {
			generated.get(pat.getIdentifier()).add(new double[] {rng.draw(), rng.draw()});
		}
		final double[] rnd = generated.get(pat.getIdentifier()).get(eventCounter[pat.getIdentifier()]++);
		final long timeToDeath = pat.getTimeToDeath();
		final double usedRR = rr.getRR(pat);
		final long timeToHypo = CommonParams.getAnnualBasedTimeToEvent(pat, -1 / annualProb, rnd[0], usedRR);
		if (timeToHypo >= timeToDeath)
			return new Progression(Long.MAX_VALUE, false);
		return new Progression(timeToHypo, rnd[1] < pDeath);
	}

	/**
	 * Tells the parameter to reuse the last values generated 
	 * @param pat A patient
	 */
	public void cancelLast(T1DMPatient pat) {		
		eventCounter[pat.getIdentifier()]--;
	}
	
	@Override
	public void reset() {
		Arrays.fill(eventCounter, 0);
	}
	
	/**
	 * Computes the cost of the acute event for a patient
	 * @param pat A patient
	 * @return The cost of the acute event for a patient
	 */
	public abstract double getCostOfComplication(T1DMPatient pat);
	
	/**
	 * Computes the disutility associated to this acute event
	 * @param pat A patient
	 * @return The disutility associated to this acute event
	 */
	public abstract double getDisutility(T1DMPatient pat);
}
