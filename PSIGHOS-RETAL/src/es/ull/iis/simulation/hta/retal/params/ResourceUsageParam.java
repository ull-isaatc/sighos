/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RETALSimulation;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

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

	/** Annual frequency of antiVEGF treatment the first two years and later on for CNV AMD */
	final private static int[] N_ANTIVEGF_CNV = {12, 4};
	/** Annual frequency of antiVEGF treatment the first two years and later on for CSME */
	final private static int[] N_ANTIVEGF_CSME = {13, 4};
	/** Min and max use of PFC in NON_HR_PDR and HR_PDR, respectively */
	final private static int[][] MIN_MAX_USE_PFC = {{17, 26}, {8, 13}};
	
	public ResourceUsageParam() {
		super();
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
	private double getAnnualOutpatientAppointments(RetalPatient pat) {
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
	
	private double computeAntiVEGFCost(int[] usage, double ageAtTreatment, double initAge, double endAge) {
		double cost = 0.0;
		// The treatment started more than two years ago
		if (initAge - ageAtTreatment >= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, usage[1]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for less than two years 
		else if (endAge - ageAtTreatment <= 2.0) {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, usage[0]).computeCost(initAge, endAge);
		}
		// The treatment started less than two years ago and it's been active for more than two years 
		else {
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, usage[0]).computeCost(initAge, ageAtTreatment + 2.0);
			cost += new ResourceUsageItem(OphthalmologicResource.RANIBIZUMAB, usage[1]).computeCost(ageAtTreatment + 2.0, endAge);
		}
		return cost;
	}

	/**
	 * Computes the cost of the treatment with antiVEGF for a specified eye, by taking into account whether the eye
	 * is affected by more than one problem
	 * @param pat
	 * @param initAge
	 * @param endAge
	 * @param eyeIndex
	 * @return
	 */
	private double getAntiVEGFCost(RetalPatient pat, double initAge, double endAge, int eyeIndex) {
		// Affected at least by CNV
		if (pat.getEyeState(eyeIndex).contains(EyeState.AMD_CNV)) {
			final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
			if (stage.getPosition() != CNVStage.Position.SF || stage.getType() != CNVStage.Type.OCCULT) {
				// If affected by both severe states, check which treatment started later
				if (pat.getEyeState(eyeIndex).contains(EyeState.CSME)) {
					if (pat.getOnAntiVEGFCNV(eyeIndex) > pat.getOnAntiVEGFCSME(eyeIndex)) {
						return computeAntiVEGFCost(N_ANTIVEGF_CNV, TimeUnit.DAY.convert(pat.getOnAntiVEGFCNV(eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);					
					}
					else {
						return computeAntiVEGFCost(N_ANTIVEGF_CSME, TimeUnit.DAY.convert(pat.getOnAntiVEGFCSME(eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);									
					}
				}
				else {
					return computeAntiVEGFCost(N_ANTIVEGF_CNV, TimeUnit.DAY.convert(pat.getOnAntiVEGFCNV(eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);				
				}
				
			}
			else {
				if (pat.getEyeState(eyeIndex).contains(EyeState.CSME)) {
					return computeAntiVEGFCost(N_ANTIVEGF_CSME, TimeUnit.DAY.convert(pat.getOnAntiVEGFCSME(eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);									
				}
			}
		}
		// Affected solely by CSME
		else if (pat.getEyeState(eyeIndex).contains(EyeState.CSME)) {
			return computeAntiVEGFCost(N_ANTIVEGF_CSME, TimeUnit.DAY.convert(pat.getOnAntiVEGFCSME(eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0, initAge, endAge);			
		}
		return 0.0;
	}
	
	private double photocoagulationCost(RetalPatient pat, double initAge, double endAge, int eyeIndex) {
		if (pat.getEyeState(eyeIndex).contains(EyeState.NON_HR_PDR)){
			return new ResourceUsageItem(OphthalmologicResource.PHOTOCOAGULATION, pat.draw(RandomForPatient.ITEM.PFC_USE) * (MIN_MAX_USE_PFC[0][1] - MIN_MAX_USE_PFC[0][0])).computeCost(initAge, endAge);
		}
		else if (pat.getEyeState(eyeIndex).contains(EyeState.HR_PDR)){
			return new ResourceUsageItem(OphthalmologicResource.PHOTOCOAGULATION, pat.draw(RandomForPatient.ITEM.PFC_USE) * (MIN_MAX_USE_PFC[1][1] - MIN_MAX_USE_PFC[1][0])).computeCost(initAge, endAge);
		}
		return 0.0;
	}
	
	public double getResourceUsageCost(RetalPatient pat, double initAge, double endAge) {
		if (!pat.isDiagnosed()) {
			return 0.0;
		}
		else {
			double cost = 0.0;
			// Outpatient appointments
			double annualVisits = getAnnualOutpatientAppointments(pat);
			if (annualVisits > 0.0) {
				cost += new ResourceUsageItem(OphthalmologicResource.OPH_APPOINTMENT, annualVisits).computeCost(initAge, endAge);
				cost += new ResourceUsageItem(OphthalmologicResource.RETINO, annualVisits).computeCost(initAge, endAge);
			}
			
			// Use of AntiVEGF in both eyes
			cost += getAntiVEGFCost(pat, initAge, endAge, 0);
			cost += getAntiVEGFCost(pat, initAge, endAge, 1);
			
			if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.DR)) {
				cost += photocoagulationCost(pat, initAge, endAge, 0);
				cost += photocoagulationCost(pat, initAge, endAge, 1);
			}
			// If affected by ARMD and...
			if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD)) {
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
	
	public double getDiagnosisCost(RetalPatient pat) {
		double cost = 0.0;
		for (ResourceUsageItem usage : diagResources) {
			cost += usage.getUnitCost();
		}
		if (pat.getAffectedBy().contains(RETALSimulation.DISEASES.ARMD))
			cost += OphthalmologicResource.ANGIO.getUnitCost();
		return cost;
	}
	
	public double getScreeningCost(RetalPatient pat) {
		double cost = 0.0;
		for (ResourceUsageItem usage : screenResources) {
			cost += usage.getUnitCost();
		}
		return cost;		
	}
}
