package es.ull.iis.simulation.hta.osdi;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
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
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		
		Disease disease = new Disease(secParams, diseaseName, OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(wrap, diseaseName, "")) {

			@Override
			public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
				try {
					final OSDiWrapper wrap = ((OSDiGenericRepository) secParams).getOwlWrapper();
					
					createUtilityParam(wrap, this);
					createCostParams(wrap, this);
				} catch (MalformedOSDiModelException e) {
					System.err.println(e.getMessage());
				}
			}
			
		};
		// Build developments
		final Set<String> developments = OSDiWrapper.Clazz.DEVELOPMENT.getIndividuals(wrap, true);
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(secParams, developmentName, disease);
		}
		
		// Build manifestations
		final Set<String> manifestations = OSDiWrapper.Clazz.MANIFESTATION.getIndividuals(wrap, true);
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			final Manifestation manif = disease.getManifestation(manifestationName);
			final Set<String> pathways = OSDiWrapper.ObjectProperty.HAS_PATHWAY.getValues(wrap, manifestationName, true);
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(secParams, manif, pathwayName);
			// Also include exclusions among manifestations
			final Set<String> exclusions = OSDiWrapper.ObjectProperty.EXCLUDES_MANIFESTATION.getValues(wrap, manifestationName);
			for (String excludedManif : exclusions) {
				disease.addExclusion(manif, disease.getManifestation(excludedManif));
			}
		}
		return disease;
	}
	
	private static void createCostParam(OSDiWrapper wrap, OSDiWrapper.ObjectProperty costProperty, CostParamDescriptions paramDescription, Disease disease) throws MalformedOSDiModelException {
		Set<String> costs = costProperty.getValues(wrap, disease.name(), true);
		if (costs.size() > 1 )
			wrap.printWarning(disease.name(), costProperty, "Found more than one cost for a disease. Using only " + costs.toArray()[0]);
		// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual cost from all the defined ones
		
		final CostParameterWrapper costParam = new CostParameterWrapper(wrap, (String)costs.toArray()[0], 0.0);
		final OSDiWrapper.TemporalBehavior tempBehavior = costParam.getTemporalBehavior();
		// Checking coherence between type of cost parameter and its temporal behavior. Assumed to be ok if temporal behavior not specified 
		if (CostParamDescriptions.DIAGNOSIS_COST.equals(paramDescription)) {
			if (OSDiWrapper.TemporalBehavior.ANNUAL.equals(tempBehavior))
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE, disease.name(), costProperty, "Diagnosis costs directly associated to a disease should be ONE_TIME. Instead, " + tempBehavior + " found");
		}
		else {
			if (OSDiWrapper.TemporalBehavior.ONETIME.equals(tempBehavior))
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE, disease.name(), costProperty, "Follow-up, treatment and non specific costs directly associated to a disease should be ANNUAL. Instead, " + tempBehavior + " found");
		}
		paramDescription.addParameter(disease.getRepository(), disease, costParam.getSource(), costParam.getYear(),
				costParam.getDeterministicValue(), costParam.getProbabilisticValue());
	}
	/**
	 * Creates the costs associated to a specific disease by extracting the information from the ontology. 
	 * TODO: Process strategies and not only costs directly defined
	 * @param wrap The ontology wrapper
	 * @param disease A disease
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 */
	private static void createCostParams(OSDiWrapper wrap, Disease disease) throws MalformedOSDiModelException {
		createCostParam(wrap, OSDiWrapper.ObjectProperty.HAS_COST, CostParamDescriptions.ANNUAL_COST, disease);
		createCostParam(wrap, OSDiWrapper.ObjectProperty.HAS_FOLLOW_UP_COST, CostParamDescriptions.ANNUAL_COST, disease);
		createCostParam(wrap, OSDiWrapper.ObjectProperty.HAS_TREATMENT_COST, CostParamDescriptions.ANNUAL_COST, disease);
		createCostParam(wrap, OSDiWrapper.ObjectProperty.HAS_DIAGNOSIS_COST, CostParamDescriptions.ONE_TIME_COST, disease);
		createCostParam(wrap, OSDiWrapper.ObjectProperty.HAS_SCREENING_COST, CostParamDescriptions.ONE_TIME_COST, disease);
	}
	
	/**
	 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
	 * @param disease A disease
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 */
	private static void createUtilityParam(OSDiWrapper wrap, Disease disease) throws MalformedOSDiModelException {
		final Set<String> utilities = OSDiWrapper.ObjectProperty.HAS_UTILITY.getValues(wrap, disease.name(), true);
		if (utilities.size() > 1 )
			wrap.printWarning(disease.name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "A maximum of one annual (dis)utility should be associated to a disease. Using only " + utilities.toArray()[0]);

		final String utilityName = (String) utilities.toArray()[0];
		final UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilityName); 
		final OSDiWrapper.TemporalBehavior tempBehavior = utilityParam.getTemporalBehavior();
		
		if (OSDiWrapper.TemporalBehavior.ONETIME.equals(tempBehavior))
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.DISEASE, disease.name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Only annual (dis)utilities should be associated to a disease. Instead, " + tempBehavior + " found");
		
		final UtilityParamDescriptions utilityDesc = OSDiWrapper.UtilityType.DISUTILITY.equals(utilityParam.getType()) ? UtilityParamDescriptions.DISUTILITY : UtilityParamDescriptions.UTILITY;
		utilityDesc.addParameter(disease.getRepository(), disease, utilityParam.getSource(),
					utilityParam.getDeterministicValue(), utilityParam.getProbabilisticValue());
	}

}
