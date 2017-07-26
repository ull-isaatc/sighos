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
	/** If true, simulations are launched in a parallel fashion */  
	protected boolean parallel = false;
	/** Number of threads to launch parallel simulations */
	protected int nThreads;
	
	/**
	 * Creates a new experiment.
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 */
	public Experiment(String description, int nExperiments) {
		this(description, nExperiments, false, 1);
	}
	
	/**
	 * Creates a new experiment that launches parallel simulations using the maximum number of available processors
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 * @param parallel If true, simulations are launched in a parallel fashion 
	 */
	public Experiment(String description, int nExperiments, boolean parallel) {
		this(description, nExperiments, true, Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Creates a new experiment that launches parallel simulations using {@link #nThreads} threads
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 * @param parallel If true, simulations are launched in a parallel fashion
	 * @param nThreads Number of threads to launch parallel simulations
	 */
	public Experiment(String description, int nExperiments, boolean parallel, int nThreads) {
		this.description = description;
		this.nExperiments = nExperiments;
		this.parallel = parallel;
		this.nThreads = nThreads;
	}
	
	/**
	 * Creates a simulation corresponding to the #ind experiment.
	 * @param ind Number of the experiment
	 * @return A new simulation object.
	 */
	public abstract Simulation getSimulation(int ind);
	
	/**
	 * Implementations of this method must call {@link #getSimulation(int)} to carry out all the 
	 * simulations planned in this experiment.
	 */
	public void start() {
		if (! parallel) {
			for (int i = 0; i < nExperiments; i++) {
				getSimulation(i).run();
			}
		}
		else {
			try {
				for (int i = 0; i < nThreads; i++) {
					final Thread th = new Thread(new SimulationLauncher(nExperiments * i, Math.min(nExperiments, nExperiments * (i + 1) - 1)));
					th.start();
					th.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		end();
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
	
	protected class SimulationLauncher implements Runnable {
		private final int firstIndex;
		private final int lastIndex;

		
		public SimulationLauncher(int firstIndex, int lastIndex) {
			this.firstIndex = firstIndex;
			this.lastIndex = lastIndex;
		}

		@Override
		public void run() {
			for (int i = firstIndex; i <= lastIndex; i++) {
				getSimulation(i).run();
			}
		}
		
	}
}
