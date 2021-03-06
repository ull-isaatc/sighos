/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import es.ull.iis.simulation.hta.retal.params.ScreeningParam;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class RETALMain {
	final private static int NSIM = 1;
	
	public static void main(String[] args) {
		final boolean baseCase = true;
		final Screening interv1 = new Screening(new SimulationPeriodicCycle(TimeUnit.YEAR, (long)(ScreeningParam.START_YEAR * 365), 
				new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", ScreeningParam.PERIODICITY_YEARS*365), 0), new ScreeningParam());
		for (int i = 0; i < NSIM; i++) {
			final RETALSimulation simul = new RETALSimulation(i, new NullIntervention());
			simul.run();
			// TODO: Check that reset works fine!!!
			RandomForPatient.reset();
			final RETALSimulation simul2 = new RETALSimulation(simul, interv1);
			simul2.run();
		}

//		final Random RNG = new Random(); 
//		for (int i = 0; i < 10000; i++)
//			System.out.println("" + i + "\t" + armdParams.getEARMTime(CommonParams.INIT_AGE) + "\t" + commonParams.getDeathTime(CommonParams.INIT_AGE, (RNG.nextDouble() < 0.5) ? 0 : 1));
		
//		System.out.println("EMPIRICAL");
//		testGetTimeToEvent(100000, P_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
//		System.out.println("GOMPERTZ");
//		testGompertz(100000, P_ARM2AMD_FE_NOARM, ALPHA_ARM2AMD_FE_NOARM, BETA_ARM2AMD_FE_NOARM, RNG_ARM2AMD_FE_NOARM);
	}
}
