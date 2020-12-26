/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;

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
		this.calc = new AnnualRiskBasedTimeToEventCalculator(SecondOrderParamsRepository.NO_RR);
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
	public Transition setCalculator(TimeToEventCalculator calc) {
		this.calc = calc;
		return this;
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
			randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getnPatients(), true);
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
	
	/**
	 * Calculates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @author Iván Castilla
	 */
	public class AnnualRiskBasedTimeToEventCalculator implements TimeToEventCalculator {
		/** Relative risk calculator */
		private final RRCalculator rr;

		/**
		 * 
		 * @param rr Relative risk calculator
		 */
		public AnnualRiskBasedTimeToEventCalculator(RRCalculator rr) {
			this.rr = rr;
		}

		@Override
		public long getTimeToEvent(Patient pat) {
			final int id = pat.getSimulation().getIdentifier();
			return SecondOrderParamsRepository.getAnnualBasedTimeToEvent(pat, 
					secParams.getProbability(srcManifestation, destManifestation, pat.getSimulation()), getRandomSeedForPatients(id).draw(pat), rr.getRR(pat));
		}		
	}

	/**
	 * Calculates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @author Iván Castilla
	 */
	public class AnnualRateBasedTimeToEventCalculator implements TimeToEventCalculator {
		/** Incidence rate ratio calculator */
		private final RRCalculator irr;

		/**
		 * 
		 * @param irr Incidence rate ratio calculator
		 */
		public AnnualRateBasedTimeToEventCalculator(RRCalculator irr) {
			this.irr = irr;
		}

		@Override
		public long getTimeToEvent(Patient pat) {
			final int id = pat.getSimulation().getIdentifier();
			return SecondOrderParamsRepository.getAnnualBasedTimeToEventFromRate(pat, secParams.getProbability(srcManifestation, destManifestation, pat.getSimulation()), 
					getRandomSeedForPatients(id).draw(pat), irr.getRR(pat));
		}		
	}
	
	public class AgeBasedTimeToEventCalculator implements TimeToEventCalculator {
		/** Annual risks of the events */
		private final double[][] ageRisks;
		/** Relative risk calculator */
		private final RRCalculator rr;
		
		public AgeBasedTimeToEventCalculator(final double[][] ageRisks, RRCalculator rr) {
			this.ageRisks = ageRisks;
			this.rr = rr;			
		}
		
		@Override
		public long getTimeToEvent(Patient pat) {
			final int id = pat.getSimulation().getIdentifier();
			final double age = pat.getAge();
			final double lifetime = pat.getAgeAtDeath() - age;
			// Searches the corresponding age interval
			int interval = 0;
			while (age > ageRisks[interval][0])
				interval++;
			// Computes time to event within such interval
			double time = Statistics.getAnnualBasedTimeToEvent(ageRisks[interval][1], getRandomSeedForPatients(id).draw(pat), rr.getRR(pat));
			
			// Checks if further intervals compute lower time to event
			for (; interval < ageRisks.length; interval++) {
				final double newTime = Statistics.getAnnualBasedTimeToEvent(ageRisks[interval][1], getRandomSeedForPatients(id).draw(pat), rr.getRR(pat));
				if ((newTime != Double.MAX_VALUE) && (ageRisks[interval][0] - age + newTime < time))
					time = ageRisks[interval][0] - age + newTime;
			}
			return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		}
	}
}
