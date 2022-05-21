/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
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
public class PopulationBuilder {

	/**
	 * 
	 */
	private PopulationBuilder() {
	}

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
	
	private static class OSDiPopulation extends StdPopulation {
		private final String populationName;
		private final String[] strParamFemale = new String[2];
		private final String[] strParamPrevalence = new String[2];
		private final String[] strParamBirthPrevalence = new String[2];
		private final ProbabilityDistribution ageDist;
		private boolean hasPrevalence;
		private boolean hasBirthPrevalence;
		
		public OSDiPopulation(SecondOrderParamsRepository secParams, Disease disease, String populationName) {
			super(secParams, disease);
			this.populationName = populationName;
			this.strParamFemale[0] = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "FEMALE_" + populationName;
			this.strParamFemale[1] = "Proportion of females among individuals in population " + populationName;
			this.strParamPrevalence[0] = "PREVALENCE_" + populationName;
			this.strParamPrevalence[1] = "Prevalence of disease " + disease + " in population " + populationName;
			this.strParamBirthPrevalence[0] = "BIRTH_PREVALENCE_" + populationName;
			this.strParamBirthPrevalence[1] = "Birth prevalence of disease " + disease + " in population " + populationName;
			final String strAge = OwlHelper.getDataPropertyValue(populationName, OSDiNames.DataProperty.HAS_AGE.getDescription(), "0.0");
			this.ageDist = ValueParser.splitProbabilityDistribution(strAge);
			this.hasPrevalence = false;
			this.hasBirthPrevalence = false;
		}
		
		@Override
		public void registerSecondOrderParameters() {
			final String strPFemale = OwlHelper.getDataPropertyValue(populationName, OSDiNames.DataProperty.HAS_FEMALE_PROPORTION.getDescription(), "0.5");
			final ProbabilityDistribution pFemale = ValueParser.splitProbabilityDistribution(strPFemale);
			if (pFemale != null) {
				SecondOrderParam sexParam = new SecondOrderParam(secParams, strParamFemale[0], strParamFemale[1], 
					"",	pFemale.getDeterministicValue(), pFemale.getProbabilisticValue());
				secParams.addProbParam(sexParam);
			}
			// Register epidemiologic parameters
			List<String> epidemParams = OwlHelper.getChildsByClassName(populationName, OSDiNames.Class.EPIDEMIOLOGICAL_PARAMETER.getDescription());
			for (String paramName : epidemParams) {
				final String type = OwlHelper.getDataPropertyValue(paramName, OSDiNames.DataProperty.HAS_EPIDEMIOLOGICAL_PARAMETER_KIND.getDescription());
				if (OSDiNames.DataPropertyRange.KIND_EPIDEMIOLOGICAL_PARAMETER_PREVALENCE.getDescription().equals(type)) {
					registerEpidemParam(paramName, strParamPrevalence);
					hasPrevalence = true;
				}
				else if (OSDiNames.DataPropertyRange.KIND_EPIDEMIOLOGICAL_PARAMETER_BIRTH_PREVALENCE.getDescription().equals(type)) {
					registerEpidemParam(paramName, strParamBirthPrevalence);
					hasBirthPrevalence = true;
				}

			}
		}
		
		private void registerEpidemParam(String paramName, String[] nameAndDesc) {
			// Check if the prevalence is related to the objective disease
			final List<String> strDiseases = OwlHelper.getObjectPropertiesByName(paramName, OSDiNames.ObjectProperty.IS_PARAMETER_OF_DISEASE.getDescription());
			boolean found = false;
			for (String strDisease : strDiseases) {
				if (strDisease.equals(disease.name()))
					found = true;
			}
			if (found) {
				// We assume a 100% prevalence in case it is not specified
				final String strValue = OwlHelper.getDataPropertyValue(paramName, OSDiNames.DataProperty.HAS_VALUE.getDescription(), "1.0");
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
	}
}
