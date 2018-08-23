/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.T1DMHealthState;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdUtilityCalculator implements UtilityCalculator {
	private final TreeMap<T1DMHealthState, Double> disutilities;
	private final double duDNC;
	private final double genPopUtility;
	private final double duHypoEvent;
	
	private final CombinationMethod method;

	/**
	 */
	public StdUtilityCalculator(CombinationMethod method, double duDNC, double genPopUtility, double duHypoEvent) {
		this.duDNC = duDNC;
		this.disutilities = new TreeMap<>();
		this.method = method;
		this.genPopUtility = genPopUtility;
		this.duHypoEvent = duHypoEvent;
	}

	public void addDisutilityForHealthState(T1DMHealthState state, double disutility) {
		disutilities.put(state, disutility);
	}
	
	public double getHypoEventDisutilityValue() {
		return duHypoEvent;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		final Collection<T1DMHealthState> state = pat.getDetailedState();
		double u = genPopUtility;
		switch(method) {
		case ADD:
			u -= duDNC;
			for (T1DMHealthState comp : state) {
				if (disutilities.containsKey(comp))
					u -= disutilities.get(comp);
			}
			break;
		case MIN:
			double du = duDNC;
			for (T1DMHealthState comp : state) {
				if (disutilities.containsKey(comp)) {
					final double newDu = disutilities.get(comp);
					if (newDu > du) {
						du = newDu;
					}					
				}
			}
			u -= du;
			break;
		case MULT:
			u -= duDNC;
			for (T1DMHealthState comp : state) {
				if (disutilities.containsKey(comp))
					u *= (genPopUtility - disutilities.get(comp));
			}
			break;
		}
		return u;
	}
}
