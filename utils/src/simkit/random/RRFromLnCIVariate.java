/**
 * 
 */
package simkit.random;

/**
 * A random variate to generate numbers from relative risks. Generally, relative risks are normal in the log space. 
 * This class receives mean and 95% confidence intervals (expressed in natural units, converts them to the log space, and parameterized 
 * a normal variate with the ln mean and sd. 
 * @author Iván Castilla Rodríguez
 *
 */
public class RRFromLnCIVariate extends NormalVariate {
	private int n;
	private double[] lnCIs = new double[2];
	/**
	 * 
	 */
	public RRFromLnCIVariate() {
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	@Override
	public double generate() {
		return Math.exp(super.generate());
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	@Override
	public Object[] getParameters() {
		return new Object[] {Math.exp(getMean()), Math.exp(lnCIs[0]), Math.exp(lnCIs[1]), n};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
       if (params.length != 4) {
            throw new IllegalArgumentException("Need (mean, lower 95%CI, upper 95%CI, n), received "
                    + params.length + " parameters");
        } else {
        	final double lnMean = Math.log(((Number) params[0]).doubleValue());
        	lnCIs[0] = Math.log(((Number) params[1]).doubleValue());
        	lnCIs[1] = Math.log(((Number) params[2]).doubleValue());
        	n = ((Number) params[3]).intValue();
        	final double sd = Math.sqrt(n) * (lnCIs[1] - lnCIs[0]) / 3.92;
        	setMean(lnMean);
            setStandardDeviation(sd);
        }
    }

	@Override
	public String toString() {
		return "RRFromLnCI (" + getMean() + ", " + getStandardDeviation() + ")";
	}
//	public static void main(String[] args) {
//		NormalVariate original = new NormalVariate();
//		original.setParameters(Math.log(1.51), (Math.log(2.28) - Math.log(1)) / 3.92);
//		ExponentialTransform exp = new ExponentialTransform();
//		exp.setParameters(original);
//		NormalVariate rnd = new RRFromLnCIVariate();
//		rnd.setParameters(1.51, 1.00, 2.28, 1);
//		for (int i = 0; i < 1000; i++)
//			System.out.println(rnd.generate() + "\t" + exp.generate());
//	}
}
