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
import es.ull.iis.simulation.retal.outcome.QualityAdjustedLifeExpectancy;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
// FIXME: Only working for 2 interventions
public class ICERView extends Listener {
	private final PrintStream out = System.out;
	private final boolean singleUse;

	/**
	 * @param simul
	 * @param singleUse True if this is going to be used after a single simulation; false otherwise
	 */
	public ICERView(Simulation simul, boolean singleUse) {
		super(simul, "Cost, qaly and ICER viewer");
		this.singleUse = singleUse;
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
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			RETALSimulation simul = (RETALSimulation)info.getSimul();
			if (singleUse) {
				if (simul.isCloned()) {
					final Cost cost = simul.getCost();
					final QualityAdjustedLifeExpectancy qaly = simul.getQaly();
					cost.print(false);
					qaly.print(false);
					final double icer = (cost.getValue(1) - cost.getValue(0)) / (qaly.getValue(1) - qaly.getValue(0));
					System.out.print("ICER = " + ICER2String(icer, cost.getValue(0), cost.getValue(1), cost.getUnit() + "/" + qaly.getUnit()));
				}				
			}
			else {
				if (simul.isCloned()) {
					final double[] cost = {simul.getCost().getValue(0), simul.getCost().getValue(1)};
					final double[] qaly = {simul.getQaly().getValue(0), simul.getQaly().getValue(1)};
					final double icer = (cost[1] - cost[0]) / (qaly[1] - qaly[0]);
					// FIXME Add average and SD or CIs
					out.println(simul.getIdentifier() + "\t" + cost[0] + "\t" + cost[1] + "\t" + qaly[0] + "\t" + qaly[1] + "\t" + ICER2String(icer, cost[0], cost[1], ""));
				}
				else if (simul.getIdentifier() == 1) {
					// TODO: Print header
				}
			}
		}
	}

}
