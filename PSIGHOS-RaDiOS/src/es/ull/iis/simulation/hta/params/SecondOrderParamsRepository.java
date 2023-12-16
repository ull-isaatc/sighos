/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionEvents;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A repository to define the second order parameters for the simulation, as well as the basic components to be simulated. 
 * A repository should be created in two stages:
 * <ol>
 * <li>First, the basic components should be added: population ({@link #setPopulation(Population)}),
 * one or more diseases ({@link #addDisease(Disease)}), one or more manifestations ({@link #addDiseaseProgression(Manifestation)}, which are generally defined within the constructor of the 
 * corresponding disease), and one or more interventions ({@link #addIntervention(Intervention)}).</li>
 * <li>All those basic components define a {@link CreatesSecondOrderParameters#registerSecondOrderParameters() method} to create and register second order parameters. Once all the basic 
 * components have been added to the repository, the method {@link #registerAllSecondOrderParams()} must be invoked.</li> 
 * </ol> 
 * {@link Parameter Second order parameters} are stored in a number of "standard" collections, grouped by type: probability, {@link SecondOrderCostParam cost} and utility. There is also 
 * a miscellaneous category (others) for all other parameters which do not fit in the former collections. To add a parameter:
 * <ol>
 * <li>Create a constant name</li>
 * <li>Create a method to return the parameter (or ensure that the parameter is added to one of the standard collections, which already defined access methods)</li>
 * <li>Remember to add the value of the parameter in the corresponding subclasses</li> 
 * </ol>
 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
 * @author Iván Castilla Rodríguez
 * TODO: Make this class a singleton
 * TODO: Change name to ModelBuilder or something similar
 */
public abstract class SecondOrderParamsRepository implements PrettyPrintable {


	public static final String STR_MOD_PREFIX = "MOD_";
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG_FIRST_ORDER = RandomNumberFactory.getInstance();
	
	// TODO: Change by scenarios: each parameter could be defined according to an scenario. This woulud require adding a factory to secondOrderParams and allowing a user to add several parameter settings
	/** Simulation time unit: defines the finest grain */
	private static TimeUnit simulationTimeUnit = TimeUnit.DAY;
	/** Minimum time among consecutive events. */
	private static long minTimeToEvent = simulationTimeUnit.convert(TimeStamp.getMonth());

	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 */
	protected SecondOrderParamsRepository(final int nRuns, final int nPatients) {
		this.interventionModifiers = new HashMap<>();
	}
	
	/**
	 * Returns a registered disease progression with the specified name; <code>null</code> is not found.
	 * Currently implemented as a sequential search (not the most efficient method), but we assume that the number of disease progressions is limited and this method is not used during simulations.
	 * @param name Name of a progression
	 * @return a registered disease progression with the specified name; <code>null</code> is not found.
	 */
	public DiseaseProgression getDiseaseProgressionByName(String name) {
		for (DiseaseProgression progression : registeredProgressions) {
			if (progression.name().equals(name))
				return progression;
		}
		return null;
	}

	/**
	 * @return the simulationTimeUnit
	 */
	public static TimeUnit getSimulationTimeUnit() {
		return simulationTimeUnit;
	}

	/**
	 * @param simulationTimeUnit the simulationTimeUnit to set
	 */
	public static void setSimulationTimeUnit(TimeUnit timeUnit) {
		simulationTimeUnit = timeUnit;
	}

	/**
	 * Returns an internal simulation time stamp expressed as years
	 * @param internalTs Internal time stamp
	 * @return an internal simulation time stamp expressed as years
	 */
	public static double simulationTimeToYears(long internalTs) {
		return simulationTimeToYears((double)internalTs);
	}

	/**
	 * Returns an internal simulation time stamp expressed as years
	 * @param ts Internal time stamp
	 * @return an internal simulation time stamp expressed as years
	 */
	public static double simulationTimeToYears(double ts) {
		return ts / (double)simulationTimeUnit.convert(TimeStamp.getYear());
	}

	/**
	 * If the specified timestamp is less than the minimum time to event described in this repository, returns such minimum time.
	 * @param ts Time stamp that represents a time to event.
	 * @return the highest value between the minimum time to event described in this repository and the specified timestamp
	 */
	public static long adjustTimeToEvent(long timeToEvent) {
		return Math.max(minTimeToEvent, timeToEvent);
	}

	/**
	 * If the specified timestamp is less than the minimum time to event described in this repository, returns such minimum time.
	 * @param ts Time stamp that represents a time to event.
	 * @return the highest value between the minimum time to event described in this repository and the specified timestamp
	 */
	public static long adjustTimeToEvent(double timeToEvent, TimeUnit unit) {
		return adjustTimeToEvent(simulationTimeUnit.convert(timeToEvent, unit));
	}

	/**
	 * @param minTimeToEvent the minTimeToEvent to set
	 */
	public static void setMinTimeToEvent(TimeStamp minTime) {
		minTimeToEvent = simulationTimeUnit.convert(minTime);
	}
	
	/**
	 * Returns the random number generator for first order uncertainty
	 * @return the random number generator for first order uncertainty
	 */
	public static RandomNumber getRNG_FIRST_ORDER() {
		return RNG_FIRST_ORDER;
	}

	/**
	 * Changes the default random number generator for first order uncertainty
	 * @param rngFirstOrder New random number generator
	 */
	public static void setRNG_FIRST_ORDER(RandomNumber rngFirstOrder) {
		RNG_FIRST_ORDER = rngFirstOrder;
	}

	public static String getModificationString(Intervention interv, Named from, Named to) {
		return getModificationString(interv, RiskParamDescriptions.PROBABILITY.getParameterName(from, to));
	}
	
	public static String getModificationString(Intervention interv, String name) {
		return STR_MOD_PREFIX + interv.name() + "_" + name;
	}
	
	/**
	 * Creates a Gamma distribution to add uncertainty to a deterministic cost. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
	 * parameters to adjust the uncertainty
	 * @param detCost Deterministic cost
	 * @return a Gamma random distribution that represents the uncertainty around a cost
	 */
	public static RandomVariate getRandomVariateForCost(double detCost) {
		if (detCost == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detCost);
		}
		final double costVariance2 = BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST * BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST;
		final double invCostVariance2 = 1 / costVariance2;
		return RandomVariateFactory.getInstance("GammaVariate", invCostVariance2, costVariance2 * detCost);
	}

	/**
	 * Creates a uniform distribution to add uncertainty to a deterministic probability. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
	 * parameters to adjust the uncertainty
	 * @param detProb Deterministic probability
	 * @return a uniform distribution that represents the uncertainty around a probability parameter
	 */
	public static RandomVariate getRandomVariateForProbability(double detProb) {
		if (detProb == 0.0) {
			return RandomVariateFactory.getInstance("ConstantVariate", detProb);
		}
		final double instRate = -Math.log(1 - detProb);
		return RandomVariateFactory.getInstance("UniformVariate", 1 - Math.exp(-instRate * (1 - BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY)), 1 - Math.exp(-instRate * (1 + BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY)));
	}
}
