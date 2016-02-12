/**
 * 
 */
package es.ull.iis.simulation.retal;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class RETALMain {
//	public final static TimeStamp GENSTART = TimeStamp.getZero();
//	public final static TimeStamp GENPERIOD = TimeStamp.getDay();

//	private final static SimulationTimeFunction deathTime = new SimulationTimeFunction(SIMUNIT, "ConstantVariate", new TimeStamp(TimeUnit.YEAR, 40));
	
	public static void main(String[] args) {
		final CommonParams commonParams = new CommonParams(true);
		final ARMDParams armdParams = new ARMDParams(true);
		final RETALSimulation simul = new RETALSimulation(0, commonParams, armdParams);
		simul.run();
//		System.out.println("EMPIRICAL");
//		testGetTimeToEvent(100000, P_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
//		System.out.println("GOMPERTZ");
//		testGompertz(100000, P_ARM2AMD_FE_NOARM, ALPHA_ARM2AMD_FE_NOARM, BETA_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
	}
}
