/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumMap;

/**
 * @author Iván Castilla
 *
 */
public final class OphthalmologicResourceUsage {
	/** The set of resources required in each stage */
	private final static EnumMap<EyeState, ResourceUsageItem[]> resources = new EnumMap<EyeState, ResourceUsageItem[]>(EyeState.class);
	
	static {
		resources.put(EyeState.EARM, new ResourceUsageItem[] {new ResourceUsageItem("Appointment", 60, 1)});
		resources.put(EyeState.AMD_CNV, new ResourceUsageItem[] {new ResourceUsageItem("Appointment", 60, 2)});
		resources.put(EyeState.AMD_GA, new ResourceUsageItem[] {new ResourceUsageItem("Appointment", 60, 2)});
	}

	public static ResourceUsageItem[] getResourceUsageItems(EyeState stage) {
		return resources.get(stage);
	}
}
