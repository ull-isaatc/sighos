package es.ull.iis.simulation.hta.params;

import java.util.HashMap;
import java.util.Map;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;

/**
 * Defines a parameter that is defined by a table of values. The table is indexed by a key that is calculated from the characteristics of the patient.
 */
public abstract class TabularParameter<K extends Comparable<K>> extends Parameter {
    /** The table of values */
    private final Map<K, Double> table;

    /**
     * Creates a parameter that is defined by a table of values
     * @param secParams Repository of parameters
     * @param name The unique name of the parameter
     * @param table The table of values, indexed by a key that is calculated from the characteristics of the patient
     */
    public TabularParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, Map<K, Double> table) {
        super(model, name, description, source, year, type);
        this.table = table;
    }

    /**
     * Creates a parameter that is defined by a table of values. The table is initially empty.
     * @param secParams Repository of parameters
     * @param name The unique name of the parameter
     */
    public TabularParameter(HTAModel model, String name, String description, String source, int year, ParameterType type) {
        super(model, name, description, source, year, type);
        this.table = new HashMap<>();
    }

    /**
     * Adds values to the table
     * @param key Index for that entry of the table
     * @param value Value associated to the index
     */
    public void add(K key, double value) {
        table.put(key, value);
    }

    /**
     * Returns the key for a patient to search in the table
     * @param pat A patient
     * @return the key for a patient to search in the table
     */
    public abstract K getKey(Patient pat);

    @Override
    public double getValue(Patient pat) {
        return table.get(getKey(pat));
    }

}
