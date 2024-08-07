/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import java.util.ArrayList;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.info.SimulationInfo;

/**
 * Checks the elements created and finished during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckElementsListener extends CheckerListener {
	private final static String ERROR_FINISHED = "Wrong number of elements finished";
	private final static String ERROR_CREATED = "Wrong number of elements created";
	private ArrayList<Integer> elements;
	private int[] elemCreated;
	private int[] elemFinished;

	/**
	 * 
	 * @param simul The simulation to view
	 * @param elements An array where each position is an element type, and each value is the amount of 
	 * elements which should be created per type.
	 */
	public CheckElementsListener(final ArrayList<Integer> elements) {
		super("Element checker ");
		this.elements = elements;
		elemCreated = new int[elements.size()];
		elemFinished = new int[elements.size()];
		addEntrance(ElementInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			int et;
			switch(eInfo.getType()) {
			case START:
				et = eInfo.getElement().getType().getIdentifier();
				elemCreated[et]++;
				break;
			case FINISH:
				et = eInfo.getElement().getType().getIdentifier();
				elemFinished[et]++;
				break;
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType()))  {
				for (int i = 0; i < elements.size(); i++) {
					if (elemFinished[i] != elemCreated[i]) {
						addProblem("GENERAL", tInfo.getTs(), ERROR_FINISHED + "\tType:" + i);
					}
					if (elemCreated[i] != elements.get(i)) {
						addProblem("GENERAL", tInfo.getTs(), ERROR_CREATED + "\tType:" + i);						
					}
				}
			}
		}
		
	}

}
