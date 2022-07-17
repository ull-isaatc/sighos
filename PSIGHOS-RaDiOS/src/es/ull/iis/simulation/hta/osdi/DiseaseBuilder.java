package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DiseaseBuilder {
	public static StandardDisease getDiseaseInstance(SecondOrderParamsRepository secParams, String diseaseName) throws TranspilerException {
		
		StandardDisease disease = new StandardDisease(secParams, diseaseName, OwlHelper.getDataPropertyValue(diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "")) {

			@Override
			public void registerSecondOrderParameters() {
				try {
					createUtilityParam(secParams, this);
				} catch (TranspilerException e) {
					System.err.println(e.getMessage());
				}
			}

			@Override
			public double getDiagnosisCost(Patient pat) {
				return 0;
			}

			@Override
			public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
				return 0;
			}
			
		};
		// Build developments
		List<String> developments = OwlHelper.getChildsByClassName(diseaseName, OSDiNames.Class.DEVELOPMENT.getDescription());
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(developmentName, disease);
		}
		
		// Build manifestations
		List<String> manifestations = OwlHelper.getChildsByClassName(diseaseName, OSDiNames.Class.MANIFESTATION.getDescription());
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			List<String> pathways = OwlHelper.getChildsByClassName(manifestationName, OSDiNames.Class.MANIFESTATION_PATHWAY.getDescription());
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(secParams, disease.getManifestation(manifestationName), pathwayName);
		}
		
//		disease.setScreeningStrategies(ScreeningBuilder.getScreeningStrategies(diseaseName));
//		disease.setClinicalDiagnosisStrategies(ClinicalDiagnosisBuilder.getClinicalDiagnosisStrategies(diseaseName));
//		disease.setInterventions(InterventionBuilder.getInterventions(diseaseName));
//		disease.setFollowUpStrategies(FollowUpBuilder.getFollowUpStrategies(diseaseName));				
//		disease.setTreatmentStrategies(TreatmentBuilder.getTreatmentStrategies(diseaseName));

		return disease;
	}

	/**
	 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
	 * @param secParams Repository
	 * @param disease A disease
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	public static void createUtilityParam(SecondOrderParamsRepository secParams, StandardDisease disease) throws TranspilerException {
		List<String> utilities = OwlHelper.getObjectPropertiesByName(disease.name(), OSDiNames.ObjectProperty.HAS_UTILITY.getDescription());
		if (utilities.size() > 1)
			throw new TranspilerException("A maximum of one annual (dis)utility should be associated to the disease \"" + disease.name() + "\". Instead, " + utilities.size() + " found");
		for (String utilityName : utilities) {
			// Assumes annual behavior if not specified
			final String strTempBehavior = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getDescription(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Only annual (dis)utilities should be associated to the disease \"" + disease.name() + "\". Instead, " + strTempBehavior + " found");
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_UTILITY_KIND.getDescription(), OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType);
			// Default value for utilities is 1; 0 for disutilities
			final String strValue = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_VALUE.getDescription(), isDisutility ? "0.0" : "1.0");
			// Assumes a default calculation method specified in Constants if not specified
			final String strCalcMethod = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getDescription(), Constants.UTILITY_DEFAULT_CALCULATION_METHOD);
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + disease.name() + "\"");
			secParams.addUtilityParam(disease, 
					OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "Utility for " + disease.name() + " calculated using " + strCalcMethod),  
					OSDiNames.getSource(utilityName), 
					probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost(), isDisutility);			
		}
	}
	

	// TODO
	private static void calculateDiseaseStrategyCost(SecondOrderParamsRepository secParams, String paramName, String paramDescription, Matrix costs, String costType) {
		Object[] calculatedCost = null;
		if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(costType)) {
			calculatedCost = CostUtils.calculateOnetimeCostFromMatrix(costs);
		} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equalsIgnoreCase(costType)) {
			calculatedCost = CostUtils.calculateAnnualCostFromMatrix(costs);
		}
		RandomVariate distribution = RandomVariateFactory.getInstance("ConstantVariate", (Double) calculatedCost[1]);
		if (calculatedCost[2] != null) {
			distribution = (RandomVariate) calculatedCost[2];
		}
		secParams.addCostParam(new SecondOrderCostParam(secParams, paramName, paramDescription, "", (Integer) calculatedCost[0], (Double) calculatedCost[1], distribution));
	}

}
