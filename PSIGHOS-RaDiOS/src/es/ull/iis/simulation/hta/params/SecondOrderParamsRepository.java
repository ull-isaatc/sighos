/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.costs.CostCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.DeathSubmodel;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
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
 * one or more diseases ({@link #addDisease(Disease)}), one or more manifestations ({@link #addManifestation(Manifestation)}, which are generally defined within the constructor of the 
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
 * TODO El c�lculo de tiempo hasta complicaci�n usa siempre el mismo n�mero aleatorio para la misma complicaci�n. Si aumenta el riesgo de esa
 * complicaci�n en un momento de la simulaci�n, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. �deber�a reescalar de alguna manera el tiempo hasta evento en estos casos (�proporcional al RR?)?
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class SecondOrderParamsRepository implements PrettyPrintable {
	public static final String STR_MOD_PREFIX = "MOD_";
	/** A null relative risk, i.e., RR = 1.0 */
	public static final RRCalculator NO_RR = new StdComplicationRR(1.0);
	
	/** The collection of probability parameters */
	final protected TreeMap<String, SecondOrderParam> probabilityParams;
	/** The collection of cost parameters */
	final protected TreeMap<String, SecondOrderCostParam> costParams;
	/** The collection of utility parameters */
	final protected TreeMap<String, SecondOrderParam> utilParams;
	/** The collection of miscellaneous parameters */
	final protected TreeMap<String, SecondOrderParam> otherParams;
	/** A map where the key is the name of a parameter, and the value is a modification */
	private final TreeMap <String, Modification> modificationParams;
	/** A random number generator for first order parameter values */
	private static RandomNumber RNG_FIRST_ORDER = RandomNumberFactory.getInstance();
	/** The collection of defined manifestations */
	final protected ArrayList<Manifestation> registeredManifestations;
	/** The collection of defined diseases */
	final protected ArrayList<Disease> registeredDiseases;
	/** The collection of defined developments */
	final protected ArrayList<Development> registeredDevelopments;
	
	/** The death submodel to be used */
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
	private static final DiseaseProgression NULL_PROGRESSION = new DiseaseProgression(); 
	/** A dummy disease that represents a non-disease state, i.e., being healthy. Useful to avoid null comparisons. */
	public final Disease HEALTHY;
	/** A dummy modification that do not modify anything */
	public final Modification NO_MODIF;
	
	/**
	 * Creates a repository of second order parameters. By default, generates the base case values.
	 * @param nPatients Number of patients to create
	 */
	protected SecondOrderParamsRepository(final int nRuns, final int nPatients) {
		this.probabilityParams = new TreeMap<>();
		this.costParams = new TreeMap<>();
		this.otherParams = new TreeMap<>();
		this.utilParams = new TreeMap<>();
		this.modificationParams = new TreeMap<>();
		this.nPatients = nPatients;
		this.nRuns = nRuns;
		this.registeredManifestations = new ArrayList<>();
		this.registeredDiseases = new ArrayList<>();
		this.registeredDevelopments = new ArrayList<>();
		this.registeredInterventions = new ArrayList<>();
		this.HEALTHY = new Disease(this, "HEALTHY", "Healthy") {

			@Override
			public DiseaseProgression getProgression(Patient pat) {
				return NULL_PROGRESSION;
			}

			@Override
			public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
				return 0;
			}

			@Override
			public void registerSecondOrderParameters() {
			}

			@Override
			public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
				double [] results = new double[(int)endT - (int)initT + 1];
				return results;
			}

			@Override
			public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
				return 0;
			}

			@Override
			public double getDiagnosisCost(Patient pat, double time, Discount discountRate) {
				return 0;
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
		registeredPopulation.registerSecondOrderParameters();
		registeredDeathSubmodel.registerSecondOrderParameters();
		for (Disease disease : registeredDiseases)
			disease.registerSecondOrderParameters();
		for (Manifestation manif : registeredManifestations) {
			manif.registerSecondOrderParameters();
			for (ManifestationPathway pathway : manif.getPathways()) {
				pathway.registerSecondOrderParameters();
			}
		}
		for (Intervention intervention : registeredInterventions)
			intervention.registerSecondOrderParameters();
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
	 * Adds a new manifestation to this repository. This method is invoked from the method {@link Disease#addManifestation(Manifestation)} and should not be invoked elsewhere
	 * @param manif New manifestation to be registered
	 */
	public void addManifestation(Manifestation manif) {
		manif.setOrder(registeredManifestations.size());
		registeredManifestations.add(manif);
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
	 * Returns a registered manifestation with the specified name; <code>null</code> is not found.
	 * Currently implemented as a sequential search (not the most efficient method), but we assume that the number of manifestations is limited and this method is not used during simulations.
	 * @param name Name of a manifestation
	 * @return a registered manifestation with the specified name; <code>null</code> is not found.
	 */
	public Manifestation getManifestationByName(String name) {
		for (Manifestation manif : registeredManifestations) {
			if (manif.name().equals(name))
				return manif;
		}
		return null;
	}
	
	/**
	 * Returns the already registered manifestations
	 * @return The already registered manifestations
	 */
	public Manifestation[] getRegisteredManifestations() {
		final Manifestation[] array = new Manifestation[registeredManifestations.size()];
		return (Manifestation[]) registeredManifestations.toArray(array);
	}

	/**
	 * Returns the already registered manifestations of the specified type
	 * @return The already registered manifestations of the specified type
	 */
	public Manifestation[] getRegisteredManifestations(Manifestation.Type type) {
		final ArrayList<Manifestation> arrayTyped = new ArrayList<>();
		for (final Manifestation manif : registeredManifestations) {
			if (type.equals(manif.getType()))
				arrayTyped.add(manif);
		}
		final Manifestation[] array = new Manifestation[arrayTyped.size()];
		return (Manifestation[]) arrayTyped.toArray(array);
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
	 * Adds a probability parameter
	 * @param param Probability parameter
	 */
	public void addProbParam(SecondOrderParam param) {
		probabilityParams.put(param.getName(), param);
	}
	
	public void addModificationParam(Modification param) {
		modificationParams.put(param.getName(), param);		
	}
	
	public void addModificationParam(Intervention interv, Modification.Type type, Manifestation fromManifestation, Manifestation toManifestation, String source, double detValue, RandomVariate rnd) {
		addModificationParam(new Modification(this, type, getModificationString(interv, fromManifestation, toManifestation), "Modification of probability of going from " + fromManifestation + " to " + toManifestation + " due to " + interv, source, detValue, rnd));
	}
	
	public void addModificationParam(Intervention interv, Modification.Type type, String paramName, String source, double detValue, RandomVariate rnd) {
		addModificationParam(new Modification(this, type, getModificationString(interv, paramName), "Modification of parameter " + paramName + " due to " + interv, source, detValue, rnd));
	}
	
	/**
	 * Adds a cost parameter
	 * @param param Cost parameter
	 */
	public void addCostParam(SecondOrderCostParam param) {
		costParams.put(param.getName(), param);
	}
	
	/**
	 * Adds a utility or disutility parameter
	 * @param param Utility parameter
	 */
	public void addUtilityParam(SecondOrderParam param) {
		utilParams.put(param.getName(), param);
	}

	/**
	 * Adds a utility parameter to a chronic manifestation
	 * @param manifestation A chronic manifestation
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @param disutility If true, the value is a disutility; otherwise, it is a utility
	 * @param oneTime If true, the utility is applied only upon incidence of the manifestation; otherwise, it is considered to be annual
	 */
	public void addUtilityParam(ChronicManifestation manifestation, String description, String source, double detValue, RandomVariate rnd, boolean disutility, boolean oneTime) {
		final ProbabilityParamDescriptions paramDef = (disutility ? 
				(oneTime ? ProbabilityParamDescriptions.ONE_TIME_DISUTILITY : ProbabilityParamDescriptions.DISUTILITY) :
				(oneTime ? ProbabilityParamDescriptions.ONE_TIME_UTILITY : ProbabilityParamDescriptions.UTILITY)); 
		addUtilityParam(new SecondOrderParam(this, paramDef.getParameterName(manifestation), description, source, detValue, rnd));
	}

	/**
	 * Adds an annual utility parameter to a chronic manifestation
	 * @param manifestation A chronic manifestation
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @param disutility If true, the value is a disutility; otherwise, it is a utility
	 */
	public void addUtilityParam(ChronicManifestation manifestation, String description, String source, double detValue, RandomVariate rnd, boolean disutility) {
		addUtilityParam(manifestation, description, source, detValue, rnd, disutility, false);
	}

	/**
	 * Adds a utility parameter to an acute manifestation
	 * @param manifestation An acute manifestation
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @param disutility If true, the value is a disutility; otherwise, it is a utility
	 */
	public void addUtilityParam(AcuteManifestation manifestation, String description, String source, double detValue, RandomVariate rnd, boolean disutility) {
		final String paramName = disutility ? ProbabilityParamDescriptions.ONE_TIME_DISUTILITY.getParameterName(manifestation) : ProbabilityParamDescriptions.ONE_TIME_UTILITY.getParameterName(manifestation);
		addUtilityParam(new SecondOrderParam(this, paramName, description, source, detValue, rnd));
	}

	/**
	 * Adds a utility parameter to a disease
	 * @param disease A disease
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @param disutility If true, the value is a disutility; otherwise, it is a utility
	 */
	public void addUtilityParam(Disease disease, String description, String source, double detValue, RandomVariate rnd, boolean disutility) {
		final String paramName = disutility ? ProbabilityParamDescriptions.DISUTILITY.getParameterName(disease) : ProbabilityParamDescriptions.UTILITY.getParameterName(disease);
		addUtilityParam(new SecondOrderParam(this, paramName, description, source, detValue, rnd));
	}

	/**
	 * Adds the base utility parameter to the registered population
	 * @param type A population
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public void addBaseUtilityParam(String description, String source, double detValue, RandomVariate rnd) {
		final String paramName = ProbabilityParamDescriptions.UTILITY.getParameterName("POPULATION");
		addUtilityParam(new SecondOrderParam(this, paramName, description, source, detValue, rnd));
	}

	/**
	 * Adds a miscellaneous parameter
	 * @param param Miscellanous parameter
	 */
	public void addOtherParam(SecondOrderParam param) {
		otherParams.put(param.getName(), param);
	}


	/**
	 * Returns a value for a miscellaneous parameter
	 * @param name String identifier of the miscellaneous parameter
	 * @return A value for the specified miscellaneous parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getOtherParam(String name, DiseaseProgressionSimulation simul) {
		return getOtherParam(name, Double.NaN, simul);
	}

	/**
	 * Returns a value for a miscellaneous parameter
	 * @param name String identifier of the miscellaneous parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified miscellaneous parameter; the specified default value in case the parameter is not defined
	 */
	public double getOtherParam(String name, double defaultValue, DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		final SecondOrderParam param = otherParams.get(name);
		if (param == null)
			return defaultValue;
		final Modification modif = modificationParams.get(getModificationString(simul.getIntervention(), name));
		return (modif == null) ? param.getValue(id) : param.getValue(id, modif); 
	}
	
	/**
	 * Returns a value for a cost parameter
	 * @param name String identifier of the cost parameter
	 * @return A value for the specified cost parameter; {@link Double#NaN} in case the parameter is not defined
	 */
	public double getCostParam(String name, DiseaseProgressionSimulation simul) {
		return getCostParam(name, Double.NaN, simul); 
	}
	
	/**
	 * Returns a value for a cost parameter
	 * @param name String identifier of the cost parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified cost parameter; defaultValue in case the parameter is not defined
	 */
	public double getCostParam(String name, double defaultValue, DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		final SecondOrderParam param = costParams.get(name);
		return (param == null) ? defaultValue : param.getValue(id); 
	}

	/**
	 * Returns a value for a probability parameter
	 * @param name String identifier of the probability parameter
	 * @return A value for the specified probability parameter; {@link Double#NaN} in case the parameter is not defined
	 */	
	public double getProbParam(String name, DiseaseProgressionSimulation simul) {
		return getProbParam(name, Double.NaN, simul);
	}

	/**
	 * Returns a value for a probability parameter
	 * @param name String identifier of the probability parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified probability parameter; the specified default value in case the parameter is not defined
	 */	
	public double getProbParam(String name, double defaultValue, DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		final SecondOrderParam param = probabilityParams.get(name);
		if (param == null)
			return defaultValue;
		final Modification modif = modificationParams.get(getModificationString(simul.getIntervention(), name));
		return (modif == null) ? param.getValue(id) : param.getValue(id, modif); 
	}

	/**
	 * Returns a value for a utility parameter
	 * @param name String identifier of the utility parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified probability parameter; {@link Double#NaN} in case the parameter is not defined
	 */	
	public double getUtilityParam(String name, DiseaseProgressionSimulation simul) {
		return getUtilityParam(name, Double.NaN, simul);
	}

	/**
	 * Returns a value for a utility parameter
	 * @param name String identifier of the utility parameter
	 * @param defaultValue Default value in case the parameter is not defined
	 * @return A value for the specified probability parameter; the specified default value in case the parameter is not defined
	 */	
	public double getUtilityParam(String name, double defaultValue, DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		final SecondOrderParam param = utilParams.get(name);
		if (param == null)
			return defaultValue;
		final Modification modif = modificationParams.get(getModificationString(simul.getIntervention(), name));
		return (modif == null) ? param.getValue(id) : param.getValue(id, modif); 
	}
	
	/**
	 * Returns the disutility for a manifestation &ltannual disutility, disutility at incidence&gt. If it is not defined as disutility, 
	 * looks for a utility value and uses the population utility to compute a disutility. If not defined neither, returns &lt0, 0&gt.
	 * @param manif Manifestation
	 * @param id Identifier of the simulation
	 * @return the disutility for a manifestation; 0.0 if not defined 
	 */
	public double[] getDisutilitiesForManifestation(Manifestation manif, int id) {
		final double[] utils = new double[2];
		final double refUtility = getBaseUtility(id);
//		utils[0] = UtilityParamDescriptions.DISUTILITY.getValueIfExists(this, manif, null)
		SecondOrderParam annualParam = utilParams.get(ProbabilityParamDescriptions.DISUTILITY.getParameterName(manif));
		if (annualParam != null) {
			utils[0] = annualParam.getValue(id);
		}
		else {
			annualParam = utilParams.get(ProbabilityParamDescriptions.UTILITY.getParameterName(manif));
			// Do not allow "negative" disutilities
			utils[0] = (annualParam == null) ? 0.0 : Math.max(0.0, refUtility - annualParam.getValue(id));
		}
		SecondOrderParam oneTimeParam = utilParams.get(ProbabilityParamDescriptions.ONE_TIME_DISUTILITY.getParameterName(manif));
		if (oneTimeParam != null) {
			utils[1] = oneTimeParam.getValue(id);
		}
		else {
			oneTimeParam = utilParams.get(ProbabilityParamDescriptions.ONE_TIME_UTILITY.getParameterName(manif));
			// Do not allow "negative" disutilities
			utils[1] = (oneTimeParam == null) ? 0.0 : Math.max(0.0, refUtility - oneTimeParam.getValue(id));
		}
		return utils;		
	}
	
	/**
	 * Returns the base disutility for a disease. If it is not defined as disutility, looks for a utility
	 * value and uses the utility of the registered population to compute a disutility. If not defined neither, returns 0.0.
	 * @param disease Disease
	 * @param id Identifier of the simulation
	 * @return the base disutility for a disease; 0.0 if not defined 
	 */
	public double getDisutilityForDisease(Disease disease, int id) {
		double value = 0.0;
		SecondOrderParam param = utilParams.get(ProbabilityParamDescriptions.DISUTILITY.getParameterName(disease));
		if (param == null) {
			param = utilParams.get(ProbabilityParamDescriptions.UTILITY.getParameterName(disease));
			if (param != null)
				value = getBaseUtility(id) - param.getValue(id);
		}
		else {
			value = param.getValue(id);
		}
		return value;		
	}
	
	/**
	 * Returns the base utility for the registered population. If not defined, returns 1.0.
	 * @param id Identifier of the simulation
	 * @return the base utility for the registered population; 1.0 if not defined 
	 */
	public double getBaseUtility(int id) {
		final SecondOrderParam param = utilParams.get(ProbabilityParamDescriptions.UTILITY.getParameterName("POPULATION"));
		return (param == null) ? 1.0 : param.getValue(id);		
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
	 * Returns the class that computes costs 
	 * @return the class that computes costs 
	 */
	public abstract CostCalculator getCostCalculator();
	
	/**
	 * Returns the class that computes utilities 
	 * @return the class that computes utilities 
	 */
	public abstract UtilityCalculator getUtilityCalculator();
	
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
		for (SecondOrderParam param : probabilityParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : costParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : utilParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : otherParams.values())
			str.append(param.getName()).append("\t");
		for (SecondOrderParam param : modificationParams.values())
			str.append(param.getName()).append("\t");
		return str.toString();
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams.values()) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}			
		for (SecondOrderParam param : costParams.values()) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : utilParams.values()) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : otherParams.values()) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		for (SecondOrderParam param : modificationParams.values()) {
			str.append(param.prettyPrint(linePrefix)).append("\n");
		}
		return str.toString();
	}
	
	public String print(int id) {
		StringBuilder str = new StringBuilder();
		for (SecondOrderParam param : probabilityParams.values())
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : costParams.values())
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : utilParams.values())
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : otherParams.values())
			str.append(param.getValue(id)).append("\t");
		for (SecondOrderParam param : modificationParams.values())
			str.append(param.getValue(id)).append("\t");
		return str.toString();
	}

	/**
	 * Restarts the parameters among interventions. Useful to reuse already computed values for a previous intervention and
	 * preserve common random numbers
	 * @param id Identifier of the simulation to reset
	 */
	public void reset(int id) {
		for (Disease dis : registeredDiseases)
			dis.reset(id);
	}
}
