package es.ull.iis.simulation.hta.osdi.ontology;

/**
 * @author Iván Castilla
 * TODO: Process parameters when expressed in different ways. E.g. gamma parameters may be average and standard deviation
 */
public enum OSDiProbabilityDistributionExpressions {
	NORMAL(OSDiClasses.NORMAL_DISTRIBUTION_EXPRESSION, "NormalVariate", 2),
	UNIFORM(OSDiClasses.UNIFORM_DISTRIBUTION_EXPRESSION, "UniformVariate", 2) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_LOWER_LIMIT_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_UPPER_LIMIT_PARAMETER.getValue(instanceId, "0")};				
		}
	},
	BETA(OSDiClasses.BETA_DISTRIBUTION_EXPRESSION, "BetaVariate", 2) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_BETA_PARAMETER.getValue(instanceId, "0")};				
		}
	},
	GAMMA(OSDiClasses.GAMMA_DISTRIBUTION_EXPRESSION, "GammaVariate", 2) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_ALFA_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
		}
	},
	EXPONENTIAL(OSDiClasses.EXPONENTIAL_DISTRIBUTION_EXPRESSION, "ExponentialVariate", 1) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
		}
	},
	POISSON(OSDiClasses.POISSON_DISTRIBUTION_EXPRESSION, "PoissonVariate", 1) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_LAMBDA_PARAMETER.getValue(instanceId, "0")};				
		}
	},
	BERNOULLI(OSDiClasses.BERNOULLI_DISTRIBUTION_EXPRESSION, "BernoulliVariate", 1) {
		public String[] getParameters(String instanceId) {
			return new String[] {OSDiDataProperties.HAS_PROBABILITY_PARAMETER.getValue(instanceId, "0")};				
		}			
	};
	private final OSDiClasses clazz;
	private final String variateName;
	private final int nParameters;
	
	private OSDiProbabilityDistributionExpressions(OSDiClasses clazz, String variateName, int nParameters) {
		this.clazz = clazz;
		this.variateName = variateName;
		this.nParameters = nParameters;
	}
	
	public String[] getParameters(String instanceId) {
		return new String[] {OSDiDataProperties.HAS_AVERAGE_PARAMETER.getValue(instanceId, "0"), OSDiDataProperties.HAS_STANDARD_DEVIATION_PARAMETER.getValue(instanceId, "0")};
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
		return nParameters;
	}
	

}