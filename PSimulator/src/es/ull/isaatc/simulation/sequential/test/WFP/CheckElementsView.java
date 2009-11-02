/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.sequential.info.ElementInfo;

/**
 * Checks the elements created and finished during the simulation
 * @author Iv�n Castilla Rodr�guez
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
	public CheckElementsView(WFPTestSimulation simul, int []elements) {
		super(simul, "Element checker");
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
				et = eInfo.getElem().getType().getIdentifier();
				elemCreated[et]++;
				break;
			case FINISH:
				et = eInfo.getElem().getType().getIdentifier();
				elemFinished[et]++;
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			boolean ok = true;
			System.out.println("--------------------------------------------------");
			System.out.println("Checking elements...");
			for (int i = 0; i < elements.length; i++) {
				System.out.print(getSimul().getElementType(i) + " (" + elements[i] + ")\t");
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
