package es.ull.iis.simulation.hta.osdi;

import java.util.GregorianCalendar;
import java.util.List;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DiseaseBuilder {
	public static Disease getDiseaseInstance(OSDiGenericRepository secParams, String diseaseName) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		
		Disease disease = new Disease(secParams, diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(helper, diseaseName, "")) {

			@Override
			public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
				try {
					createUtilityParam((OSDiGenericRepository) secParams, this);
					createCostParams((OSDiGenericRepository) secParams, this);
				} catch (TranspilerException e) {
					System.err.println(e.getMessage());
				}
			}
			
		};
		// Build developments
		List<String> developments = OSDiNames.Class.DEVELOPMENT.getDescendantsOf(helper, diseaseName);
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(secParams, developmentName, disease);
		}
		
		// Build manifestations
		List<String> manifestations = OSDiNames.Class.MANIFESTATION.getDescendantsOf(helper, diseaseName);
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			final Manifestation manif = disease.getManifestation(manifestationName);
			final List<String> pathways = OSDiNames.Class.MANIFESTATION_PATHWAY.getDescendantsOf(helper, manifestationName);
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(secParams, manif, pathwayName);
			// Also include exclusions among manifestations
			final List<String> exclusions = OSDiNames.ObjectProperty.EXCLUDES_MANIFESTATION.getValues(helper, manifestationName);
			for (String excludedManif : exclusions) {
				disease.addExclusion(manif, disease.getManifestation(excludedManif));
			}
		}
		return disease;
	}
	
	private static void createCostParam(OwlHelper helper, String costName, CostParamDescriptions paramType, OSDiGenericRepository secParams, Disease disease) throws TranspilerException {
		// Assumes annual behavior if not specified
		final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(helper, costName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
		if (CostParamDescriptions.DIAGNOSIS_COST.equals(paramType)) {
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Diagnosis costs directly associated to the disease \"" + disease.name() + "\" should be ONE_TIME. Instead, " + strTempBehavior + " found");
		}
		else {
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Follow-up, treatment and non specific costs directly associated to the disease \"" + disease.name() + "\" should be ANNUAL. Instead, " + strTempBehavior + " found");
		}
		// Default value for utilities is 1; 0 for disutilities
		final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, costName, "0.0");
		// Assumes current year if not specified
		final int year = Integer.parseInt(OSDiNames.DataProperty.HAS_YEAR.getValue(helper, costName, "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
		try {
			final ProbabilityDistribution probDistribution = new ProbabilityDistribution(strValue);
			paramType.addParameter(secParams, disease,  
					OSDiNames.getSource(helper, costName), year, 
					probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
		} catch(TranspilerException ex) {
			throw new TranspilerException(OSDiNames.Class.UTILITY, costName, OSDiNames.DataProperty.HAS_VALUE, strValue);
		}
		
	}
	/**
	 * Creates the costs associated to a specific disease by extracting the information from the ontology. 
	 * TODO: Process strategies and not only costs directly defined
	 * @param secParams Repository
	 * @param disease A disease
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	private static void createCostParams(OSDiGenericRepository secParams, Disease disease) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		List<String> costs = OSDiNames.ObjectProperty.HAS_COST.getValues(helper, disease.name());
		if (costs.size() == 1)
			createCostParam(helper, costs.get(0), CostParamDescriptions.ANNUAL_COST, secParams, disease);
		else if (costs.size() > 1)
			throw new TranspilerException("A maximum of one non specific cost should be directly associated to the disease \"" + disease.name() + "\". Instead, " + costs.size() + " found");
		costs = OSDiNames.ObjectProperty.HAS_FOLLOWUP_COST.getValues(helper, disease.name());
		if (costs.size() == 1)
			createCostParam(helper, costs.get(0), CostParamDescriptions.FOLLOW_UP_COST, secParams, disease);
		else if (costs.size() > 1)
			throw new TranspilerException("A maximum of one follow-up cost should be directly associated to the disease \"" + disease.name() + "\". Instead, " + costs.size() + " found");
		costs = OSDiNames.ObjectProperty.HAS_TREATMENT_COST.getValues(helper, disease.name());
		if (costs.size() == 1)
			createCostParam(helper, costs.get(0), CostParamDescriptions.TREATMENT_COST, secParams, disease);
		else if (costs.size() > 1)
			throw new TranspilerException("A maximum of one treatment cost should be directly associated to the disease \"" + disease.name() + "\". Instead, " + costs.size() + " found");
		costs = OSDiNames.ObjectProperty.HAS_DIAGNOSIS_COST.getValues(helper, disease.name());
		if (costs.size() == 1)
			createCostParam(helper, costs.get(0), CostParamDescriptions.DIAGNOSIS_COST, secParams, disease);
		else if (costs.size() > 1)
			throw new TranspilerException("A maximum of one diagnosis cost should be directly associated to the disease \"" + disease.name() + "\". Instead, " + costs.size() + " found");
		
	}
	
	/**
	 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
	 * @param secParams Repository
	 * @param disease A disease
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	private static void createUtilityParam(OSDiGenericRepository secParams, Disease disease) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		List<String> utilities = OSDiNames.ObjectProperty.HAS_UTILITY.getValues(helper, disease.name());
		if (utilities.size() > 1)
			throw new TranspilerException("A maximum of one annual (dis)utility should be associated to the disease \"" + disease.name() + "\". Instead, " + utilities.size() + " found");
		for (String utilityName : utilities) {
			// Assumes annual behavior if not specified
			final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(helper, utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Only annual (dis)utilities should be associated to the disease \"" + disease.name() + "\". Instead, " + strTempBehavior + " found");
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(helper, utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType);
			// Default value for utilities is 1; 0 for disutilities
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, utilityName, isDisutility ? "0.0" : "1.0");
			try {
				final ProbabilityDistribution probDistribution = new ProbabilityDistribution(strValue);
				if (isDisutility) {
					UtilityParamDescriptions.DISUTILITY.addParameter(secParams, disease,  
							OSDiNames.getSource(helper, utilityName), 
							probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
				else {
					UtilityParamDescriptions.UTILITY.addParameter(secParams, disease,  
							OSDiNames.getSource(helper, utilityName), 
							probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
			} catch(TranspilerException ex) {
				throw new TranspilerException(OSDiNames.Class.UTILITY, utilityName, OSDiNames.DataProperty.HAS_VALUE, strValue);
			}
		}
	}

}
