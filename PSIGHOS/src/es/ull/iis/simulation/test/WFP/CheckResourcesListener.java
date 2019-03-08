/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationTimeInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckResourcesListener extends CheckerListener {
	private final static String ROLONERROR = "\tWrong activation time of resource role";
	private final static String ROLOFFERROR = "\tWrong deactivation time of resource role";
	private final static String RESCREATEDERROR = "\tNot all the resources were created";
	private final static String RESFINISHEDERROR = "\tNot all the resources were finished";
	private final int resources;
	private int resCreated;
	private int resFinished;
	private final StringBuilder problems;

	public CheckResourcesListener(final int resources) {
		super("Resource checker");
		this.resources = resources;
		this.resCreated = 0;
		this.resFinished = 0;
		this.problems = new StringBuilder();
		
		addEntrance(ResourceInfo.class);
		addEntrance(SimulationTimeInfo.class);
	}

	@Override
	public void infoEmited(final SimulationInfo info) {
		if (info instanceof ResourceInfo) {
			final ResourceInfo rInfo = (ResourceInfo)info;
			switch(rInfo.getType()) {
			case ROLON:
				if (rInfo.getTs() != WFPTestSimulation.RESSTART) {
					problems.append(rInfo.getResource() + "\t" + rInfo.getTs() + "\t" + ROLONERROR + System.lineSeparator());
				}
				resCreated++;
				break;
			case ROLOFF:
				if (rInfo.getTs() != WFPTestSimulation.RESSTART + WFPTestSimulation.RESAVAILABLE) {
					problems.append(rInfo.getResource() + "\t" + rInfo.getTs() + "\t" + ROLOFFERROR + System.lineSeparator());
				}
				resFinished++;
				break;
			default:
				break;
			}
		}
		else if (info instanceof SimulationTimeInfo) {
			final SimulationTimeInfo tInfo = (SimulationTimeInfo) info;
			if (SimulationTimeInfo.Type.END.equals(tInfo.getType()))  {
				if (resCreated != resources) {
					problems.append("GENERAL\t" + tInfo.getTs() + "\t" + RESCREATEDERROR + System.lineSeparator());
				}
				if (resFinished != resources) {
					problems.append("GENERAL\t" + tInfo.getTs() + "\t" + RESFINISHEDERROR + System.lineSeparator());
				}
			}
		}
		
	}

	@Override
	public boolean testPassed() {
		return problems.length() == 0;
	}

	@Override
	public String testProblems() {
		return problems.toString();
	}

}
