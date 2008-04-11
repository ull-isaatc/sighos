package es.ull.isaatc.simulation.listener.xml;

/**
 * 
 * @author Yurena García-Hevia
 *
 */
public class XMLSelectableActivityListenerProcessor extends XMLPeriodicListenerProcessor {

	private SelectableActivityListener listener;

	/**
	 * Constructor
	 * @param Number of experiments in the simulation
	 * @param The SimulationListener
	 */
	public XMLSelectableActivityListenerProcessor(int experiments,
			SimulationListener simListener) {

		super(experiments, simListener);
		initialize((SelectableActivityListener) simListener);
	}

	/**
	 * Initialize the listener to the first element
	 * @param The SelectableActivityListener
	 */
	private void initialize(SelectableActivityListener actList) {
		listener = new SelectableActivityListener();

		// For each activity
		for (SelectableActivityListener.Activity a : actList.getActivity()) {
			SelectableActivityListener.Activity act = new SelectableActivityListener.Activity();
			SelectableActivityListener.Activity.ActQueue queue = new SelectableActivityListener.Activity.ActQueue();
			SelectableActivityListener.Activity.ActPerformed performed = new SelectableActivityListener.Activity.ActPerformed();

			act.setActId(a.getActId());

			// Copy each queue element
			for (double q : a.getActQueue().getQueue()) {
				queue.getQueue().add(q);
			}

			act.setActQueue(queue);
			
			// Copy each performed element
			for (double p : a.getActPerformed().getPerformed()) {
				performed.getPerformed().add(p);
			}
			act.setActPerformed(performed);

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
			SelectableActivityListener.Activity act = listener.getActivity().get(a); // listener activity
			
			// Divides each result between the number of experiments
			for (int q = 0; q < act.getActQueue().getQueue().size(); q++) {
				act.getActQueue().getQueue().set(q, act.getActQueue().getQueue().get(q) / (double)experiments);
				act.getActPerformed().getPerformed().set(q, act.getActPerformed().getPerformed().get(q) / (double)experiments);
			}
		}
	}

	/** 
	 * Increase each value of the listener 
	 * @param the listener to be processed
	 */
	@Override
	public void process(SimulationListener simList) {
		SelectableActivityListener list = (SelectableActivityListener) simList;
		
		// For each activity
		for (int a = 0; a < listener.getActivity().size(); a++) {
			SelectableActivityListener.Activity act = listener.getActivity().get(a); // listener activity
			SelectableActivityListener.Activity simAct = list.getActivity().get(a); // new activity to process
			
			// Increase the queue and performed values in each period
			for (int q = 0; q < act.getActQueue().getQueue().size(); q++) {
				act.getActQueue().getQueue().set(q, act.getActQueue().getQueue().get(q) + simAct.getActQueue().getQueue().get(q));
				act.getActPerformed().getPerformed().set(q, act.getActPerformed().getPerformed().get(q) + simAct.getActPerformed().getPerformed().get(q));
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
