package es.ull.isaatc.simulation.listener.xml;


/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLSimulationTimeListenerProcessor extends XMLListenerProcessor {

	private SimulationTimeListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLSimulationTimeListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments);
		initialize((SimulationTimeListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The SimulationTimeListener
	 */
	private void initialize(SimulationTimeListener actList) {
		listener = new SimulationTimeListener();
		listener.setSimulationTime(actList.getSimulationTime());
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		listener.setSimulationTime(listener.getSimulationTime() / experiments);
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */

	@Override
	public void process(SimulationListener simList) {
		listener.setSimulationTime(listener.getSimulationTime() + ((SimulationTimeListener)simList).getSimulationTime());
	}

	/**
	 * @return the listener
	 */
	@Override
	public SimulationListener getListener() {
		return listener;
	}

}
