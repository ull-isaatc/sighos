/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UniqueRandomSeedPerPatient;

/**
 * Defines a transition from a manifestation to another manifestation
 * @author Iván Castilla
 *
 */
public class Transition {
	/** Manifestation that produces the transition */
	private final Manifestation srcManifestation;
	/** Manifestation that the transition leads to */
	private final Manifestation destManifestation;
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	/** Indicates whether the destination manifestation of this transition replaces the source destination in the state of the patient */
	private final boolean replacesPrevious;
	private final RandomSeedForPatients[] randomSeeds;
	private TimeToEventCalculator calc;
	
	/**
	 * 
	 */
	public Transition(final SecondOrderParamsRepository secParams, Manifestation srcManifestation, Manifestation destManifestation, boolean replacesPrevious) {
		this.secParams = secParams;
		this.srcManifestation = srcManifestation;
		this.destManifestation = destManifestation;
		this.replacesPrevious = replacesPrevious;
		this.randomSeeds = new RandomSeedForPatients[secParams.getnRuns() + 1];
		Arrays.fill(randomSeeds, null);
		this.calc = new AnnualRiskBasedTimeToEventCalculator();
	}

	/**
	 * @return the calc
	 */
	public TimeToEventCalculator getCalculator() {
		return calc;
	}

	/**
	 * @param calc the calc to set
	 */
	public void setCalculator(TimeToEventCalculator calc) {
		this.calc = calc;
	}

	/**
	 * Returns the manifestation that produces the transition
	 * @return the manifestation that produces the transition
	 */
	public Manifestation getSrcManifestation() {
		return srcManifestation;
	}

	/**
	 * Returns the manifestation that the transition leads to
	 * @return the manifestation that the transition leads to
	 */
	public Manifestation getDestManifestation() {
		return destManifestation;
	}

	/**
	 * @return the replacesPrevious
	 */
	public boolean replacesPrevious() {
		return replacesPrevious;
	}

	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			if (Manifestation.Type.ACUTE.equals(destManifestation.getType())) {
				randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getnPatients(), true);
			}
			else {
				randomSeeds[id] = new UniqueRandomSeedPerPatient(secParams.getnPatients(), true);				
			}
		}
		return randomSeeds[id];
	}
	/**
	 * Returns the time to event for a patient
	 * @param pat A patient
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, long limit) {
		final long time = calc.getTimeToEvent(pat);
		return (time >= limit) ? Long.MAX_VALUE : time;		
	}
	
	public class AnnualRiskBasedTimeToEventCalculator implements TimeToEventCalculator {

		public AnnualRiskBasedTimeToEventCalculator() {
		}

		@Override
		public long getTimeToEvent(Patient pat) {
			final int id = pat.getSimulation().getIdentifier();
			return SecondOrderParamsRepository.getAnnualBasedTimeToEvent(pat, 
					secParams.getProbability(srcManifestation, destManifestation, pat.getSimulation()), getRandomSeedForPatients(id).draw(pat), 1.0);
		}
		
	}
}
