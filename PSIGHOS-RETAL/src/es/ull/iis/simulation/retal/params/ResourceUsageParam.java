/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.core.TimeUnit;
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
	final private ArrayList<ResourceUsageItem> screenResources;
	final private static int VISITS_CNV = 12; 
	final private static int VISITS_GA = 4; 
	final private static int VISITS_EARM = 1; 
	
	final private static int MIN_VISITS_CSME = 4; 
	final private static int MAX_VISITS_CSME = 6; 
	final private static int MIN_VISITS_PDR = 2; 
	final private static int MAX_VISITS_PDR = 4; 
	final private static int MIN_VISITS_NPDR = 1;
	final private static int MAX_VISITS_NPDR = 6; 

	/** Annual frequency of antiVEGF treatment the first two years and later on */
	final private static int[] N_ANTIVEGF = {12, 4};
	
	public ResourceUsageParam(boolean baseCase) {
		super(baseCase);
		diagResources = new ArrayList<ResourceUsageItem>();
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.OCT, 1));
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.RETINO, 1));
		diagResources.add(new ResourceUsageItem(OphthalmologicResource.CLINICAL_EXAM, 1));
		screenResources = new ArrayList<ResourceUsageItem>();
		screenResources.add(new ResourceUsageItem(OphthalmologicResource.OCT, 1));
		screenResources.add(new ResourceUsageItem(OphthalmologicResource.RETINO, 1));
		screenResources.add(new ResourceUsageItem(OphthalmologicResource.OCTAL, 1));
		screenResources.add(new ResourceUsageItem(OphthalmologicResource.APPOINTMENT, 1));
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
	
	private double getAntiVEGFForARMDCost(double ageAtTreatment, double initAge, double endAge) {
		double cost = 0.0;
		// The treatment started more than two years ago
		if (initAge - ageAtTreatment >= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF[1]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for less than two years 
		else if (endAge - ageAtTreatment <= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF[0]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for more than two years 
		else {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF[0]).computeCost(initAge, ageAtTreatment + 2.0);
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, N_ANTIVEGF[1]).computeCost(ageAtTreatment + 2.0, endAge);
		}
		return cost;
	}
	
	public double getResourceUsageCost(Patient pat, double initAge, double endAge) {
		if (!pat.isDiagnosed()) {
			return 0.0;
		}
		else {
			double cost = 0.0;
			// Outpatient appointments
			double annualVisits = getAnnualOutpatientAppointments(pat);
			if (annualVisits > 0.0)
				cost += new ResourceUsageItem(OphthalmologicResource.OPH_APPOINTMENT, annualVisits).computeCost(initAge, endAge);
			
			// If affected by ARMD and...
			if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD)) {
				// Treatment only for CNV
				if (pat.getEyeState(0).contains(EyeState.AMD_CNV) || pat.getEyeState(1).contains(EyeState.AMD_CNV)) {
					// Treatment for first eye
					if (pat.getEyeState(0).contains(EyeState.AMD_CNV)) {
						final CNVStage stage = pat.getCurrentCNVStage(0);
						if (stage.getPosition() != CNVStage.Position.SF || stage.getType() != CNVStage.Type.OCCULT) {
							cost += getAntiVEGFForARMDCost(TimeUnit.DAY.convert(pat.getOnAntiVEGFSince(0), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);
						}
					}
					// Treatment for second eye
					if (pat.getEyeState(1).contains(EyeState.AMD_CNV)) {
						final CNVStage stage = pat.getCurrentCNVStage(1);
						if (stage.getPosition() != CNVStage.Position.SF || stage.getType() != CNVStage.Type.OCCULT) {
							cost += getAntiVEGFForARMDCost(TimeUnit.DAY.convert(pat.getOnAntiVEGFSince(1), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);
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
			return cost;
		}
	}
	
	public double getDiagnosisCost(Patient pat, RETALSimulation.DISEASES disease) {
		double cost = 0.0;
		for (ResourceUsageItem usage : diagResources) {
			cost += usage.getUnitCost();
		}
		if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD))
			cost += OphthalmologicResource.ANGIO.getUnitCost();
		return cost;
	}
	
	public double getScreeningCost(Patient pat) {
		double cost = 0.0;
		for (ResourceUsageItem usage : screenResources) {
			cost += usage.getUnitCost();
		}
		return cost;		
	}
}
