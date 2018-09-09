/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ChronicComplicationSubmodel extends ComplicationSubmodel {
	/**
	 * 
	 */
	public ChronicComplicationSubmodel() {
		super();
	}

	public abstract T1DMProgression getNextComplication(T1DMPatient pat);
	public abstract int getNSubstates();
	public abstract T1DMComorbidity[] getSubstates();
	public abstract TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat);
	public abstract double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge);
	public abstract double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent);
	public abstract double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method);
	
}
