package es.ull.iis.simulation.hta.osdi.wrappers;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
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

	public ExpressionWrapper(OSDiWrapper wrap, String instanceId) throws MalformedOSDiModelException {
		final Set<String> superclasses = wrap.getClassesForIndividual(instanceId);
		if (superclasses.contains(OSDiWrapper.Clazz.AD_HOC_EXPRESSION.getShortName())) {
			exprToEvaluate = OSDiWrapper.DataProperty.HAS_EXPRESSION_VALUE.getValue(instanceId, "");
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
			if (dist == null)
				throw new MalformedOSDiModelException("Unsupported probability distribution " + instanceId);
			rnd = buildDistributionVariate(dist.getVariateName(), dist.getParameters(wrap, instanceId));
		}
	}
	
	/**
	 * Initializes a probability distribution from a string in the form [&ltOFFSET&gt + &ltSCALE&gt * ] &ltDISTRIBUTION_NAME&gt (&ltPARAM1&gt, [...])
	 * @param expression
	 */
	private static RandomVariate parseExpressionAsProbabilityDistribution(String expression) {
		String valueNormalized = expression.replace(" ", "");
		Matcher matcher = PATTERN.matcher(valueNormalized);
		try {
			if (matcher.find()) {	
				final String distributionName = matcher.group(DIST_NAME);
				final String[] parameters = new String[4];
				parameters[0] = matcher.group(DIST_PARAM1);
				parameters[1] = matcher.group(DIST_PARAM2);
				parameters[2] = matcher.group(DIST_PARAM3);
				parameters[3] = matcher.group(DIST_PARAM4);
				final RandomVariate rnd = buildDistributionVariate(distributionName, parameters);
				final String strScale = matcher.group(DIST_SCALE);
				final String strOffset = matcher.group(DIST_OFFSET);					
				if (strScale != null || strOffset != null) {
					final String strSign = matcher.group(DIST_SCALE_SIGN);
					final double sign = "-".equals(strSign) ? -1.0 : 1.0;  
					return RandomVariateFactory.getInstance("ScaledVariate", rnd, sign * 
								((strScale == null) ? 1.0 : Double.parseDouble(strScale)), (strOffset == null) ? 0.0 : Double.parseDouble(strOffset));
				}
				return rnd;
			}
			return null;
		}
		catch(IllegalArgumentException ex) {
			return null;
		}
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
