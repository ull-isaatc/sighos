package es.ull.iis.simulation.hta.osdi.ontology;

/**
 * @author Iván Castilla
 * TODO: Process parameters when expressed in different ways. E.g. gamma parameters may be average and standard deviation
 */
public enum OSDiProbabilityDistributionExpressions {
	NORMAL(OSDiClasses.NORMAL_DISTRIBUTION_EXPRESSION, "NormalVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_AVERAGE_PARAMETER, OSDiDataProperties.HAS_STANDARD_DEVIATION_PARAMETER}),
	UNIFORM(OSDiClasses.UNIFORM_DISTRIBUTION_EXPRESSION, "UniformVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_LOWER_LIMIT_PARAMETER, OSDiDataProperties.HAS_UPPER_LIMIT_PARAMETER}),
	BETA(OSDiClasses.BETA_DISTRIBUTION_EXPRESSION, "BetaVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_ALFA_PARAMETER, OSDiDataProperties.HAS_BETA_PARAMETER}),
	GAMMA(OSDiClasses.GAMMA_DISTRIBUTION_EXPRESSION, "GammaVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_ALFA_PARAMETER, OSDiDataProperties.HAS_LAMBDA_PARAMETER}),
	EXPONENTIAL(OSDiClasses.EXPONENTIAL_DISTRIBUTION_EXPRESSION, "ExponentialVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER}),
	POISSON(OSDiClasses.POISSON_DISTRIBUTION_EXPRESSION, "PoissonVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER}),
	BERNOULLI(OSDiClasses.BERNOULLI_DISTRIBUTION_EXPRESSION, "BernoulliVariate", new OSDiDataProperties[] {OSDiDataProperties.HAS_PROBABILITY_PARAMETER});
	private final OSDiClasses clazz;
	private final String variateName;
	private final OSDiDataProperties[] parameters;
	
	private OSDiProbabilityDistributionExpressions(OSDiClasses clazz, String variateName, OSDiDataProperties[] parameters) {
		this.clazz = clazz;
		this.variateName = variateName;
		this.parameters = parameters;
	}
	
	public String[] getParameters(String instanceId) {
		String [] result = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			result[i] = parameters[i].getValue(instanceId, "0");
		return result;
	}	
	
	/**
	 * @return the variateName
	 */
	public String getVariateName() {
		return variateName;
	}

	/**
	 * @return the clazz
	 */
	public OSDiClasses getClazz() {
		return clazz;
	}

	/**
	 * @return the nParameters
	 */
	public int getnParameters() {
		return parameters.length;
	}
	
	public void add(String instanceId, double[] parameterValues) {
		if (parameters.length != parameterValues.length)
			throw new IllegalArgumentException("Creating a " + name() + " probability distribution requires " + parameters.length + " parameters. Passed " + parameterValues.length);

		clazz.add(instanceId);
		for (int i = 0; i < parameterValues.length; i++) {
			this.parameters[i].add(instanceId, Double.toString(parameterValues[i]));
		}
	}

}