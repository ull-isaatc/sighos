/**
 * 
 */
package es.ull.iis.simulation.retal.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.outcome.Cost;
import es.ull.iis.simulation.retal.outcome.Outcome;
import es.ull.iis.simulation.retal.outcome.QualityAdjustedLifeExpectancy;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
// FIXME: Only working for 2 interventions
public class ICERView extends Listener {
	private final PrintStream out = System.out;
	private final boolean singleUse;
	private final boolean showSD;
	private final boolean showCI;
	private final boolean percentileCI;

	/**
	 * @param simul
	 * @param singleUse True if this is going to be used after a single simulation; false otherwise
	 * @param showSD
	 * @param showCI
	 * @param precentileCI
	 */
	public ICERView(Simulation simul, boolean singleUse, boolean showSD, boolean showCI, boolean percentileCI) {
		super(simul, "Cost, qaly and ICER viewer");
		this.singleUse = singleUse;
		this.showSD = showSD;
		this.showCI = showCI;
		this.percentileCI = percentileCI;
		addEntrance(SimulationEndInfo.class);
	}

	private String ICER2String(double icer, double cost1, double cost2, String unit) {
		if (icer < 0.0) {
			if (cost2 < cost1)
				return "DOMINATES";
			else 
				return "DOMINATED";
		}
		else {
			return "" + icer + unit;
		}
	}
	
	private void printOutcome(double[] results) {
		out.print(results[0] + "\t" + (showSD ? (results[1] + "\t") : "")); 
		if (showCI) {
			out.print(results[2 + (percentileCI ? 2 : 0)] + "\t" + results[3 + (percentileCI ? 2 : 0)] + "\t");
		}		
	}
	private void printHeaderforOutcome(String head) {
		out.print(head + "\t" + (showSD ? ("SD_" + head + "\t") : ""));
		if (showCI) {
			out.print("L95CI_" + head + "\tU95CI_" + head + "\t");
		}
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			RETALSimulation simul = (RETALSimulation)info.getSimul();
			final Cost cost = simul.getCost();
			final QualityAdjustedLifeExpectancy qaly = simul.getQaly();
			final double[][] costResults = cost.getResults();
			final double[][] qalyResults = qaly.getResults();
			if (singleUse) {
				if (simul.isCloned()) {
					cost.print(false, false);
					qaly.print(false, false);
					final double icer = (costResults[1][0] - costResults[0][0]) / (qalyResults[1][0] - qalyResults[0][0]);
					System.out.println("ICER = " + ICER2String(icer, costResults[0][0], costResults[1][0], cost.getUnit() + "/" + qaly.getUnit()));
				}				
			}
			else {
				if (simul.isCloned()) {
					final double icer = (costResults[1][0] - costResults[0][0]) / (qalyResults[1][0] - qalyResults[0][0]);
					// FIXME Add average and SD or CIs
					out.print(simul.getIdentifier() + "\t");
					printOutcome(costResults[0]);
					printOutcome(costResults[1]);
					printOutcome(qalyResults[0]);
					printOutcome(qalyResults[1]);
					out.println(ICER2String(icer, costResults[0][0], costResults[1][0], ""));
				}
				else if (simul.getIdentifier() == 0) {
					out.print("SIMID\t");
					printHeaderforOutcome("C1");
					printHeaderforOutcome("C2");
					printHeaderforOutcome("E1");
					printHeaderforOutcome("E2");
					out.println("ICER");
				}
			}
		}
	}

}
