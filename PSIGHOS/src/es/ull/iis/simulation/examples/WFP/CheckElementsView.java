/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;

/**
 * Checks the elements created and finished during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckElementsView extends WFPTestView {
	private int[] elements;
	private int[] elemCreated;
	private int[] elemFinished;

	/**
	 * 
	 * @param simul The simulation to view
	 * @param elements An array where each position is an element type, and each value is the amount of 
	 * elements which should be created per type.
	 */
	public CheckElementsView(int []elements) {
		super("Element checker");
		this.elements = elements;
		elemCreated = new int[elements.length];
		elemFinished = new int[elements.length];
		addEntrance(ElementInfo.class);
		addEntrance(SimulationEndInfo.class);
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
		else if (info instanceof SimulationEndInfo) {
			boolean ok = true;
			System.out.println("--------------------------------------------------");
			System.out.println("Checking elements...");
			for (int i = 0; i < elements.length; i++) {
				System.out.print(info.getSimul().getElementTypeList().get(i) + " (" + elements[i] + ")\t");
				System.out.print(elemCreated[i] + "\t" + elemFinished[i] + "\t");				
				if ((elemCreated[i] & elemFinished[i]) == elements[i])
					System.out.println("PASSED");
				else {
					ok = false;
					System.out.println("ERROR!!!");
				}
			}
			notifyResult(ok);
		}
		
	}

}
