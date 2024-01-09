package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Parses an expression defined in the ontology
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionWrapper {

	// TODO: Process parameters when expressed in different ways. E.g. gamma parameters may be average and standard deviation
	public enum SupportedProbabilityDistributions {
		BERNOULLI("BernoulliVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_PROBABILITY_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		NORMAL("NormalVariate"),
		UNIFORM("UniformVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_LOWER_LIMIT_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_UPPER_LIMIT_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		BETA("BetaVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_BETA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		GAMMA("GammaVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		EXPONENTIAL("ExponentialVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		POISSON("PoissonVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		};
		private final String variateName;
		
		private SupportedProbabilityDistributions(String variateName) {
			this.variateName = variateName;
		}
		
		public String[] getParameters(OSDiWrapper wrap, String instanceId) {
			return new String[] {OSDiDataProperties.HAS_AVERAGE_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_STANDARD_DEVIATION_PARAMETER.getValue(instanceId, "0")};
		}
		
		/**
		 * @return the variateName
		 */
		public String getVariateName() {
			return variateName;
		}
		
	}
	private final RandomVariate rnd;
	private final String instanceIRI;

	public ExpressionWrapper(OSDiWrapper wrap, String instanceIRI) throws MalformedOSDiModelException {
		final Set<String> superclasses = wrap.getClassesForIndividual(instanceIRI);
		RandomVariate rnd = null;
		this.instanceIRI = instanceIRI; 
		SupportedProbabilityDistributions dist = null;
		if (superclasses.contains(OSDiClasses.NORMAL_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.NORMAL;
		}
		else if (superclasses.contains(OSDiClasses.UNIFORM_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.UNIFORM;				
		}
		else if (superclasses.contains(OSDiClasses.BETA_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.BETA;
		}
		else if (superclasses.contains(OSDiClasses.GAMMA_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.GAMMA;
		}
		else if (superclasses.contains(OSDiClasses.EXPONENTIAL_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.EXPONENTIAL;
		}
		else if (superclasses.contains(OSDiClasses.POISSON_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.POISSON;
		}
		else if (superclasses.contains(OSDiClasses.BERNOULLI_DISTRIBUTION_EXPRESSION.getShortName())) {
			dist = SupportedProbabilityDistributions.BERNOULLI;
		}
		if (dist == null) {
			throw new MalformedOSDiModelException("Unsupported probability distribution " + instanceIRI);
		}
		
		rnd = buildDistributionVariate(dist.getVariateName(), dist.getParameters(wrap, instanceIRI)); 

		String strOffset = OSDiDataProperties.HAS_OFFSET_PARAMETER.getValue(instanceIRI, "0.0");
		double offset = 0.0;
		try {
			offset = Double.parseDouble(strOffset);
		} catch(NumberFormatException ex) {
			throw new MalformedOSDiModelException(OSDiClasses.PROBABILITY_DISTRIBUTION_EXPRESSION, instanceIRI, OSDiDataProperties.HAS_OFFSET_PARAMETER, "Invalid offset parameter for probabilistic expression. Found " + strOffset);
		}
		String strScale = OSDiDataProperties.HAS_SCALE_PARAMETER.getValue(instanceIRI, "1.0");
		double scale = 1.0;
		try {
			scale = Double.parseDouble(strScale);
		} catch(NumberFormatException ex) {
			throw new MalformedOSDiModelException(OSDiClasses.PROBABILITY_DISTRIBUTION_EXPRESSION, instanceIRI, OSDiDataProperties.HAS_SCALE_PARAMETER, "Invalid scale parameter for probabilistic expression. Found " + strScale);
		}
		if (scale != 1.0 || offset != 0.0) {
			rnd = RandomVariateFactory.getInstance("ScaledVariate", rnd, scale, offset);
		}
		this.rnd = rnd;
	}

	public String getOriginalIndividualIRI() {
		return instanceIRI;
	}
	/**
	 * @return the rnd
	 */
	public RandomVariate getRnd() {
		return rnd;
	}

	/**
	 * 
	 * @param distributionName
	 * @param parameters
	 * @return
	 */
	private static RandomVariate buildDistributionVariate(String distributionName, String[] parameters) {
		double []numParams = new double[parameters.length];
		for (int i = 0; i < numParams.length; i++)
			numParams[i] = Double.parseDouble(parameters[i]);
		if (numParams.length == 4)
			return RandomVariateFactory.getInstance(distributionName, numParams[0], numParams[1], numParams[2], numParams[3]);
		if (numParams.length == 3)
			return RandomVariateFactory.getInstance(distributionName, numParams[0], numParams[1], numParams[2]);
		if (numParams.length == 2)
			return RandomVariateFactory.getInstance(distributionName, numParams[0], numParams[1]);
		return RandomVariateFactory.getInstance(distributionName, numParams[0]);
	}

}
