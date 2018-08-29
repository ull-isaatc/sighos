/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdUtilityCalculator implements UtilityCalculator {
	private final TreeMap<T1DMComorbidity, Double> disutilities;
	private final double duDNC;
	private final double genPopUtility;
	private final double duHypoEvent;
	
	private final DisutilityCombinationMethod method;

	/**
	 */
	public StdUtilityCalculator(DisutilityCombinationMethod method, double duDNC, double genPopUtility, double duHypoEvent) {
		this.duDNC = duDNC;
		this.disutilities = new TreeMap<>();
		this.method = method;
		this.genPopUtility = genPopUtility;
		this.duHypoEvent = duHypoEvent;
	}

	public void addDisutilityForHealthState(T1DMComorbidity state, double disutility) {
		disutilities.put(state, disutility);
	}
	
	public double getHypoEventDisutilityValue() {
		return duHypoEvent;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		double du = duDNC;
		for (T1DMComorbidity comp : state) {
			if (disutilities.containsKey(comp))
				du = method.combine(du, disutilities.get(comp));
		}
		return genPopUtility - du;
	}
}
