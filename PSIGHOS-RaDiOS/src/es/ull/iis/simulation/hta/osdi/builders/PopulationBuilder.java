/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.AttributeValueWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.InitiallySetPopulationAttribute;
import es.ull.iis.simulation.hta.populations.PopulationAttribute;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author IvÃ¡n Castilla RodrÃ­guez
 *
 */
public interface PopulationBuilder {

	/**
	 * Returns a population according to the description in the ontology. 
	 * TODO: Currently, it only uses the deterministic values for the population parameters. It is remaining to create second order parameters to represent the uncertainty on age, sex...
	 * @param secParams
	 * @param disease
	 * @param populationName
	 * @return
	 */
	public static StdPopulation getPopulationInstance(OSDiGenericRepository secParams, String populationName, Disease disease) throws MalformedSimulationModelException {
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(populationName, "");
		return new OSDiPopulation(secParams, populationName, description, disease);		
	}
	
	static class OSDiPopulation extends StdPopulation {
		private final RandomVariate ageVariate;
		private final DiscreteRandomVariate sexVariate;
		private ParameterWrapper prevalenceParam;
		private ParameterWrapper birthPrevalenceParam;
		private final int minAge; 
		private final int maxAge; 
		private final UtilityParameterWrapper utilityParam;
		private final OSDiWrapper wrap;
		
		public OSDiPopulation(OSDiGenericRepository secParams, String populationName, String populationDescription, Disease disease) throws MalformedSimulationModelException {
			super(secParams, populationName, populationDescription, disease);
			wrap = secParams.getOwlWrapper();
			final String strMinAge = OSDiWrapper.DataProperty.HAS_MIN_AGE.getValue(populationName, "" + super.getMinAge());
			try {
				minAge = Integer.parseInt(strMinAge);
			} catch(NumberFormatException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.POPULATION, populationName, OSDiWrapper.DataProperty.HAS_MIN_AGE, "Wrong format of " + strMinAge + " . Expected an integer value.");
			}
			final String strMaxAge = OSDiWrapper.DataProperty.HAS_MAX_AGE.getValue(populationName, "" + super.getMaxAge());
			try {
			maxAge = Integer.parseInt(strMaxAge);
			} catch(NumberFormatException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.POPULATION, populationName, OSDiWrapper.DataProperty.HAS_MAX_AGE, "Wrong format of " + strMaxAge + " . Expected an integer value.");
			}
			 // TODO: Currently we are only defining initially assigned attributes, i.e., attributes whose value does not change during the simulation 
			// Process population age
			final String ageAttribute = OSDiWrapper.ObjectProperty.HAS_AGE.getValue(populationName, true);
			final AttributeValueWrapper ageWrapper = new AttributeValueWrapper(wrap, ageAttribute);
			ageVariate = ageWrapper.getProbabilisticValue();
			// Process population sex
			final String sexAttribute = OSDiWrapper.ObjectProperty.HAS_SEX.getValue(populationName, true);
			final AttributeValueWrapper sexWrapper = new AttributeValueWrapper(wrap, sexAttribute) {
				@Override
				public RandomVariate getDefaultProbabilisticValue() {
					return RandomVariateFactory.getInstance("DiscreteConstantVariate", getDeterministicValue());
				}
			};
			sexVariate = (DiscreteRandomVariate) sexWrapper.getProbabilisticValue();
			
			this.utilityParam = addUtilityParam();
			
			// Gets all the epidemiological parameters related to the working model
			final Set<String> epidemParams = OSDiWrapper.Clazz.EPIDEMIOLOGICAL_PARAMETER.getIndividuals(true);
			// Gets the population parameters that are epidemiological parameters
			final Set<String> populationParams = OSDiWrapper.ObjectProperty.HAS_PARAMETER.getValues(name());
			populationParams.retainAll(epidemParams);
			// Processes and register the epidemiological parameters related to the population
			for (String paramName : populationParams) {
				// Ignores parameters that are both parameters of the disease and a manifestation, since they are supposed to be processed in the corresponding manifestation
				if (OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_MANIFESTATION.getValues(paramName, true).size() == 0) {
					final ParameterWrapper paramWrapper = new ParameterWrapper(wrap, paramName, "Epidemiological parameter for population " + this.name());
					// If the parameter is a prevalence
					if (OSDiWrapper.Clazz.PREVALENCE.containsIntance(paramName)) {
						if (prevalenceParam != null)
							wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_PARAMETER, "A population can define just one prevalence. Ignoring " + paramName);														
						else if (birthPrevalenceParam != null)
							wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_PARAMETER, "A population cannot define both prevalence and birth prevalence. Ignoring " + paramName);														
						else {
							checkEpidemParam(paramWrapper);
							prevalenceParam = paramWrapper;
						}
					}
					else if (OSDiWrapper.Clazz.BIRTH_PREVALENCE.containsIntance(paramName)) {
						if (birthPrevalenceParam != null)
							wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_PARAMETER, "A population can define just one birth prevalence. Ignoring " + paramName);														
						else if (prevalenceParam != null)
							wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_PARAMETER, "A population cannot define both prevalence and birth prevalence. Ignoring " + paramName);														
						else {
							checkEpidemParam(paramWrapper);
							birthPrevalenceParam = paramWrapper;
						}
					}
				}
			}
		}
		
		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			if (prevalenceParam != null)
				ProbabilityParamDescriptions.PREVALENCE.addParameter(secParams, this, prevalenceParam.getSource(), prevalenceParam.getDeterministicValue(), prevalenceParam.getProbabilisticValue());
			else if (birthPrevalenceParam != null)
				ProbabilityParamDescriptions.BIRTH_PREVALENCE.addParameter(secParams, this, birthPrevalenceParam.getSource(), birthPrevalenceParam.getDeterministicValue(), birthPrevalenceParam.getProbabilisticValue());

			if (utilityParam != null)
				UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, name(), utilityParam.getDescription(), utilityParam.getSource(), 
						utilityParam.getDeterministicValue(), utilityParam.getProbabilisticValue()); 
		}
		
		private void checkEpidemParam(ParameterWrapper paramWrapper) {
			final String paramName = paramWrapper.getParamId();
			// The parameter should be related to both the population and the disease
			final Set<String> relatedDiseases = OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_DISEASE.getValues(paramName, true);
			if (relatedDiseases.size() == 0 || relatedDiseases.contains(disease.name())) {
				if (relatedDiseases.size() == 0) {
					wrap.printWarning(paramName, OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_DISEASE, "The parameter is only related to the population but not to the disease. We will assume that it is as far as only one disease is supported by OSDi");
				}
				else if (relatedDiseases.size() > 1) {
					wrap.printWarning(paramName, OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_DISEASE, "Until more than one disease is supported by OSDi, a population parameter should be related to the only one disease. Currently " + relatedDiseases.size());														
				}
			}
		}
		
		/**
		 * Registers the base utility associated to the population by extracting the information from the ontology. 
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private UtilityParameterWrapper addUtilityParam() throws MalformedOSDiModelException {
			final Set<String> utilities = OSDiWrapper.ObjectProperty.HAS_UTILITY.getValues(name(), true);
			if (utilities.size() == 0)
				return null;
			if (utilities.size() > 1)
				wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Found more than one utility for a population. Using only " + utilities.toArray()[0]);

			final UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, (String)utilities.toArray()[0], "Utility for population " + this.name()); 
			final OSDiWrapper.TemporalBehavior tempBehavior = utilityParam.getTemporalBehavior();
			if (OSDiWrapper.TemporalBehavior.ONETIME.equals(tempBehavior))
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.POPULATION, name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Only annual utilities should be associated to a population. Instead, " + tempBehavior + " found");
			if (OSDiWrapper.UtilityType.DISUTILITY.equals(utilityParam.getType()))
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.POPULATION, name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Only utilities should be associated to a population. Instead, disutility found");
			return utilityParam;
		}

		@Override
		protected DiscreteRandomVariate getSexVariate(Patient pat) {
			return sexVariate;
		}
		
		@Override
		protected DiscreteRandomVariate getDiseaseVariate(Patient pat) {
			if (birthPrevalenceParam != null)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), ProbabilityParamDescriptions.BIRTH_PREVALENCE.getValue(getRepository(), name(), pat));
			else if (prevalenceParam != null)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), ProbabilityParamDescriptions.PREVALENCE.getValue(getRepository(), name(), pat));
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
		}
		
		@Override
		protected DiscreteRandomVariate getDiagnosedVariate(Patient pat) {
			// TODO Do something with true and apparent epidemiologic parameters
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
		}
		
		@Override
		protected RandomVariate getBaselineAgeVariate(Patient pat) {
			return ageVariate;
		}
		
		@Override
		public int getMinAge() {
			return minAge;
		}
		
		@Override
		public int getMaxAge() {
			return maxAge;
		}

		@Override
		protected List<PopulationAttribute> initializePatientAttributeList() throws MalformedSimulationModelException {
			final OSDiGenericRepository OSDiParams = ((OSDiGenericRepository)getRepository()); 
			final OSDiWrapper wrap = OSDiParams.getOwlWrapper();

			final Set<String> attributes = OSDiWrapper.ObjectProperty.HAS_ATTRIBUTE_VALUE.getValues(name(), true);
			final ArrayList<PopulationAttribute> attributeList = new ArrayList<>();
			try {
				for (String attribute : attributes) {
					final AttributeValueWrapper attWrapper = new AttributeValueWrapper(wrap, attribute);
					final String attributeName = OSDiWrapper.DataProperty.HAS_NAME.getValue(attWrapper.getAttributeId(), attWrapper.getAttributeId());
					attributeList.add(new InitiallySetPopulationAttribute(attributeName, attWrapper.getProbabilisticValue()));										
				}
			} catch(MalformedOSDiModelException ex) {
				throw new MalformedSimulationModelException("Error parsing attribute value from ontology. Caused by " + ex.getMessage());
			}
			return attributeList;
		}

	}
}
