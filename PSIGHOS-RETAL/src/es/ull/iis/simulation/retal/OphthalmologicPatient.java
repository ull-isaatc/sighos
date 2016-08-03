/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.info.PatientInfo;
import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;
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
	private static final Random RNG_P_CNV2 = new Random();
	private static final Random RNG_SENSITIVITY = new Random();
	private static final Random RNG_SPECIFICITY = new Random();
	
	// Time to events, and events
	private long timeToEARM = Long.MAX_VALUE;
	private long timeToGA = Long.MAX_VALUE;
	private long timeToCNV = Long.MAX_VALUE;
	private EARMEvent eARMEvent = null;
	private CNVEvent cNVEvent = null;
	private GAEvent gAEvent = null;
	
	/** The random reference to compare with the probability of developing CNV in the first eye */ 
	private final double rndProbCNV1;
	/** The random reference to compare with the probability of developing CNV in the fellow eye */ 
	private final double rndProbCNV2;
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
	
	private final CommonParams commonParams;
	private final ARMDParams armdParams;

	/**
	 * @param simul
	 * @param initAge
	 * @param sex
	 */
	public OphthalmologicPatient(RETALSimulation simul, double initAge, int sex) {
		super(simul, initAge, sex);
		eye1 = EnumSet.of(EyeState.HEALTHY);
		eye2 = EnumSet.of(EyeState.HEALTHY);
		
		this.rndProbCNV1 = RNG_P_CNV1.nextDouble();
		this.rndProbCNV2 = RNG_P_CNV2.nextDouble();
		this.rndSensitivity = RNG_SENSITIVITY.nextDouble();
		this.rndSpecificity = RNG_SPECIFICITY.nextDouble();
		this.commonParams = simul.getCommonParams();
		this.armdParams = simul.getArmdParams();
	}

	/**
	 * @param original
	 * @param nIntervention
	 */
	public OphthalmologicPatient(OphthalmologicPatient original, int nIntervention) {
		super(original, nIntervention);
		this.eye1 = EnumSet.copyOf(original.eye1);
		this.eye2 = EnumSet.copyOf(original.eye2);
		
		// FIXME: Modify to implement new times to events
		this.timeToEARM = original.timeToEARM;
		this.timeToGA = original.timeToGA;
		this.timeToCNV = original.timeToCNV;
		
		this.rndProbCNV1 = original.rndProbCNV1;
		this.rndProbCNV2 = original.rndProbCNV2;
		this.rndSensitivity = original.rndSensitivity;
		this.rndSpecificity = original.rndSpecificity;
		this.commonParams = original.commonParams;
		this.armdParams = original.armdParams;
	}

	@Override
	protected void init() {
		super.init();
		timeToEARM = armdParams.getTimeToEARM().getValidatedTimeToEvent(this, true);
		final long timeToAMD = armdParams.getTimeToAMD().getValidatedTimeToEvent(this, true);
		// Schedule an EARM event
		if (timeToEARM < Long.MAX_VALUE) {
			eARMEvent = new EARMEvent(timeToEARM, true);
			addEvent(eARMEvent);
		}
		// Schedule either a CNV or a GA event
		else if (timeToAMD < Long.MAX_VALUE) {
			if (armdParams.getTimeToAMD().isCNV(this, true)) {
				timeToCNV = timeToAMD;
				cNVEvent = new CNVEvent(timeToCNV, true);
				addEvent(cNVEvent);				
			}
			else {
				timeToGA = timeToAMD;
				gAEvent = new GAEvent(timeToGA, true);
				addEvent(gAEvent);				
			}
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
		
	public double getRndProbCNV1() {
		return rndProbCNV1;
	}

	public double getRndProbCNV2() {
		return rndProbCNV2;
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM() {
		return timeToEARM;
	}

	/**
	 * @return the timeToCNV
	 */
	public long getTimeToCNV() {
		return timeToCNV;
	}

	/**
	 * @return the timeToGA
	 */
	public long getTimeToGA() {
		return timeToGA;
	}

	public double getAgeAt(EyeState state) {
		long ageAt = Long.MAX_VALUE;
		switch(state) {
		case EARM:
			ageAt = timeToEARM;
			break;
		case AMD_CNV:
			ageAt = timeToCNV;
			break;
		case AMD_GA:
			ageAt = timeToGA;
			break;
		case CDME:
			break;
		case HEALTHY:
			break;
		case NCDME:
			break;
		case NPDR:
			break;
		case PDR:
			break;
		default:
			break;
		}
		if (ageAt != Long.MAX_VALUE)
			return (initAge + ageAt) / 365.0;
		return Double.MAX_VALUE;
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
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}
		for(EyeState stage : eye2) {
			final ResourceUsageItem[] res = OphthalmologicResourceUsage.getResourceUsageItems(stage);
			if (res != null) {
				for (ResourceUsageItem usage : res) {
					cost += usage.computeCost(initAge, endAge);
				}
			}
		}
		return cost;
	}
	
	public final class EARMEvent extends CancelableEvent {
		private final boolean firstEye;
		
		public EARMEvent(long ts, boolean firstEye) {
			super(ts);
			this.firstEye = firstEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				final EnumSet<EyeState> affectedEye = (firstEye) ? eye1 : eye2;
				affectedEye.remove(EyeState.HEALTHY);
				affectedEye.add(EyeState.EARM);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (firstEye)? PatientInfo.Type.EARM1 : PatientInfo.Type.EARM2, this.getTs()));
				final long timeToAMD = armdParams.getTimeToAMDFromEARM().getValidatedTimeToEvent(OphthalmologicPatient.this, firstEye);
//				System.out.println(ts + "\t" + OphthalmologicPatient.this + "\t" + timeToCNV + "\t" + timeToDeath);
				// Schedule AMD event only if death is not happening before
				// FIXME: Check whether timeToCNV should always be lower than death or MAX_VALUE
				if (armdParams.getTimeToAMDFromEARM().isCNV(OphthalmologicPatient.this, firstEye)) {
					cNVEvent = new CNVEvent(timeToAMD, firstEye);
					timeToCNV = timeToAMD;
					addEvent(cNVEvent);
				}
				else {
					gAEvent = new GAEvent(timeToAMD, firstEye);
					timeToGA = timeToAMD;
					addEvent(gAEvent);
				}
				
			}			
		}
		
	}

	public final class GAEvent extends CancelableEvent {
		private final boolean firstEye;

		public GAEvent(long ts, boolean firstEye) {
			super(ts);
			this.firstEye = firstEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				final EnumSet<EyeState> affectedEye = (firstEye) ? eye1 : eye2;
				// Remove previous stages
				affectedEye.remove(EyeState.HEALTHY);
				affectedEye.remove(EyeState.EARM);
				
				// Assign new stage
				affectedEye.add(EyeState.AMD_GA);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (firstEye)? PatientInfo.Type.GA1 : PatientInfo.Type.GA2, this.getTs()));
				timeToCNV = (firstEye) ? armdParams.getTimeToE1CNV().getValidatedTimeToEvent(OphthalmologicPatient.this, firstEye) :
					armdParams.getTimeToE2CNV().getValidatedTimeToEvent(OphthalmologicPatient.this, firstEye) ;
				if (timeToCNV < Long.MAX_VALUE) {
					cNVEvent = new CNVEvent(timeToCNV, firstEye);
					addEvent(cNVEvent);
				}
			}			
		}
		
	}
	
	public final class CNVEvent extends CancelableEvent {
		private final boolean firstEye;

		public CNVEvent(long ts, boolean firstEye) {
			super(ts);
			this.firstEye = firstEye;
		}

		@Override
		public void event() {
			if (!cancelled) {
				final EnumSet<EyeState> affectedEye = (firstEye) ? eye1 : eye2;
				// Remove previous stages
				affectedEye.remove(EyeState.HEALTHY);
				affectedEye.remove(EyeState.EARM);
				affectedEye.remove(EyeState.AMD_GA);

				// Assign new stage
				affectedEye.add(EyeState.AMD_CNV);
				simul.getInfoHandler().notifyInfo(new PatientInfo(simul, OphthalmologicPatient.this, (firstEye)? PatientInfo.Type.CNV1 : PatientInfo.Type.CNV2, this.getTs()));
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
