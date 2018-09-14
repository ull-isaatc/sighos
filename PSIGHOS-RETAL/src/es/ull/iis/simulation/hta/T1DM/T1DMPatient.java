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
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A patient with Type 1 Diabetes Mellitus. The patient is initially characterized with an age, sex, HbA1c level, and an intervention.
 * Depending on the effect of the intervention, HbA1c level may change. The effect of the intervention itself can be lifelong or restricted 
 * to a time period.
 * The patient may progress to several chronic complications (see {@link MainChronicComplications}), or may develop acute complications 
 * (see {@link MainAcuteComplications}).
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMPatient extends Patient {
	/** The state of the patient */
	private final EnumSet<MainChronicComplications> state;
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
	/** How long is the effect of the intervention active. When finished, time to events must be updated */
	final private long durationOfEffect;
	/** Common parameters to characterize progression, time to events... */
	private final CommonParams commonParams;
	
	// Events
	/** Events related to each chronic complication */
	private final ChronicComorbidityEvent[] comorbidityEvents;
	/** Events related to each acute complication */
	private final ArrayList<ArrayList<AcuteEvent>> acuteEvents;
	/** Death event */ 
	protected DeathEvent deathEvent = null;
	/** Event related to loss of treatment effect */
	private LostTreatmentEffectEvent lostEffectEvent = null;

	/**
	 * Creates a new patient with Type 1 diabetes mellitus.
	 * @param simul Simulation this patient belongs to
	 * @param intervention Intervention assigned to this patient
	 */
	public T1DMPatient(T1DMSimulation simul, T1DMMonitoringIntervention intervention) {
		super(simul, intervention);
		this.commonParams = simul.getCommonParams();
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(MainChronicComplications.class);

		this.initAge = BasicConfigParams.YEAR_CONVERSION*commonParams.getBaselineAge();
		this.sex = commonParams.getSex(this);
		this.baselineHBA1c = commonParams.getBaselineHBA1c();
		comorbidityEvents = new ChronicComorbidityEvent[commonParams.getAvailableHealthStates().size()];
		Arrays.fill(comorbidityEvents, null);
		acuteEvents = new ArrayList<>(MainAcuteComplications.values().length);
		for (int i = 0; i < MainAcuteComplications.values().length; i++)
			acuteEvents.add(new ArrayList<>());
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	/**
	 * Creates a patient with Type 1 diabetes mellitus which replicates another patient.
	 * @param simul Simulation this patient belongs to
	 * @param original Original patient
	 * @param intervention Intervention assigned to this patient
	 */
	public T1DMPatient(T1DMSimulation simul, T1DMPatient original, T1DMMonitoringIntervention intervention) {
		super(simul, original, intervention);
		this.commonParams = original.commonParams;
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(MainChronicComplications.class);

		this.initAge = original.initAge;
		this.sex = original.sex;
		this.baselineHBA1c = original.baselineHBA1c;
		comorbidityEvents = new ChronicComorbidityEvent[commonParams.getAvailableHealthStates().size()];
		Arrays.fill(comorbidityEvents, null);
		acuteEvents = new ArrayList<>(MainAcuteComplications.values().length);
		for (int i = 0; i < MainAcuteComplications.values().length; i++)
			acuteEvents.add(new ArrayList<>());
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
	}

	/**
	 * Returns the state of the patient as regards to main chronic complications
	 * @return the state of the patient as regards to main chronic complications
	 */
	public EnumSet<MainChronicComplications> getState() {
		return state;
	}

	/**
	 * Returns true if the patient currently has a specified complication; false otherwise
	 * @param comp One of the {@link MainChronicComplications}
	 * @return True if the patient currently has a specified complication; false otherwise
	 */
	public boolean hasComplication(MainChronicComplications comp) {
		return state.contains(comp);
	}

	/**
	 * Returns the state of the patient as regards to the detailed progression of main chronic complications
	 * @return the state of the patient as regards to the detailed progression of main chronic complications
	 */
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
	 * Returns the initial age assigned to the patient
	 * @return The initial age assigned to the patient
	 */
	public double getInitAge() {
		return initAge / BasicConfigParams.YEAR_CONVERSION;
	}

	/**
	 * Returns the current age of the patient
	 * @return The current age of the patient
	 */
	public double getAge() {
		return (initAge + simul.getSimulationEngine().getTs() - startTs) / BasicConfigParams.YEAR_CONVERSION;
	}
	
	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public int getSex() {
		return sex;
	}
	
	/**
	 * Returns the HbA1c level initially assigned to the patient
	 * @return The HbA1c level initially assigned to the patient
	 */
	public double getBaselineHBA1c() {
		return baselineHBA1c;
	}

	/**
	 * Returns the current the HbA1c level of the patient
	 * @return The current the HbA1c level of the patient
	 */
	public double getHba1c() {
		return hba1c;
	}

	/**
	 * Returns true if the effect of the intervention is still active; false otherwise
	 * @return true if the effect of the intervention is still active; false otherwise
	 */
	public boolean isEffectActive() {
		return durationOfEffect > getTs();
	}

	/**
	 * Returns the predicted age at death of the patient. If not yet predicted, returns Double.MAX_VALUE.
	 * This age is susceptible to change during the simulation. 
	 * @return the predicted age at death of the patient
	 */
	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : ((initAge + timeToDeath) / BasicConfigParams.YEAR_CONVERSION);
	}
	
	/**
	 * Returns the predicted time to death of the patient (in simulation time units). If not yet predicted, returns Long.MAX_VALUE.
	 * @return the predicted time to death of the patient (in simulation time units)
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	/**
	 * Return the timestamp when certain chronic complication started (or is planned to start)  
	 * @param comp A chronic complication
	 * @return the timestamp when certain chronic complication started (or is planned to start)
	 */
	public long getTimeToChronicComorbidity(T1DMComorbidity comp) {
		return (comorbidityEvents[comp.ordinal()] == null) ? Long.MAX_VALUE : comorbidityEvents[comp.ordinal()].getTs(); 
	}

	/**
	 * The first event of the patient that initializes everything and computes initial time to events.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class StartEvent extends DiscreteEvent.DefaultStartEvent {

		public StartEvent(EventSource source, long ts) {
			super(source, ts);
		}
		
		@Override
		public void event() {
			super.event();
			startTs = this.getTs();
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
			for (MainChronicComplications comp : MainChronicComplications.values()) {
				final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
				if (progs.getCancelEvents().size() > 0)
					error("Cancel complications at start?");
				for (T1DMProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents[pr.getState().ordinal()] = ev;
					simul.addEvent(ev);						
				}
			}
			
			for (MainAcuteComplications comp : MainAcuteComplications.values()) {
				// Assign severe hypoglycemic events
				final AcuteComplicationSubmodel.Progression acuteEvent = commonParams.getTimeToAcuteEvent(T1DMPatient.this, comp, false);
				if (acuteEvent.timeToEvent < timeToDeath) {
					final AcuteEvent ev = new AcuteEvent(comp, acuteEvent);
					acuteEvents.get(comp.ordinal()).add(ev);
					simul.addEvent(ev);
				}				
			}
			
			// Assign lost of treatment effect depending on the intervention
			if (durationOfEffect < timeToDeath) {
				lostEffectEvent = new LostTreatmentEffectEvent(durationOfEffect);
				simul.addEvent(lostEffectEvent);
			}
		}
		
	}
	
	/** 
	 * The last event of the patient, executed when he/she dies or if the simulation finishes.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class FinalizeEvent extends DiscreteEvent {

		public FinalizeEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
        	debug("Ends execution");
			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, T1DMPatientInfo.Type.FINISH, this.getTs()));
		}
	}

	/**
	 * An event related to the progression to a new chronic complication. Updates the state of the patient and recomputes 
	 * time to develop other complications in case the risks change.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class ChronicComorbidityEvent extends DiscreteEvent {
		private final T1DMProgressionPair progress;

		public ChronicComorbidityEvent(T1DMProgressionPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}

		@Override
		public void event() {
			T1DMComorbidity complication = progress.getState();
			if (T1DMPatient.this.detailedState.contains(complication)) {
				error("Health state already assigned!! " + complication.name());
			}
			else {
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
				for (MainChronicComplications comp : MainChronicComplications.values()) {
					final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
					for (T1DMComorbidity st: progs.getCancelEvents()) {
						comorbidityEvents[st.ordinal()].cancel();
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

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				comorbidityEvents[progress.getState().ordinal()] = null;
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * An event related to the onset of an acute complication. Updates the state of the patient and computes a new time to acute event (in case
	 * it is possible).
	 * @author Iván Castilla Rodríguez
	 *
	 */
	private class AcuteEvent extends DiscreteEvent {
		private final boolean causesDeath;
		private final MainAcuteComplications comp;

		public AcuteEvent(MainAcuteComplications comp, AcuteComplicationSubmodel.Progression prog) {
			super(prog.timeToEvent);
			this.comp = comp;
			this.causesDeath = prog.causesDeath;
		}
		
		@Override
		public void event() {
			simul.notifyInfo(new T1DMPatientInfo(simul, T1DMPatient.this, comp, this.getTs()));
			// If the hypoglycemic event causes the death of the patient
			if (causesDeath) {
				deathEvent.cancel();
				deathEvent = new DeathEvent(ts);
				simul.addEvent(deathEvent);
			}
			else {
				// Schedule new event (if required)
				final AcuteComplicationSubmodel.Progression acuteEvent = commonParams.getTimeToAcuteEvent(T1DMPatient.this, comp, false);
				if (acuteEvent.timeToEvent < deathEvent.getTs()) {
					final AcuteEvent ev = new AcuteEvent(comp, acuteEvent);
					acuteEvents.get(comp.ordinal()).add(ev);
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
			for (MainAcuteComplications comp : MainAcuteComplications.values()) {
				// Check last acute event
				final ArrayList<AcuteEvent> acuteEventList = acuteEvents.get(comp.ordinal());
				if (!acuteEventList.isEmpty()) {
					AcuteEvent acuteEv = acuteEventList.get(acuteEventList.size() - 1);
					if (acuteEv.getTs() > ts) {
						acuteEv.cancel();
						acuteEventList.remove(acuteEv);
						final AcuteComplicationSubmodel.Progression prog = commonParams.getTimeToAcuteEvent(T1DMPatient.this, comp, true);
						acuteEv = new AcuteEvent(comp, prog);
						acuteEventList.add(acuteEv);
						simul.addEvent(acuteEv);
					}
					
				}
			}

			// Check all the complications in case the loss of treatment affects the time to events
			for (MainChronicComplications comp : MainChronicComplications.values()) {
				final T1DMProgression progs = commonParams.getNextComplication(T1DMPatient.this, comp);
				for (T1DMComorbidity st: progs.getCancelEvents()) {
					comorbidityEvents[st.ordinal()].cancel();
				}
				for (T1DMProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents[pr.getState().ordinal()] = ev;
					simul.addEvent(ev);		
				}
			}
		}
		
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				lostEffectEvent = null;
				return true;
			}
			return false;
		}
	}

	/**
	 * The event of the death of the patient.  
	 * @author Ivan Castilla Rodriguez
	 *
	 */
	public final class DeathEvent extends DiscreteEvent {
		
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
					}
				}
			}
			for (ArrayList<AcuteEvent> acuteEventList : acuteEvents) {
				int counter = acuteEventList.size();
				while (counter != 0) {
					counter--;
					if (acuteEventList.get(counter).getTs() >= getTs()) {
						acuteEventList.get(counter).cancel();
						acuteEventList.remove(counter);
					}
					else {
						counter = 0;
					}
				}
			}
			if (lostEffectEvent != null) {
				if (lostEffectEvent.getTs() >= getTs()) {
					lostEffectEvent.cancel();
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
