/**
 * 
 */
package simkit.random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LogisticVariate extends RandomVariateBase {
	private double location;
	private double scale;
	
	public LogisticVariate() {
		
	}
	
	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		double r = rng.draw();
		return location + scale * Math.log(r / (1 - r));
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] {location, scale};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
		if (params.length != 2) {
            throw new IllegalArgumentException("Should be two parameters for Logistic: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Number) || !(params[1] instanceof Number)) {
            throw new IllegalArgumentException("Parameters must be a Number");
        }
        else {
            setLocation(((Number) params[0]).doubleValue());
            setScale(((Number) params[1]).doubleValue());
        }

	}

	/**
	 * @return the location
	 */
	public double getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(double location) {
		this.location = location;
	}

	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

}
