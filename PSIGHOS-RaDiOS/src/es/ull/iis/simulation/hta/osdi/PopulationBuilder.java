/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.ClinicalParameter;
import es.ull.iis.simulation.hta.populations.InitiallySetClinicalParameter;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
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
	public static StdPopulation getPopulationInstance(OSDiGenericRepository secParams, Disease disease, String populationName) throws TranspilerException, MalformedSimulationModelException {
				
		return new OSDiPopulation(secParams, disease, populationName, OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(secParams.getOwlWrapper(), populationName, ""));		
	}
	
	static class OSDiPopulation extends StdPopulation {
		private final ProbabilityDistribution ageDist;
		private boolean hasPrevalence;
		private boolean hasBirthPrevalence;
		private final int minAge; 
		private final String strFemaleParamName;
		
		public OSDiPopulation(OSDiGenericRepository secParams, Disease disease, String populationName, String populationDescription) throws TranspilerException, MalformedSimulationModelException {
			super(secParams, populationName, populationDescription, disease);
			final String strAge = OSDiNames.DataProperty.HAS_AGE.getValue(secParams.getOwlHelper(), populationName, "0.0");
			this.ageDist = new ProbabilityDistribution(strAge);
			this.hasPrevalence = false;
			this.hasBirthPrevalence = false;
			final String strMinAge = OSDiNames.DataProperty.HAS_MIN_AGE.getValue(secParams.getOwlHelper(), populationName, "" + super.getMinAge());
			minAge = Integer.parseInt(strMinAge);
			this.strFemaleParamName = "FEMALE_" + populationName;
		}
		
		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			final OSDiGenericRepository OSDiParams = ((OSDiGenericRepository)secParams); 
			final OwlHelper helper = OSDiParams.getOwlHelper(); 
			try {
				final String strPFemale = OSDiNames.DataProperty.HAS_FEMALE_PROPORTION.getValue(helper, name(), "0.5");
				final ProbabilityDistribution pFemale = new ProbabilityDistribution(strPFemale);
				ProbabilityParamDescriptions.PROPORTION.addParameter(secParams, strFemaleParamName, "females in population " + name(), 
						"",	pFemale.getDeterministicValue(), pFemale.getProbabilisticValue());
				// Register epidemiologic parameters
				List<String> epidemParams = OSDiNames.Class.EPIDEMIOLOGICAL_PARAMETER.getDescendantsOf(helper, name());
				for (String paramName : epidemParams) {
					final String type = OSDiNames.DataProperty.HAS_EPIDEMIOLOGICAL_PARAMETER_KIND.getValue(helper, paramName);
					if (OSDiNames.DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_PREVALENCE.getDescription().equals(type)) {
						registerEpidemParam(OSDiParams, paramName, ProbabilityParamDescriptions.PREVALENCE);
						hasPrevalence = true;
					}
					else if (OSDiNames.DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_BIRTH_PREVALENCE.getDescription().equals(type)) {
						registerEpidemParam(OSDiParams, paramName, ProbabilityParamDescriptions.BIRTH_PREVALENCE);
						hasBirthPrevalence = true;
					}

				}
				registerUtilityParam(OSDiParams);
			} catch(TranspilerException ex) {
				System.err.println(ex.getMessage());
			}
		}
		
		/**
		 * Registers the base utility associated to the population by extracting the information from the ontology. 
		 * @throws TranspilerException When there was a problem parsing the ontology
		 */
		private void registerUtilityParam(OSDiGenericRepository secParams) throws TranspilerException {
			final OwlHelper helper = secParams.getOwlHelper();		
			
			List<String> utilities = OSDiNames.Class.UTILITY.getDescendantsOf(helper, name());
			if (utilities.size() > 1)
				throw new TranspilerException("Only one base utility should be associated to the population \"" + name() + "\". Instead, " + utilities.size() + " found");
			for (String utilityName : utilities) {
				// Assumes annual behavior if not specified
				final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(helper, utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
				// Indeed, only annual behavior is valid 
				if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
					throw new TranspilerException("Only utilities with annual temporal behavior should be associated to the population \"" + name() + "\". Instead, " + strTempBehavior + " found");
				// Assumes that it is a utility (not a disutility) if not specified
				final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(helper, utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
				// Only utilities can be used for populations
				if (OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType))
					throw new TranspilerException("Only utilities should be associated to the population \"" + name() + "\". Instead, found a disutility");
				// Default value is 1;
				final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, utilityName, "1.0");
				// Assumes a default calculation method specified in Constants if not specified
				final String strCalcMethod = OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getValue(helper, utilityName, "Unknown");
				try {
					final ProbabilityDistribution probDistribution = new ProbabilityDistribution(strValue);
					UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, name(), OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(helper, utilityName, "Utility for " + name() + " calculated using " + strCalcMethod),  
							OSDiNames.getSource(helper, utilityName), probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				} catch(TranspilerException ex) {
					throw new TranspilerException(OSDiNames.Class.UTILITY, utilityName, OSDiNames.DataProperty.HAS_VALUE, strValue, ex);
				}
			}
		}
		
		private void registerEpidemParam(OSDiGenericRepository secParams, String paramName, ProbabilityParamDescriptions secondOrderParam) throws TranspilerException {
			final OwlHelper helper = secParams.getOwlHelper();		
			
			final List<String> manifestations = OSDiNames.ObjectProperty.IS_PARAMETER_OF_MANIFESTATION.getValues(helper, paramName);
			// If the parameter is related to a manifestation, is intended to be an initial proportion
			if (!manifestations.isEmpty()) {
				// We assume a 100% prevalence in case it is not specified
				final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, paramName, "1.0");
				try {
					final ProbabilityDistribution probabilityDistribution = new ProbabilityDistribution(strValue);
					for (String manifestationName: manifestations) {
						ProbabilityParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, disease.getManifestation(manifestationName), OSDiNames.getSource(helper, paramName),
								probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());						
					}
				} catch(TranspilerException ex) {
					throw new TranspilerException(OSDiNames.Class.EPIDEMIOLOGICAL_PARAMETER, paramName, OSDiNames.DataProperty.HAS_VALUE, strValue, ex);
				}
			}
			else {
				// Check if the prevalence is related to the objective disease
				final List<String> strDiseases = OSDiNames.ObjectProperty.IS_PARAMETER_OF_DISEASE.getValues(helper, paramName);
				boolean found = false;
				for (String strDisease : strDiseases) {
					if (strDisease.equals(disease.name()))
						found = true;
				}
				if (found) {
					// We assume a 100% prevalence in case it is not specified
					final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, paramName, "1.0");
					try {
						final ProbabilityDistribution probabilityDistribution = new ProbabilityDistribution(strValue);
						secondOrderParam.addParameter(secParams, name(), name(), OSDiNames.getSource(helper, paramName), 
								probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
					} catch(TranspilerException ex) {
						throw new TranspilerException(OSDiNames.Class.EPIDEMIOLOGICAL_PARAMETER, paramName, OSDiNames.DataProperty.HAS_VALUE, strValue, ex);
					}
				}
			}
		}

		@Override
		protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), ProbabilityParamDescriptions.PROPORTION.getValue(getRepository(), strFemaleParamName, simul));
		}
		
		@Override
		protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
			if (hasBirthPrevalence)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), ProbabilityParamDescriptions.BIRTH_PREVALENCE.getValue(getRepository(), name(), simul));
			else if (hasPrevalence)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), ProbabilityParamDescriptions.PREVALENCE.getValue(getRepository(), name(), simul));
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
		}
		
		@Override
		protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
			// TODO Do something with true and apparent epidemiologic parameters
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
		}
		
		@Override
		protected RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul) {
			return ageDist.getProbabilisticValue();
		}
		
		@Override
		public int getMinAge() {
			return minAge;
		}

		@Override
		protected List<ClinicalParameter> initializePatientParameterList() throws MalformedSimulationModelException {
			final OwlHelper helper = ((OSDiGenericRepository) getRepository()).getOwlHelper();		

			final ArrayList<ClinicalParameter> paramList = new ArrayList<>();
			List<String> indParams = OSDiNames.ObjectProperty.DEFINES_INDIVIDUAL_PARAMETER_VALUE.getValues(helper, name());
			for (String paramName : indParams) {
				try {
					final String indParamParamName = OSDiNames.ObjectProperty.IS_VALUE_OF_INDIVIDUAL_PARAMETER.getValue(helper, paramName);
					final String indParamName = OSDiNames.DataProperty.HAS_NAME.getValue(helper, indParamParamName);
					final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, paramName, "0.0");
					final ProbabilityDistribution probabilityDistribution = new ProbabilityDistribution(strValue);
					paramList.add(new InitiallySetClinicalParameter(indParamName, probabilityDistribution.getProbabilisticValue()));				
				} catch(TranspilerException ex) {
					System.err.println(ex.getMessage());
				}
			}
			return paramList;
		}

	}
}
