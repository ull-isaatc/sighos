/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CVDCanadaDeathParam extends UniqueEventParam<Long> {
	final static double[] UPPER_AGE = {60, 70, 80, BasicConfigParams.MAX_AGE}; 

	final static double[][] PROBS_PER_YEAR = {
	{0.14, 0.245, 0.2, 0.44}, 
	{0.205, 0.21, 0.25, 0.425},
	{0.019, 0.145, 0.175, 0.305}, 
	{0.034, 0.06, 0.19, 0.31},
	{0.185, 0.125, 0.17, 0.145}, 
	{0.125, 0.13, 0.32, 0.275},
	{0.125, 0.14, 0.135, 0.515}, 
	{0.075, 0.065, 0.13, 0.61},
	{0.107, 0.075, 0.265, 0.57}, 
	{0.16, 0.135, 0.245, 0.57}
	};
	
	private final double[] rr;
	public CVDCanadaDeathParam(int nPatients, double[] rr) {
		super(nPatients);
		this.rr = rr;
	}

	@Override
	public Long getValue(T1DMPatient pat) {
		final double age = pat.getAge();
		int interval = 0;
		if (age > 80)
			interval = 3;
		else if (age > 70)
			interval = 2;
		else if (age > 60)
			interval = 1;
		final int yearsFromCHD = Math.min(9, (int)((pat.getTs() - pat.getTimeToComplication(Complication.CHD)) / BasicConfigParams.YEAR_CONVERSION));
		return CommonParams.getAnnualBasedTimeToEvent(pat, -1 / PROBS_PER_YEAR[yearsFromCHD][interval], draw(pat), rr);
	}

}
