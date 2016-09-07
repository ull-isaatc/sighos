/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.function.ConstantFunction;
import es.ull.iis.function.TimeFunction;

/**
 * An item that describes a specific resource, the unit cost of this resource and how frequently is used this resource during a year 
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageItem {
	private final OphthalmologicResource resource;
	private final TimeFunction yearlyUse;
	
	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(OphthalmologicResource resource, TimeFunction yearlyUse) {
		this.resource = resource;
		this.yearlyUse = yearlyUse;
	}

	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(OphthalmologicResource resource, double yearlyUse) {
		this(resource, new ConstantFunction(yearlyUse));
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return resource.getDescription();
	}

	/**
	 * @return the unitCost
	 */
	public double getUnitCost() {
		return resource.getUnitCost();
	}

	/**
	 * @return the yearlyUse
	 */
	public TimeFunction getYearlyUse() {
		return yearlyUse;
	}

	/**
	 * Computes the cost of using a resource during a period of time
	 * @param initAge Age at which the patient starts using the resource
	 * @param endAge Age at which the patient ends using the resource 
	 * @return The proportional cost of using a resource during the defined period of time 
	 */
	public double computeCost(double initAge, double endAge) {
		double usage = yearlyUse.getValue(initAge);
		return getUnitCost() * usage * (endAge - initAge);
	}
	
	@Override
	public String toString() {
		return resource.getDescription();
	}
}
