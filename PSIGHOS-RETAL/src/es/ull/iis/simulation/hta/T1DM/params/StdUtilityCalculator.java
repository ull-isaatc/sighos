/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * A standard utility calculator that simply collects constant disutility values for each complication and then 
 * combines them according to the defined {@link DisutilityCombinationMethod} and the current health state of the patient.
 * Acute events and no complication utilities are defined in the constructor. The disutility for every stage of each chronic complication
 * is defined by using {@link #addDisutilityForComplicationStage(T1DMComplicationStage, double)}
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdUtilityCalculator implements UtilityCalculator {
	/** Disutility associated to each chronic complication stage */
	private final TreeMap<T1DMComplicationStage, Double> disutilities;
	/** Disutility for diabetes with no complications */
	private final double duDNC;
	/** Utility assigned to the general population, without diabetes */
	private final double genPopUtility;
	/** Disutilities associated to each acute event */
	private final double[] duAcuteEvent;
	/** Method used to combine the disutilities for different chronic complications */
	private final DisutilityCombinationMethod method;

	/**
	 * Creates an instance of a standard calculator that simply combines the disutilities for every complication that suffers the
	 * patient
	 * @param method Method to combine the disutilies
	 * @param duDNC Disutility of diabetes with no complications
	 * @param genPopUtility Utility for general population without diabetes
	 * @param duAcuteEvent Disutility of acute events
	 */
	public StdUtilityCalculator(DisutilityCombinationMethod method, double duDNC, double genPopUtility, double[] duAcuteEvent) {
		this.duDNC = duDNC;
		this.disutilities = new TreeMap<>();
		this.method = method;
		this.genPopUtility = genPopUtility;
		this.duAcuteEvent = duAcuteEvent;
	}

	/**
	 * Adds a disutility for a chronic complication stage
	 * @param stage Stage of a chronic complicatin
	 * @param disutility Disutility applied to the stage
	 */
	public void addDisutilityForComplicationStage(T1DMComplicationStage stage, double disutility) {
		disutilities.put(stage, disutility);
	}
	
	@Override
	public double getAcuteEventDisutilityValue(T1DMPatient pat, T1DMAcuteComplications comp) {
		return duAcuteEvent[comp.ordinal()];
	}
	
	@Override	
	public double getUtilityValue(T1DMPatient pat) {
		final Collection<T1DMComplicationStage> state = pat.getDetailedState();
		double du = duDNC;
		for (T1DMComplicationStage comp : state) {
			if (disutilities.containsKey(comp))
				du = method.combine(du, disutilities.get(comp));
		}
		return genPopUtility - du - ((T1DMMonitoringIntervention)pat.getIntervention()).getDisutility(pat);
	}
}
