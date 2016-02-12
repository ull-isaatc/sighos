/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.Random;

import es.ull.isaatc.util.DiscreteCycleIterator;

/**
 * @author Iván Castilla
 *
 */
public class OphthalmologicPatient extends Patient {
	public enum Disease {
		AMD,
		RD
	}

	public enum DiseaseStage {
		EARM1(Disease.AMD),
		AMD_CNV1(Disease.AMD),
		AMD_GA1(Disease.AMD),
		EARM2(Disease.AMD),
		AMD_CNV2(Disease.AMD),
		AMD_GA2(Disease.AMD);
		
		private final Disease disease;
		private DiseaseStage(Disease dis) {
			this.disease = dis;
		}
		
		/**
		 * @return the disease
		 */
		public Disease getDisease() {
			return disease;
		}
	}
	
	// Random number generators for initial risks to be compared with specific probabilities
	private static final Random RNG_P_CNV1 = new Random();
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	private static final int TEST_DELAY = 10;

	// Time to events and events
	private long timeToEARM;
	private long timeToAMD;
	private EARMEvent eARMEvent;
	private AMDEvent aMDEvent;

	/** The random reference to compare with the probability of developing CNV in the first eye */ 
	private final double rndProbCNV1;
	/** The random reference to compare with the sensitivity */
	private final double rndSensitivity;
	/** The random reference to compare with the specificity */
	private final double rndSpecificity;
	/** Current visual acuity, measured as logMAR units: 0 is the best possible vision*/
	private double va;
	/** Defines whether the patient is currently diagnosed */
	private boolean isDiagnosed = false;
	private final EnumSet<EyeState> eye1;
	private final EnumSet<EyeState> eye2;
	/** The current state of the patient */
	private final EnumSet<DiseaseStage> diseaseStage = EnumSet.noneOf(DiseaseStage.class);

	/**
	 * @param simul
	 * @param initAge
	 * @param sex
	 */
	public OphthalmologicPatient(RETALSimulation simul, double initAge, int sex) {
		super(simul, initAge, sex);
		eye1 = EnumSet.noneOf(EyeState.class);
		eye2 = EnumSet.noneOf(EyeState.class);
		// Check if all the events are required
		timeToEARM = simul.getTimeToEARM(this);
		timeToAMD = simul.getTimeToAMD(this);
		
		this.rndProbCNV1 = RNG_P_CNV1.nextDouble();
		this.rndSensitivity = RNG_SENSITIVITY.nextDouble();
		this.rndSpecificity = RNG_SPECIFICITY.nextDouble();
	}

	/**
	 * @param original
	 * @param nIntervention
	 */
	public OphthalmologicPatient(OphthalmologicPatient original, int nIntervention) {
		super(original, nIntervention);
		this.eARMEvent = new EARMEvent(original.getEARMEvent().getTs() + TEST_DELAY * nIntervention);
		this.aMDEvent = new AMDEvent(original.getAMDEvent().getTs() + TEST_DELAY * nIntervention);
		this.rndProbCNV1 = original.rndProbCNV1;
		this.rndSensitivity = original.rndSensitivity;
		this.rndSpecificity = original.rndSpecificity;
		this.eye1 = EnumSet.copyOf(original.eye1);
		this.eye2 = EnumSet.copyOf(original.eye2);
		// FIXME: See what happen to resourceUsage
	}

	@Override
	protected void init() {
		super.init();
		// Cancel EARM if death happens before
		if (timeToDeath <= timeToEARM) {
			this.eARMEvent = null;
			timeToEARM = Long.MAX_VALUE;
		}
		// Cancel AMD if death happens before
		if (timeToDeath <= timeToAMD) {
			this.aMDEvent = null;
			timeToAMD = Long.MAX_VALUE;
		}

		if (timeToEARM < timeToAMD) {
			this.aMDEvent = null;
			this.eARMEvent = new EARMEvent(timeToEARM);
			addEvent(eARMEvent);
		}
		else if (timeToAMD < Long.MAX_VALUE) {
			// EARM would never happen
			this.eARMEvent = null;
			this.aMDEvent = new AMDEvent(timeToAMD);
			addEvent(aMDEvent);
		}
		
		if (intervention instanceof Screening) {
			final DiscreteCycleIterator screeningIterator = ((Screening)intervention).getScreeningCycle().getCycle().iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
			addEvent(new ScreeningEvent(screeningIterator.next(), screeningIterator));
		}
	}
	
	/**
	 * @return the isDiagnosed
	 */
	public boolean isDiagnosed() {
		return isDiagnosed;
	}

	/**
	 * Updates the state of the patient to reflect that he/she has been diagnosed
	 */
	public void setDiagnosed() {
		this.isDiagnosed = true;
	}

	/**
	 * @return the diseaseStage
	 */
	public EnumSet<DiseaseStage> getDiseaseStage() {
		return diseaseStage;
	}

	public void progress(DiseaseStage newStage) {
		diseaseStage.add(newStage);
	}
	
	public void progressFrom(DiseaseStage oldStage, DiseaseStage newStage) {
		diseaseStage.remove(oldStage);
		progress(newStage);
	}
	
	public boolean isHealthy() {
		return diseaseStage.isEmpty();
	}
		
	/**
	 * Computes the cost associated to the current state between initAge and endAge
	 * @param initAge Age at which the patient starts using the resources
	 * @param endAge Age at which the patient ends using the resources 
	 * @return The accumulated cost during the defined period
	 */
	@Override
	public double computeCost(double initAge, double endAge) {
		double cost = 0.0;
		for(EyeState stage : eye1) {
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			for (ResourceUsageItem usage : res) {
				cost += usage.computeCost(initAge, endAge);
			}
		}
		for(EyeState stage : eye2) {
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			for (ResourceUsageItem usage : res) {
				cost += usage.computeCost(initAge, endAge);
			}
		}
		return cost;
	}
	
	/**
	 * @return The EARM event of this patient.
	 */
	public EARMEvent getEARMEvent() {
		return eARMEvent;
	}
	
	/**
	 * @return The AMD event of this patient.
	 */
	public AMDEvent getAMDEvent() {
		return aMDEvent;
	}

	public final class EARMEvent extends CancelableEvent {

		public EARMEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			if (!cancelled) {
				eye1.add(EyeState.EARM);
				progress(DiseaseStage.EARM1);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.EARM1, this.getTs()));
				long timeToAMD = ((RETALSimulation)simul).getTimeToAMDFromEARM(OphthalmologicPatient.this);
				// Schedule AMD event only if death is not happening before
				if (timeToAMD < timeToDeath) 
					aMDEvent = new AMDEvent(timeToAMD); 
			}			
		}
		
	}

	public final class AMDEvent extends CancelableEvent {

		public AMDEvent(long ts) {
			super(ts);
		}

		@Override
		public void event() {
			if (!cancelled) {
				eye1.remove(EyeState.EARM);
				// Switches state to AMD, either GA or CNV
				if (rndProbCNV1 <= ((RETALSimulation)simul).getProbabilityCNV(OphthalmologicPatient.this)) {
					eye1.add(EyeState.AMD_CNV);
					progressFrom(DiseaseStage.EARM1, DiseaseStage.AMD_CNV1);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.CNV1, this.getTs()));
				}
				else {
					eye1.add(EyeState.AMD_GA);
					progressFrom(DiseaseStage.EARM1, DiseaseStage.AMD_GA1);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, PatientInfo.Type.GA1, this.getTs()));
				}
			}			
		}
		
	}
	
	public final class ScreeningEvent extends CancelableEvent {
		private final DiscreteCycleIterator iterator;

		public ScreeningEvent(long ts, DiscreteCycleIterator screeningIterator) {
			super(ts);
			this.iterator = screeningIterator;
		}

		@Override
		public void event() {
			if (!cancelled) {
				// Patient healthy
				if (isHealthy()) {
					// True negative
					if (rndSpecificity > ((Screening)intervention).getSpecificity()) {
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
					if (rndSensitivity > ((Screening)intervention).getSensitivity()) {
						// Schedule next screening appointment (if required) 
						long next = iterator.next();
						if (next != -1) {
							addEvent(new ScreeningEvent(next, iterator));
						}
					}
					// True positive
					else {
						setDiagnosed();
						// TODO: Add costs of true positive						
					}					
				}
			}
		}
		
	}
	
}
