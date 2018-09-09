/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.AcuteEventParam;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class AcuteComplicationSubmodel extends ComplicationSubmodel {

	/**
	 * 
	 */
	public AcuteComplicationSubmodel() {
		super();
	}
	
	public abstract AcuteEventParam getParam();
	public abstract double getCostOfComplication(T1DMPatient pat);
	public abstract double getDisutility(T1DMPatient pat);
}
