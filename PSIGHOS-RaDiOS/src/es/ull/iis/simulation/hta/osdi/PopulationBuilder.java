/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import org.w3c.xsd.owl2.Ontology;

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
	 * @param ontology
	 * @param secParams
	 * @param disease
	 * @param populationName
	 * @return
	 */
	public static StdPopulation getPopulationInstance(Ontology ontology, SecondOrderParamsRepository secParams, Disease disease, String populationName) {
		return new OSDiPopulation(ontology, secParams, disease, populationName);		
	}
	
	private static class OSDiPopulation extends StdPopulation {
		private final String populationName;
		private final Ontology ontology;
		private final String[] strParamFemale = new String[2];
		
		public OSDiPopulation(Ontology ontology, SecondOrderParamsRepository secParams, Disease disease, String populationName) {
			super(secParams, disease);
			this.ontology = ontology;
			this.populationName = populationName;
			this.strParamFemale[0] = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "FEMALE_" + populationName;
			this.strParamFemale[1] = "Proportion of females among individuals in population " + populationName;
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
					// TODO: Check if the prevalence is related to the objective disease
					// We assume a 100% prevalence in case it is not specified
					final String strValue = OwlHelper.getDataPropertyValue(paramName, OSDiNames.DataProperty.HAS_VALUE.getDescription(), "1.0");
					final ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strValue);
					if (probabilityDistribution != null) {
						SecondOrderParam prevParam = new SecondOrderParam(secParams, paramName, "Prevalence of " + this.disease, OSDiNames.getSource(paramName), 
								probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
						secParams.addProbParam(prevParam);
					}
				}
			}
		}
		
		@Override
		protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
			return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), secParams.getProbParam(strParamFemale[0], simul));
		}
		
		@Override
		protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
