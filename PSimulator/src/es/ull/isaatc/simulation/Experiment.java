/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.model.Model;



/**
 * Controls a set of experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment implements Describable {
	protected SimulationType type;
	/** A short text describing this experiment */
	protected String description = "";
	/** Number of experiments to be carried out */
	protected int nExperiments = 1;	
	protected Model model;

	/**
	 * Default constructor
	 */
	public Experiment() {		
	}
	
	/**
	 * Creates a new experiment.
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 */
	public Experiment(SimulationType type, String description, Model model, int nExperiments) {
		this.type = type;
		this.model = model;
		this.description = description;
		this.nExperiments = nExperiments;
	}
	
	/**
	 * Executes the simulations obtained when <code>getSimulation</code> is invoked.
	 */
	public abstract void start();

	/**
	 * Makes the postprocess of the experiments. The user should place here the actions that must be
	 * performed when all the experiments have finished: close files, DB access...
	 */
	protected void end() {		
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Describable#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the number of experiments to be carried out.
	 * @return The number of experiments to be carried out.
	 */
	public int getNExperiments() {
		return nExperiments;
	}

	/**
	 * Sets a short text describing this experiment
	 * @param description A short text describing this experiment
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the number of experiments to be carried out.
	 * @param experiments The number of experiments to be carried out.
	 */
	public void setNExperiments(int experiments) {
		nExperiments = experiments;
	}
}
