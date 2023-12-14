package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Parses an expression defined in the ontology
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionWrapper implements ExpressableWrapper {

	// TODO: Process parameters when expressed in different ways. E.g. gamma parameters may be average and standard deviation
	public enum SupportedProbabilityDistributions {
		NORMAL("NormalVariate"),
		UNIFORM("UniformVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LOWER_LIMIT_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_UPPER_LIMIT_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		BETA("BetaVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_BETA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		GAMMA("GammaVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		EXPONENTIAL("ExponentialVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		},
		POISSON("PoissonVariate") {
			public String[] getParameters(OSDiWrapper wrap, String instanceId) {
				return new String[] {OSDiWrapper.DataProperty.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
			}
		};
		private final String variateName;
		
		private SupportedProbabilityDistributions(String variateName) {
			this.variateName = variateName;
		}
		
		public String[] getParameters(OSDiWrapper wrap, String instanceId) {
			return new String[] {OSDiWrapper.DataProperty.HAS_AVERAGE_PARAMETER.getValue(instanceId, "0"), OSDiWrapper.DataProperty.HAS_STANDARD_DEVIATION_PARAMETER.getValue(instanceId, "0")};
		}
		
		/**
		 * @return the variateName
		 */
		public String getVariateName() {
			return variateName;
		}
		
	}
	private static final String DISTRIBUTION_NAME_SUFFIX = "Variate";
	private final RandomVariate rnd;
	private final String exprToEvaluate;
	private final String instanceIRI;

	public ExpressionWrapper(OSDiWrapper wrap, String instanceIRI) throws MalformedOSDiModelException {
		final Set<String> superclasses = wrap.getClassesForIndividual(instanceIRI);
		RandomVariate rnd = null;
		String exprToEvaluate = null;
		this.instanceIRI = instanceIRI; 
		if (superclasses.contains(OSDiWrapper.Clazz.AD_HOC_EXPRESSION.getShortName())) {
			exprToEvaluate = OSDiWrapper.DataProperty.HAS_EXPRESSION_VALUE.getValue(instanceIRI, "");
			final Set<String> referencedAttributes = OSDiWrapper.ObjectProperty.DEPENDS_ON_ATTRIBUTE.getValues(instanceIRI, true);
			final Set<String> referencedParameters = OSDiWrapper.ObjectProperty.DEPENDS_ON_PARAMETER.getValues(instanceIRI, true);
			// TODO: Process dependences with Attributes and Parameters
		}
		else if (superclasses.contains(OSDiWrapper.Clazz.PROBABILITY_DISTRIBUTION_EXPRESSION.getShortName())) {
			SupportedProbabilityDistributions dist = null;
			if (superclasses.contains(OSDiWrapper.Clazz.NORMAL_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.NORMAL;
			}
			else if (superclasses.contains(OSDiWrapper.Clazz.UNIFORM_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.UNIFORM;				
			}
			else if (superclasses.contains(OSDiWrapper.Clazz.BETA_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.BETA;
			}
			else if (superclasses.contains(OSDiWrapper.Clazz.GAMMA_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.GAMMA;
			}
			else if (superclasses.contains(OSDiWrapper.Clazz.EXPONENTIAL_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.EXPONENTIAL;
			}
			else if (superclasses.contains(OSDiWrapper.Clazz.POISSON_DISTRIBUTION_EXPRESSION.getShortName())) {
				dist = SupportedProbabilityDistributions.POISSON;
			}
			if (dist == null) {
				throw new MalformedOSDiModelException("Unsupported probability distribution " + instanceIRI);
			}
			
			rnd = buildDistributionVariate(dist.getVariateName(), dist.getParameters(wrap, instanceIRI)); 

			String strOffset = OSDiWrapper.DataProperty.HAS_OFFSET_PARAMETER.getValue(instanceIRI, "0.0");
			double offset = 0.0;
			try {
				offset = Double.parseDouble(strOffset);
			} catch(NumberFormatException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PROBABILITY_DISTRIBUTION_EXPRESSION, instanceIRI, OSDiWrapper.DataProperty.HAS_OFFSET_PARAMETER, "Invalid offset parameter for probabilistic expression. Found " + strOffset);
			}
			String strScale = OSDiWrapper.DataProperty.HAS_SCALE_PARAMETER.getValue(instanceIRI, "1.0");
			double scale = 1.0;
			try {
				scale = Double.parseDouble(strScale);
			} catch(NumberFormatException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PROBABILITY_DISTRIBUTION_EXPRESSION, instanceIRI, OSDiWrapper.DataProperty.HAS_SCALE_PARAMETER, "Invalid scale parameter for probabilistic expression. Found " + strScale);
			}
			if (scale != 1.0 || offset != 0.0) {
				rnd = RandomVariateFactory.getInstance("ScaledVariate", rnd, scale, offset);
			}
		}
		this.exprToEvaluate = exprToEvaluate;
		this.rnd = rnd;
	}

	@Override
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
	 * @return the exprToEvaluate
	 */
	public String getExprToEvaluate() {
		return exprToEvaluate;
	}

	/**
	 * 
	 * @param distributionName
	 * @param parameters
	 * @return
	 */
	private static RandomVariate buildDistributionVariate(String distributionName, String[] parameters) {
		int validParams = 3;
		while ((validParams > 0) && parameters[validParams] == null)
			validParams--;
		double []numParams = new double[validParams + 1];
		for (int i = 0; i <= validParams; i++)
			numParams[i] = Double.parseDouble(parameters[i]);
		if (validParams == 3)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1], numParams[2], numParams[3]);
		if (validParams == 2)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1], numParams[2]);
		if (validParams == 1)
			return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0], numParams[1]);
		return RandomVariateFactory.getInstance(distributionName + DISTRIBUTION_NAME_SUFFIX, numParams[0]);
	}

}
