/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla
 *
 */
public final class ResourceUsageParam extends Param {

	public ResourceUsageParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}
	
	public ArrayList<ResourceUsageItem> getResourceUsageItems(Patient pat) {
		return null;
	}
	
	public ArrayList<ResourceUsageItem> getResourceUsageForDiagnosis(Patient pat, RETALSimulation.DISEASES disease) {
		return null;
	}
}
