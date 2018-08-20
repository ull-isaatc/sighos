/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CHDComplication;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.Complication;
import es.ull.iis.simulation.hta.T1DM.params.ResourceUsageParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.SevereHypoglycemicEventParam;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMPatient extends Patient {
	/** The state of the patient, initialized with no complications */
	private final EnumSet<Complication> state = EnumSet.noneOf(Complication.class);
	/** Type of CHD complication that the patient develops (in case he/she develops any) */ 
	private final CHDComplication chdComplication;
	/** Initial age of the patient (stored in days) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** Initial level of HBA1c */
	private final double baselineHBA1c;
	/** Current level of HBA1c */
	private double hba1c;
	/** Days a week usage of the sensor (for continuous glucose monitoring) */
	final private double weeklySensorUsage;
	final private long durationOfEffect;
	
	private final CommonParams commonParams;
	private final ResourceUsageParams resUsageParams;
	private final UtilityParams utilParams;
	
	// Events
	private final ComplicationEvent[] complicationEvents;
	private final ArrayList<SevereHypoglycemicEvent> hypoEvents;
	protected DeathEvent deathEvent = null;
	private LostTreatmentEffectEvent lostEffectEvent = null;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public T1DMPatient(T1DMSimulation simul, T1DMMonitoringIntervention intervention) {
		super(simul, intervention);
		this.commonParams = simul.getCommonParams();
		this.utilParams = simul.getUtilParams();
		this.resUsageParams = simul.getResUsageParams();

		this.initAge = BasicConfigParams.YEAR_CONVERSION*commonParams.getBaselineAge();
		this.sex = commonParams.getSex(this);
		this.baselineHBA1c = commonParams.getBaselineHBA1c();
		this.weeklySensorUsage = commonParams.getWeeklySensorUsage();
		complicationEvents = new ComplicationEvent[SecondOrderParams.N_COMPLICATIONS];
		Arrays.fill(complicationEvents, null);
		hypoEvents = new ArrayList<>();
		chdComplication = commonParams.getCHDComplication(this);
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	public T1DMPatient(T1DMSimulation simul, T1DMPatient original, T1DMMonitoringIntervention intervention) {
		super(simul, original, intervention);
		this.commonParams = original.commonParams;
		this.utilParams = original.utilParams;
		this.resUsageParams = original.resUsageParams;

		this.initAge = original.initAge;
		this.sex = original.sex;
		this.baselineHBA1c = original.baselineHBA1c;
		this.weeklySensorUsage = original.weeklySensorUsage;
		complicationEvents = new ComplicationEvent[SecondOrderParams.N_COMPLICATIONS];
		Arrays.fill(complicationEvents, null);
		hypoEvents = new ArrayList<>();
		chdComplication = original.chdComplication;
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	/**
	 * @return the state
	 */
	public EnumSet<Complication> getState() {
		return state;
	}

	/**
	 * @return the chdComplication
	 */
	public CHDComplication getCHDComplication() {
		return chdComplication;
	}

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new StartEvent(this, ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new FinalizeEvent(ts);
	}

	/**
	 * @return the initAge
	 */
	public double getInitAge() {
		return initAge / BasicConfigParams.YEAR_CONVERSION;
	}

	/**
	 * 
	 * @return the current age of the patient
	 */
	public double getAge() {
		return (initAge + simul.getSimulationEngine().getTs() - startTs) / BasicConfigParams.YEAR_CONVERSION;
	}
	
	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}
	
	/**
	 * @return the baselineHBA1c
	 */
	public double getBaselineHBA1c() {
		return baselineHBA1c;
	}

	/**
	 * @return the hba1c
	 */
	public double getHba1c() {
		return hba1c;
	}

	/**
	 * @return the weeklySensorUsage
	 */
	public double getWeeklySensorUsage() {
		return weeklySensorUsage;
	}

	/**
	 * Returns true if the effect of the intervention is still active; false otherwise
	 * @return true if the effect of the intervention is still active; false otherwise
	 */
	public boolean isEffectActive() {
		return durationOfEffect > getTs();
	}

	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : ((initAge + timeToDeath) / BasicConfigParams.YEAR_CONVERSION);
	}
	
	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	/**
	 * Return the timestamp when certain complication started (or is planned to start)  
	 * @param comp Complication
	 * @return the timestamp when certain complication started (or is planned to start)
	 */
	public long getTimeToComplication(Complication comp) {
		return (complicationEvents[comp.ordinal()] == null) ? Long.MAX_VALUE : complicationEvents[comp.ordinal()].getTs(); 
	}

	/**
	 * The basic class for patient's events.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class T1DMPatientEvent extends DiscreteEvent {

		public T1DMPatientEvent(long ts) {
			super(ts);
		}

		/**
		 * Sets the current timestamp for this patient, saves the previous timestamp in @link(lastTs), and updates costs.
		 * @param ts New timestamp to be assigned
		 */
		@Override
		public void event() {
			if (lastTs != ts) {
				final double initAge = TimeUnit.DAY.convert(lastTs, simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION; 
				final double endAge = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION;
				
				// Update lastTs
				lastTs = this.ts;
				
				// Update outcomes
				if (ts > 0) {
					final double periodCost = resUsageParams.getAnnualCostWithinPeriod(T1DMPatient.this, initAge, endAge);
					cost.update(T1DMPatient.this, periodCost, initAge, endAge);
					ly.update(T1DMPatient.this, 1.0, initAge, endAge);
					qaly.update(T1DMPatient.this, utilParams.getUtilityValue(T1DMPatient.this), initAge, endAge);
				}
			}
		}
	}
	
	private class StartEvent extends DiscreteEvent.DefaultStartEvent {

		public StartEvent(EventSource source, long ts) {
			super(source, ts);
		}
		
		@Override
		public void event() {
			super.event();
			startTs = this.getTs();
			lastTs = startTs;
			// Assign level of HBA1c expected after the intervention (we assume that the effect is immediate).
			hba1c = ((T1DMMonitoringIntervention)intervention).getHBA1cLevel(T1DMPatient.this);
			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, T1DMPatientInfo.Type.START, this.getTs()));

			// Assign death event
			final long timeToDeath = commonParams.getTimeToDeath(T1DMPatient.this);
			deathEvent = new DeathEvent(timeToDeath);
			simul.addEvent(deathEvent);
			
			// Assign complication events
			EnumSet<Complication> initComplications = EnumSet.of(Complication.CHD, Complication.RET, Complication.NEU, Complication.NPH);
			for (Complication comp : initComplications) {
				final long timeToComplication = commonParams.getTimeToComplication(T1DMPatient.this, comp);
				if (timeToComplication < Long.MAX_VALUE) {
					final ComplicationEvent ev = new ComplicationEvent(timeToComplication, comp);
					complicationEvents[comp.ordinal()] = ev;
					simul.addEvent(ev);
				}
			}
			
			// Assign severe hypoglycemic events
			final SevereHypoglycemicEventParam.ReturnValue hypoEvent = commonParams.getTimeToSevereHypoglycemicEvent(T1DMPatient.this, false);
			if (hypoEvent.timeToEvent < timeToDeath) {
				final SevereHypoglycemicEvent ev = new SevereHypoglycemicEvent(hypoEvent.timeToEvent, hypoEvent.causesDeath);
				hypoEvents.add(ev);
				simul.addEvent(ev);
			}
			
			// Assign lost of treatment effect depending on the intervention
			if (durationOfEffect < timeToDeath) {
				lostEffectEvent = new LostTreatmentEffectEvent(durationOfEffect);
				simul.addEvent(lostEffectEvent);
			}
		}
		
	}
	
	private class FinalizeEvent extends T1DMPatientEvent {

		public FinalizeEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
			super.event();
        	debug("Ends execution");
			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, T1DMPatientInfo.Type.FINISH, this.getTs()));
		}
	}

	private class ComplicationEvent extends T1DMPatientEvent {
		private final Complication complication;

		public ComplicationEvent(long ts, Complication complication) {
			super(ts);
			this.complication = complication;
		}

		@Override
		public void event() {
			super.event();
			if (T1DMPatient.this.state.contains(complication)) {
				error("Complication already assigned!! " + complication);
			}
			else {
				final double age = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION;
				cost.update(T1DMPatient.this, resUsageParams.getCostOfComplication(T1DMPatient.this, complication), age);
				
				simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, complication, this.getTs()));
				T1DMPatient.this.state.add(complication);
				
				// Recompute time to death in case the risk increases
				final long newTimeToDeath = commonParams.getTimeToDeath(T1DMPatient.this);
				if (newTimeToDeath < deathEvent.getTs()) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(newTimeToDeath);
					simul.addEvent(deathEvent);
				}
				// Check whether new complications may appear
				EnumSet<Complication> toCheck;
				if (commonParams.isCanadaValidation()) {
					if (state.contains(Complication.ESRD) || state.contains(Complication.LEA) || state.contains(Complication.BLI)) {
						toCheck = EnumSet.noneOf(Complication.class);
					}
					else if (state.containsAll(EnumSet.of(Complication.NEU, Complication.NPH))) {
						toCheck = EnumSet.of(Complication.ESRD, Complication.LEA);
					}
					else {
						toCheck = EnumSet.noneOf(Complication.class);
						if (state.contains(Complication.NEU)) {
							toCheck.add(Complication.LEA);							
						}
						if (state.contains(Complication.NPH)) {
							toCheck.add(Complication.ESRD);							
						}
						if (state.contains(Complication.RET)) {
							toCheck.add(Complication.BLI);							
						}
						if (!state.contains(Complication.CHD)) {
							toCheck.add(Complication.CHD);							
						}						
					}
				}
				else {
					toCheck = EnumSet.complementOf(state);
					if (toCheck.contains(Complication.ESRD) && !state.contains(Complication.NPH))
						toCheck.remove(Complication.ESRD);
					if (toCheck.contains(Complication.BLI) && !state.contains(Complication.RET))
						toCheck.remove(Complication.BLI);
					// FIXME Válido sólo para los canadienses
					if (toCheck.contains(Complication.LEA) && !state.contains(Complication.NEU))
						toCheck.remove(Complication.LEA);
				}
				for (Complication comp : toCheck) {
					final long timeToComplication = commonParams.getTimeToComplication(T1DMPatient.this, comp);
					if (timeToComplication < Long.MAX_VALUE) {
						// If the event was not previously created
						if (complicationEvents[comp.ordinal()] == null) {
							final ComplicationEvent ev = new ComplicationEvent(timeToComplication, comp);
							complicationEvents[comp.ordinal()] = ev;
							simul.addEvent(ev);
						}
						else {
							// If the old event was scheduled to happen later than the new timestamp, we have to cancel it
							if (complicationEvents[comp.ordinal()].getTs() > timeToComplication) {
								complicationEvents[comp.ordinal()].cancel();
								final ComplicationEvent ev = new ComplicationEvent(timeToComplication, comp);
								complicationEvents[comp.ordinal()] = ev;
								simul.addEvent(ev);
							}
						}							
					}
				}
			}
		}
		
		/**
		 * @return the complication
		 */
		public Complication getComplication() {
			return complication;
		}
		
	}
	
	private class SevereHypoglycemicEvent extends T1DMPatientEvent {
		private final boolean causesDeath;

		public SevereHypoglycemicEvent(long ts, boolean causesDeath) {
			super(ts);
			this.causesDeath = causesDeath;
		}
		
		@Override
		public void event() {
			super.event();
			final double age = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION;
			qaly.update(T1DMPatient.this, utilParams.getHypoEventDisutilityValue(), age);
			cost.update(T1DMPatient.this, resUsageParams.getCostForSevereHypoglycemicEpisode(T1DMPatient.this), age);

			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, T1DMPatientInfo.Type.HYPO_EVENT, this.getTs()));
			// If the hypoglycemic event causes the death of the patient
			if (causesDeath) {
				deathEvent.cancel();
				deathEvent = new DeathEvent(ts);
				simul.addEvent(deathEvent);
			}
			else {
				// Schedule new event (if required)
				final SevereHypoglycemicEventParam.ReturnValue hypoEvent = commonParams.getTimeToSevereHypoglycemicEvent(T1DMPatient.this, false);
				if (hypoEvent.timeToEvent < deathEvent.getTs()) {
					final SevereHypoglycemicEvent ev = new SevereHypoglycemicEvent(hypoEvent.timeToEvent, hypoEvent.causesDeath);
					hypoEvents.add(ev);
					simul.addEvent(ev);
				}
			}
		}
	}

	/**
	 * An event to recompute time to complications as the treatment effect is lost
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class LostTreatmentEffectEvent extends DiscreteEvent {

		public LostTreatmentEffectEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
			// Update HbA1c level
			hba1c = ((T1DMMonitoringIntervention)intervention).getHBA1cLevel(T1DMPatient.this);
			// Check last hypoglycemic event
			if (!hypoEvents.isEmpty()) {
				SevereHypoglycemicEvent hypoEv = hypoEvents.get(hypoEvents.size() - 1);
				if (hypoEv.getTs() > ts) {
					hypoEv.cancel();
					hypoEvents.remove(hypoEv);
					final SevereHypoglycemicEventParam.ReturnValue hypoEvent = commonParams.getTimeToSevereHypoglycemicEvent(T1DMPatient.this, true);
					hypoEv = new SevereHypoglycemicEvent(hypoEvent.timeToEvent, hypoEvent.causesDeath);
					hypoEvents.add(hypoEv);
					simul.addEvent(hypoEv);
				}
				
			}

			// Check all scheduled complication events
			// FIXME: Shouldn't check ALL the complications????
			for (int i = 0; i < complicationEvents.length; i++) {
				if (complicationEvents[i] != null) {
					final Complication comp = complicationEvents[i].complication;
					final long newTimeToComplication = commonParams.getTimeToComplication(T1DMPatient.this, comp);
					// Since the treatment effect has been lost, the complication may appear earlier 
					if (newTimeToComplication < complicationEvents[i].getTs()) {
						complicationEvents[i].cancel();
						final ComplicationEvent ev = new ComplicationEvent(newTimeToComplication, comp);
						complicationEvents[comp.ordinal()] = ev;
						simul.addEvent(ev);
					}
				}
			}
		}
	}

	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends T1DMPatientEvent {
		
		public DeathEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			// Cancel all events that cannot happen now
			for (int i = 0; i < complicationEvents.length; i++) {
				if (complicationEvents[i] != null) {
					if (complicationEvents[i].getTs() >= getTs()) {
						complicationEvents[i].cancel();
						complicationEvents[i] = null;
					}
				}
			}
			int counter = hypoEvents.size();
			while (counter != 0) {
				counter--;
				if (hypoEvents.get(counter).getTs() >= getTs()) {
					hypoEvents.get(counter).cancel();
					hypoEvents.remove(counter);
				}
				else {
					counter = 0;
				}
			}
			if (lostEffectEvent != null) {
				if (lostEffectEvent.getTs() >= getTs()) {
					lostEffectEvent.cancel();
					lostEffectEvent = null;					
				}
			}
			
			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, T1DMPatientInfo.Type.DEATH, this.getTs()));
			notifyEnd();
		}
	
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				deathEvent = null;
				return true;
			}
			return false;
		}
	}
}
