/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * Controls a set of simulation experiments. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Experiment implements Describable {
	/** A short text describing this experiment */
	protected String description = "";
	/** Number of experiments to be carried out */
	protected int nExperiments = 1;	

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
	public Experiment(String description, int nExperiments) {
		this.description = description;
		this.nExperiments = nExperiments;
	}
	
	/**
	 * Creates a simulation corresponding to the #ind experiment.
	 * @param ind Number of the experiment
	 * @return A new simulation object.
	 */
	public abstract Simulation getModel(int ind);
	
	/**
	 * Implementations of this method must call {@link #getSimulation(int)} to carry out all the 
	 * simulations planned in this experiment.
	 */
	public void start() {
		for (int i = 0; i < nExperiments; i++) {
			getModel(i).run();
		}		
	}

	/**
	 * Makes the postprocess of the experiments. The user should place here the actions that must be
	 * performed when all the experiments have finished: close files, DB access...
	 */
	protected void end() {		
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.common.Describable#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the number of experiments to be carried out.
	 * @return The number of experiments to be carried out
	 */
	public int getNExperiments() {
		return nExperiments;
	}

	/**
	 * Sets a short text describing this experiment.
	 * @param description A short text describing this experiment
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the number of experiments to be carried out.
	 * @param experiments The number of experiments to be carried out
	 */
	public void setNExperiments(int experiments) {
		nExperiments = experiments;
	}
}
