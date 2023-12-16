package es.ull.iis.simulation.hta;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.params.SpanishCPIUpdate;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionEvents;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

public class HTAModel {
    /** The complete collection of Parameters with unique names defined in this model */
    private final Map<String, Parameter> parameters;
    /** A collection of modifiers of parameters associated to a specific intervention */
	final private HashMap<String, TreeMap<Intervention, ParameterModifier>> interventionModifiers;

    /** The experiment this model belongs to */
    final private HTAExperiment experiment;
	/** The collection of defined diseases */
	final protected Map<String, Disease> registeredDiseases;
	/** The collection of defined developments */
	final protected Map<String, Development> registeredDevelopments;
	/** The collection of interventions */
	final protected Map<String, Intervention> registeredInterventions;
	/** The collection of defined progressions */
	final protected Map<String, DiseaseProgression> registeredProgressions;
	/** The registeredPopulation */
	private Population registeredPopulation = null;
 	/** A dummy disease that represents a non-disease state, i.e., being healthy. Useful to avoid null comparisons. */
	public final Disease HEALTHY;
	/** Absence of progression */
	private static final DiseaseProgressionEvents NULL_PROGRESSION = new DiseaseProgressionEvents(); 
	/** Year used to update the costs */
	private static int studyYear = Year.now().getValue();

    /**
     * Creates a new HTA model
     */
    public HTAModel(HTAExperiment experiment) {
        this.experiment = experiment;
        this.parameters = new HashMap<>();
		this.interventionModifiers = new HashMap<>();
        this.registeredDiseases = new TreeMap<>();
        this.registeredDevelopments = new TreeMap<>();
        this.registeredInterventions = new TreeMap<>();
        this.registeredProgressions = new TreeMap<>();
        this.HEALTHY = new Disease(this, "HEALTHY", "Healthy") {
            @Override
            public DiseaseProgressionEvents getProgression(Patient pat) {
                return NULL_PROGRESSION;
            }
        };
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
	 * Registers the  parameters associated to the population, death submodel, diseases, manifestations and interventions that were
	 * previously included in this model. This method must be invoked after all these components have been created. 
     */	
    public void createParameters() {
		registeredPopulation.createParameters();
		for (Disease disease : registeredDiseases.values())
			disease.createParameters();
		for (DiseaseProgression progression : registeredProgressions.values()) {
			progression.createParameters();
			for (DiseaseProgressionPathway pathway : progression.getPathways()) {
				pathway.createParameters();
			}
		}
		for (Intervention intervention : registeredInterventions.values())
			intervention.createParameters();
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
     * Returns the experiment this model belongs to
     * @return the experiment this model belongs to
     */
    public HTAExperiment getExperiment() {
        return experiment;
    }

    /**
     * Registers a disease in this model. Returns false if a disease with the same name already exists,
     * and cancels the registration. This method is invoked from the constructor of {@link Disease} and should not be invoked elsewhere 
     * @param disease The disease to be registered
     * @return False if a disease with the same name already exists, true otherwise
     */
    public boolean register(Disease disease) {
        if (registeredDiseases.containsKey(disease.name()))
            return false;
        registeredDiseases.put(disease.name(), disease);
        disease.setOrder(registeredDiseases.size() - 1);
        return true;
    }

    /**
     * Registers a development in this model. Returns false if a development with the same name already exists,
     * and cancels the registration. This method is invoked from the constructor of {@link Development} and should not be invoked elsewhere 
     * @param development  The development to be registered
     * @return False if a development with the same name already exists, true otherwise
     */
    public boolean register(Development development) {
        if (registeredDevelopments.containsKey(development.name()))
            return false;
        registeredDevelopments.put(development.name(), development);
        return true;
    }

    /**
     * Registers an intervention in this model. Returns false if an intervention with the same name already exists,
     * and cancels the registration. This method is invoked from the constructor of {@link Intervention} and should not be invoked elsewhere 
     * @param intervention The intervention to be registered
     * @return False if an intervention with the same name already exists, true otherwise
     */
    public boolean register(Intervention intervention) {
        if (registeredInterventions.containsKey(intervention.name()))
            return false;
        registeredInterventions.put(intervention.name(), intervention);
        intervention.setOrder(registeredInterventions.size() - 1);
        return true;
    }

    /**
     * Registers a progression in this model. Returns false if a progression with the same name already exists,
     * and cancels the registration. This method is invoked from the constructor of {@link DiseaseProgression} and should not be invoked elsewhere 
     * @param progression The progression to be registered
     * @return False if a progression with the same name already exists, true otherwise
     */
    public boolean register(DiseaseProgression progression) {
        if (registeredProgressions.containsKey(progression.name()))
            return false;
        registeredProgressions.put(progression.name(), progression);
        progression.setOrder(registeredProgressions.size() - 1);
        return true;
    }

    /**
     * Registers a population in this model. Returns false if a population has already been registered,
     * and cancels the registration. This method is invoked from the constructor of {@link Population} and should not be invoked elsewhere 
     * @param population The population to be registered
     * @return False if a population has already been registered, true otherwise.
     */
    public boolean register(Population population) {
        if (registeredPopulation != null)
            return false;
        registeredPopulation = population;
        return true;
    }

	
	/**
	 * Returns the registered diseases
	 * @return the registered diseases
	 */
	public Disease[] getRegisteredDiseases() {
		final Disease[] array = new Disease[registeredDiseases.size()];
		return (Disease[])registeredDiseases.values().toArray(array);
	}
	
	/**
	 * Returns the registered developments
	 * @return the registered developments
	 */
	public Development[] getRegisteredDevelopments() {
		final Development[] array = new Development[registeredDevelopments.size()];
		return (Development[])registeredDevelopments.values().toArray(array);
	}
	
	/**
	 * Returns the already registered disease progressions
	 * @return The already registered disease progressions
	 */
	public DiseaseProgression[] getRegisteredDiseaseProgressions() {
		final DiseaseProgression[] array = new DiseaseProgression[registeredProgressions.size()];
		return (DiseaseProgression[]) registeredProgressions.values().toArray(array);
	}

	/**
	 * Returns the already registered disease progressions of the specified type
	 * @return The already registered disease progressions of the specified type
	 */
	public DiseaseProgression[] getRegisteredDiseaseProgressions(DiseaseProgression.Type type) {
		final ArrayList<DiseaseProgression> arrayTyped = new ArrayList<>();
		for (final DiseaseProgression manif : registeredProgressions.values()) {
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
		return (Intervention[]) registeredInterventions.values().toArray(array);
	}

	/**
	 * Returns the number of interventions included in this model
	 * @return The number of interventions included in this model
	 */
	public final int getNInterventions() {
		return registeredInterventions.size();
	}

    /**
     * Returns the population registered in this model
     * @return the population registered in this model
     */    
	public Population getPopulation() {
		return registeredPopulation;
	}

    /**
     * Returns the collection of parameters created for a model component
     * @return the collection of parameters created for a model component
     */
    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    /**
     * Adds a parameter to the collection, unless a parameter with the same name already exists.
     * @param param The parameter to be added
     * @return true if the parameter was added, false otherwise
     */
    public boolean addParameter(Parameter param) {
        if (parameters.containsKey(param.name()))
            return false;
        parameters.put(param.name(), param);
        return true;
    }

    /**
     * Adds a parameter modifier associated to certain intervention
     * @param paramName The name of the parameter to be modified
     * @param interv The intervention associated to the modifier
     * @param modifier The modifier to be added
     */
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
		final Parameter param = parameters.get(name);
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
		if (ParameterType.COST.equals(param.getType())) {
			return SpanishCPIUpdate.updateCost(value, param.getYear(), studyYear);
		}
		return value;
	}

}
