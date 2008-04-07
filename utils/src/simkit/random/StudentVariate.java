/**
 * 
 */
package simkit.random;

/**
 * @author Iván
 *
 */
public class StudentVariate extends RandomVariateBase {
	private int degrees;
	
	public StudentVariate() {
		
	}
	
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		double v, z, r, theta;
		v = 0;
		for (int i = 1; i <= degrees; i++){
			r = Math.sqrt(-2 * Math.log(Math.random()));
			theta = 2 * Math.PI * Math.random();
			z = r * Math.cos(theta);
			v = v + z * z;
		}
		r = Math.sqrt(-2 * Math.log(Math.random()));
		theta = 2 * Math.PI * Math.random();
		z = r * Math.cos(theta);
		return z / Math.sqrt(v / degrees);
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] {degrees};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
		if (params.length != 1) {
            throw new IllegalArgumentException("Should be two parameters for Student: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Parameters must be a Number");
        }
        else {
            setDegrees(((Integer) params[0]).intValue());
        }
		
	}

	/**
	 * @return the degrees
	 */
	public int getDegrees() {
		return degrees;
	}

	/**
	 * @param degrees the degrees to set
	 */
	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}

}
