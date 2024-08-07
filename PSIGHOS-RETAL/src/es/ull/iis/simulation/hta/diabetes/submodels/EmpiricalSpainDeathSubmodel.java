/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.simulation.hta.retal.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * A death submodel based on the Spanish 2017 Mortality risk from the Instituto Nacional de Estad�stica (INE). The
 * parameters are adjusted using an empirical distribution based on survival data simulated from the mortality risks.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class EmpiricalSpainDeathSubmodel extends SecondOrderDeathSubmodel {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};
	/** The increased mortality risk associated to each chronic complication stage */
	private final TreeMap<DiabetesComplicationStage, Double> imrs;

	/** Survival for men and women, in inverse order (the first value is the survival at 100 years old). Survival is computed by
	 * iteratively applying the mortality risk from the INE table to a hypotethical population of 10000 individuals */
	private static double[][] INV_SURVIVAL = {
//		{180.5886298, 247.9029831, 334.1752564, 451.284885, 606.540786, 805.0895261, 1041.719246, 1324.501104, 
//			1639.465451, 1981.322166, 2355.794297, 2755.146762, 3163.844415, 3575.603855, 3977.671332, 4369.295139, 
//			4752.635127, 5114.885586, 5455.783642, 5784.453407, 6093.221276, 6381.096349, 6645.257812, 6896.789066, 
//			7121.077629, 7318.772377, 7521.175229, 7706.811639, 7876.14544, 8038.448729, 8185.574726, 8326.405339, 
//			8457.561376, 8579.997049, 8697.074657, 8804.342709, 8902.014295, 8994.651863, 9079.082141, 9157.842398, 
//			9230.131356, 9297.636789, 9360.32571, 9418.22388, 9470.380518, 9517.772731, 9562.058741, 9599.469848, 
//			9636.459435, 9668.863185, 9696.767545, 9721.327101, 9743.334848, 9764.212575, 9781.089694, 9795.681042, 
//			9808.089014, 9819.483023, 9829.861453, 9839.222408, 9848.75532, 9856.277171, 9864.183937, 9871.108531, 
//			9877.615054, 9883.441788, 9889.111473, 9894.649153, 9899.969511, 9904.226879, 9909.039721, 9913.925948, 
//			9918.091121, 9921.947463, 9925.953186, 9929.90779, 9933.686123, 9937.135944, 9940.372328, 9943.638746, 
//			9946.721133, 9949.445349, 9952.152314, 9954.42109, 9956.499633, 9957.559925, 9958.639944, 9959.52887, 
//			9960.605057, 9961.154614, 9961.696341, 9962.455094, 9963.220521, 9963.817119, 9964.545272, 9965.208912, 
//			9965.905751, 9967.06719, 9968.549861, 9970.951663, 10000},
//		{423.7085092, 583.585077, 787.1139177, 1053.859897, 1377.290458, 1746.647096, 2166.350113, 2629.870883, 
//			3120.783388, 3628.452575, 4157.851874, 4675.591221, 5164.964471, 5627.387659, 6052.279895, 6445.584963, 
//			6803.47253, 7122.834478, 7409.218957, 7663.126053, 7884.08704, 8083.868669, 8261.520142, 8418.111711, 
//			8550.225882, 8665.57719, 8778.883649, 8875.803811, 8961.972667, 9042.248909, 9110.787611, 9177.058042, 
//			9237.165754, 9292.152323, 9343.728329, 9391.376027, 9434.324084, 9478.932494, 9519.482033, 9555.523771, 
//			9589.667495, 9622.091961, 9651.47506, 9679.288195, 9705.937886, 9729.574328, 9750.330015, 9770.251414, 
//			9788.032873, 9805.468246, 9822.017267, 9835.815057, 9849.056534, 9860.231095, 9869.517988, 9878.513774, 
//			9886.535258, 9894.062465, 9900.025827, 9905.101748, 9910.209791, 9914.686685, 9919.391633, 9923.564057, 
//			9926.544958, 9929.999153, 9932.442893, 9935.103623, 9937.428256, 9939.456101, 9941.334771, 9943.116334, 
//			9944.942985, 9947.056494, 9948.650928, 9950.640319, 9952.333852, 9954.014806, 9955.388577, 9956.869449, 
//			9958.627503, 9960.031134, 9961.243615, 9962.43125, 9963.695206, 9964.727946, 9966.090422, 9966.904582, 
//			9967.412568, 9968.080336, 9968.782728, 9969.269052, 9970.169551, 9970.679322, 9971.541386, 9972.15983, 
//			9972.854337, 9973.472054, 9974.237409, 9976.293046, 10000}
//	}; 
	{145.788810904594, 211.219185625134, 292.94698106997, 398.563458827163, 540.186961426779, 711.939219058348, 916.732587579603, 1166.92488348363, 1434.24060060512, 1749.93183937888, 
		2090.35307383761, 2448.75224457732, 2826.51712780609, 3214.53606703618, 3606.7189680989, 3994.26735951236, 4373.48007493914, 4742.12232867963, 5102.50656212774, 5446.73080138293, 
		5772.0835032255, 6068.24552834854, 6350.61821911675, 6612.03883029477, 6859.61157762764, 7078.35116244525, 7293.05613840903, 7494.94624342919, 7680.0845150826, 7851.15922458584, 
		8006.49145626314, 8145.3241678032, 8282.17122208956, 8410.75094819144, 8527.55108093695, 8641.31656409149, 8741.7698746222, 8842.88392495927, 8933.90460821435, 9016.2360110274, 
		9096.40244871763, 9168.97754864375, 9237.67926029652, 9301.26783259724, 9362.6955794509, 9414.87810690355, 9465.78857417933, 9510.3271110343, 9554.31946450028, 9591.27104731617, 
		9626.94237616044, 9657.49630920091, 9685.34472706198, 9711.20982821501, 9733.3837621058, 9753.12126804052, 9770.42395380539, 9787.04852058106, 9800.91611113886, 9813.33155895769, 
		9824.88381257965, 9834.88630790557, 9843.29321938481, 9851.71523078425, 9858.86325611008, 9865.87085947202, 9871.79150080907, 9878.11944920878, 9883.1140739717, 9888.42311300546, 
		9893.86118385315, 9898.68135043489, 9903.297764475, 9907.84369473324, 9911.72482430295, 9915.94404564628, 9920.1733236468, 9923.91955420473, 9927.78142393953, 9932.83824080066, 
		9937.1656646071, 9940.96440803532, 9944.59002459061, 9947.05164678385, 9948.77205984779, 9950.18783242595, 9952.32463491417, 9953.40860004546, 9954.16678279166, 9954.8179577447, 
		9955.93339322789, 9957.0452959643, 9957.79713601333, 9958.64806805772, 9959.88543790608, 9960.74977050359, 9961.6385136596, 9963.03569340721, 9964.82147258848, 9967.582848989, 
		10000},
		{388.202680708036, 545.33857176819, 742.341870296193, 982.420608891018, 1282.96289467347, 1632.18048933741, 2022.63707757841, 2468.62846420138, 2917.03926420319, 3410.25880907242, 
		3895.06729156902, 4387.9451171834, 4869.54072287718, 5332.46847800405, 5769.51010301739, 6176.19567726856, 6561.45257141405, 6904.96616450313, 7210.60694894695, 7487.34089935424, 
		7733.41333027906, 7953.78043834144, 8145.88321233857, 8317.72680806935, 8464.71257012173, 8600.43943649874, 8719.23242708765, 8823.56267765463, 8917.12582408449, 9000.99227695687, 
		9075.22902096386, 9143.65290449814, 9207.47202937961, 9262.9644079618, 9311.16811801048, 9358.49868215777, 9399.06538253443, 9441.26828760976, 9478.81590971662, 9513.67565084986, 
		9548.2027924024, 9582.09514417466, 9610.81858150249, 9639.23185727807, 9666.63461870759, 9688.84554034605, 9710.34122562351, 9732.12863818065, 9753.31706522698, 9774.01438322892, 
		9790.16672637164, 9806.64926372192, 9820.74617596282, 9833.92274945523, 9845.19977617811, 9855.03487784949, 9864.94731671146, 9874.53868770762, 9882.58418282476, 9889.10983376889, 
		9895.13368384641, 9900.95021644819, 9906.1039606692, 9910.45065659675, 9914.60613625325, 9917.82285616906, 9921.10397504825, 9923.65926427081, 9926.4592639495, 9929.25307430603, 
		9931.85570899614, 9933.91675535452, 9935.84482286577, 9937.74467708557, 9939.56176219465, 9941.57402482284, 9943.08788425214, 9944.5099068599, 9946.14746102418, 9947.39525436378, 
		9948.85981216294, 9951.09582775799, 9952.57797789123, 9954.59537476538, 9955.70415372077, 9956.95947870834, 9958.25343098431, 9959.45217239923, 9960.25677354908, 9961.04002108308, 
		9961.8583918682, 9962.4453402991, 9962.97452182874, 9963.53266777561, 9964.50704403383, 9965.299176584, 9966.20519508153, 9967.25276251774, 9968.39064030574, 9971.129453895, 
		10000}
	};

	/**
	 * Creates a death submodel based on the Spanish 2016 Mortality risk
	 * @param rng A random number generator
	 * @param nPatients Number of simulated patients
	 */
	public EmpiricalSpainDeathSubmodel(SecondOrderParamsRepository secParams) {
		super(EnumSet.allOf(DiabetesType.class));
		imrs = new TreeMap<>();
		for (DiabetesComplicationStage stage : secParams.getRegisteredComplicationStages()) {
			imrs.put(stage, secParams.getIMR(stage));
		}
	}

	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
	}


	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledDeathInstance(this);
	}

	public class Instance extends DeathSubmodel {
		/** A random value [0, 1] for each patient (useful for common numbers techniques) */
		private final double[] rnd;
		
		public Instance(SecondOrderParamsRepository secParams) {
			super(EmpiricalSpainDeathSubmodel.this);
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
			rnd = new double[nPatients];
			for (int i = 0; i < nPatients; i++) {
				rnd[i] = rng.draw();
			}
		}
		
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
			final int sex = Sex.MAN.equals(pat.getSex()) ? 0 : 1;
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
			final int sex = Sex.MAN.equals(pat.getSex()) ? 0 : 1;
			final double time = Math.min(ModelParams.generateGompertz(ALPHA_DEATH[sex], BETA_DEATH[sex], pat.getAge(), rnd[pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - pat.getAge());
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
		}
	}
}
