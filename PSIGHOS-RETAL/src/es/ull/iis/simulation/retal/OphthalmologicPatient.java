/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.util.DiscreteCycleIterator;

/**
 * @author Iván Castilla
 *
 */
public class OphthalmologicPatient extends Patient {
	public enum Disease {
		AMD,
		RD
	}

	// Random number generators for initial risks to be compared with specific probabilities
	private static final Random RNG_P_CNV1 = new Random();
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	private static final int TEST_DELAY = 10;

	// Time to events, and events
	private long timeToEARM;
	private long timeToAMD;
	private EARMEvent eARMEvent = null;
	private AMDEvent aMDEvent = null;
	private CNVFromGAEvent cNVFromGAEvent = null; 
	
	/** The random reference to compare with the probability of developing CNV in the first eye */ 
	private final double rndProbCNV1;
	/** The random reference to compare with the sensitivity */
	private final double rndSensitivity;
	/** The random reference to compare with the specificity */
	private final double rndSpecificity;
	/** Current visual acuity, measured as logMAR units: 0 is the best possible vision*/
//	private double va;
	/** Defines whether the patient is currently diagnosed */
	private boolean isDiagnosed = false;
	/** The current state of the first eye of the patient */
	private final EnumSet<EyeState> eye1;
	/** The current state of the second eye of the patient */
	private final EnumSet<EyeState> eye2;

	/**
	 * @param simul
	 * @param initAge
	 * @param sex
	 */
	public OphthalmologicPatient(RETALSimulation simul, double initAge, int sex, long timeToDeath, long timeToEARM, long timeToAMD) {
		super(simul, initAge, sex, timeToDeath);
		eye1 = EnumSet.of(EyeState.HEALTHY);
		eye2 = EnumSet.of(EyeState.HEALTHY);
		// Check if all the events are required
		this.timeToEARM = timeToEARM;
		this.timeToAMD = timeToAMD;
		
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
		this.eye1 = EnumSet.copyOf(original.eye1);
		this.eye2 = EnumSet.copyOf(original.eye2);
		// FIXME: Check if this is the way to apply improvements
		if (original.timeToEARM < Long.MAX_VALUE) {
			this.timeToAMD = Long.MAX_VALUE;
			if (original.timeToEARM + TEST_DELAY * nIntervention < timeToDeath)
				this.timeToEARM += TEST_DELAY * nIntervention;
			else
				this.timeToEARM = Long.MAX_VALUE;
		} 
		else if (original.timeToAMD < Long.MAX_VALUE) {
			this.timeToEARM = Long.MAX_VALUE;
			if (original.timeToAMD + TEST_DELAY * nIntervention < timeToDeath)
				this.timeToAMD += TEST_DELAY * nIntervention;
			else
				this.timeToAMD = Long.MAX_VALUE;
		}
		this.rndProbCNV1 = original.rndProbCNV1;
		this.rndSensitivity = original.rndSensitivity;
		this.rndSpecificity = original.rndSpecificity;
	}

	@Override
	protected void init() {
		super.init();
		// Do not schedule AMD if EARM happens before
		if (timeToEARM < Long.MAX_VALUE) {
			this.aMDEvent = null;
			this.eARMEvent = new EARMEvent(timeToEARM, eye1, eye2);
			addEvent(eARMEvent);
		}
		// Do not schedule EARM if AMD happens before
		else if (timeToAMD < Long.MAX_VALUE) {
			// EARM would never happen
			this.eARMEvent = null;
			this.aMDEvent = new AMDEvent(timeToAMD, eye1, eye2);
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
	 * @return the state of the first eye
	 */
	public EnumSet<EyeState> getEye1State() {
		return eye1;
	}

	/**
	 * @return the state of the fellow eye
	 */
	public EnumSet<EyeState> getEye2State() {
		return eye2;
	}

	public boolean isHealthy() {
		return eye1.isEmpty() && eye2.isEmpty();
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
			// FIXME: Check if res == null
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
	
	public final class EARMEvent extends CancelableEvent {
		private final EnumSet<EyeState> affectedEye;
		private final EnumSet<EyeState> fellowEye;
		
		public EARMEvent(long ts, EnumSet<EyeState> affectedEye, EnumSet<EyeState> fellowEye) {
			super(ts);
			this.affectedEye = affectedEye;
			this.fellowEye = fellowEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				affectedEye.remove(EyeState.HEALTHY);
				affectedEye.add(EyeState.EARM);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (affectedEye.equals(eye1))? PatientInfo.Type.EARM1 : PatientInfo.Type.EARM2, this.getTs()));
				long timeToAMD = ((RETALSimulation)simul).getTimeToAMDFromEARM(OphthalmologicPatient.this);
//				System.out.println(ts + "\t" + OphthalmologicPatient.this + "\t" + timeToAMD + "\t" + timeToDeath);
				// Schedule AMD event only if death is not happening before
				// FIXME: Check whether timeToAMD should always be lower than death of MAX_VALUE
				if (timeToAMD < timeToDeath) {
					aMDEvent = new AMDEvent(timeToAMD, affectedEye, fellowEye);
					addEvent(aMDEvent);
				}
			}			
		}
		
	}

	public final class AMDEvent extends CancelableEvent {
		private final EnumSet<EyeState> affectedEye;
		private final EnumSet<EyeState> fellowEye;

		public AMDEvent(long ts, EnumSet<EyeState> affectedEye, EnumSet<EyeState> fellowEye) {
			super(ts);
			this.affectedEye = affectedEye;
			this.fellowEye = fellowEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				affectedEye.remove(EyeState.EARM);
				// Switches state to AMD, either GA or CNV
				if (rndProbCNV1 <= ((RETALSimulation)simul).getProbabilityCNV(OphthalmologicPatient.this)) {
					affectedEye.add(EyeState.AMD_CNV);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (affectedEye.equals(eye1))? PatientInfo.Type.CNV1 : PatientInfo.Type.CNV2, this.getTs()));
				}
				else {
					affectedEye.add(EyeState.AMD_GA);
					simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (affectedEye.equals(eye1))? PatientInfo.Type.GA1 : PatientInfo.Type.GA2, this.getTs()));
					long timeToCNV = ((RETALSimulation)simul).getTimeToCNVFromGA(OphthalmologicPatient.this, fellowEye);
					if (timeToCNV < Long.MAX_VALUE) {
						cNVFromGAEvent = new CNVFromGAEvent(timeToCNV, affectedEye, fellowEye);
						addEvent(cNVFromGAEvent);
					}
				}
			}			
		}
		
	}
	
	public final class CNVFromGAEvent extends CancelableEvent {
		private final EnumSet<EyeState> affectedEye;
		private final EnumSet<EyeState> fellowEye;

		public CNVFromGAEvent(long ts, EnumSet<EyeState> affectedEye, EnumSet<EyeState> fellowEye) {
			super(ts);
			this.affectedEye = affectedEye;
			this.fellowEye = fellowEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				affectedEye.remove(EyeState.AMD_GA);
				affectedEye.add(EyeState.AMD_CNV);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (affectedEye.equals(eye1))? PatientInfo.Type.CNV1 : PatientInfo.Type.CNV2, this.getTs()));
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
