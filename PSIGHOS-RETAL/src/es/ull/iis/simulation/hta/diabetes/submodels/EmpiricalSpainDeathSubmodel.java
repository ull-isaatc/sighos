/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Arrays;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * A death submodel based on the Spanish 2017 Mortality risk from the Instituto Nacional de Estadística (INE). The
 * parameters are adjusted using an empirical distribution based on survival data simulated from the mortality risks.
 * @author Iván Castilla Rodríguez
 *
 */
public class EmpiricalSpainDeathSubmodel extends DeathSubmodel {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};
	/** A random value [0, 1] for each patient (useful for common numbers techniques) */
	private final double[] rnd;
	/** The increased mortality risk associated to each chronic complication stage */
	private final TreeMap<DiabetesComplicationStage, Double> imrs;

	/** Survival for men and women, in inverse order (the first value is the survival at 100 years old). Survival is computed by
	 * iteratively applying the mortality risk from the INE table to a hypotethical population of 10000 individuals */
	private static double[][] INV_SURVIVAL = {
		{180.5886298, 247.9029831, 334.1752564, 451.284885, 606.540786, 805.0895261, 1041.719246, 1324.501104, 
			1639.465451, 1981.322166, 2355.794297, 2755.146762, 3163.844415, 3575.603855, 3977.671332, 4369.295139, 
			4752.635127, 5114.885586, 5455.783642, 5784.453407, 6093.221276, 6381.096349, 6645.257812, 6896.789066, 
			7121.077629, 7318.772377, 7521.175229, 7706.811639, 7876.14544, 8038.448729, 8185.574726, 8326.405339, 
			8457.561376, 8579.997049, 8697.074657, 8804.342709, 8902.014295, 8994.651863, 9079.082141, 9157.842398, 
			9230.131356, 9297.636789, 9360.32571, 9418.22388, 9470.380518, 9517.772731, 9562.058741, 9599.469848, 
			9636.459435, 9668.863185, 9696.767545, 9721.327101, 9743.334848, 9764.212575, 9781.089694, 9795.681042, 
			9808.089014, 9819.483023, 9829.861453, 9839.222408, 9848.75532, 9856.277171, 9864.183937, 9871.108531, 
			9877.615054, 9883.441788, 9889.111473, 9894.649153, 9899.969511, 9904.226879, 9909.039721, 9913.925948, 
			9918.091121, 9921.947463, 9925.953186, 9929.90779, 9933.686123, 9937.135944, 9940.372328, 9943.638746, 
			9946.721133, 9949.445349, 9952.152314, 9954.42109, 9956.499633, 9957.559925, 9958.639944, 9959.52887, 
			9960.605057, 9961.154614, 9961.696341, 9962.455094, 9963.220521, 9963.817119, 9964.545272, 9965.208912, 
			9965.905751, 9967.06719, 9968.549861, 9970.951663, 10000},
		{423.7085092, 583.585077, 787.1139177, 1053.859897, 1377.290458, 1746.647096, 2166.350113, 2629.870883, 
			3120.783388, 3628.452575, 4157.851874, 4675.591221, 5164.964471, 5627.387659, 6052.279895, 6445.584963, 
			6803.47253, 7122.834478, 7409.218957, 7663.126053, 7884.08704, 8083.868669, 8261.520142, 8418.111711, 
			8550.225882, 8665.57719, 8778.883649, 8875.803811, 8961.972667, 9042.248909, 9110.787611, 9177.058042, 
			9237.165754, 9292.152323, 9343.728329, 9391.376027, 9434.324084, 9478.932494, 9519.482033, 9555.523771, 
			9589.667495, 9622.091961, 9651.47506, 9679.288195, 9705.937886, 9729.574328, 9750.330015, 9770.251414, 
			9788.032873, 9805.468246, 9822.017267, 9835.815057, 9849.056534, 9860.231095, 9869.517988, 9878.513774, 
			9886.535258, 9894.062465, 9900.025827, 9905.101748, 9910.209791, 9914.686685, 9919.391633, 9923.564057, 
			9926.544958, 9929.999153, 9932.442893, 9935.103623, 9937.428256, 9939.456101, 9941.334771, 9943.116334, 
			9944.942985, 9947.056494, 9948.650928, 9950.640319, 9952.333852, 9954.014806, 9955.388577, 9956.869449, 
			9958.627503, 9960.031134, 9961.243615, 9962.43125, 9963.695206, 9964.727946, 9966.090422, 9966.904582, 
			9967.412568, 9968.080336, 9968.782728, 9969.269052, 9970.169551, 9970.679322, 9971.541386, 9972.15983, 
			9972.854337, 9973.472054, 9974.237409, 9976.293046, 10000}
	}; 

	/**
	 * Creates a death submodel based on the Spanish 2016 Mortality risk
	 * @param rng A random number generator
	 * @param nPatients Number of simulated patients
	 */
	public EmpiricalSpainDeathSubmodel(RandomNumber rng, int nPatients) {
		super();
		rnd = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rnd[i] = rng.draw();
		}
		imrs = new TreeMap<>();
	}

	public void addIMR(DiabetesComplicationStage state, double imr) {
		imrs.put(state, imr);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables and increased according to the 
	 * state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public long getTimeToDeath(DiabetesPatient pat) {
		double imr = 1.0;
		for (final DiabetesComplicationStage state : pat.getDetailedState()) {
			if (imrs.containsKey(state)) {
				final double newIMR = imrs.get(state);
				if (newIMR > imr) {
					imr = newIMR;
				}
			}
		}
		final double age = pat.getAge();
		final int sex = pat.getSex();
		final double rnd = this.rnd[pat.getIdentifier()];
		
		final double reference = INV_SURVIVAL[sex][100 - (int)age];
		final double index = rnd * reference / imr;
		final int ageToDeath = 101 - Math.abs(Arrays.binarySearch(INV_SURVIVAL[sex], index));
		final double time = Math.min((ageToDeath > age) ? ageToDeath - age + rnd : rnd, BasicConfigParams.DEF_MAX_AGE - age);

		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}
	
	public long legacyGetTimeToDeath(DiabetesPatient pat) {
		double imr = 1.0;
		for (final DiabetesComplicationStage state : pat.getDetailedState()) {
			if (imrs.containsKey(state)) {
				final double newIMR = imrs.get(state);
				if (newIMR > imr) {
					imr = newIMR;
				}
			}
		}
		final double time = Math.min(ModelParams.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - pat.getAge());
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}

}
