package es.ull.isaatc.simulation.listener.xml;

/**
 * 
 * @author Yurena García-Hevia
 *
 */
public abstract class XMLListenerProcessor {

	/** Number of experiment */
	protected int experiments;
	
	/** Constructor */
	public XMLListenerProcessor(int experiments) {
		this.experiments = experiments;
	}
	
	/** Increase each value of the listener */
	public abstract void process(SimulationListener simListener);
	
	/** Returns the average results for the listener */
	public abstract void average();
	
	/** Return the SimulationListener of the corresponding the XMLListenerProcessor */
	public abstract SimulationListener getListener();
	
	public int getExperiments() {
		return experiments;
	}
	public void setExperiments(int nxperiments) {
		this.experiments = nxperiments;
	}
}
