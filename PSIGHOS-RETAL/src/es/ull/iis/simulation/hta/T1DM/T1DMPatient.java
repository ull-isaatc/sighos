/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.SevereHypoglycemicEventParam;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMPatient extends Patient {
	/** The state of the patient */
	private final EnumSet<MainComplications> state;
	/** The detailed state of the patient */
	private final TreeSet<T1DMComorbidity> detailedState;
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
	
	// Events
	private final ChronicComorbidityEvent[] comorbidityEvents;
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
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(MainComplications.class);

		this.initAge = BasicConfigParams.YEAR_CONVERSION*commonParams.getBaselineAge();
		this.sex = commonParams.getSex(this);
		this.baselineHBA1c = commonParams.getBaselineHBA1c();
		this.weeklySensorUsage = commonParams.getWeeklySensorUsage();
		comorbidityEvents = new ChronicComorbidityEvent[commonParams.getAvailableHealthStates().size()];
		Arrays.fill(comorbidityEvents, null);
		hypoEvents = new ArrayList<>();
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	public T1DMPatient(T1DMSimulation simul, T1DMPatient original, T1DMMonitoringIntervention intervention) {
		super(simul, original, intervention);
		this.commonParams = original.commonParams;
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(MainComplications.class);

		this.initAge = original.initAge;
		this.sex = original.sex;
		this.baselineHBA1c = original.baselineHBA1c;
		this.weeklySensorUsage = original.weeklySensorUsage;
		comorbidityEvents = new ChronicComorbidityEvent[commonParams.getAvailableHealthStates().size()];
		Arrays.fill(comorbidityEvents, null);
		hypoEvents = new ArrayList<>();
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	/**
	 * @return the state
	 */
	public EnumSet<MainComplications> getState() {
		return state;
	}

	public boolean hasComplication(MainComplications comp) {
		return state.contains(comp);
	}

	public TreeSet<T1DMComorbidity> getDetailedState() {
		return detailedState;
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
	 * Return the timestamp when certain chronic complication started (or is planned to start)  
	 * @param comp Health state
	 * @return the timestamp when certain chronic complication started (or is planned to start)
	 */
	public long getTimeToChronicComorbidity(T1DMComorbidity comp) {
		return (comorbidityEvents[comp.ordinal()] == null) ? Long.MAX_VALUE : comorbidityEvents[comp.ordinal()].getTs(); 
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
					final double periodCost = commonParams.getAnnualCostWithinPeriod(T1DMPatient.this, initAge, endAge);
					cost.update(T1DMPatient.this, periodCost, initAge, endAge);
					ly.update(T1DMPatient.this, 1.0, initAge, endAge);
					qaly.update(T1DMPatient.this, commonParams.getUtilityValue(T1DMPatient.this), initAge, endAge);
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
			
			for (T1DMComorbidity st : commonParams.getInitialState(T1DMPatient.this)) {
				detailedState.add(st);				
				comorbidityEvents[st.ordinal()] = new ChronicComorbidityEvent(new T1DMProgressionPair(st, 0));
			}
			// Assign chronic complication events
			for (MainComplications comp : MainComplications.values()) {
				final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
				if (progs.getCancelEvents().size() > 0)
					error("Cancel complications at start?");
				for (T1DMProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents[pr.getState().ordinal()] = ev;
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

	private class ChronicComorbidityEvent extends T1DMPatientEvent {
		private final T1DMProgressionPair progress;

		public ChronicComorbidityEvent(T1DMProgressionPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}

		@Override
		public void event() {
			super.event();
			T1DMComorbidity complication = progress.getState();
			if (T1DMPatient.this.detailedState.contains(complication)) {
				error("Health state already assigned!! " + complication.name());
			}
			else {
				final double age = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION;
				cost.update(T1DMPatient.this, commonParams.getCostOfComplication(T1DMPatient.this, complication), age);
				
				simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, complication, this.getTs()));
				T1DMPatient.this.detailedState.add(complication);
				T1DMPatient.this.state.add(complication.getComplication());
				
				// Recompute time to death in case the risk increases
				final long newTimeToDeath = commonParams.getTimeToDeath(T1DMPatient.this);
				if (newTimeToDeath < deathEvent.getTs()) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(newTimeToDeath);
					simul.addEvent(deathEvent);
				}
				// Update complications
				for (MainComplications comp : MainComplications.values()) {
					final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
					for (T1DMComorbidity st: progs.getCancelEvents()) {
						comorbidityEvents[st.ordinal()].cancel();
						comorbidityEvents[st.ordinal()] = null;
					}
					for (T1DMProgressionPair pr : progs.getNewEvents()) {
						final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
						comorbidityEvents[pr.getState().ordinal()] = ev;
						simul.addEvent(ev);		
					}
				}
			}
		}
		
		@Override
		public String toString() {
			return T1DMPatient.ChronicComorbidityEvent.class.getSimpleName() + "\t" + progress.getState().name() + "[" + progress.getTimeToEvent() + "]";
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
			qaly.update(T1DMPatient.this, commonParams.getHypoEventDisutilityValue(), age);
			cost.update(T1DMPatient.this, commonParams.getCostForSevereHypoglycemicEpisode(T1DMPatient.this), age);

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

			// Check all the complications in case the loss of treatment affects the time to events
			for (MainComplications comp : MainComplications.values()) {
				final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
				for (T1DMComorbidity st: progs.getCancelEvents()) {
					comorbidityEvents[st.ordinal()].cancel();
					comorbidityEvents[st.ordinal()] = null;
				}
				for (T1DMProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents[pr.getState().ordinal()] = ev;
					simul.addEvent(ev);		
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
			for (int i = 0; i < comorbidityEvents.length; i++) {
				if (comorbidityEvents[i] != null) {
					if (comorbidityEvents[i].getTs() >= getTs()) {
						comorbidityEvents[i].cancel();
						comorbidityEvents[i] = null;
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
