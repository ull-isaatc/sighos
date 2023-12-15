package es.ull.iis.simulation.hta;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.progression.Development;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

public class HTAModel {
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
 
    /**
     * Creates a new HTA model
     */
    public HTAModel() {
        registeredDiseases = new TreeMap<>();
        registeredDevelopments = new TreeMap<>();
        registeredInterventions = new TreeMap<>();
        registeredProgressions = new TreeMap<>();
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

}
