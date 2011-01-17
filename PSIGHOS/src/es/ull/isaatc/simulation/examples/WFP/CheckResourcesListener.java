/**
 * 
 */
package es.ull.isaatc.simulation.examples.WFP;

import es.ull.isaatc.simulation.core.Simulation;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.View;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckResourcesListener extends View {
	private int[] resources;
	private int[] resCreated;
	private int[] resFinished;

	public CheckResourcesListener(Simulation simul, int []resources) {
		super(simul, "Element checker");
		this.resources = resources;
		resCreated = new int[resources.length];
		resFinished = new int[resources.length];
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			ResourceInfo rInfo = (ResourceInfo)info;
			int rt;
			switch(rInfo.getType()) {
			case ROLON:
				rt = rInfo.getResourceType().getIdentifier();
				resCreated[rt]++;
				break;
			case ROLOFF:
				rt = rInfo.getResourceType().getIdentifier();
				resFinished[rt]++;
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			boolean ok = true;
			System.out.println("--------------------------------------------------");
			System.out.println("Checking elements...");
			for (int i = 0; i < resources.length; i++) {
				System.out.print(getSimul().getElementType(i) + " (" + resources[i] + ")\t");
				System.out.print(resCreated[i] + "\t" + resFinished[i] + "\t");				
				if ((resCreated[i] & resFinished[i]) == resources[i])
					System.out.println("PASSED");
				else {
					ok = false;
					System.out.println("ERROR!!!");
				}
				if (!ok)
					System.out.println("There are error... Please review your model.");
			}
		}
		
	}

}
