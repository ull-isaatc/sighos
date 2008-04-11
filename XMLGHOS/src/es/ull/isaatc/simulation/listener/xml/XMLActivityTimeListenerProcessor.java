package es.ull.isaatc.simulation.listener.xml;

/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLActivityTimeListenerProcessor extends XMLPeriodicListenerProcessor {

	private ActivityTimeListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLActivityTimeListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((ActivityTimeListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The activityTimeListener
	 */
	private void initialize(ActivityTimeListener actList) {
		listener = new ActivityTimeListener();

		// For each activity
		for (ActivityTimeListener.Activity a : actList.getActivity()) {
			ActivityTimeListener.Activity act = new ActivityTimeListener.Activity();

			act.setActId(a.getActId());

			// Copy each queue element
			for (double t : a.getTime()) {
				act.getTime().add(t);
			}
			listener.getActivity().add(act);
		}
	}

	/**
	 * @return the average listener 
	 */
	@Override
	public void average() {
		// For each activity
		for (int a = 0; a < listener.getActivity().size(); a++) {		
			ActivityTimeListener.Activity act = listener.getActivity().get(a); // listener activity
			
			// Divides each result between the number of experiments
			for (int t = 0; t < act.getTime().size(); t++) {
				act.getTime().set(t, act.getTime().get(t) / (double)experiments);
			}
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */
	@Override
	public void process(SimulationListener simList) {
		ActivityTimeListener list = (ActivityTimeListener) simList;
		
		// For each activity
		for (int a = 0; a < listener.getActivity().size(); a++) {
			ActivityTimeListener.Activity act = listener.getActivity().get(a); // listener activity
			ActivityTimeListener.Activity simAct = list.getActivity().get(a); // new activity to process
			
			// Increase the queue and performed values in each period
			for (int t = 0; t < act.getTime().size(); t++) {
				act.getTime().set(t, act.getTime().get(t) + simAct.getTime().get(t));
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
