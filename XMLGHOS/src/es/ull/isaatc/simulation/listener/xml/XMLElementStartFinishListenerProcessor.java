package es.ull.isaatc.simulation.listener.xml;

/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLElementStartFinishListenerProcessor extends XMLPeriodicListenerProcessor {

	private ElementStartFinishListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLElementStartFinishListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((ElementStartFinishListener) simListener);
	}
	
	/**
	 * Initialize the listener to the first element
	 * @param The ElementStartFinishListener
	 */
	private void initialize(ElementStartFinishListener actList) {
		listener = new ElementStartFinishListener();
		ElementStartFinishListener.Created created = new ElementStartFinishListener.Created();
		ElementStartFinishListener.Finished finished = new ElementStartFinishListener.Finished();

		// copy each created period
		for (double c : actList.getCreated().getValue()) {
			created.getValue().add(c);
		}
		listener.setCreated(created);
		
		for (double f : actList.getFinished().getValue()) {
			finished.getValue().add(f);
		}
		listener.setFinished(finished);
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		ElementStartFinishListener.Created created = listener.getCreated();
		ElementStartFinishListener.Finished finished = listener.getFinished();
		
		// For each period return the average of created and finish elements
		for (int i = 0; i < listener.getCreated().getValue().size(); i++) {
			created.getValue().set(i, created.getValue().get(i) / (double) experiments);
			finished.getValue().set(i, finished.getValue().get(i) / (double) experiments);
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */

	@Override
	public void process(SimulationListener simList) {
		ElementStartFinishListener.Created created = listener.getCreated();
		ElementStartFinishListener.Created simCreated = ((ElementStartFinishListener)simList).getCreated();
		ElementStartFinishListener.Finished finished = listener.getFinished();		
		ElementStartFinishListener.Finished simFinished = ((ElementStartFinishListener)simList).getFinished();

		// For each period return the average of created and finish elements
		for (int i = 0; i < listener.getCreated().getValue().size(); i++) {
			created.getValue().set(i, created.getValue().get(i) + simCreated.getValue().get(i));
			finished.getValue().set(i, finished.getValue().get(i) + simFinished.getValue().get(i));
		}
	}

	/**
	 * @return the listener
	 */
	@Override
	public SimulationListener getListener() {
		return listener;
	}

}
