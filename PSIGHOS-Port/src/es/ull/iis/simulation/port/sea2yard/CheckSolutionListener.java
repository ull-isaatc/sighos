/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.HashMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A way to check that the simulation is able to reproduce a schedule. Takes the schedule as stated in the QCSP solution and 
 * checks whether the simulation faithfully reproduces each step. 
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckSolutionListener extends Listener {
	/** The expected order of tasks */
	final private HashMap<Integer, long[]> expectedSolution;
	/** The simulated order of tasks */
	final private HashMap<Integer, long[]> obtainedSolution;

	/**
	 * Creates the listener
	 * @param plan Schedule
	 */
	public CheckSolutionListener(StowagePlan plan) {
		super("Time container");
		this.expectedSolution = new HashMap<Integer, long[]>();
		this.obtainedSolution = new HashMap<Integer, long[]>();
		for (int task = 0; task < plan.getNTasks(); task++) {
			expectedSolution.put(task, new long[] {plan.getOptStartTime(task), plan.getOptStartTime(task) + plan.getVessel().getContainerProcessingTime(task)});
		}
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationTimeInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			final ElementActionInfo eInfo = (ElementActionInfo) info;
			final String act = eInfo.getActivity().getDescription();
			final long ts = eInfo.getTs() / PortModel.T_OPERATION;
			switch(eInfo.getType()) {
			case ACQ:
				if (act.contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					obtainedSolution.put(containerId, new long[] {ts, -1});
				}
				break;
			case END:
				break;
			case INTACT:
				break;
			case REL:
				if (act.contains(PortModel.ACT_UNLOAD)) {
					final int containerId = ((UnloadTask)eInfo.getActivity().getParent()).getContainerId();
					long[] sol = obtainedSolution.get(containerId);
					sol[1] = ts;
					obtainedSolution.put(containerId, sol);
				}
				break;
			case REQ:
				break;
			case RESACT:
				break;
			case START:
				break;
			default:
				break;
			
			}
		}
		else if (info instanceof SimulationTimeInfo) {
			final SimulationTimeInfo tInfo = (SimulationTimeInfo) info;
			if (SimulationTimeInfo.Type.END.equals(tInfo.getType()))  {
				boolean error = false;
				for (int containerId : expectedSolution.keySet()) {
					long[] expected = expectedSolution.get(containerId);
					long[] obtained = obtainedSolution.get(containerId);
					if (obtained == null) {
						System.out.println("ERROR: task " + containerId + " never scheduled; expected at " + expected[0]);
						error = true;										
					}
					else {
						if (expected[0] != obtained[0]) {
							System.out.println("ERROR: task " + containerId + " scheduled at " + obtained[0] + "; expected at " + expected[0]);
							error = true;					
						}
						if (obtained[1] == -1) {
							System.out.println("ERROR: task " + containerId + " never finished; expected at " + expected[1]);
							error = true;					
						}
						else if (expected[1] != obtained[1]) {
							System.out.println("ERROR: task " + containerId + " finished at " + obtained[1] + "; expected at " + expected[1]);
							error = true;					
						}
					}
					
				}
				if (error) {
					System.out.println("ERRORS DETECTED IN SCHEDULE!");
				}
				else {
					System.out.println("CHECKED WITH NO ERRORS");				
				}
			}
		}
	}

}
