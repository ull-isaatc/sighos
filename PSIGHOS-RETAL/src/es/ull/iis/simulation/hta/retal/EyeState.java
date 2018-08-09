/**
 * 
 */
package es.ull.iis.simulation.retal;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public enum EyeState {
	HEALTHY,	// Non-apparent problem
	EARM,		// Early age-related macular degeneration
	AMD_GA,		// Late macular degeneration with geographic atrophy
	AMD_CNV,	// Late macular degeneration with choroidal neovascularization
	NPDR,		// Non-prolipherative diabetic retinopathy
	NON_HR_PDR,	// Non high-risk prolipherative diabetic retinopathy
	HR_PDR,		// High-risk prolipherative diabetic retinopathy
	CSME		// Clinically significant diabetic macular edema
}
