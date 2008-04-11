package es.ull.isaatc.simulation.listener.xml;


/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLElementTypeTimeListenerProcessor extends XMLPeriodicListenerProcessor {

	private ElementTypeTimeListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLElementTypeTimeListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((ElementTypeTimeListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The activityListener
	 */
	private void initialize(ElementTypeTimeListener actList) {
		listener = new ElementTypeTimeListener();

		// for each element type
		for (ElementTypeTimeListener.Et e : actList.getEt()) {
			ElementTypeTimeListener.Et et = new ElementTypeTimeListener.Et(); // new elementType
			ElementTypeTimeListener.Et.Created created = new ElementTypeTimeListener.Et.Created();
			ElementTypeTimeListener.Et.Finished finished = new ElementTypeTimeListener.Et.Finished();

			// init created elements
			for (double c : e.getCreated().getValue()) {
				created.getValue().add(c);
			}
			et.setCreated(created);
			
			// init finished elements
			for (double f : e.getFinished().getValue()) {
				finished.getValue().add(f);
			}
			et.setFinished(finished);
			
			// init worktime
			ElementTypeTimeListener.Et.WorkTime worktime = new ElementTypeTimeListener.Et.WorkTime();
			for (double w : e.getWorkTime().getValue()) {
				worktime.getValue().add(w);
			}
			et.setWorkTime(worktime);
			
			listener.getEt().add(et);
		}
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		
		// for each element type
		for (ElementTypeTimeListener.Et e : listener.getEt()) {
			ElementTypeTimeListener.Et.Created created = e.getCreated();
			ElementTypeTimeListener.Et.Finished finished = e.getFinished();
			ElementTypeTimeListener.Et.WorkTime worktime = e.getWorkTime();
		
			// for each period get the average
			for (int i = 0; i < created.getValue().size(); i++) {
				created.getValue().set(i, created.getValue().get(i) / (double) experiments);
				finished.getValue().set(i, finished.getValue().get(i) / (double) experiments);
				worktime.getValue().set(i, worktime.getValue().get(i) / (double) experiments);
			}
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */

	@Override
	public void process(SimulationListener simList) {
		ElementTypeTimeListener list = (ElementTypeTimeListener) simList;
		
		// for each element type
		for (int e = 0; e < listener.getEt().size(); e++) {
			ElementTypeTimeListener.Et et = listener.getEt().get(e);
			ElementTypeTimeListener.Et simEt = list.getEt().get(e);
			
			ElementTypeTimeListener.Et.Created created = et.getCreated();
			ElementTypeTimeListener.Et.Created simCreated = simEt.getCreated();
			
			ElementTypeTimeListener.Et.Finished finished = et.getFinished();
			ElementTypeTimeListener.Et.Finished simFinished = simEt.getFinished();

			ElementTypeTimeListener.Et.WorkTime worktime = et.getWorkTime();
			ElementTypeTimeListener.Et.WorkTime simWorktime = simEt.getWorkTime();

			// for each period increase each attribute
			for (int i = 0; i < created.getValue().size(); i++) {
				created.getValue().set(i, created.getValue().get(i) + simCreated.getValue().get(i));
				finished.getValue().set(i, finished.getValue().get(i) + simFinished.getValue().get(i));
				worktime.getValue().set(i, worktime.getValue().get(i) + simWorktime.getValue().get(i));
			}
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
