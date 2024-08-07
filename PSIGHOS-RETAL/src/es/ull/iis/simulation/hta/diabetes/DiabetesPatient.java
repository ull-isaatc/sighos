/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.VariableStoreSimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A patient with Diabetes Mellitus. The patient is initially characterized with an age, sex, HbA1c level, and an intervention.
 * Depending on the effect of the intervention, HbA1c level may change. The effect of the intervention itself can be lifelong or restricted 
 * to a time period.
 * The patient may progress to several chronic complications (see {@link DiabetesChronicComplications}), or may develop acute complications 
 * (see {@link DiabetesAcuteComplications}).
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DiabetesPatient extends VariableStoreSimulationObject implements EventSource {
	private final static String OBJ_TYPE_ID = "PAT";
	/** The original patient, this one was cloned from */ 
	private final DiabetesPatient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	protected final int nIntervention;
	/** The specific intervention assigned to the patient */
	protected final DiabetesIntervention intervention;
	/** The timestamp when this patient enters the simulation */
	protected long startTs;
	/** True if the patient is dead */
	private boolean dead; 
	/** The state of the patient */
	private final EnumSet<DiabetesChronicComplications> state;
	/** The detailed state of the patient */
	private final TreeSet<DiabetesComplicationStage> detailedState;
	/** Patient profile */
	private final DiabetesPatientProfile profile;
	/** Initial age of the patient (stored as simulation time units) */
	private final long initAge;
	/** Current level of HBA1c */
	private double hba1c;
	/** How long is the effect of the intervention active. When finished, time to events must be updated */
	private final long durationOfEffect;
	/** Duration of diabetes at the creation of the patient (stored as simulation time units) */
	private final long baseDurationOfDiabetes;
	/** True if the patient smokes */
	private boolean smoker;
	/** True if the patient has atrial fibrillation */
	private boolean atrialFib;
	/** Systolic blood presure, per 10 mm Hg */
	private double sbp;
	/** Lipid ratio, T:H */
	private double lipidRatio;
	/** Common parameters to characterize progression, time to events... */
	private final RepositoryInstance commonParams;
	/** Initial complications */
	private final TreeSet<DiabetesComplicationStage> initComplications;
	
	// Events
	/** Events related to each chronic complication */
	private final TreeMap<DiabetesComplicationStage, ChronicComorbidityEvent> comorbidityEvents;
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
	public DiabetesPatient(final DiabetesSimulation simul, final DiabetesIntervention intervention, final DiabetesPopulation population) {
		super(simul, simul.getPatientCounter(), OBJ_TYPE_ID);
		// Initialize patients with no complications
		this.intervention = intervention;
		this.nIntervention = intervention.getIdentifier();
		this.clonedFrom = null;
		this.dead = false;
		this.commonParams = simul.getCommonParams();
		this.profile = population.getPatientProfile();
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(DiabetesChronicComplications.class);

		this.initAge = BasicConfigParams.SIMUNIT.convert(profile.getInitAge(), TimeUnit.YEAR);
		this.baseDurationOfDiabetes = Math.min(Math.max(BasicConfigParams.SIMUNIT.convert(profile.getInitDurationOfDiabetes(), TimeUnit.YEAR), 0), initAge);
		this.smoker = profile.isSmoker();
		this.atrialFib = profile.hasAtrialFibrillation();
		this.sbp = profile.getSbp();
		this.lipidRatio = profile.getLipidRatio();
		comorbidityEvents = new TreeMap<>();
		acuteEvents = new ArrayList<>(DiabetesAcuteComplications.values().length);
		for (int i = 0; i < DiabetesAcuteComplications.values().length; i++)
			acuteEvents.add(new ArrayList<>());
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
		// Only initialize the structure, the complications are added in a subsequent step
		this.initComplications = new TreeSet<>();
	}

	/**
	 * Creates a patient with Type 1 diabetes mellitus which replicates another patient.
	 * @param simul Simulation this patient belongs to
	 * @param original Original patient
	 * @param intervention Intervention assigned to this patient
	 */
	public DiabetesPatient(DiabetesSimulation simul, DiabetesPatient original, DiabetesIntervention intervention) {
		super(simul, original.id, OBJ_TYPE_ID);
		this.intervention = intervention;
		this.nIntervention = intervention.getIdentifier();
		this.clonedFrom = original;		
		this.dead = false;
		this.commonParams = original.commonParams;
		this.detailedState = new TreeSet<>();
		this.state = EnumSet.noneOf(DiabetesChronicComplications.class);
		this.profile = original.profile;
		this.initAge = original.initAge;
		this.baseDurationOfDiabetes = original.baseDurationOfDiabetes;
		comorbidityEvents = new TreeMap<>();
		acuteEvents = new ArrayList<>(DiabetesAcuteComplications.values().length);
		for (int i = 0; i < DiabetesAcuteComplications.values().length; i++)
			acuteEvents.add(new ArrayList<>());
		this.durationOfEffect = BasicConfigParams.SIMUNIT.convert(intervention.getYearsOfEffect(), TimeUnit.YEAR);
		// Only initialize the structure, the complications are added in a subsequent step
		this.initComplications = new TreeSet<>();
	}

	/**
	 * Returns the state of the patient as regards to main chronic complications
	 * @return the state of the patient as regards to main chronic complications
	 */
	public EnumSet<DiabetesChronicComplications> getState() {
		return state;
	}

	/**
	 * Returns true if the patient currently has a specified complication; false otherwise
	 * @param comp One of the {@link DiabetesChronicComplications}
	 * @return True if the patient currently has a specified complication; false otherwise
	 */
	public boolean hasComplication(DiabetesChronicComplications comp) {
		return state.contains(comp);
	}

	/**
	 * Returns the state of the patient as regards to the detailed progression of main chronic complications
	 * @return the state of the patient as regards to the detailed progression of main chronic complications
	 */
	public TreeSet<DiabetesComplicationStage> getDetailedState() {
		return detailedState;
	}
	

	@Override
	public void notifyEnd() {
        simul.addEvent(onDestroy(simul.getSimulationEngine().getTs()));
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// Nothing to do
	}
	
	/**
	 * Returns the identifier of the intervention applied to this patient
	 * @return the identifier of the intervention applied to this patient
	 */
	public int getnIntervention() {
		return nIntervention;
	}

	/**
	 * Returns the intervention applied to this patient
	 * @return the intervention applied to this patient
	 */
	public DiabetesIntervention getIntervention() {
		return intervention;
	}
	
	/**
	 * Returns the patient this patient was cloned from; null if this is an original patient
	 * @return the patient this patient was cloned from; null if this is an original patient
	 */
	public DiabetesPatient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * Returns the timestamp when the patient was added to the simulation
	 * @return the timestamp when the patient was added to the simulation
	 */
	public long getStartTs() {
		return startTs;
	}

	/**
	 * Returns true if the patient is dead; false otherwise
	 * @return true if the patient is dead; false otherwise
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Sets the patient as dead
	 */
	public void setDead() {
		this.dead = true;
	}

	@Override
	public DiscreteEvent onCreate(long ts) {
		return new StartEvent(this, ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}

	/**
	 * Returns the initial age assigned to the patient
	 * @return The initial age assigned to the patient
	 */
	public double getInitAge() {
		return profile.getInitAge();
	}

	/**
	 * Returns the current age of the patient
	 * @return The current age of the patient
	 */
	public double getAge() {
		return ((double)(initAge + simul.getSimulationEngine().getTs() - startTs)) / BasicConfigParams.YEAR_CONVERSION;
	}
	
	/**
	 * Returns the duration of the diabetes at the creation of the patient
	 * @return the duration of the diabetes at the creation of the patient
	 */
	public double getBaseDurationOfDiabetes() {
		return profile.getInitDurationOfDiabetes();
	}
	
	/**
	 * Returns the current duration of diabetes of the patient
	 * @return the current duration of diabetes of the patient
	 */
	public double getDurationOfDiabetes() {
		return ((double)(baseDurationOfDiabetes + simul.getSimulationEngine().getTs() - startTs)) / BasicConfigParams.YEAR_CONVERSION;		
	}
	
	/**
	 * Returns the sex assigned to the patient (0: male; 1: female)
	 * @return The sex assigned to the patient (0: male; 1: female)
	 */
	public Sex getSex() {
		return profile.getSex();
	}
	
	/**
	 * Returns the HbA1c level initially assigned to the patient
	 * @return The HbA1c level initially assigned to the patient
	 */
	public double getBaselineHBA1c() {
		return profile.getInitHBA1c();
	}

	/**
	 * Returns the current the HbA1c level of the patient
	 * @return The current the HbA1c level of the patient
	 */
	public double getHba1c() {
		return hba1c;
	}

	/**
	 * Returns true if the patient is an active smoker
	 * @return true if the patient is an active smoker; false otherwise
	 */
	public boolean isSmoker() {
		return smoker;
	}
	
	/**
	 * Returns true if the patient suffers from atrial fibrillation
	 * @return true if the patient suffers from atrial fibrillation; false otherwise
	 */
	public boolean hasAtrialFibrillation() {
		return atrialFib;
	}
	
	/**
	 * @return the sbp
	 */
	public double getSbp() {
		return sbp;
	}

	/**
	 * @return the lipidRatio
	 */
	public double getLipidRatio() {
		return lipidRatio;
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
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : (((double)(initAge + timeToDeath)) / BasicConfigParams.YEAR_CONVERSION);
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
	public long getTimeToChronicComorbidity(DiabetesComplicationStage comp) {
		return (!comorbidityEvents.containsKey(comp)) ? Long.MAX_VALUE : comorbidityEvents.get(comp).getTs(); 
	}

	/** 
	 * Return whether if the patient started with the specified complication
	 * @param comp A chronic complication
	 * @return whether if the patient started with the specified complication
	 */
	public boolean hasComplicationFromStart(DiabetesComplicationStage comp) {
		return initComplications.contains(comp);
	}
	
	private void assignInitialComplication(final DiabetesComplicationStage complication) {
		if (DiabetesPatient.this.detailedState.contains(complication)) {
			error("Health state already assigned!! " + complication.name());
		}
		else {
			DiabetesPatient.this.initComplications.add(complication);
			simul.notifyInfo(new DiabetesPatientInfo(simul, DiabetesPatient.this, complication, this.getTs()));
			DiabetesPatient.this.detailedState.add(complication);
			DiabetesPatient.this.state.add(complication.getComplication());
			
			// Recompute time to death in case the risk increases
			final long newTimeToDeath = commonParams.getTimeToDeath(DiabetesPatient.this);
			if (newTimeToDeath < deathEvent.getTs()) {
				deathEvent.cancel();
				deathEvent = new DeathEvent(newTimeToDeath, complication);
				simul.addEvent(deathEvent);
			}
		}
	}
	
	public static String getStrHeader(SecondOrderParamsRepository secParams) {
		final StringBuilder str = new StringBuilder("ID\t");
		str.append(DiabetesPatientProfile.getStrHeader());
		
		for (DiabetesComplicationStage stage : secParams.getRegisteredComplicationStages()) {
			str.append("\tBASE_").append(stage);
		}			
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("" + id);
		str.append("\t").append(profile);
		for (DiabetesComplicationStage stage : commonParams.getRegisteredComplicationStages()) {
			str.append("\t" + (initComplications.contains(stage) ? 1:0));
		}		
		return str.toString();
	}

	/**
	 * The first event of the patient that initializes everything and computes initial time to events.
	 * @author Iv�n Castilla Rodr�guez
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
			hba1c = ((DiabetesIntervention)intervention).getHBA1cLevel(DiabetesPatient.this);
			simul.notifyInfo(new DiabetesPatientInfo(simul, DiabetesPatient.this, DiabetesPatientInfo.Type.START, this.getTs()));

			// Assign death event
			final long timeToDeath = commonParams.getTimeToDeath(DiabetesPatient.this);
			deathEvent = new DeathEvent(timeToDeath);
			simul.addEvent(deathEvent);
			
			for (DiabetesComplicationStage st : commonParams.getInitialState(DiabetesPatient.this)) {
				// I was scheduling these events in the usual way, but they were not executed before the next loop and progression fails
				comorbidityEvents.put(st, new ChronicComorbidityEvent(new DiabetesProgressionPair(st, 0)));
				assignInitialComplication(st);
			}
			// Assign chronic complication events
			for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
				final DiabetesProgression progs = commonParams.getProgression(DiabetesPatient.this, comp);
				if (progs.getCancelEvents().size() > 0)
					error("Cancel complications at start?");
				for (DiabetesProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents.put((DiabetesComplicationStage) pr.getComplication(), ev);
					simul.addEvent(ev);						
				}
			}
			
			for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
				// Assign severe hypoglycemic events
				final DiabetesProgressionPair acuteEvent = commonParams.getTimeToAcuteEvent(DiabetesPatient.this, comp, false);
				if (acuteEvent.getTimeToEvent() < timeToDeath) {
					final AcuteEvent ev = new AcuteEvent(acuteEvent);
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
	 * An event related to the progression to a new chronic complication. Updates the state of the patient and recomputes 
	 * time to develop other complications in case the risks change.
	 * @author Iv�n Castilla Rodr�guez
	 *
	 */
	private class ChronicComorbidityEvent extends DiscreteEvent {
		private final DiabetesProgressionPair progress;

		public ChronicComorbidityEvent(DiabetesProgressionPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}

		@Override
		public void event() {
			final DiabetesComplicationStage complication = (DiabetesComplicationStage) progress.getComplication();
			if (DiabetesPatient.this.detailedState.contains(complication)) {
				error("Health state already assigned!! " + complication.name());
			}
			else {
				simul.notifyInfo(new DiabetesPatientInfo(simul, DiabetesPatient.this, complication, this.getTs()));
				DiabetesPatient.this.detailedState.add(complication);
				DiabetesPatient.this.state.add(complication.getComplication());
				
				if (progress.causesDeath()) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(ts, complication);
					simul.addEvent(deathEvent);
				}
				else {
					// Recompute time to death in case the risk increases
					final long newTimeToDeath = commonParams.getTimeToDeath(DiabetesPatient.this);
					if (newTimeToDeath < deathEvent.getTs()) {
						deathEvent.cancel();
						deathEvent = new DeathEvent(newTimeToDeath, complication);
						simul.addEvent(deathEvent);
					}
					// Update complications
					for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
						final DiabetesProgression progs = commonParams.getProgression(DiabetesPatient.this, comp);
						for (DiabetesComplicationStage st: progs.getCancelEvents()) {
							comorbidityEvents.get(st).cancel();
						}
						for (DiabetesProgressionPair pr : progs.getNewEvents()) {
							final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
							comorbidityEvents.put((DiabetesComplicationStage) pr.getComplication(), ev);
							simul.addEvent(ev);		
						}
					}
				}
			}
		}
		
		@Override
		public String toString() {
			return DiabetesPatient.ChronicComorbidityEvent.class.getSimpleName() + "\t" + progress.getComplication().name() + "[" + progress.getTimeToEvent() + "]";
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				comorbidityEvents.remove(progress.getComplication());
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * An event related to the onset of an acute complication. Updates the state of the patient and computes a new time to acute event (in case
	 * it is possible).
	 * @author Iv�n Castilla Rodr�guez
	 *
	 */
	private class AcuteEvent extends DiscreteEvent {
		private final DiabetesProgressionPair progress;

		public AcuteEvent(DiabetesProgressionPair progress) {
			super(progress.getTimeToEvent());
			this.progress = progress;
		}
		
		@Override
		public void event() {
			final DiabetesAcuteComplications comp = (DiabetesAcuteComplications) progress.getComplication();
			simul.notifyInfo(new DiabetesPatientInfo(simul, DiabetesPatient.this, comp, this.getTs()));
			// If the acute event causes the death of the patient
			if (progress.causesDeath()) {
				deathEvent.cancel();
				deathEvent = new DeathEvent(ts, progress.getComplication());
				simul.addEvent(deathEvent);
			}
			else {
				// Schedule new event (if required)
				final DiabetesProgressionPair acuteEvent = commonParams.getTimeToAcuteEvent(DiabetesPatient.this, comp, false);
				if (acuteEvent.getTimeToEvent() < deathEvent.getTs()) {
					final AcuteEvent ev = new AcuteEvent(acuteEvent);
					acuteEvents.get(comp.ordinal()).add(ev);
					simul.addEvent(ev);
				}
			}
		}
	}

	/**
	 * An event to recompute time to complications as the treatment effect is lost
	 * @author Iv�n Castilla Rodr�guez
	 *
	 */
	private class LostTreatmentEffectEvent extends DiscreteEvent {

		public LostTreatmentEffectEvent(long ts) {
			super(ts);
		}
		
		@Override
		public void event() {
			// Update HbA1c level
			hba1c = ((DiabetesIntervention)intervention).getHBA1cLevel(DiabetesPatient.this);
			for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
				// Check last acute event
				final ArrayList<AcuteEvent> acuteEventList = acuteEvents.get(comp.ordinal());
				if (!acuteEventList.isEmpty()) {
					AcuteEvent acuteEv = acuteEventList.get(acuteEventList.size() - 1);
					if (acuteEv.getTs() > ts) {
						acuteEv.cancel();
						acuteEventList.remove(acuteEv);
						final DiabetesProgressionPair prog = commonParams.getTimeToAcuteEvent(DiabetesPatient.this, comp, true);
						acuteEv = new AcuteEvent(prog);
						acuteEventList.add(acuteEv);
						simul.addEvent(acuteEv);
					}
					
				}
			}

			// Check all the complications in case the loss of treatment affects the time to events
			for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
				final DiabetesProgression progs = commonParams.getProgression(DiabetesPatient.this, comp);
				for (DiabetesComplicationStage st: progs.getCancelEvents()) {
					comorbidityEvents.get(st).cancel();
				}
				for (DiabetesProgressionPair pr : progs.getNewEvents()) {
					final ChronicComorbidityEvent ev = new ChronicComorbidityEvent(pr);
					comorbidityEvents.put((DiabetesComplicationStage) pr.getComplication(), ev);
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
		/** Cause of the death; null if non-specific */
		final Named cause;
		
		public DeathEvent(long ts) {
			this(ts, null);
		}

		public DeathEvent(long ts, Named cause) {
			super(ts);
			this.cause = cause;
		}

		@Override
		public void event() {
			// Cancel all events that cannot happen now
			final ArrayList<ChronicComorbidityEvent> toRemove = new ArrayList<>();
			for (final ChronicComorbidityEvent ev : comorbidityEvents.values()) {
				if (ev.getTs() >= getTs()) {
					toRemove.add(ev);
				}
			}
			for (final ChronicComorbidityEvent ev : toRemove) {
				ev.cancel();
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
			setDead();
			simul.notifyInfo(new DiabetesPatientInfo(simul, DiabetesPatient.this, DiabetesPatientInfo.Type.DEATH, cause, this.getTs()));
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
