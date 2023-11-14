/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.outcomes.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.DeathSubmodel;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionEvents;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A repository to define the second order parameters for the simulation, as well as the basic components to be simulated. 
 * A repository should be created in two stages:
 * <ol>
 * <li>First, the basic components should be added: population ({@link #setPopulation(Population)}), death submodel ({@link #setDeathSubmodel(DeathSubmodel)},
 * one or more diseases ({@link #addDisease(Disease)}), one or more manifestations ({@link #addDiseaseProgression(Manifestation)}, which are generally defined within the constructor of the 
 * corresponding disease), and one or more interventions ({@link #addIntervention(Intervention)}).</li>
 * <li>All those basic components define a {@link CreatesSecondOrderParameters#registerSecondOrderParameters() method} to create and register second order parameters. Once all the basic 
 * components have been added to the repository, the method {@link #registerAllSecondOrderParams()} must be invoked.</li> 
 * </ol> 
 * {@link SecondOrderParam Second order parameters} are stored in a number of "standard" collections, grouped by type: probability, {@link SecondOrderCostParam cost} and utility. There is also 
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
 */
public abstract class SecondOrderParamsRepository implements PrettyPrintable {
	public enum ParameterType {
		PROBABILITY,
		COST,
		UTILITY,
		MODIFICATION,
		OTHER
	}
	public static final String STR_MOD_PREFIX = "MOD_";
	/** A null relative risk, i.e., RR = 1.0 */
	public static final RRCalculator NO_RR = new StdComplicationRR(1.0);
	
	final protected HashMap<String, SecondOrderParam> params;
	/** The collection of probability parameters */
	final protected TreeSet<SecondOrderParam> probabilityParams;
	/** The collection of cost parameters */
	final protected TreeSet<SecondOrderCostParam> costParams;
	/** The collection of utility parameters */
	final protected TreeSet<SecondOrderParam> utilParams;
	/** The collection of miscellaneous parameters */
	final protected TreeSet<SecondOrderParam> otherParams;
	/** A map where the key is the name of a parameter, and the value is a modification */
	private final TreeSet<Modification> modificationParams;
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG_FIRST_ORDER = RandomNumberFactory.getInstance();
	/** The collection of defined progressions */
	final protected ArrayList<DiseaseProgression> registeredProgressions;
	/** The collection of defined diseases */
	final protected ArrayList<Disease> registeredDiseases;
	/** The collection of defined developments */
	final protected ArrayList<Development> registeredDevelopments;
	
	/** The death submodel to be used 
	 * TODO: Conceptually, the death submodel should be linked to the population, not to the repository
	 * */
	protected DeathSubmodel registeredDeathSubmodel = null;
	/** The collection of interventions */
	final protected ArrayList<Intervention> registeredInterventions;
	// TODO: Change by scenarios: each parameter could be defined according to an scenario. This woulud require adding a factory to secondOrderParams and allowing a user to add several parameter settings
	/** Number of patients that should be generated */
	final protected int nPatients;
	/** The registeredPopulation */
	private Population registeredPopulation = null;
	/** The number of probabilistic simulations to run by using this repository */
	final private int nRuns;
	/** Absence of progression */
	private static final DiseaseProgressionEvents NULL_PROGRESSION = new DiseaseProgressionEvents(); 
	/** A dummy disease that represents a non-disease state, i.e., being healthy. Useful to avoid null comparisons. */
	public final Disease HEALTHY;
	/** A dummy modification that do not modify anything */
	public final Modification NO_MODIF;
	/** The method to combine different disutilities. {@link DisutilityCombinationMethod#ADD} by default */
	private DisutilityCombinationMethod method = DisutilityCombinationMethod.ADD;
	/** Year used to update the costs */
	private int studyYear;
	
	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 */
	protected SecondOrderParamsRepository(final int nRuns, final int nPatients) {
		this.params = new HashMap<>();
		this.probabilityParams = new TreeSet<>();
		this.costParams = new TreeSet<>();
		this.otherParams = new TreeSet<>();
		this.utilParams = new TreeSet<>();
		this.modificationParams = new TreeSet<>();
		this.nPatients = nPatients;
		this.nRuns = nRuns;
		this.studyYear = Year.now().getValue();
		this.registeredProgressions = new ArrayList<>();
		this.registeredDiseases = new ArrayList<>();
		this.registeredDevelopments = new ArrayList<>();
		this.registeredInterventions = new ArrayList<>();
		this.HEALTHY = new Disease(this, "HEALTHY", "Healthy") {

			@Override
			public DiseaseProgressionEvents getProgression(Patient pat) {
				return NULL_PROGRESSION;
			}
		};
		this.NO_MODIF = new Modification(this, Modification.Type.DIFF, "NOMOD", "Null modification", "", 0.0);
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
		if (registeredDeathSubmodel == null) {
			str.append("No death submodel defined").append(System.lineSeparator());
		}
		return (str.length() > 0) ? str.toString() : null;
	}
	
	/**
	 * Registers the second order parameters associated to the population, death submodel, diseases, manifestations and interventions that were
	 * previously included in this repository. This method must be invoked after all these components have been created. 
	 */
	public void registerAllSecondOrderParams() {
		registeredPopulation.registerSecondOrderParameters(this);
		registeredDeathSubmodel.registerSecondOrderParameters(this);
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
	 * Sets the population characteristics that will be simulated
	 * @param population A definition of the characteristics of a population
	 * @return False if a population was already defined for this repository; true otherwise 
	 */
	public boolean setPopulation(Population population) {
		if (registeredPopulation != null)
			return false;
		this.registeredPopulation = population;
		return true;
	}
	
	/**
	 * Adds a new disease to his repository. This method is invoked from the constructor of {@link Disease} and should not be invoked elsewhere 
	 * @param disease New disease to be registered
	 */
	public void addDisease(Disease disease) {
		disease.setOrder(registeredDiseases.size());
		registeredDiseases.add(disease);
	}
	
	/**
	 * Adds a new development to his repository. This method is invoked from the method {@link Disease#addDevelopment(Development)} and should not be invoked elsewhere 
	 * @param development New development to be registered
	 */
	public void addDevelopment(Development development) {
		registeredDevelopments.add(development);
	}

	/**
	 * Adds a new disease progression to this repository. This method is invoked from the method {@link Disease#addDiseaseProgression(DiseaseProgression)} and should not be invoked elsewhere
	 * @param progression New disease progression to be registered
	 */
	public void addDiseaseProgression(DiseaseProgression progression) {
		progression.setOrder(registeredProgressions.size());
		registeredProgressions.add(progression);
	}

	/**
	 * Adds a new intervention to this repository. This method is invoked from the constructor of {@link Intervention} and should not be invoked elsewhere 
	 * @param intervention The description of an intervention
	 */
	public void addIntervention(Intervention intervention) {
		intervention.setOrder(registeredInterventions.size());
		registeredInterventions.add(intervention);
	}

	/**
	 * Sets the death submodel that will be used during the simulation. Returns false if there was an already registered death submodel
	 * @param deathSubmodel Death submodel to be used
	 * @return false if there was an already registered death submodel; true otherwise
	 */
	public boolean setDeathSubmodel(DeathSubmodel deathSubmodel) {
		if (registeredDeathSubmodel != null)
			return false;
		registeredDeathSubmodel = deathSubmodel;
		return true;
	}
	
	/**
	 * Returns the registered diseases
	 * @return the registered diseases
	 */
	public Disease[] getRegisteredDiseases() {
		final Disease[] array = new Disease[registeredDiseases.size()];
		return (Disease[])registeredDiseases.toArray(array);
	}
	
	/**
	 * Returns the registered developments
	 * @return the registered developments
	 */
	public Development[] getRegisteredDevelopments() {
		final Development[] array = new Development[registeredDevelopments.size()];
		return (Development[])registeredDevelopments.toArray(array);
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
	 * Returns the already registered disease progressions
	 * @return The already registered disease progressions
	 */
	public DiseaseProgression[] getRegisteredDiseaseProgressions() {
		final DiseaseProgression[] array = new DiseaseProgression[registeredProgressions.size()];
		return (DiseaseProgression[]) registeredProgressions.toArray(array);
	}

	/**
	 * Returns the already registered disease progressions of the specified type
	 * @return The already registered disease progressions of the specified type
	 */
	public DiseaseProgression[] getRegisteredDiseaseProgressions(DiseaseProgression.Type type) {
		final ArrayList<DiseaseProgression> arrayTyped = new ArrayList<>();
		for (final DiseaseProgression manif : registeredProgressions) {
			if (type.equals(manif.getType()))
				arrayTyped.add(manif);
		}
		final DiseaseProgression[] array = new DiseaseProgression[arrayTyped.size()];
		return (DiseaseProgression[]) arrayTyped.toArray(array);
	}
	
	/**
	 * Returns the already registered intervention
	 * @return The already registered interventions
	 */
	public Intervention[] getRegisteredInterventions() {
		final Intervention[] array = new Intervention[registeredInterventions.size()];
		return (Intervention[]) registeredInterventions.toArray(array);
	}
	
	/**
	 * Returns the registered death submodel; null if no submodel was registered
	 * @return the registered death submodel; null if no submodel was registered
	 */
	public DeathSubmodel getRegisteredDeathSubmodel() {
		return registeredDeathSubmodel;
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
	 * Returns the number of interventions included in the simulation
	 * @return The number of interventions included in the simulation
	 */
	public final int getNInterventions() {
		return registeredInterventions.size();
	}

	public Population getPopulation() {
		return registeredPopulation;
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
	public int getStudyYear() {
		return studyYear;
	}

	/**
	 * Sets the value of the year that is used to update the cost parameters
	 * @param studyYear The new year of study
	 */
	public void setStudyYear(int studyYear) {
		this.studyYear = studyYear;
	}

	/**
	 * Adds a parameter
	 * @param param Parameter
	 * @param type Type of the parameter
	 */
	public void addParameter(SecondOrderParam param, ParameterType type) {		
		params.put(param.getName(), param);
		switch(type) {
		case COST:
			costParams.add((SecondOrderCostParam) param);
			break;
		case MODIFICATION:
			modificationParams.add((Modification) param);
			break;
		case OTHER:
			otherParams.add(param);
			break;
		case PROBABILITY:
			probabilityParams.add(param);
			break;
		case UTILITY:
			utilParams.add(param);
			break;
		default:
			break;
		
		}
	}
	
	public void addModificationParam(Intervention interv, Modification.Type type, DiseaseProgression fromManifestation, DiseaseProgression toManifestation, String source, double detValue, RandomVariate rnd) {
		addParameter(new Modification(this, type, getModificationString(interv, fromManifestation, toManifestation), "Modification of probability of going from " + fromManifestation + " to " + toManifestation + " due to " + interv, source, detValue, rnd), SecondOrderParamsRepository.ParameterType.MODIFICATION);
	}
	
	public void addModificationParam(Intervention interv, Modification.Type type, String paramName, String source, double detValue, RandomVariate rnd) {
		addParameter(new Modification(this, type, getModificationString(interv, paramName), "Modification of parameter " + paramName + " due to " + interv, source, detValue, rnd), SecondOrderParamsRepository.ParameterType.MODIFICATION);
	}

	/**
	 * Returns a value for a parameter
	 * @param name String identifier of the parameter
	 * @return A value for the specified parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getParameter(String name, Patient pat) {
		return getParameter(name, Double.NaN, pat);
	}

	/**
	 * Returns a value for a parameter
	 * @param name String identifier of the parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified parameter; the specified default value in case the parameter is not defined
	 */
	public double getParameter(String name, double defaultValue, Patient pat) {
		final SecondOrderParam param = params.get(name);
		if (param == null)
			return defaultValue;
		final Modification modif = (Modification) params.get(getModificationString(pat.getSimulation().getIntervention(), name));
		return (modif == null) ? param.getValue(pat) : param.getValue(pat, modif); 
	}
	
	/**
	 * Returns the time to death of the specified patient
	 * @param pat A patient
	 * @return the time to death of the specified patient
	 */
	public long getTimeToDeath(Patient pat) {
		return registeredDeathSubmodel.getTimeToDeath(pat);
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
		return getModificationString(interv, ProbabilityParamDescriptions.PROBABILITY.getParameterName(from, to));
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
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param annualRisk Annual risk of the event)
	 * @param logRnd The natural log of a random number (0,1)
	 * @param rr Relative risk for the patient
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(Patient pat, double annualRisk, double logRnd, double rr) {
		final double time = Statistics.getAnnualBasedTimeToEvent(annualRisk, logRnd, rr);
	
		return (time >= (pat.getAgeAtDeath() - pat.getAge())) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	/**
	 * Generates a time to event based on annual rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param annualRate Annual rate of the event
	 * @param logRnd The natural log of a random number (0,1)
	 * @param irr Incidence rate ratio for the patient
	 * @return a time to event based on annual rate
	 */
	public static long getAnnualBasedTimeToEventFromRate(Patient pat, double annualRate, double logRnd, double irr) {
		final double time = Statistics.getAnnualBasedTimeToEventFromRate(annualRate, logRnd, irr);
	
		return (time >= (pat.getAgeAtDeath() - pat.getAge())) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	/**
	 * Creates a string that contains a tab separated list of the parameter names defined in this repository
	 * @return a string that contains a tab separated list of the parameter names defined in this repository
	 */
	public String getStrHeader() {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams)
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : costParams)
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : utilParams)
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : otherParams)
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : modificationParams)
			str.append(param.getName()).append("\t");
		return str.toString();
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}			
		for (SecondOrderParam param : costParams) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : utilParams) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : otherParams) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : modificationParams) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		return str.toString();
	}
	
	// TODO: The way values are obtained should change. Meanwhile, this method is left behind for convenience
	public String print(int id) {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams)
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : costParams)
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : utilParams)
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : otherParams)
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : modificationParams)
			str.append(param.getValue(id)).append("\t");
		return str.toString();
	}
}
