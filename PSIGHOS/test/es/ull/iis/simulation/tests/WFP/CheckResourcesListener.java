/**
 * 
 */
package es.ull.iis.simulation.tests.WFP;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckResourcesListener extends Listener {
	private final static String ERROR_ROLON = "Wrong activation time of resource role";
	private final static String ERROR_ROLOFF = "Wrong deactivation time of resource role";
	private final static String ERROR_RESCREATED = "Not all the resources were created";
	private final static String ERROR_RESFINISHED = "Not all the resources were finished";
	private final static String ERROR_SEIZE = "Resource already seized";
	private final static String ERROR_RELEASE = "Resource not previously seized";
	private final int resources;
	private int resCreated;
	private int resFinished;
	private boolean []inUse;

	public CheckResourcesListener(final int resources) {
		super("Resource checker");
		this.resources = resources;
		this.resCreated = 0;
		this.resFinished = 0;
		this.inUse = new boolean[resources];
		Arrays.fill(inUse, false);
		addEntrance(SimulationStartStopInfo.class);		
		addEntrance(ResourceInfo.class);
		addEntrance(ResourceUsageInfo.class);
	}

	@Override
	public void infoEmited(final SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			final ResourceInfo rInfo = (ResourceInfo)info;
			switch(rInfo.getType()) {
			case ROLON:
				assertEquals(rInfo.getTs(),  WFPTestSimulation.RESSTART, rInfo.getResource().toString() + "\t" + ERROR_ROLON);
				resCreated++;
				break;
			case ROLOFF:
				assertEquals(rInfo.getTs(), WFPTestSimulation.RESSTART + WFPTestSimulation.RESAVAILABLE, rInfo.getResource().toString() + "\t" + ERROR_ROLOFF);
				resFinished++;
				break;
			default:
				break;
			}
		}
		else if (info instanceof ResourceUsageInfo) {
			final ResourceUsageInfo rInfo = (ResourceUsageInfo)info;
			final int resId = rInfo.getResource().getIdentifier();
			switch(rInfo.getType()) {
				case CAUGHT:
					assertFalse(inUse[resId], rInfo.getResource().toString() + "\t" + ERROR_SEIZE);
					inUse[resId] = true;
					break;
				case RELEASED:
					assertTrue(inUse[resId], rInfo.getResource().toString() + "\t" + ERROR_RELEASE);
					inUse[resId] = false;
					break;
				default:
					break;			
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType()))  {
				assertEquals(resCreated, resources, ERROR_RESCREATED);
				assertEquals(resFinished, resources, ERROR_RESFINISHED);
			}
		}
		
	}
}
