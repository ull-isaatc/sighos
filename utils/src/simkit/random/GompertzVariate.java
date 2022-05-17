/**
 * 
 */
package simkit.random;

/**
 * @author Iván Castilla
 *
 */
public class GompertzVariate extends RandomVariateBase {
	private double alpha = 1;
	private double beta = 1;
	private double age = 1;
	/** Precomputation of Beta / Alpha to accelerate generation of values */
	private double beta_alpha;
	/** Precomputation of exp(-Beta*Age) to accelerate generation of values */
	private double exp_minusBeta_Age;
	
	/**
	 * 
	 */
	public GompertzVariate() {
	}

	@Override
	public double generate() {
		final double r = rng.draw();

		return Math.log(1-beta_alpha*Math.log(1-r)*exp_minusBeta_Age)/beta;
	}

	@Override
	public Object[] getParameters() {
		return new Object[] {alpha, beta, age};
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length != 3) {
            throw new IllegalArgumentException("Should be three parameters for Gompertz: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Number) || !(params[1] instanceof Number) || !(params[2] instanceof Number)) {
            throw new IllegalArgumentException("Parameters must be a Number");
        }
        if ((((Number)params[0]).doubleValue() <= 0.0) || (((Number)params[1]).doubleValue() <= 0.0)) {
            throw new IllegalArgumentException("Parameters alpha and beta must be higher than 0");
        }
        if (((Number)params[2]).doubleValue() < 0.0) {
            throw new IllegalArgumentException("Parameter age must be higher or equal to 0");
        }
        else {
            setAlpha(((Number) params[0]).doubleValue());
            setBeta(((Number) params[1]).doubleValue());
            setAge(((Number) params[2]).doubleValue());
        }
		
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alfa the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
		this.beta_alpha = beta / alpha;
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
		this.beta_alpha = beta / alpha;
		this.exp_minusBeta_Age = Math.exp(-beta * age);
	}

	/**
	 * @return the age
	 */
	public double getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(double age) {
		this.age = age;
		this.exp_minusBeta_Age = Math.exp(-beta * age);
	}

	// FIXME: Analizar si debe ir aquí y revisar implementación
	public static double generateGompertz(double alpha, double beta, double currentAge, double initProb) {
		return Math.log(1-(beta/alpha)*Math.log(1-initProb)*Math.exp(-beta*currentAge))/beta;
	}

}
