/**
 * 
 */
package es.ull.iis.simulation.retal;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public enum EyeState {
	HEALTHY,	// Non-apparent problem
	EARM,		// Early age-related macular degeneration
	AMD_GA,		// Late macular degeneration with geographic atrophy
	AMD_CNV,	// Late macular degeneration with choroidal neovascularization
	NPDR,		// Non-prolipherative diabetic retinopathy 
	PDR,		// Prolipherative diabetic retinopathy
	CDME,		// Central diabetic macular edema
	NCDME,		// Non-central diabetic macular edema
	UNTREATABLE	// An state where there is no posible treatment for the eye  
}
