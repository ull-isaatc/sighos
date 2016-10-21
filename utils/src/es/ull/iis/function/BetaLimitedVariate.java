package es.ull.iis.function;

import es.ull.iis.function.TimeFunction;
import simkit.random.BetaVariate;

public class BetaLimitedVariate extends TimeFunction {
	
	private BetaVariate rnd;

	private double lowerLimit;
	private double upperLimit;
	
	/**
	 * 
	 */
	public BetaLimitedVariate(BetaVariate rnd, double lowerLimit, double upperLimit ) {
		super();
		this.rnd = rnd;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}
	
	public BetaLimitedVariate() {
		super();
	}
	
	@Override
	public double getValue(TimeFunctionParams params) {
		return rnd.generate()*(upperLimit - lowerLimit) + lowerLimit;
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length < 4) 
			throw new IllegalArgumentException("Need (alpha, beta, lowerLimit, upperLimit), received " +
		            params.length + " parameters");
		else {
			BetaVariate betaVar = new BetaVariate();
			betaVar.setParameters((Double) params[0], (Double) params[1]);
			setBeta(betaVar);
			setLowerLimit((Double)params[2]);
			setUpperLimit((Double)params[3]);
		}
	}

	public BetaVariate getBeta() {
		return rnd;
	}

	public void setBeta(BetaVariate rnd) {
		this.rnd = rnd;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}
}
