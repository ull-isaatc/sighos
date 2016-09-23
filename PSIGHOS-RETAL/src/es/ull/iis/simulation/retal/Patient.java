/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.outcome.Cost;
import es.ull.iis.simulation.retal.outcome.QualityAdjustedLifeExpectancy;
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
	private EnumSet<RETALSimulation.DISEASES> affectedBy = EnumSet.noneOf(RETALSimulation.DISEASES.class); 
	/** The original patient, this one was cloned from */ 
	private Patient clonedFrom;
	/** The intervention branch that this "clone" of the patient belongs to */
	private final int nIntervention;
	/** Initial age of the patient (stored in days) */
	private final double initAge;
	/** Sex of the patient: 0 for men, 1 for women */
	private final int sex;
	/** Type of DM; -1 if no diabetic */
	private int diabetesType = -1;
	
	/** The specific intervention assigned to the patient */
	protected final Intervention intervention;
	/** The timestamp of the last event executed (but the current one) */
	private long lastTs = -1;
	/** The timestamp when this patient enters the simulation */
	private long startTs;
	/** The current currentUtility applied to this patient */
	private double currentUtility = 1.0;
	/** The cost measured for this patient */
	private final Cost cost;
	/** The QALYs for this patient */
	private final QualityAdjustedLifeExpectancy qaly; 
	
	/** Random number generators for initial risks to be compared with specific probabilities */
	private final RandomForPatient rng;
	// Events
	private DeathEvent deathEvent = null;
	private DiabetesEvent diabetesEvent = null;
	private final EARMEvent[] eARMEvent = {null, null};
	private final GAEvent[] gAEvent = {null, null};
	private final CNVEvent[] cNVEvent = {null, null};
	private final LinkedList<?>[] cNVStageEvents = {new LinkedList<CNVStageEvent>(), new LinkedList<CNVStageEvent>()};
	private final LinkedList<?>[] vaProgression = {new LinkedList<VAProgressionPair>(), new LinkedList<VAProgressionPair>()};
	private NonProliferativeDREvent[] nPDREvent = {null, null};;
	private NonHighRiskProliferativeDREvent[] nonHRPDREvent = {null, null};;
	private HighRiskProliferativeDREvent[] hRPDREvent = {null, null};;
	private ClinicallySignificantDMEEvent[] cSMEEvent = {null, null};;
	private DiagnoseEvent diagnoseEvent = null;
	/** Last timestamp when VA changed */
	private final long[] lastVAChangeTs = new long[2];
	/** Timestamp when treatmen with antiVEGF for CNV started */
	private final long[] onAntiVEGFCNV = {Long.MAX_VALUE, Long.MAX_VALUE};
	/** Timestamp when treatmen with antiVEGF for CSME started */
	private final long[] onAntiVEGFCSME = {Long.MAX_VALUE, Long.MAX_VALUE};
	
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
	@SuppressWarnings("unchecked")
	public Patient(RETALSimulation simul, double initAge, Intervention intervention) {
		super(simul.getPatientCounter(), simul);
		this.rng = new RandomForPatient();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
		this.drParams = simul.getDrParams();

		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.initAge = 365*initAge;
		this.sex = commonParams.getSex(this);
		this.clonedFrom = null;
		this.cost = simul.getCost();
		this.qaly = simul.getQaly();
		
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
		setUtility(commonParams.getUtilityFromVA(this));
	}

	/**
	 * Creates a new patient who is a clone of another one and who is assigned to a different intervention
	 * @param original The original patient whose attributes will be cloned 
	 * @param nIntervention New intervention this clone is assigned to
	 */
	@SuppressWarnings("unchecked")
	public Patient(RETALSimulation simul, Patient original, Intervention intervention) {
		super(original.id, simul);
		this.rng = new RandomForPatient(original.rng); 
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
		this.drParams = original.drParams;

		this.intervention = intervention;
		this.nIntervention = intervention.getId();
		this.clonedFrom = original;
		this.initAge = original.initAge;
		this.sex = original.sex;
		this.cost = original.cost;
		this.qaly = original.qaly;

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
		setUtility(commonParams.getUtilityFromVA(this));
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "PAT";
	}

	@Override
	protected void init() {
		startTs = this.getTs();
		simul.getInfoHandler().notifyInfo(new PatientInfo(this.simul, this, PatientInfo.Type.START, this.getTs()));

		// Checks if he/she is diabetic
		final long timeToDiabetes = commonParams.isDiabetic(this) ? startTs : commonParams.getTimeToDiabetes(this);
		if (timeToDiabetes < Long.MAX_VALUE) {
			diabetesEvent = new DiabetesEvent(timeToDiabetes);
			addEvent(diabetesEvent);
		}
		// If he/she is diabetic, the time to death has been already assigned
		if (!isDiabetic()) {
			deathEvent = new DeathEvent(commonParams.getTimeToDeath(this));
			addEvent(deathEvent);
		}
		
		if (RETALSimulation.ACTIVE_DISEASES.contains(RETALSimulation.DISEASES.ARMD)) {
			// Schedules ARMD-related events
			final long timeToEvent = armdParams.getTimeToEARM(this);
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
	 * @return the diabetesType
	 */
	public int getDiabetesType() {
		return diabetesType;
	}

	public boolean isDiabetic() {
		return diabetesType != -1;
	}
	
	/**
	 * @return the timeToDeath
	 */
	public long getTimeToDeath() {
		return (deathEvent == null) ? Long.MAX_VALUE : deathEvent.getTs();
	}

	public double getAgeAtDeath() {
		final long timeToDeath = getTimeToDeath();
		return (timeToDeath == Long.MAX_VALUE) ? Double.MAX_VALUE : ((initAge + timeToDeath) / 365.0);
	}
	
	public long getTimeToDiabetes() {
		return (diabetesEvent == null) ? Long.MAX_VALUE : diabetesEvent.getTs();
	}

	public double getAgeAtDiabetes() {
		if (diabetesEvent == null)
			return Double.MAX_VALUE;
		return (initAge + diabetesEvent.getTs()) / 365.0;
	}
	
	/**
	 * Returns the timestamp when the patient started treatment with antiVEGF in the specified eye for CNV
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the timestamp when the patient started treatment with antiVEGF in the specified eye for CNV
	 */
	public long getOnAntiVEGFCNV(int eyeIndex) {
		return onAntiVEGFCNV[eyeIndex];
	}

	/**
	 * Returns the timestamp when the patient started treatment with antiVEGF in the specified eye for CSME
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the timestamp when the patient started treatment with antiVEGF in the specified eye for CSME
	 */
	public long getOnAntiVEGFCSME(int eyeIndex) {
		return onAntiVEGFCSME[eyeIndex];
	}

	/**
	 * @return the clonedFrom
	 */
	public Patient getClonedFrom() {
		return clonedFrom;
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
		super.setTs(ts);
		if (lastTs != ts) {
			final double initAge = TimeUnit.DAY.convert(lastTs, simul.getTimeUnit()) / 365.0; 
			final double endAge = TimeUnit.DAY.convert(ts, simul.getTimeUnit()) / 365.0;
			lastTs = this.ts;
			final double periodCost = commonParams.getCostForState(this, initAge, endAge);
			cost.update(this, periodCost, initAge, endAge);
			qaly.update(this, currentUtility, initAge, endAge);
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
		if (diagnoseEvent == null)
			return false;
		return (diagnoseEvent.getTs() <= ts);
	}
	
	/**
	 * Updates the state of the patient to reflect whether he/she has been diagnosed
	 */
	public void checkDiagnosis() {
		final long timeToDiagnosis = (diagnoseEvent == null) ? Long.MAX_VALUE : diagnoseEvent.getTs(); 
		if (timeToDiagnosis > ts) {
			long timeToARMDDiagnosis = Long.MAX_VALUE;
			if (affectedBy.contains(RETALSimulation.DISEASES.ARMD)) {
				timeToARMDDiagnosis = armdParams.getTimeToClinicalPresentation(this);
			}
			long timeToDRDiagnosis = Long.MAX_VALUE;
			if (affectedBy.contains(RETALSimulation.DISEASES.DR)) {
				timeToDRDiagnosis = drParams.getTimeToClinicalPresentation(this);
			}
			if (timeToARMDDiagnosis < timeToDiagnosis) {
				if (timeToDiagnosis < Long.MAX_VALUE) {
					diagnoseEvent.cancel();
				}					
				if (timeToDRDiagnosis < timeToARMDDiagnosis) {
					diagnoseEvent = new DiagnoseEvent(timeToDRDiagnosis);					
				}
				else {
					diagnoseEvent = new DiagnoseEvent(timeToARMDDiagnosis);					
				}
				addEvent(diagnoseEvent);
			}
			else if (timeToDRDiagnosis < timeToDiagnosis) {
				if (timeToDiagnosis < Long.MAX_VALUE) {
					diagnoseEvent.cancel();
				}					
				diagnoseEvent = new DiagnoseEvent(timeToDRDiagnosis);					
				addEvent(diagnoseEvent);				
			}
		}
	}

	/**
	 * @return the affectedBy
	 */
	public EnumSet<RETALSimulation.DISEASES> getAffectedBy() {
		return affectedBy;
	}

	/**
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
	 * @return the state of the first eye
	 */
	@SuppressWarnings("unchecked")
	public EnumSet<EyeState> getEyeState(int eyeIndex) {
		return (EnumSet<EyeState>) eyes[eyeIndex];
	}

	/**
	 * @param eyeIndex Index of the eye (0 for first eye, 1 for second eye)
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
		setUtility(commonParams.getUtilityFromVA(this));
	}

	/**
	 * @return the vaProgression
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<VAProgressionPair> getVaProgression(int eyeIndex) {
		return (LinkedList<VAProgressionPair>)vaProgression[eyeIndex];
	}

	/**
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
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
	 * @param item The case that requires a random number
	 * @return a random number between 0 and 1 for the specified item
	 */
	public double draw(RandomForPatient.ITEM item) {
		return rng.draw(item);
	}
	
	/**
	 * Returns N random number between 0 and 1 for the specified item
	 * @param item The case that requires a random number
	 * @param n The number of random numbers to return
	 * @return N random number between 0 and 1 for the specified item
	 */
	public double[] draw(RandomForPatient.ITEM item, int n) {
		return rng.draw(item, n);
	}
	
	/**
	 * @return the time to a specific eye state
	 */
	public long getTimeToEyeState(EyeState state, int eyeIndex) {
		final long time;
		switch (state) {
		case AMD_CNV:
			time = (cNVEvent[eyeIndex] == null) ? Long.MAX_VALUE : cNVEvent[eyeIndex].getTs();
			break;
		case AMD_GA:
			time = (gAEvent[eyeIndex] == null) ? Long.MAX_VALUE : gAEvent[eyeIndex].getTs();
			break;
		case EARM:
			time = (eARMEvent[eyeIndex] == null) ? Long.MAX_VALUE : eARMEvent[eyeIndex].getTs();
			break;
		case HEALTHY:
			time = 0;
			break;
		case CSME:
			time = (cSMEEvent[eyeIndex] == null) ? Long.MAX_VALUE : cSMEEvent[eyeIndex].getTs();
			break;
		case NPDR:
			time = (nPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : nPDREvent[eyeIndex].getTs(); 
			break;
		case NON_HR_PDR:
			time = (nonHRPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : nonHRPDREvent[eyeIndex].getTs();
			break;
		case HR_PDR:
			time = (hRPDREvent[eyeIndex] == null) ? Long.MAX_VALUE : hRPDREvent[eyeIndex].getTs(); 
			break;
		default:
			time = Long.MAX_VALUE;
		}
		return time;
	}

	/**
	 * 
	 * @param stage
	 * @param eyeIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public long getTimeToCNVStage(CNVStage stage, int eyeIndex) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex])) {			
			if (stage.equals(event.getNewStage()))
				return event.getTs();
		}
		return Long.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param stage
	 * @param eyeIndex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double getAgeAt(CNVStage stage, int eyeIndex) {
		for (CNVStageEvent event : ((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex])) {			
			if (stage.equals(event.getNewStage()))
				return (initAge + event.getTs()) / 365.0;
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * Returns the age that the patient had when a particular eye state was reached
	 * @param state
	 * @param eyeIndex
	 * @return The age that the patient had when a particular eye state was reached
	 */
	public double getAgeAt(EyeState state, int eyeIndex) {
		long ageAt = getTimeToEyeState(state, eyeIndex);
		if (ageAt != Long.MAX_VALUE)
			return (initAge + ageAt) / 365.0;
		return Double.MAX_VALUE;
	}
	
	/**
	 * Last things to do when the patient is death, and before the {@link FinalizeEvent} event is launched.
	 */
	protected void death() {
		// Updates VA changes produced since the last event
		updateVA(commonParams.getVAProgressionToDeath(this, 0), 0);
		updateVA(commonParams.getVAProgressionToDeath(this, 1), 1);
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
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.EARM), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.add(EyeState.EARM);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.EARM, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

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
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.AMD_GA), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			
			// Assign new stage
			affectedEye.add(EyeState.AMD_GA);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.AMD_GA, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

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
		
		@SuppressWarnings("unchecked")
		@Override
		public void event() {
			// Advances the calculation of the incident CNV stage
			final CNVStage stage = armdParams.getInitialCNVStage(Patient.this, eyeIndex);			
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, stage), eyeIndex);
			
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];
			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.EARM);
			affectedEye.remove(EyeState.AMD_GA);

			// Assign new stage
			affectedEye.add(EyeState.AMD_CNV);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.AMD_CNV, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.ARMD);

			// Assign specific CNV stage
			final CNVStageEvent newEvent = new CNVStageEvent(ts, stage, eyeIndex);
			((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
			addEvent(newEvent);
			// When the disease advances in an eye, the risk for the fellow eye increases
			checkFellowEye();
			
			// Checks diagnosis
			checkDiagnosis();
			// If already diagnosed, the treatment with antiVEGF starts
			if (isDiagnosed()) {
				onAntiVEGFCNV[eyeIndex] = ts;
			}
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

		@SuppressWarnings("unchecked")
		@Override
		public void event() {
			// Only update VA if it's a progression in CNV stage and not setting the first CNV stage
			if (currentCNVStage[eyeIndex] != null) {
				updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, newStage), eyeIndex);				
			}
			currentCNVStage[eyeIndex] = newStage;
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, newStage, eyeIndex, this.getTs()));
			// Schedule next CNV stage
			CNVStageAndValue nextStage = armdParams.getTimeToNextCNVStage(Patient.this, eyeIndex);
			if (nextStage != null) {
				final CNVStageEvent newEvent = new CNVStageEvent(nextStage.getValue(), nextStage.getStage(), eyeIndex);
				((LinkedList<CNVStageEvent>)cNVStageEvents[eyeIndex]).add(newEvent);
				addEvent(newEvent);
			}
			
			// Checks diagnosis
			checkDiagnosis();
			// If state worsens, the treatment with antiVEGF starts again
			if (isDiagnosed()) {
				onAntiVEGFCNV[eyeIndex] = ts;
			}
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
			final double screenCost = commonParams.getScreeningCost(Patient.this);
			cost.update(Patient.this, screenCost, getAge());
			// Patient healthy
			if (isHealthy()) {
				// True negative
				if (rng.draw(RandomForPatient.ITEM.SPECIFICITY) <= ((Screening)intervention).getSpecificity(Patient.this)) {
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.ScreeningResult.TN, this.getTs()));
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// False positive
				else {
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.ScreeningResult.FP, this.getTs()));
					final double diagnosisCost = commonParams.getDiagnosisCost(Patient.this);
					cost.update(Patient.this, diagnosisCost, getAge());
				}
			}
			// Patient ill
			else {
				// False negative
				if (rng.draw(RandomForPatient.ITEM.SENSITIVITY) > ((Screening)intervention).getSensitivity(Patient.this)) {
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.ScreeningResult.FN, this.getTs()));
					// Schedule next screening appointment (if required) 
					long next = iterator.next();
					if (next != -1) {
						addEvent(new ScreeningEvent(next, iterator));
					}
				}
				// True positive
				else {
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.ScreeningResult.TP, this.getTs()));
					if (diagnoseEvent != null)
						diagnoseEvent.cancel();
					diagnoseEvent = new DiagnoseEvent(ts);
					addEvent(diagnoseEvent);
					// TODO: Add changes of true positive						
				}					
			}
		}
		
	}
	
	protected final class DiagnoseEvent extends DiscreteEvent {
		
		public DiagnoseEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			final double diagnosisCost = commonParams.getDiagnosisCost(Patient.this);
			cost.update(Patient.this, diagnosisCost, getAge());
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DIAGNOSED, this.getTs()));
			// If suffering CNV when diagnosed, the treatment with antiVEGF starts
			if (eyes[0].contains(EyeState.AMD_CNV))
				onAntiVEGFCNV[0] = ts;
			if (eyes[1].contains(EyeState.AMD_CNV))
				onAntiVEGFCNV[1] = ts;
			// If suffering CSME when diagnosed, the treatment with antiVEGF starts
			if (eyes[0].contains(EyeState.CSME))
				onAntiVEGFCSME[0] = ts;
			if (eyes[1].contains(EyeState.CSME))
				onAntiVEGFCSME[1] = ts;
		}
		
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				diagnoseEvent = null;
				return true;
			}
			return false;
		}
	}
	
	protected final class DiabetesEvent extends DiscreteEvent {
		
		public DiabetesEvent(long ts) {
			super(ts);
		}

		@SuppressWarnings("unchecked")
		private void assignInitialEyeState(EnumSet<EyeState> startWith, int eyeIndex) {
			if (startWith.size() > 0) {
				eyes[eyeIndex].remove(EyeState.HEALTHY);
				for (EyeState state : startWith)
					((EnumSet<EyeState>)eyes[eyeIndex]).add(state);
				affectedBy.add(RETALSimulation.DISEASES.DR);

				if (eyes[eyeIndex].contains(EyeState.NPDR)) {
					// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
					nPDREvent[eyeIndex] = new NonProliferativeDREvent(ts, eyeIndex);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.NPDR, eyeIndex, ts));						
					final long timeToEvent = drParams.getTimeToPDR(Patient.this);
					if (timeToEvent < Long.MAX_VALUE) {
						nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToEvent, eyeIndex);
						addEvent(nonHRPDREvent[eyeIndex]);
					}
					if (!eyes[eyeIndex].contains(EyeState.CSME)) {
						final long timeToCSME = drParams.getTimeToCSME(Patient.this);
						if (timeToCSME < Long.MAX_VALUE) {
							cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
							addEvent(cSMEEvent[eyeIndex]);
						}
					}
				}
				else if (eyes[eyeIndex].contains(EyeState.NON_HR_PDR)) {
					// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
					nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(ts, eyeIndex);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.NON_HR_PDR, eyeIndex, ts));						
					if (!eyes[eyeIndex].contains(EyeState.CSME)) {
						final long timeToCSME = drParams.getTimeToCSMEAndNonHRPDRFromNonHRPDR(Patient.this);
						if (timeToCSME < Long.MAX_VALUE) {
							cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
							addEvent(cSMEEvent[eyeIndex]);
						}
						final long timeToHRPDR = drParams.getTimeToHRPDRFromNonHRPDR(Patient.this);
						if (timeToHRPDR < Long.MAX_VALUE) {
							hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
							addEvent(hRPDREvent[eyeIndex]);
						}					
					}
					else {
						final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(Patient.this);
						if (timeToHRPDR < Long.MAX_VALUE) {
							hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
							addEvent(hRPDREvent[eyeIndex]);
						}										
					}				
				}
				else if (eyes[eyeIndex].contains(EyeState.HR_PDR)) {
					// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
					hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(ts, eyeIndex);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.HR_PDR, eyeIndex, ts));						
					if (!eyes[eyeIndex].contains(EyeState.CSME)) {
						final long timeToCSME = drParams.getTimeToCSMEAndHRPDRFromHRPDR(Patient.this);
						if (timeToCSME < Long.MAX_VALUE) {
							cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
							addEvent(cSMEEvent[eyeIndex]);
						}
					}
				}
				if (eyes[eyeIndex].contains(EyeState.CSME)) {
					// Assign the event but not execute it. This is done to ensure that "timeTo..." and "ageAt..." methods work fine 
					cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(ts, eyeIndex);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.CSME, eyeIndex, ts));						
				}
			}
			else {
				final long timeToEvent = drParams.getTimeToNPDR(Patient.this);
				if (timeToEvent < Long.MAX_VALUE) {
					nPDREvent[eyeIndex] = new NonProliferativeDREvent(timeToEvent, eyeIndex);
					addEvent(nPDREvent[eyeIndex]);
				}
			}
			// Update the visual acuity to reflect the incident problem
			// FIXME: Why I'm doing this here???????? If healthy... why updating VA???
			final ArrayList<VAProgressionPair> newVA = new ArrayList<VAProgressionPair>(); 
			newVA.add(new VAProgressionPair(ts, commonParams.getInitialVA(Patient.this, eyeIndex)));			
			updateVA(newVA, eyeIndex);			
		}
		
		@Override
		public void event() {
			diabetesType = commonParams.getDiabetesType(Patient.this);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, PatientInfo.Type.DIABETES, this.getTs()));			
			
			if (RETALSimulation.ACTIVE_DISEASES.contains(RETALSimulation.DISEASES.DR)) {
				// TODO: Check if it has sense to go directly from diabetes to some degree of DR. It would make sense if it were 
				// "diagnosed" diabetes and not "real" diabetes
				final EnumSet<EyeState>[] startWith = drParams.startsWith(Patient.this);
				// Initializes the state of both eyes
				assignInitialEyeState(startWith[0], 0);
				assignInitialEyeState(startWith[1], 1);
				// Check if the new state arises the diagnosis
				checkDiagnosis();
			}
			long newTimeToDeath = commonParams.getTimeToDeathDiabetic(Patient.this);
			if (newTimeToDeath < getTimeToDeath()) {
				if (deathEvent != null) {
					deathEvent.cancel();
					deathEvent = new DeathEvent(newTimeToDeath);
					addEvent(deathEvent);
					// Cancel all pending events scheduled to happen after death
					if (diabetesEvent != null) {
						if (diabetesEvent.getTs() >= newTimeToDeath) {
							diabetesEvent.cancel();
							diabetesEvent = null;
						}
					}
					if (diagnoseEvent != null) {
						if (diagnoseEvent.getTs() >= newTimeToDeath) {
							diagnoseEvent.cancel();
							diagnoseEvent = null;
						}
					}
					for (int i = 0; i < 2; i++) {
						if (eARMEvent[i] != null) {
							if (eARMEvent[i].getTs() >= newTimeToDeath) {
								eARMEvent[i].cancel();
								eARMEvent[i] = null;
							}
						}
						if (gAEvent[i] != null) {
							if (gAEvent[i].getTs() >= newTimeToDeath) {
								gAEvent[i].cancel();
								gAEvent[i] = null;
							}
						}
						if (cNVEvent[i] != null) {
							if (cNVEvent[i].getTs() >= newTimeToDeath) {
								cNVEvent[i].cancel();
								cNVEvent[i] = null;
							}
						}
						if (nPDREvent[i] != null) {
							if (nPDREvent[i].getTs() >= newTimeToDeath) {
								nPDREvent[i].cancel();
								nPDREvent[i] = null;
							}
						}
						if (nonHRPDREvent[i] != null) {
							if (nonHRPDREvent[i].getTs() >= newTimeToDeath) {
								nonHRPDREvent[i].cancel();
								nonHRPDREvent[i] = null;
							}
						}
						if (hRPDREvent[i] != null) {
							if (hRPDREvent[i].getTs() >= newTimeToDeath) {
								hRPDREvent[i].cancel();
								hRPDREvent[i] = null;
							}
						}
						if (cSMEEvent[i] != null) {
							if (cSMEEvent[i].getTs() >= newTimeToDeath) {
								cSMEEvent[i].cancel();
								cSMEEvent[i] = null;
							}
						}
						@SuppressWarnings("unchecked")
						Iterator<CNVStageEvent> iter = (Iterator<CNVStageEvent>)cNVStageEvents[i].iterator();
						while (iter.hasNext()) {
							final CNVStageEvent event = iter.next();
							if (event.getTs() >= newTimeToDeath) {
								event.cancel();
								iter.remove();
							}
						}
					}
				}
				else {
					deathEvent = new DeathEvent(newTimeToDeath);
					addEvent(deathEvent);
				}
			}
		}		
	}
	
	protected final class NonProliferativeDREvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public NonProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				nPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.NPDR), eyeIndex);
			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			
			// Assign new stage
			affectedEye.add(EyeState.NPDR);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.NPDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// Schedules events for PDR and CSME 
			final long timeToEvent = drParams.getTimeToPDR(Patient.this);
			if (timeToEvent < Long.MAX_VALUE) {
				nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToEvent, eyeIndex);
				addEvent(nonHRPDREvent[eyeIndex]);
			}
			final long timeToCSME = drParams.getTimeToCSME(Patient.this);
			if (timeToCSME < Long.MAX_VALUE) {
				cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
				addEvent(cSMEEvent[eyeIndex]);
			}
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class NonHighRiskProliferativeDREvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public NonHighRiskProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				nonHRPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.NON_HR_PDR), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.NPDR);
			
			// Assign new stage
			affectedEye.add(EyeState.NON_HR_PDR);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.NON_HR_PDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events or reschedule existing ones
			final long timeToHRPDR;
			if (!affectedEye.contains(EyeState.CSME)) {
				final long timeToCSME = drParams.getTimeToCSMEAndNonHRPDRFromNonHRPDR(Patient.this);
				if (timeToCSME < getTimeToEyeState(EyeState.CSME, eyeIndex)) {
					if (cSMEEvent[eyeIndex] != null) {
						cSMEEvent[eyeIndex].cancel();
					}
					cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
					addEvent(cSMEEvent[eyeIndex]);
				}
				timeToHRPDR = drParams.getTimeToHRPDRFromNonHRPDR(Patient.this);
			}
			else {
				timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSMEAndNonHRPDR(Patient.this);
			}
			// TODO: Check if it could arrive here with an already scheduled HR_DR event
			if (timeToHRPDR < Long.MAX_VALUE) {
				hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
				addEvent(hRPDREvent[eyeIndex]);
			}										
			
			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class HighRiskProliferativeDREvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public HighRiskProliferativeDREvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				hRPDREvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.HR_PDR), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			affectedEye.remove(EyeState.NPDR);
			
			// Assign new stage
			affectedEye.add(EyeState.HR_PDR);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.HR_PDR, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events or reschedule existing ones
			if (!affectedEye.contains(EyeState.CSME)) {
				final long timeToCSME = drParams.getTimeToCSMEAndHRPDRFromHRPDR(Patient.this);
				if (timeToCSME < getTimeToEyeState(EyeState.CSME, eyeIndex)) {
					if (cSMEEvent[eyeIndex] != null) {
						cSMEEvent[eyeIndex].cancel();
					}
					cSMEEvent[eyeIndex] = new ClinicallySignificantDMEEvent(timeToCSME, eyeIndex);
					addEvent(cSMEEvent[eyeIndex]);
				}
			}

			// Checks diagnosis
			checkDiagnosis();
		}		
	}
	
	protected final class ClinicallySignificantDMEEvent extends DiscreteEvent {
		private final int eyeIndex;
		
		public ClinicallySignificantDMEEvent(long ts, int eyeIndex) {
			super(ts);
			this.eyeIndex = eyeIndex;
		}

		@Override
		public boolean cancel() {
			if (super.cancel()) {
				cSMEEvent[eyeIndex] = null;
				return true;
			}
			return false;
		}
		
		@Override
		public void event() {
			// Update VA changes before changing the state. 
			updateVA(commonParams.getVAProgression(Patient.this, eyeIndex, EyeState.CSME), eyeIndex);

			@SuppressWarnings("unchecked")
			final EnumSet<EyeState> affectedEye = (EnumSet<EyeState>)eyes[eyeIndex];

			// Remove previous stages
			affectedEye.remove(EyeState.HEALTHY);
			
			// Assign new stage
			affectedEye.add(EyeState.CSME);
			simul.getInfoHandler().notifyInfo(new PatientInfo(simul, Patient.this, EyeState.CSME, eyeIndex, this.getTs()));
			affectedBy.add(RETALSimulation.DISEASES.DR);
			
			// schedule new events
			// If the eye is affected by non-high risk proliferative DR
			if (affectedEye.contains(EyeState.NON_HR_PDR)) {
				final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSME(Patient.this);
				if (timeToHRPDR < getTimeToEyeState(EyeState.HR_PDR, eyeIndex)) {
					if (hRPDREvent[eyeIndex] != null) {
						hRPDREvent[eyeIndex].cancel();
					}
					hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
					addEvent(hRPDREvent[eyeIndex]);
				}
			}
			// If the eye hadn't reached PDR yet
			else if (affectedEye.contains(EyeState.NPDR)) {
				final long timeToNonHRPDR = drParams.getTimeToCSMEAndNonHRPDRFromCSME(Patient.this);
				final long timeToHRPDR = drParams.getTimeToCSMEAndHRPDRFromCSME(Patient.this);
				if (timeToNonHRPDR < timeToHRPDR) {
					if (timeToNonHRPDR < getTimeToEyeState(EyeState.NON_HR_PDR, eyeIndex)) {
						if (nonHRPDREvent[eyeIndex] != null) {
							nonHRPDREvent[eyeIndex].cancel();
						}
						nonHRPDREvent[eyeIndex] = new NonHighRiskProliferativeDREvent(timeToNonHRPDR, eyeIndex);
						addEvent(nonHRPDREvent[eyeIndex]);
					}
				}
				else if (timeToHRPDR < Long.MAX_VALUE) {
					if (timeToHRPDR < getTimeToEyeState(EyeState.HR_PDR, eyeIndex)) {
						if (hRPDREvent[eyeIndex] != null) {
							hRPDREvent[eyeIndex].cancel();
						}
						hRPDREvent[eyeIndex] = new HighRiskProliferativeDREvent(timeToHRPDR, eyeIndex);
						addEvent(hRPDREvent[eyeIndex]);
					}
				}
			}
			
			// Checks diagnosis
			checkDiagnosis();
			// If already diagnosed, the treatment with antiVEGF starts
			if (isDiagnosed()) {
				onAntiVEGFCSME[eyeIndex] = ts;
			}
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
