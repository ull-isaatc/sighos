/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla
 *
 */
public final class ResourceUsageParam extends Param {
	final private ArrayList<ResourceUsageItem> diagResources;
	final private static int VISITS_CNV = 12; 
	final private static int VISITS_GA = 4; 
	final private static int VISITS_EARM = 1; 
	
	final private static int MIN_VISITS_CSME = 4; 
	final private static int MAX_VISITS_CSME = 6; 
	final private static int MIN_VISITS_PDR = 2; 
	final private static int MAX_VISITS_PDR = 4; 
	final private static int MIN_VISITS_NPDR = 1;
	final private static int MAX_VISITS_NPDR = 6; 

	// FIXME: Change by real number
	final private static int N_ANTIVEGF = 3;
	
	public ResourceUsageParam(boolean baseCase) {
		super(baseCase);
		diagResources = new ArrayList<ResourceUsageItem>();
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.OCT, 1));
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.RETINO, 1));
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.CLINICAL_EXAM, 1));
	}

	/**
	 * Returns the number of outpatient appointments with the ophthalmologist for a patient according to his/her health state
	 * @param pat A patient
	 * @return The number of outpatient appointments with the ophthalmologist for a patient according to his/her health state
	 */
	private double getAnnualOutpatientAppointments(Patient pat) {
		double annualVisits = 0.0;
		// If affected by ARMD
		if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD)) {
			if (pat.getEyeState(0).contains(EyeState.AMD_CNV) || pat.getEyeState(1).contains(EyeState.AMD_CNV)) {
				annualVisits = VISITS_CNV;
			}
			else if (pat.getEyeState(0).contains(EyeState.AMD_GA) || pat.getEyeState(1).contains(EyeState.AMD_GA)) {
				annualVisits = VISITS_GA;				
			}
			else if (pat.getEyeState(0).contains(EyeState.EARM) || pat.getEyeState(1).contains(EyeState.EARM)) {
				annualVisits = VISITS_EARM;
			}
		}		
		// If affected by DR		
		if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.DR)) {
			if (pat.getEyeState(0).contains(EyeState.CSME) || pat.getEyeState(1).contains(EyeState.CSME)) {
				annualVisits = MIN_VISITS_CSME + pat.draw(RandomForPatient.ITEM.ANNUAL_VISITS) * (MAX_VISITS_CSME - MIN_VISITS_CSME);
			}
			if (pat.getEyeState(0).contains(EyeState.HR_PDR) || pat.getEyeState(1).contains(EyeState.HR_PDR) ||
					pat.getEyeState(0).contains(EyeState.NON_HR_PDR) || pat.getEyeState(1).contains(EyeState.NON_HR_PDR)) {
				annualVisits = Math.max(MIN_VISITS_PDR + pat.draw(RandomForPatient.ITEM.ANNUAL_VISITS) * (MAX_VISITS_PDR - MIN_VISITS_PDR), annualVisits);
			}
			if (pat.getEyeState(0).contains(EyeState.NPDR) || pat.getEyeState(1).contains(EyeState.NPDR)) {
				annualVisits = Math.max(MIN_VISITS_NPDR + pat.draw(RandomForPatient.ITEM.ANNUAL_VISITS) * (MAX_VISITS_NPDR - MIN_VISITS_NPDR), annualVisits);
			}
		}
		return annualVisits;
	}
	
	public ArrayList<ResourceUsageItem> getResourceUsageItems(Patient pat) {
		if (!pat.isDiagnosed()) {
			return null;
		}
		else {
			final ArrayList<ResourceUsageItem> resources = new ArrayList<ResourceUsageItem>();
			// Outpatient appointments
			double annualVisits = getAnnualOutpatientAppointments(pat);
			if (annualVisits > 0.0)
				resources.add(new ResourceUsageItem(OphthalmologicResource.APPOINTMENT, annualVisits));
			
			// If affected by ARMD and...
			if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD)) {
				// Treatment only for CNV
				if (pat.getEyeState(0).contains(EyeState.AMD_CNV) || pat.getEyeState(1).contains(EyeState.AMD_CNV)) {
					// Treatment for first eye
					if (pat.getEyeState(0).contains(EyeState.AMD_CNV)) {
						final CNVStage stage = pat.getCurrentCNVStage(0);
						if (stage.getPosition() != CNVStage.Position.SF || stage.getType() != CNVStage.Type.OCCULT) {
							resources.add(new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF));
						}
					}
					// Treatment for second eye
					if (pat.getEyeState(1).contains(EyeState.AMD_CNV)) {
						final CNVStage stage = pat.getCurrentCNVStage(1);
						if (stage.getPosition() != CNVStage.Position.SF || stage.getType() != CNVStage.Type.OCCULT) {
							resources.add(new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF));
						}
					}
				}
				
				// If affected by both DR and ARMD
				if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.DR)) {
				}	
				// If affected by ARMD solely
				else {
					
				}
			}
			// If affected by DR solely
			else if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.DR)) {
				
			}
			return resources;
		}
	}
	
	public ArrayList<ResourceUsageItem> getResourceUsageForDiagnosis(Patient pat, RETALSimulation.DISEASES disease) {
		final ArrayList<ResourceUsageItem> resources = new ArrayList<ResourceUsageItem>(diagResources);
		if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD))
			resources.add(new ResourceUsageItem(OphthalmologicResource.ANGIO, 1));
		return resources;
	}
}
