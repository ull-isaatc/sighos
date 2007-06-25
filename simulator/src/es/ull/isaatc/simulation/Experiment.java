/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.processor.NullStateProcessor;
import es.ull.isaatc.simulation.state.processor.StateProcessor;

/**
 * Controls a set of experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment {
	
	/** Experiment's description */
	protected String description;
	
	/** Number of experiments to carry out */
	protected int nExperiments;
	
	/** The class which process the state. */
	protected StateProcessor processor = null;

	/**
	 * Default constructor
	 */
	public Experiment() {		
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 */
	public Experiment(String description, int nExperiments) {
		this(description, nExperiments, new NullStateProcessor());
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param processor
	 */
	public Experiment(String description, int nExperiments, StateProcessor processor) {
		this.description = description;
		this.nExperiments = nExperiments;
		this.processor = processor;
	}
	
	/**
	 * Creates a simulation corresponding to the the #ind experiment.
	 * @param ind Number of the experiment
	 * @return A new simulation object.
	 */
	public abstract Simulation getSimulation(int ind);

	/**
	 * Executes the simulations got by <code>getSimulation</code>.
	 */
	public abstract void start();

	/**
	 * Makes the postprocess of the experiments. The user should place here the actions that must be
	 * performed when all the experiments have finished: close files, DB access...
	 */
	protected void end() {		
	}
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the nExperiments.
	 */
	public int getNExperiments() {
		return nExperiments;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param experiments The nExperiments to set.
	 */
	public void setNExperiments(int experiments) {
		nExperiments = experiments;
	}

	/**
	 * @param processor The processor to set.
	 */
	public void setProcessor(StateProcessor processor) {
		this.processor = processor;
	}
	
}
