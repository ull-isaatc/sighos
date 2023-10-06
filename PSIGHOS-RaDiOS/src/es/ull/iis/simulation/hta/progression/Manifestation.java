/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.Reseteable;
import es.ull.iis.simulation.hta.outcomes.CostProducer;
import es.ull.iis.simulation.hta.outcomes.UtilityProducer;
import es.ull.iis.simulation.hta.params.BernoulliParam;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.MultipleBernoulliParam;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;

/**
 * A manifestation of a {@link Disease} defined in the model. Manifestations may be chronic or acute. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Manifestation implements NamedAndDescribed, Comparable<Manifestation>, CreatesSecondOrderParameters, Reseteable, PrettyPrintable, CostProducer, UtilityProducer {
	/**
	 * The type of the manifestation. Currently distinguishes among chronic and acute manifestations
	 * @author Iván Castilla
	 */
	public enum Type {
		ACUTE,
		CHRONIC
	}
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;
	/** Short name of the complication stage */
	private final String name;
	/** Full description of the complication stage */
	private final String description;
	/** Disease this manifestation is related to */
	private final Disease disease;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * complications defined to be used within a simulation */ 
	private int ord = -1;
	/** Type of manifestation */
	private final Type type;
	
	/** Probability that a patient starts in this stage */
	private final BernoulliParam[] pInit;
	/** Death associated to the acute events */
	private final MultipleBernoulliParam[] associatedDeath;
	/** Probability that this manifestation leads to diagnose the patient in case he/she is not already diagnosed */
	private final MultipleBernoulliParam[] pDiagnose;
	/** Random numbers used for each patient and simulation. Each patient has a random number between 0 and 1 that determines the "risk" of 
	 * developing the manifestation; a set of random numbers in case the manifestation is acute and can recur */
	private final RandomSeedForPatients[] randomSeeds;

	/** The different ways to develop the manifestation */
	private final ArrayList<ManifestationPathway> pathways;
	/** A set of labels that may be assigned to this manifestation. Labels serve to group related manifestations */
	private final TreeSet<Named> labels;
	
	/**
	 * Creates a new complication stage of a {@link ChronicComplication chronic complication} defined in the model
	 * @param secParams Common parameters repository
	 * @param name Name of the stage
	 * @param description Full description of the stage
	 * @param disease Main chronic complication
	 * @param type The {@link Type} of the manifestation
	 */
	public Manifestation(SecondOrderParamsRepository secParams, String name, String description, Disease disease, Type type) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.disease = disease;
		this.type = type;
		pInit = new BernoulliParam[secParams.getNRuns() + 1];
		Arrays.fill(pInit, null);
		associatedDeath = new MultipleBernoulliParam[secParams.getNRuns() + 1];
		Arrays.fill(associatedDeath, null);
		pDiagnose = new MultipleBernoulliParam[secParams.getNRuns() + 1];
		Arrays.fill(pDiagnose, null);
		this.randomSeeds = new RandomSeedForPatients[secParams.getNRuns() + 1];
		Arrays.fill(randomSeeds, null);
		this.pathways = new ArrayList<>();
		this.labels = new TreeSet<Named>();
		disease.addManifestation(this);
	}
	
	/**
	 * Returns the description of the manifestation
	 * @return the description of the manifestation
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@link Disease} this manifestation is related to.
	 * @return the {@link Disease} this manifestation is related to
	 */
	public Disease getDisease() {
		return disease;
	}
	
	/**
	 * Returns the type of the manifestation (currently, ACUTE or CHRONIC)
	 * @return the type of the manifestation (currently, ACUTE or CHRONIC)
	 */
	public Type getType() {
		return type;
	}

	@Override
	public String name() {
		return name;
	}
	
	/**
	 * Returns the maximum age when this manifestation appears
	 * @param pat A patient
	 * @return the maximum age when this manifestation appears
	 */
	public double getEndAge(Patient pat) {
		return OtherParamDescriptions.END_AGE.getValue(secParams, this, pat.getSimulation());
	}
	
	/**
	 * Returns the minimum age when this manifestation appears
	 * @param pat A patient
	 * @return the minimum age when this manifestation appears
	 */
	public double getOnsetAge(Patient pat) {
		return OtherParamDescriptions.ONSET_AGE.getValue(secParams, this, pat.getSimulation());
	}
	
	/**
	 * Checks every suitable pathway for a patient to progress to this manifestation, and returns the lowest time to this manifestation from all the pathways. 
	 * @param pat A patient
	 * @param limit Threshold for the manifestation to happen (in general, the expected death time for the patient)
	 * @return The lowest time to this manifestation from all the suitable pathways
	 */
	public long getTimeTo(Patient pat, long limit) {
		long timeTo = Long.MAX_VALUE;
		for (ManifestationPathway path : pathways) {
			long newTime = path.getTimeToEvent(pat, limit);
			if (newTime < timeTo)
				timeTo = newTime;
		}
		return timeTo;
	}
	/**
	 * Returns the order assigned to this stage in a simulation.
	 * @return the order assigned to this stage in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this stage have in a simulation
	 * @param ord order that this stage have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}
	
	@Override
	public int compareTo(Manifestation o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Prepares the manifestation to be used within a new simulation that probably sets a different intervention. This method is useful to preserve the random numbers 
	 * previously generated and thus make easier the comparison among interventions (common random numbers).
	 * @param id Identifier of the simulation to reset
	 */
	@Override
	public void reset(int id) {
		if (associatedDeath[id] != null)
			associatedDeath[id].reset();
		if (randomSeeds[id] != null) {
			randomSeeds[id].reset();
		}
	}
	
	/**
	 * Returns true if the patient starts in the simulation with this manifestation
	 * @param pat A patient 
	 * @return true if the patient starts in the simulation with this manifestation
	 */
	public boolean hasManifestationAtStart(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (pInit[id] == null)
			pInit[id] = new BernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getNPatients(), 
					ProbabilityParamDescriptions.INITIAL_PROPORTION.getValue(secParams, this, pat.getSimulation()));
		return pInit[id].getValue(pat);
	}

	/**
	 * Returns true if the acute onset of the manifestation produces the death of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation produces the death of the patient
	 */
	public boolean leadsToDeath(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (associatedDeath[id] == null)
			associatedDeath[id] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getNPatients(), 
					ProbabilityParamDescriptions.PROBABILITY_DEATH.getValue(secParams, this, pat.getSimulation()));
		return associatedDeath[id].getValue(pat);
	}
	
	/**
	 * Returns true if the acute onset of the manifestation leads to the diagnosis of the patient 
	 * @param pat Patient
	 * @return True if the acute onset of the manifestation leads to the diagnosis of the patient
	 */
	public boolean leadsToDiagnose(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (pDiagnose[id] == null)
			pDiagnose[id] = new MultipleBernoulliParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getNPatients(),
					ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.getValue(secParams, this, pat.getSimulation()));
		return pDiagnose[id].getValue(pat);
	}

	/**
	 * Adds a pathway to the manifestation
	 * @param pathway A new pathway that may lead to this manifestation 
	 */
	public void addPathway(ManifestationPathway pathway) {
		pathways.add(pathway);		
	}
	
	/**
	 * Adds a new label to this manifestation
	 * @param label New label
	 */
	public void addLabel(Named label) {
		labels.add(label);
	}
	
	/**
	 * Returns true if the manifestation defines the specified label
	 * @param label A label
	 * @return true if the manifestation defines the specified label
	 */
	public boolean definesLabel(Named label) {
		return (labels.contains(label));
	}
	
	/**
	 * Returns n random values between 0 and 1 for the specified patient
	 * @param pat A patient
	 * @param n Number of random numbers to generate
	 * @return n random values between 0 and 1 for the specified patient
	 */
	public abstract List<Double> getRandomValues(Patient pat, int n);
	
	/**
	 * Returns a random values between 0 and 1 for the specified patient
	 * @param pat A patient
	 * @return A random values between 0 and 1 for the specified patient
	 */
	public abstract double getRandomValue(Patient pat);

	/**
	 * Returns the collection of pathways that lead to this manifestation
	 * @return the collection of pathways that lead to this manifestation
	 */
	public ArrayList<ManifestationPathway> getPathways() {
		return pathways;
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}


	@Override
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, this, pat.getSimulation()), initYear, endYear);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(CostParamDescriptions.ONE_TIME_COST.getValue(secParams, this, pat.getSimulation()), time);
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyAnnualDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, this, pat.getSimulation()), initYear, endYear);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double annualCost = CostParamDescriptions.TREATMENT_COST.getValue(secParams, this, pat.getSimulation()) + CostParamDescriptions.FOLLOW_UP_COST.getValue(secParams, this, pat.getSimulation());
		return discountRate.applyDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double annualCost = CostParamDescriptions.TREATMENT_COST.getValue(secParams, this, pat.getSimulation()) + CostParamDescriptions.FOLLOW_UP_COST.getValue(secParams, this, pat.getSimulation());
		return discountRate.applyAnnualDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double getAnnualDisutility(Patient pat) {
		return UtilityParamDescriptions.DISUTILITY.forceValue(secParams, this, pat.getSimulation());
	}
	
	@Override
	public double getStartingDisutility(Patient pat) {
		return UtilityParamDescriptions.ONE_TIME_DISUTILITY.forceValue(secParams, this, pat.getSimulation());
	}

	@Override
	public String prettyPrint(String linePrefix) {
		
		final StringBuilder str = new StringBuilder(linePrefix).append(type.name()).append(" manifestation: ").append(name).append(System.lineSeparator());
		if (!"".equals(description))
			str.append(linePrefix + "\t").append(description).append(System.lineSeparator());
		if (labels.size() > 0) {
			str.append(linePrefix).append("Labeled as: ");
			for (Named label : labels)
				str.append(label).append("\t");
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
}
