/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.params.ScreeningParam;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class RETALMain {
//	public final static TimeStamp GENSTART = TimeStamp.getZero();
//	public final static TimeStamp GENPERIOD = TimeStamp.getDay();

//	private final static SimulationTimeFunction deathTime = new SimulationTimeFunction(SIMUNIT, "ConstantVariate", new TimeStamp(TimeUnit.YEAR, 40));
	
	public static void main(String[] args) {
		final boolean baseCase = true;
		final RETALSimulation simul = new RETALSimulation(0, baseCase, new NullIntervention());
		simul.run();
		// TODO: Check that reset works fine!!!
		RandomForPatient.reset();
		Screening interv1 = new Screening(new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, 
				new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 730), 2), new ScreeningParam(baseCase));
		final RETALSimulation simul2 = new RETALSimulation(simul, interv1);
		simul2.run();

//		final Random RNG = new Random(); 
//		for (int i = 0; i < 10000; i++)
//			System.out.println("" + i + "\t" + armdParams.getEARMTime(CommonParams.INIT_AGE) + "\t" + commonParams.getDeathTime(CommonParams.INIT_AGE, (RNG.nextDouble() < 0.5) ? 0 : 1));
		
//		System.out.println("EMPIRICAL");
//		testGetTimeToEvent(100000, P_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
//		System.out.println("GOMPERTZ");
//		testGompertz(100000, P_ARM2AMD_FE_NOARM, ALPHA_ARM2AMD_FE_NOARM, BETA_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
	}
}