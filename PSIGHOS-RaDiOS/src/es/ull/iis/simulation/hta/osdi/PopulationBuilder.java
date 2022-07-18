/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
	public static StdPopulation getPopulationInstance(SecondOrderParamsRepository secParams, Disease disease, String populationName) {
		
		return new OSDiPopulation(secParams, disease, populationName);		
	}
	
	static class OSDiPopulation extends StdPopulation {
		private final String populationName;
		private final String[] strParamFemale = new String[2];
		private final String[] strParamPrevalence = new String[2];
		private final String[] strParamBirthPrevalence = new String[2];
		private final ProbabilityDistribution ageDist;
		private boolean hasPrevalence;
		private boolean hasBirthPrevalence;
		private final int minAge; 
		
		public OSDiPopulation(SecondOrderParamsRepository secParams, Disease disease, String populationName) {
			super(secParams, disease);
			this.populationName = populationName;
			this.strParamFemale[0] = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "FEMALE_" + populationName;
			this.strParamFemale[1] = "Proportion of females among individuals in population " + populationName;
			this.strParamPrevalence[0] = "PREVALENCE_" + populationName;
			this.strParamPrevalence[1] = "Prevalence of disease " + disease + " in population " + populationName;
			this.strParamBirthPrevalence[0] = "BIRTH_PREVALENCE_" + populationName;
			this.strParamBirthPrevalence[1] = "Birth prevalence of disease " + disease + " in population " + populationName;
			final String strAge = OSDiNames.DataProperty.HAS_AGE.getValue(populationName, "0.0");
			this.ageDist = ValueParser.splitProbabilityDistribution(strAge);
			this.hasPrevalence = false;
			this.hasBirthPrevalence = false;
			final String strMinAge = OSDiNames.DataProperty.HAS_MIN_AGE.getValue(populationName, "" + super.getMinAge());
			minAge = Integer.parseInt(strMinAge);
		}
		
		@Override
		public void registerSecondOrderParameters() {
			final String strPFemale = OSDiNames.DataProperty.HAS_FEMALE_PROPORTION.getValue(populationName, "0.5");
			final ProbabilityDistribution pFemale = ValueParser.splitProbabilityDistribution(strPFemale);
			if (pFemale != null) {
				SecondOrderParam sexParam = new SecondOrderParam(secParams, strParamFemale[0], strParamFemale[1], 
					"",	pFemale.getDeterministicValue(), pFemale.getProbabilisticValue());
				secParams.addProbParam(sexParam);
			}
			// Register epidemiologic parameters
			List<String> epidemParams = OSDiNames.Class.EPIDEMIOLOGICAL_PARAMETER.getDescendantsOf(populationName);
			for (String paramName : epidemParams) {
				final String type = OSDiNames.DataProperty.HAS_EPIDEMIOLOGICAL_PARAMETER_KIND.getValue(paramName);
				if (OSDiNames.DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_PREVALENCE.getDescription().equals(type)) {
					registerEpidemParam(paramName, strParamPrevalence);
					hasPrevalence = true;
				}
				else if (OSDiNames.DataPropertyRange.EPIDEMIOLOGICAL_PARAMETER_KIND_BIRTH_PREVALENCE.getDescription().equals(type)) {
					registerEpidemParam(paramName, strParamBirthPrevalence);
					hasBirthPrevalence = true;
				}

			}
			try {
				registerUtilityParam();
			} catch(TranspilerException ex) {
				System.err.println(ex.getMessage());
			}
		}
		
		/**
		 * Registers the base utility associated to the population by extracting the information from the ontology. 
		 * @throws TranspilerException When there was a problem parsing the ontology
		 */
		private void registerUtilityParam() throws TranspilerException {
			List<String> utilities = OSDiNames.Class.UTILITY.getDescendantsOf(populationName);
			if (utilities.size() > 1)
				throw new TranspilerException("Only one base utility should be associated to the population \"" + populationName + "\". Instead, " + utilities.size() + " found");
			for (String utilityName : utilities) {
				// Assumes annual behavior if not specified
				final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
				// Indeed, only annual behavior is valid 
				if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
					throw new TranspilerException("Only utilities with annual temporal behavior should be associated to the population \"" + populationName + "\". Instead, " + strTempBehavior + " found");
				// Assumes that it is a utility (not a disutility) if not specified
				final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
				// Only utilities can be used for populations
				if (OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType))
					throw new TranspilerException("Only utilities should be associated to the population \"" + populationName + "\". Instead, found a disutility");
				// Default value is 1;
				final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(utilityName, "1.0");
				// Assumes a default calculation method specified in Constants if not specified
				final String strCalcMethod = OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getValue(utilityName, Constants.UTILITY_DEFAULT_CALCULATION_METHOD);
				final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
				if (probDistribution == null)
					throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + populationName + "\"");
				secParams.addBaseUtilityParam(OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(utilityName, "Utility for " + populationName + " calculated using " + strCalcMethod),  
						OSDiNames.getSource(utilityName), probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
			}
		}
		
		private void registerEpidemParam(String paramName, String[] nameAndDesc) {
			// Check if the prevalence is related to the objective disease
			final List<String> strDiseases = OSDiNames.ObjectProperty.IS_PARAMETER_OF_DISEASE.getValues(paramName);
			boolean found = false;
			for (String strDisease : strDiseases) {
				if (strDisease.equals(disease.name()))
					found = true;
			}
			if (found) {
				// We assume a 100% prevalence in case it is not specified
				final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(paramName, "1.0");
				final ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strValue);
				if (probabilityDistribution != null) {
					SecondOrderParam prevParam = new SecondOrderParam(secParams, nameAndDesc[0], nameAndDesc[1], OSDiNames.getSource(paramName), 
							probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
					secParams.addProbParam(prevParam);
				}
			}
		}
		
		@Override
		protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), secParams.getProbParam(strParamFemale[0], simul));
		}
		
		@Override
		protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
			if (hasBirthPrevalence)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), secParams.getProbParam(strParamBirthPrevalence[0], simul));
			else if (hasPrevalence)
				return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), secParams.getProbParam(strParamPrevalence[0], simul));
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
		}
		
		@Override
		protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
			// TODO Make something with true and apparent epidemiologic parameters
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
	}
}
