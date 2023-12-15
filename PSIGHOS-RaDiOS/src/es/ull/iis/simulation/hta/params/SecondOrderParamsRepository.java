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
	/** Number of patients that should be generated */
	final protected int nPatients;
	/** The number of probabilistic simulations to run by using this repository */
	final private int nRuns;
	/** The collection of defined diseases */
	final protected ArrayList<Disease> registeredDiseases;
	/** The collection of defined developments */
	final protected ArrayList<Development> registeredDevelopments;
	/** The collection of interventions */
	final protected ArrayList<Intervention> registeredInterventions;
	/** The collection of defined progressions */
	final protected ArrayList<DiseaseProgression> registeredProgressions;
	/** The registeredPopulation */
	private Population registeredPopulation = null;


	public static final String STR_MOD_PREFIX = "MOD_";
	/** A null relative risk, i.e., RR = 1.0 */
	public final String NO_RR;
	
	final protected HashMap<String, Parameter> params;
	final private HashMap<String, TreeMap<Intervention, ParameterModifier>> interventionModifiers;
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG_FIRST_ORDER = RandomNumberFactory.getInstance();
	
	// TODO: Change by scenarios: each parameter could be defined according to an scenario. This woulud require adding a factory to secondOrderParams and allowing a user to add several parameter settings
	/** Absence of progression */
	private static final DiseaseProgressionEvents NULL_PROGRESSION = new DiseaseProgressionEvents(); 
	/** A dummy disease that represents a non-disease state, i.e., being healthy. Useful to avoid null comparisons. */
	public static final Disease HEALTHY = new Disease("HEALTHY", "Healthy") {
		@Override
		public DiseaseProgressionEvents getProgression(Patient pat) {
			return NULL_PROGRESSION;
		}
	};
	/** The method to combine different disutilities. {@link DisutilityCombinationMethod#ADD} by default */
	private DisutilityCombinationMethod method = DisutilityCombinationMethod.ADD;
	/** Year used to update the costs */
	private static int studyYear = Year.now().getValue();
	/** Simulation time unit: defines the finest grain */
	private static TimeUnit simulationTimeUnit = TimeUnit.DAY;
	/** Minimum time among consecutive events. */
	private static long minTimeToEvent = simulationTimeUnit.convert(TimeStamp.getMonth());

	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 */
	protected SecondOrderParamsRepository(final int nRuns, final int nPatients) {
		this.params = new HashMap<>();
		this.interventionModifiers = new HashMap<>();
		this.nPatients = nPatients;
		this.nRuns = nRuns;
		this.registeredProgressions = new ArrayList<>();
		this.registeredDiseases = new ArrayList<>();
		this.registeredDevelopments = new ArrayList<>();
		this.registeredInterventions = new ArrayList<>();
		// TODO: Define in Parameter, not here
		this.NO_RR = OtherParamDescriptions.RELATIVE_RISK.addParameter(this, "NULL", "A dummy relative risk to be used when no RR is required", "", 1.0);
	}

	/**
	 * Checks the model validity and returns a string with the missing components.
	 * @return null if everything is ok; a string with the missing components otherwise
	 */
	public String checkValidity() {
		final StringBuilder str = new StringBuilder();
		if (registeredDiseases.size() == 0)
			str.append("At least one disease must be defined").append(System.lineSeparator());
		if (registeredInterventions.size() == 0) {
			str.append("At least one intervention must be defined").append(System.lineSeparator());
		}
		if (registeredPopulation == null) {
			str.append("No population defined").append(System.lineSeparator());
		}
		return (str.length() > 0) ? str.toString() : null;
	}
	
	/**
	 * Registers the second order parameters associated to the population, death submodel, diseases, manifestations and interventions that were
	 * previously included in this repository. This method must be invoked after all these components have been created. 
	 */
	public void registerAllSecondOrderParams() {
		registeredPopulation.registerSecondOrderParameters(this);
		for (Disease disease : registeredDiseases)
			disease.registerSecondOrderParameters(this);
		for (DiseaseProgression progression : registeredProgressions) {
			progression.registerSecondOrderParameters(this);
			for (DiseaseProgressionPathway pathway : progression.getPathways()) {
				pathway.registerSecondOrderParameters(this);
			}
		}
		for (Intervention intervention : registeredInterventions)
			intervention.registerSecondOrderParameters(this);
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
	 * Returns the number of patients that will be generated during the simulation
	 * @return the number of patients that will be generated during the simulation
	 */
	public int getNPatients() {
		return nPatients;
	}

	/**
	 * Returns the minimum age for patients within this repository, which is the minimum age of the registeredPopulation
	 * @return the minimum age for patients within this repository
	 */
	public int getMinAge() {
		return registeredPopulation.getMinAge();
	}

	/**
	 * Returns the number of probabilistic simulations to run by using this repository
	 * @return The number of probabilistic simulations to run by using this repository
	 */
	public int getNRuns() {
		return nRuns;
	}

	/**
	 * Return the year that is used to update the cost parameters
	 * @return the year that is used to update the cost parameters
	 */
	public static int getStudyYear() {
		return studyYear;
	}

	/**
	 * Sets the value of the year that is used to update the cost parameters
	 * @param year The new year of study
	 */
	public static void setStudyYear(int year) {
		studyYear = year;
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
	 * Adds a parameter
	 * @param param Parameter
	 * @param type Type of the parameter
	 */
	public String addParameter(Parameter param, ParameterType type) {		
		params.put(param.name(), param);
		type.getParameters().put(param.name(), param);
		return param.name();
	}
	
	public void addParameterModifier(String paramName, Intervention interv, ParameterModifier modifier) {
		TreeMap<Intervention, ParameterModifier> map = interventionModifiers.get(paramName);
		if (map == null) {
			map = new TreeMap<>();
			interventionModifiers.put(paramName, map);
		}
		map.put(interv, modifier);
	}

	/**
	 * Returns a value for a parameter
	 * @param name String identifier of the parameter
	 * @return A value for the specified parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getParameterValue(String name, Patient pat) {
		return getParameterValue(name, Double.NaN, pat);
	}

	/**
	 * Returns the value of the parameter for a specific patient, modified according to the intervention
	 * @param name String identifier of the parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @param pat A patient
	 * @return A value for the specified parameter; the specified default value in case the parameter is not defined
	 */
	public double getParameterValue(String name, double defaultValue, Patient pat) {
		final Parameter param = params.get(name);
		if (param == null)
			return defaultValue;
		double value = param.getValue(pat);
		final TreeMap<Intervention, ParameterModifier> map = interventionModifiers.get(name);
		if (map != null) {
			final ParameterModifier modifier = map.get(pat.getIntervention());
			if (modifier != null) {
				value = modifier.getModifiedValue(pat, value);
			}
		}
		if (ParameterType.COST.getParameters().containsKey(name)) {
			final ParameterDescription desc = param.getParameterDescription();
			return SpanishCPIUpdate.updateCost(value, desc.getYear(), SecondOrderParamsRepository.getStudyYear());
		}
		return value;
	}
	
	/**
	 * Returns the parameter with the specified name; null if not found
	 * @param name The name of the parameter
	 * @return The parameter with the specified name; null if not found
	 */
	public Parameter getParameter(String name) {
		return params.get(name);
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

	/**
	 * Returns the combination method used to combine different disutilities
	 * @return the combination method used to combine different disutilities
	 */
	public DisutilityCombinationMethod getDisutilityCombinationMethod() {
		return method;
	}

	/**
	 * Sets a different combination method for disutilities
	 * @param method Combination method for disutilities
	 */
	public void setDisutilityCombinationMethod(DisutilityCombinationMethod method) {
		this.method = method;
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
	
	/**
	 * Creates a string that contains a tab separated list of the parameter names defined in this repository
	 * @return a string that contains a tab separated list of the parameter names defined in this repository
	 */
	public String getStrHeader() {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				if (param instanceof SecondOrderNatureParameter) {
					str.append(param.name()).append("\t");
				}
			}
		}
		return str.toString();
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				str.append(param.prettyPrint(linePrefix)).append("\n");
			}
		}
		return str.toString();
	}
	
	public String print(int id) {
		StringBuilder str = new StringBuilder();
		for (ParameterType type : ParameterType.values()) {
			for (Parameter param : type.getParameters().values()) {
				if (param instanceof SecondOrderNatureParameter)
					str.append(((SecondOrderNatureParameter)param).getValue(id)).append("\t");
			}
		}
		return str.toString();
	}
}
