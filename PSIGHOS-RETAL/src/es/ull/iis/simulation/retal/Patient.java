/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.outcome.Outcome;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CNVStage;
import es.ull.iis.simulation.retal.params.CNVStageAndValue;
import es.ull.iis.simulation.retal.params.CommonParams;
import es.ull.iis.simulation.retal.params.DRParams;
import es.ull.iis.simulation.retal.params.EyeStateAndValue;
import es.ull.iis.simulation.retal.params.VAProgressionPair;
import es.ull.iis.simulation.sequential.BasicElement;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * Within all the class, eye1 is always indexed as 0, while eye2 is indexed as 1  
 * @author Iván Castilla
 *
 */
public class Patient extends BasicElement {
	private enum DISEASES {
		ARMD,
		DR
	};
	private EnumSet<DISEASES> affectedBy = EnumSet.noneOf(DISEASES.class); 
	private Patient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	private final int nIntervention;
	/** Initial age of the patient (stored in days) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** The age this patient is diabetic since. Long.MAX_VALUE if he/she's not diabetic */
	private double diabeticSince = -1.0;
	/** Type of DM; -1 if no diabetic */
	private int diabetesType = -1;
	/** Hemoglobin A1c */
	private double hbA1c;
	// Event times
	/** Precomputed time to death for this patient */
	protected final long timeToDeath;
	
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** The timestamp of the last event executed (but the current one) */
	private long lastTs = -1;
	/** The timestamp when this patient enters the simulation */
	private long startTs;
	/** The current currentUtility applied to this patient */
	private double currentUtility = 1.0;
	/** The outcomes that are measured for this patient */
	private final ArrayList<Outcome> outcomes;
	
	/** Random number generators for initial risks to be compared with specific probabilities */
	private final RandomForPatient rng;
	// Events
	private DiabetesEvent diabetesEvent = null;
	private final EARMEvent[] eARMEvent = {null, null};
	private final GAEvent[] gAEvent = {null, null};
	private final CNVEvent[] cNVEvent = {null, null};
	private final LinkedList<?>[] cNVStageEvents = {new LinkedList<CNVStageEvent>(), new LinkedList<CNVStageEvent>()};
	private final LinkedList<?>[] vaProgression = {new LinkedList<VAProgressionPair>(), new LinkedList<VAProgressionPair>()};
	private NonProliferativeDREvent nPDREvent = null;
	private ProliferativeDREvent pDREvent = null;
	private ClinicallySignificantDMEEvent cSMEEvent = null;
	/** Last timestamp when VA changed */
	private final long[] lastVAChangeTs = new long[2];
	
	/** Defines whether the patient is currently diagnosed */
	private long diagnosed = Long.MAX_VALUE;
	/** The current state of the eyes of the patient */
	private final EnumSet<?>[] eyes = new EnumSet<?>[2];
	/** The current type and position of the CNV lesions in each eye; null if no CNV */
	private final CNVStage[] currentCNVStage = new CNVStage[2];
	
	private final CommonParams commonParams;
	private final ARMDParams armdParams;
	private final DRParams drParams;

	/**
	 * Creates a patient and initializes the default events
	 * @param simul Simulation this patient is attached to
	 * @param initAge The initial age of the patient
	 * @param sex Sex of the patient
	 */
	public Patient(RETALSimulation simul, double initAge, int nIntervention) {
		super(simul.getPatientCounter(), simul);
		this.rng = new RandomForPatient();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
		this.drParams = simul.getDrParams();

		intervention = new NullIntervention();
		this.initAge = 365*initAge;
		this.sex = commonParams.getSex(this);
		this.clonedFrom = null;
		this.nIntervention = nIntervention;
		// Limiting lifespan to MAX AGE
		this.timeToDeath = simul.getCommonParams().getTimeToDeath(this);
		this.outcomes = simul.getOutcomes();
		
		eyes[0] = EnumSet.of(EyeState.HEALTHY);
		eyes[1] = EnumSet.of(EyeState.HEALTHY);
		currentCNVStage[0] = null;
		currentCNVStage[1] = null;
		// Visual acuity supposed to be perfect at start
		final VAProgressionPair newVA = new VAProgressionPair(0, 0.0);
		lastVAChangeTs[0] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[0]).add(newVA);
		lastVAChangeTs[1] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[1]).add(newVA);
		setUtility(armdParams.getUtilityFromVA(this));
	}

	/**
	 * Creates a new patient who is a clone of another one and who is assigned to a different intervention
	 * @param original The original patient whose attributes will be cloned 
	 * @param nIntervention New intervention this clone is assigned to
	 */
	@SuppressWarnings("unchecked")
	public Patient(RETALSimulation simul, Patient original, int nIntervention) {
		super(original.id, simul);
		this.rng = new RandomForPatient(original.rng); 
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;

		intervention = new Screening(new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1), 1.0, 1.0);
		this.clonedFrom = original;
		this.nIntervention = nIntervention;
		this.initAge = original.initAge;
		this.sex = original.sex;
		this.timeToDeath = original.timeToDeath;
		this.outcomes = original.outcomes;

		eyes[0] = EnumSet.of(EyeState.HEALTHY);
		eyes[1] = EnumSet.of(EyeState.HEALTHY);
		currentCNVStage[0] = null;
		currentCNVStage[1] = null;
		// Visual acuity supposed to be perfect at start
		final VAProgressionPair newVA = new VAProgressionPair(0, 0.0);
		lastVAChangeTs[0] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[0]).add(newVA);
		lastVAChangeTs[1] += newVA.timeToChange;
		((LinkedList<VAProgressionPair>)vaProgression[1]).add(newVA);
		setUtility(armdParams.getUtilityFromVA(this));
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "PAT";
	}

	@Override
	protected void init() {
		startTs = this.getTs();
		simul.getInfoHandler().notifyInfo(new PatientInfo(this.simul, this, PatientInfo.Type.START, this.getTs()));
		addEvent(new DeathEvent(timeToDeath));
		// Checks if he/she is diabetic
		if (commonParams.isDiabetic(this)) {
			diabetesType = commonParams.getDiabetesType(this);
			diabeticSince = getAge() - commonParams.getDurationOfDM(this);
			final EnumSet<EyeState> startWith = drParams.startsWith(this);
			// TODO: Check if bilateral is required
			if (startWith.size() > 0) {
				eyes[0].remove(EyeState.HEALTHY);
				for (EyeState state : startWith)
					((EnumSet<EyeState>)eyes[0]).add(state);
				affectedBy.add(DISEASES.DR);
			}
		}
		// Else, schedules a diabetes event
		else {
			final long timeToDiabetes = commonParams.getTimeToDiabetes(this);
			if (timeToDiabetes < Long.MAX_VALUE) {
				diabetesEvent = new DiabetesEvent(timeToDiabetes);
				addEvent(diabetesEvent);
			}				
		}
		
		// Schedules ARMD-related events
		long timeToEvent = armdParams.getTimeToEARM(this);
		final EyeStateAndValue timeToAMD = armdParams.getTimeToE1AMD(this);
		// Schedule an EARM event
		if (timeToEvent < Long.MAX_VALUE) {
			eARMEvent[0] = new EARMEvent(timeToEvent, 0);
			addEvent(eARMEvent[0]);
		}
		// Schedule either a CNV or a GA event
		else if (timeToAMD != null) {
			if (timeToAMD.getState() == EyeState.AMD_CNV) {
				cNVEvent[0] = new CNVEvent(timeToAMD.getValue(), 0);
				addEvent(cNVEvent[0]);				
			}
			else {
				gAEvent[0] = new GAEvent(timeToAMD.getValue(), 0);
				addEvent(gAEvent[0]);				
			}
		}
		
		// Schedules screening-related events
		if (intervention instanceof Screening) {
			final DiscreteCycleIterator screeningIterator = ((Screening)intervention).getScreeningCycle().getCycle().iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
			addEvent(new ScreeningEvent(screeningIterator.next(), screeningIterator));
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElement#end()
	 */
	@Override
	protected void end() {
		simul.getInfoHandler().notifyInfo(new PatientInfo(this.simul, this, PatientInfo.Type.FINISH, this.getTs()));
	}
	
	/**
	 * Redeclaration of addEvent to make it visible from the rest of classes of this package. @see es.ull.iis.simulation.sequential.BasicElement.addEvent
	 * @param e
	 */
	protected void addEvent(DiscreteEvent e) {
		super.addEvent(e);
	}

	/**
	 * @return
	 */
	public int getnIntervention() {
		return nIntervention;
	}

	/**
	 * @return the initial age of the patient
	 */
	public double getInitAge() {
		return initAge / 365;
	}

	/**
	 * @return the startTs
	 */
	public long getStartTs() {
		return startTs;
	}

	/**
	 * 
	 * @return the current age of the patient
	 */
	public double getAge() {
		return (initAge + ts - startTs) / 365.0;
	}
	
	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @return the diabeticSince
	 */
	public double getDiabeticSince() {
		return diabeticSince;
	}

	/**
	 * @return the diabetesType
	 */
	public int getDiabetesType() {
		return diabetesType;
	}

	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return timeToDeath;
	}

	public double getAgeAtDeath() {
		return initAge + timeToDeath / 365.0;
	}
	
	/**
	 * @return the clonedFrom
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
	}

	/**
	 * @return the currentUtility
	 */
	public double getUtility() {
		return currentUtility;
	}

	/**
	 * @param currentUtility the current utility to set
	 */
	public void setUtility(double currentUtility) {
		this.currentUtility = currentUtility;
	}

	/**
	 * Sets the current timestamp for this patient, saves the previous timestamp in @link(lastTs), and updates costs and QALYs.
	 * @param ts New timestamp to be assigned
	 */
	@Override
	public void setTs(long ts) {
		lastTs = this.ts;
		super.setTs(ts);
		for (Outcome outcome : outcomes) {
			outcome.update(this);
		}
	}
	
	/**
	 * @return the lastTs
	 */
	public long getLastTs() {
		return lastTs;
	}

	/**
	 * @return the diagnosed
	 */
	public boolean isDiagnosed() {
		return (diagnosed < Long.MAX_VALUE);
	}

	/**
	 * Updates the state of the patient to reflect whether he/she has been diagnosed
	 */
	public void checkDiagnosis() {
		if (!isDiagnosed()) {
			if (affectedBy.contains(DISEASES.ARMD)) {
				if (rng.getRandomNumber(RandomForPatient.ITEM.ARMD_CLINICAL_PRESENTATION) < armdParams.getProbabilityClinicalPresentation(Patient.this))
					this.diagnosed = ts;
			}
			// TODO: Add clinical diagnosis with DR
		}		
	}

	/**
	 * @param eyeIndex
	 * @return the state of the first eye
	 */
	@SuppressWarnings("unchecked")
	public EnumSet<EyeState> getEyeState(int eyeIndex) {
		return (EnumSet<EyeState>) eyes[eyeIndex];
	}

	/**
	 * @param eyeIndex
	 * @return the currentCNVStage
	 */
	public CNVStage getCurrentCNVStage(int eyeIndex) {
		return currentCNVStage[eyeIndex];
	}

	/**
	 * @return the va
	 */
	@SuppressWarnings("unchecked")
	public double getVA(int eyeIndex) {
		return ((LinkedList<VAProgressionPair>)vaProgression[eyeIndex]).getLast().va;
	}

	/** 
	 * Updates the progression and current visual acuity of the specified eye. The progression is updated with the list of changes.
	 * The last change is used to set the current visual acuity. 
	 * @param newVAs List of changes in visual acuity since the last update 
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 */
	@SuppressWarnings("unchecked")
	private void updateVA(ArrayList<VAProgressionPair> newVAs, int eyeIndex) {
		for (VAProgressionPair pair : newVAs) {
			lastVAChangeTs[eyeIndex] += pair.timeToChange;
			((LinkedList<VAProgressionPair>)vaProgression[eyeIndex]).add(pair);
		}
		setUtility(armdParams.getUtilityFromVA(this));
	}

	/**
	 * @return the vaProgression
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<VAProgressionPair> getVaProgression(int eyeIndex) {
		return (LinkedList<VAProgressionPair>)vaProgression[eyeIndex];
	}

	/**
	 * @param eyeIndex
	 * @return the lastVAChangeTs
	 */
	public long getLastVAChangeTs(int eyeIndex) {
		return lastVAChangeTs[eyeIndex];
	}

	/**
	 * Returns true if both eyes are healthy
	 * @return True if both eyes are healthy; false in other case.
	 */
	public boolean isHealthy() {
		return eyes[0].contains(EyeState.HEALTHY) && eyes[1].contains(EyeState.HEALTHY);
	}
	
	/**
	 * Returns a random number between 0 and 1 for the specified item
	 * @param item Tha case that requires a random number
	 * @return a random number between 0 and 1 for the specified item
	 */
	public double getRandomNumber(RandomForPatient.ITEM item) {
		return rng.getRandomNumber(item);
	}
	
	/**
	 * Returns N random number between 0 and 1 for the specified item
	 * @param item The case that requires a random number
	 * @param n The number of random numbers to return
	 * @return N random number between 0 and 1 for the specified item
	 */
	public double[] getRandomNumber(RandomForPatient.ITEM item, int n) {
		return rng.getRandomNumber(item, n);
	}
	
	/**
	 * @return the time to a specific eye state
	 */
	public long getTimeToEyeState(EyeState state, int eye) {
		final long time;
		switch (state) {
		case AMD_CNV:
			time = (cNVEvent[eye] == null) ? Long.MAX_VALUE : cNVEvent[eye].getTs();
			break;
		case AMD_GA:
			time = (gAEvent[eye] == null) ? Long.MAX_VALUE : gAEvent[eye].getTs();
			break;
		case EARM:
			time = (eARMEvent[eye] == null) ? Long.MAX_VALUE : eARMEvent[eye].getTs();
			break;
		case HEALTHY:
			time = 0;
			break;
		case CDME:
		case NCDME:
		case NPDR:
		case PDR:
		default:
			time = Long.MAX_VALUE;
		}
		return time;
	}

	/**
	 * 
	 * @param stage
	 * @param eye
	 * @return
	 */
	public long getTimeToCNVStage(CNVStage stage, int eye) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eye])) {			
			if (stage.equals(event.getNewStage()))
				return event.getTs();
		}
		return Long.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param stage
	 * @param eye
	 * @return
	 */
	public double getAgeAt(CNVStage stage, int eye) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eye])) {			
			if (stage.equals(event.getNewStage()))
				return (initAge + event.getTs()) / 365.0;
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param state
	 * @param eye
	 * @return
	 */
	public double getAgeAt(EyeState state, int eye) {
		long ageAt = getTimeToEyeState(state, eye);
		if (ageAt != Long.MAX_VALUE)
			return (initAge + ageAt) / 365.0;
		return Double.MAX_VALUE;
	}
	
	/**
	 * Last things to do when the patient is death, and before the {@link FinalizeEvent} event is launched.
	 */
	protected void death() {
		// Updates VA changes produced since the last event
		updateVA(armdParams.getVAProgressionToDeath(this, 0), 0);
		updateVA(armdParams.getVAProgressionToDeath(this, 1), 1);
	}
	
	protected final class EARMEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public EARMEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			// Update VA changes before changing the state. Theoretically, no changes should occur with healthy eyes
			updateVA(armdParams.getVAProgression(Patient.this, eyeIndex, EyeState.EARM), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.add(EyeState.EARM);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.ARMD);

			EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(Patient.this, eyeIndex);
			if (timeAndState != null) {
				if (EyeState.AMD_CNV == timeAndState.getState()) {
					cNVEvent[eyeIndex] = new CNVEvent(timeAndState.getValue(), eyeIndex);
					addEvent(cNVEvent[eyeIndex]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eyeIndex] = new GAEvent(timeAndState.getValue(), eyeIndex);
					addEvent(gAEvent[eyeIndex]);
				}
			}
			// Schedule events for fellow eye if needed
			if (eyeIndex == 0) {
				final EyeStateAndValue timeAndStateE2 = armdParams.getTimeToE2AMD(Patient.this);
				if (timeAndStateE2 != null) {
					if (EyeState.AMD_CNV == timeAndStateE2.getState()) {
						cNVEvent[1] = new CNVEvent(timeAndStateE2.getValue(), 1);
						addEvent(cNVEvent[1]);
					}
					else if (EyeState.AMD_GA == timeAndStateE2.getState()) {
						gAEvent[1] = new GAEvent(timeAndStateE2.getValue(), 1);
						addEvent(gAEvent[1]);
					}
				}
			}
			// Checks diagnosis
			checkDiagnosis();
		}
		
	}

	protected abstract class AMDEvent extends DiscreteEvent {
		protected final int eyeIndex;
		public AMDEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}
		
		protected void checkFellowEye() {
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> fellowEye = (EnumSet<EyeState>)eyes[1 - eyeIndex];
			// If the other eye had CNV there's nothing else to do...
			//... but if it had GA...
			if (fellowEye.contains(EyeState.AMD_GA)) {
				final long newTimeToCNV = armdParams.getTimeToCNVFromGA(Patient.this, 1 - eyeIndex);
				if (newTimeToCNV < getTimeToEyeState(EyeState.AMD_CNV, 1 - eyeIndex)) {
					// If a CNV event was previously scheduled to happen later than the new event
					// we have to cancel it 
					if (cNVEvent[1 - eyeIndex] != null) {
						cNVEvent[1 - eyeIndex].cancel();
					}
					cNVEvent[1 - eyeIndex] = new CNVEvent(newTimeToCNV, 1 - eyeIndex);
					addEvent(cNVEvent[1 - eyeIndex]);
				}
			}
			// Only the first eye could have EARM
			else if (fellowEye.contains(EyeState.EARM)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToAMDFromEARM(Patient.this, 1 - eyeIndex);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
			// Only the second eye could be healthy
			else if (fellowEye.contains(EyeState.HEALTHY)) {
				// Recompute time to event
				final EyeStateAndValue timeAndState = armdParams.getTimeToE2AMD(Patient.this);
				// If a valid event appeared
				if (timeAndState != null) {
					rescheduleAMDEvent(timeAndState, 1 - eyeIndex);
				}
			}
		}
		
		private void rescheduleAMDEvent(EyeStateAndValue timeAndState, int eye) {
			// If the new event happens before the already scheduled ones (in case an AMD was already schedule)
			if (timeAndState.getValue() < getTimeToEyeState(EyeState.AMD_CNV, eye) || timeAndState.getValue() < getTimeToEyeState(EyeState.AMD_GA, eye)) {
				// If a CNV event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (cNVEvent[eye] != null) {
					cNVEvent[eye].cancel();
				}
				// If a GA event was previously scheduled to happen later than the new event
				// we have to cancel it 
				if (gAEvent[eye] != null) {
					gAEvent[eye].cancel();
				}
				// Schedule the new event
				if (EyeState.AMD_CNV == timeAndState.getState()) {
					cNVEvent[eye] = new CNVEvent(timeAndState.getValue(), eye);
					addEvent(cNVEvent[eye]);
				}
				else if (EyeState.AMD_GA == timeAndState.getState()) {
					gAEvent[eye] = new GAEvent(timeAndState.getValue(), eye);
					addEvent(gAEvent[eye]);
				}
			}
		}
	}
	
	protected final class GAEvent extends AMDEvent {

		public GAEvent(long ts, int eyeIndex) {
			super(ts, eyeIndex);
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				gAEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. Theoretically, no changes should occur with EARM eyes
			updateVA(armdParams.getVAProgression(Patient.this, eyeIndex, EyeState.AMD_GA), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			
			// Assign new stage
			affectedEye.add(EyeState.AMD_GA);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.ARMD);

			// Schedule a CNV event
			final long timeToEvent = armdParams.getTimeToCNVFromGA(Patient.this, eyeIndex);
			if (timeToEvent < Long.MAX_VALUE) {
				cNVEvent[eyeIndex] = new CNVEvent(timeToEvent, eyeIndex);
				addEvent(cNVEvent[eyeIndex]);
			}
			
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
			
			// Checks diagnosis
			checkDiagnosis();
		}			
		
	}
	
	protected final class CNVEvent extends AMDEvent {

		public CNVEvent(long ts, int eyeIndex) {
			super(ts, eyeIndex);
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				cNVEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Advances the calculation of the incident CNV stage
			final CNVStage stage = armdParams.getInitialCNVStage(Patient.this, eyeIndex);			
			// Update VA changes before changing the state. 
			updateVA(armdParams.getVAProgression(Patient.this, eyeIndex, stage), eyeIndex);
			
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			affectedEye.remove(EyeState.AMD_GA);

			// Assign new stage
			affectedEye.add(EyeState.AMD_CNV);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.ARMD);

			// Assign specific CNV stage
			final CNVStageEvent newEvent = new CNVStageEvent(ts, stage, eyeIndex);
			((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
			addEvent(newEvent);
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
			
			// Checks diagnosis
			checkDiagnosis();			
		}		
	}
	
	protected final class CNVStageEvent extends DiscreteEvent {
		private final int eyeIndex;
		private final CNVStage newStage;
		
		public CNVStageEvent(long ts, CNVStage newStage, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
			this.newStage = newStage;
		}

		/**
		 * @return the newStage
		 */
		public CNVStage getNewStage() {
			return newStage;
		}

		@Override
		public void event() {
			// Only update VA if it's a progression in CNV stage and not setting the first CNV stage
			if (currentCNVStage[eyeIndex] != null) {
				updateVA(armdParams.getVAProgression(Patient.this, eyeIndex, newStage), eyeIndex);				
			}
			currentCNVStage[eyeIndex] = newStage;
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_CNV_STAGE, eyeIndex, this.getTs()));
			// Schedule next CNV stage
			CNVStageAndValue nextStage = armdParams.getTimeToNextCNVStage(Patient.this, eyeIndex);
			if (nextStage != null) {
				final CNVStageEvent newEvent = new CNVStageEvent(nextStage.getValue(), nextStage.getStage(), eyeIndex);
				((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
				addEvent(newEvent);
			}
			
			// Checks diagnosis
			checkDiagnosis();
		}
		
	}
	
	protected final class ScreeningEvent extends DiscreteEvent {
		private final DiscreteCycleIterator iterator;

		public ScreeningEvent(long ts, DiscreteCycleIterator screeningIterator) {
			super(ts);
			this.iterator = screeningIterator;
		}

		@Override
		public void event() {
			// Patient healthy
			if (isHealthy()) {
				// True negative
				if (rng.getRandomNumber(RandomForPatient.ITEM.SPECIFICITY) > ((Screening)intervention).getSpecificity()) {
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// False positive
				else {
					// TODO: Add costs of false positive						
				}
			}
			// Patient ill
			else {
				// False negative
				if (rng.getRandomNumber(RandomForPatient.ITEM.SENSITIVITY) > ((Screening)intervention).getSensitivity()) {
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// True positive
				else {
					diagnosed = ts;
					// TODO: Add costs of true positive						
				}					
			}
		}
		
	}
	
	protected final class DiabetesEvent extends DiscreteEvent {
		
		public DiabetesEvent(long ts) {
			super(ts);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void event() {
			diabeticSince = getAge();
			diabetesType = commonParams.getDiabetesType(Patient.this);
			// TODO: Check if it has sense to go directly from diabetes to some degree of DR. It would make sense if it were 
			// "diagnosed" diabetes and not "real" diabetes
			final EnumSet<EyeState> startWith = drParams.startsWith(Patient.this);
			// TODO: Check if bilateral is required
			if (startWith.size() > 0) {
				eyes[0].remove(EyeState.HEALTHY);
				affectedBy.add(DISEASES.DR);
				for (EyeState state : startWith)
					((EnumSet<EyeState>)eyes[0]).add(state);
			}
			if (eyes[0].contains(EyeState.NPDR)) {
				final long timeToEvent = drParams.getTimeToPDR(Patient.this);
				if (timeToEvent < Long.MAX_VALUE) {
					pDREvent = new ProliferativeDREvent(timeToEvent, 0);
					addEvent(pDREvent);
				}
			}
			else if (eyes[0].contains(EyeState.PDR)) {
				final long timeToEvent = drParams.getTimeToCSME(Patient.this);
				if (timeToEvent < Long.MAX_VALUE) {
					cSMEEvent = new ClinicallySignificantDMEEvent(timeToEvent, 0);
					addEvent(cSMEEvent);
				}
			}
			else {
				final long timeToEvent = drParams.getTimeToNPDR(Patient.this);
				if (timeToEvent < Long.MAX_VALUE) {
					nPDREvent = new NonProliferativeDREvent(timeToEvent, 0);
					addEvent(nPDREvent);
				}
			}
			// TODO: Add actions related to become diabetic (death???)
		}		
	}
	
	protected final class NonProliferativeDREvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public NonProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			
			// Assign new stage
			affectedEye.add(EyeState.NPDR);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.DR);
			
			// Schedules a new event 
			final long timeToEvent = drParams.getTimeToPDR(Patient.this);
			if (timeToEvent < Long.MAX_VALUE) {
				pDREvent = new ProliferativeDREvent(timeToEvent, eyeIndex);
				addEvent(pDREvent);
			}
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class ProliferativeDREvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public ProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.NPDR);
			
			// Assign new stage
			affectedEye.add(EyeState.PDR);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.DR);
			
			// Schedules a new event 
			final long timeToEvent = drParams.getTimeToCSME(Patient.this);
			if (timeToEvent < Long.MAX_VALUE) {
				cSMEEvent = new ClinicallySignificantDMEEvent(timeToEvent, 0);
				addEvent(cSMEEvent);
			}
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class DMEEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public DMEEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
		}		
	}
	
	protected final class ClinicallySignificantDMEEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public ClinicallySignificantDMEEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public void event() {
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			// This state is not incompatible with DR, so there is no need to remove any other DR stage
			affectedEye.remove(EyeState.NCDME);			
			
			// Assign new stage
			affectedEye.add(EyeState.CDME);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.CHANGE_EYE_STATE, eyeIndex, this.getTs()));
			affectedBy.add(DISEASES.DR);
			
			// Checks diagnosis
			checkDiagnosis();
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
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DEATH, this.getTs()));
			death();
			notifyEnd();
		}
	
	}
}
