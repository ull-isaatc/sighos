/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.isaatc.function.ConstantFunction;
import es.ull.isaatc.function.TimeFunction;

/**
 * An item that describes a specific resource, the unit cost of this resource and how frequently is used this resource during a year 
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageItem {
	private final String description;
	private final TimeFunction unitCost;
	private final TimeFunction yearlyUse;
	
	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(String description, TimeFunction unitCost, TimeFunction yearlyUse) {
		this.description = description;
		this.unitCost = unitCost;
		this.yearlyUse = yearlyUse;
	}

	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(String description, double unitCost, TimeFunction yearlyUse) {
		this.description = description;
		this.unitCost = new ConstantFunction(unitCost);
		this.yearlyUse = yearlyUse;
	}

	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(String description, TimeFunction unitCost, double yearlyUse) {
		this.description = description;
		this.unitCost = unitCost;
		this.yearlyUse = new ConstantFunction(yearlyUse);
	}

	/**
	 * @param description
	 * @param unitCost
	 * @param yearlyUse
	 */
	public ResourceUsageItem(String description, double unitCost, double yearlyUse) {
		this.description = description;
		this.unitCost = new ConstantFunction(unitCost);
		this.yearlyUse = new ConstantFunction(yearlyUse);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the unitCost
	 */
	public TimeFunction getUnitCost() {
		return unitCost;
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
		return unitCost.getValue(initAge) * usage * (endAge - initAge);
	}
	
	@Override
	public String toString() {
		return description;
	}
}
