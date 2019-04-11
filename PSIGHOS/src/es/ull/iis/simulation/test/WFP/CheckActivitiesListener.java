/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * Checks the elements created and finished during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckActivitiesListener extends CheckerListener {
	private final static String ERROR_ACQ_NOT_REQ = "Resources acquired but not requested";
	private final static String ERROR_START_NOT_REQ = "Activity started but not requested";
	private final static String ERROR_END_NOT_REQ = "Activity ended but not requested";
	private final static String ERROR_END_NOT_START = "Activity ended but not started";
	private final static String ERROR_REL_NOT_ACQ = "Resources released but not acquired";
	private final static String ERROR_DURATION = "The activity did not last the expected time";
	private final static String ERROR_FINISHED = "Not all the activities requested were finished";
	private final static String ERROR_EXCLUSIVE = "Element in exclusive mode accessing another exclusive activity";
	
	private final PairQueue [] request;
	private final PairQueue [] start;
	private final PairQueue [] acquire;
	private final ArrayList<Long> actDuration;
	private final TreeMap<ActivityFlow, Integer> actIndex;
	private final boolean []exclusive;

	/**
	 * 
	 * @param simul The simulation to view
	 * @param elements An array where each position is an element type, and each value is the amount of 
	 * elements which should be created per type.
	 */
	public CheckActivitiesListener(final int nElems, final TreeMap<ActivityFlow, Integer> actIndex, final ArrayList<Long> actDuration) {
		super("Activity checker ");
		this.actIndex = actIndex;
		this.actDuration = actDuration;
		exclusive = new boolean[nElems];
		request = new PairQueue[actDuration.size()];
		start = new PairQueue[actDuration.size()];
		acquire = new PairQueue[actDuration.size()];
		for (int i = 0; i < actDuration.size(); i++) {
			request[i] = new PairQueue();
			start[i] = new PairQueue();
			acquire[i] = new PairQueue();
		}
		addEntrance(ElementActionInfo.class);
	}

	private int find(PairQueue queue, ElementActionInfo eInfo) {
		int index = 0;
		for (; index < queue.size(); index++) {
			final ElementActionInfo info = queue.get(index);
			if (info.getElementInstance().equals(eInfo.getElementInstance())) {
				return index;
			}
		}
		return -1;
	}
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo)info;
			final ActivityFlow act = (ActivityFlow)eInfo.getActivity().getParent(); 
			final int actId = actIndex.get(act);
			switch(eInfo.getType()) {
			case ACQ:
				if (find(request[actId], eInfo) == -1)
					addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_ACQ_NOT_REQ);
				acquire[actId].add(eInfo);
				break;
			case END:
				final int indexReq = find(request[actId], eInfo);
				if (indexReq == -1) {
					addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_END_NOT_REQ);
				}
				else {
					request[actId].remove(indexReq);
				}
				final int indexStart = find(start[actId], eInfo);
				if (indexStart == -1) {
					addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_END_NOT_START);
				}
				else {
					if (start[actId].get(indexStart).getTs() + actDuration.get(actId) != eInfo.getTs())
						addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_DURATION + " " + act.getDescription());
					start[actId].remove(indexStart);
				}
				if (act.isExclusive() && exclusive[eInfo.getElement().getIdentifier()]) {
					exclusive[eInfo.getElement().getIdentifier()] = false;
				}
				break;
			case REL:
				final int indexAcq = find(acquire[actId], eInfo);
				if (indexAcq == -1) {
					addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_REL_NOT_ACQ);
				}
				else {
					acquire[actId].remove(indexAcq);
				}
				break;
			case REQ:
				request[actId].add(eInfo);
				break;
			case START:
				if (find(request[actId], eInfo) == -1)
					addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_START_NOT_REQ);
				start[actId].add(eInfo);
				if (act.isExclusive()) {
					if (exclusive[eInfo.getElement().getIdentifier()]) {
						addProblem(eInfo.getElement().toString(), eInfo.getTs(), ERROR_EXCLUSIVE + " " + act.getDescription());					
					}
					else {
						exclusive[eInfo.getElement().getIdentifier()] = true;
					}
				}
				break;
			case RESACT:
			case INTACT:
			default:
				break;
			
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType()))  {
				for (int actId = 0; actId < request.length; actId++) {
					if (request[actId].size() > 0)
						addProblem("[ACT" + actId + "]", tInfo.getTs(), ERROR_FINISHED);
				}
			}
		}
		
	}

	private static class PairQueue extends ArrayList<ElementActionInfo> {
		private static final long serialVersionUID = 1L;

		public PairQueue() {
			super();
		}
	}
}
