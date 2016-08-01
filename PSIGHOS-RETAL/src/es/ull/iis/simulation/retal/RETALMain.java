/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.retal.params.ARMDParams;
import es.ull.iis.simulation.retal.params.CommonParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RETALMain {
//	public final static TimeStamp GENSTART = TimeStamp.getZero();
//	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	public final static int NPATIENTS = 10;

//	private final static SimulationTimeFunction deathTime = new SimulationTimeFunction(SIMUNIT, "ConstantVariate", new TimeStamp(TimeUnit.YEAR, 40));
	
	public static void main(String[] args) {
		final CommonParams commonParams = new CommonParams(true);
		final ARMDParams armdParams = new ARMDParams(true);
		final RETALSimulation simul = new RETALSimulation(0, commonParams, armdParams, NPATIENTS);
		simul.run();

//		final Random RNG = new Random(); 
//		for (int i = 0; i < 10000; i++)
//			System.out.println("" + i + "\t" + armdParams.getEARMTime(CommonParams.INIT_AGE) + "\t" + commonParams.getDeathTime(CommonParams.INIT_AGE, (RNG.nextDouble() < 0.5) ? 0 : 1));
		
//		System.out.println("EMPIRICAL");
//		testGetTimeToEvent(100000, P_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
//		System.out.println("GOMPERTZ");
//		testGompertz(100000, P_ARM2AMD_FE_NOARM, ALPHA_ARM2AMD_FE_NOARM, BETA_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
	}
}
