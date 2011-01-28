/**
 * 
 */
package es.ull.isaatc.rli;

import java.util.ArrayList;

import es.ull.isaatc.function.ConstantFunction;
import es.ull.isaatc.function.PeriodicProportionFunction;
import es.ull.isaatc.function.SimulationUniformlyDistributedSplitFunction;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.TransitionActivity;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.InfoReceiver;

/**
 * @author Iván
 *
 */
public class RLIIP7GSimulation extends Simulation {
	static final int NDAYS = 364 * 2;
	
	public enum AdmissionMethod {
		AE ("A&E(Emergencies)", 
				new double[] {0.374,0.158,0.136,0.198,0.054,0.036,0.01,0.013,0.002,0.002,0.011,0.001,0.002,0.003,0,0},
				new int[] {153,143,140,171,157,175,153,139,153,175,182,144,133,159,125,134,158,151,142,152,133,145,145,150,153,115,188,140,121,119,123,116,144,140,140,123,135,155,175,158,145,153,142,202,181,139,132,153,160,146,178,153},
				new double[] {0.150395106,0.141345909,0.138159572,0.132041805,0.143385164,0.144404792,0.150267652}), 
		GP ("GP_Request(Emergencies)", 
				new double [] {0.314,0.23,0.19,0.05,0.066,0.068,0.025,0.014,0.019,0.009,0,0.006,0.002,0.001,0.004,0.002},
				new int[] {128,161,155,157,195,168,144,157,162,167,171,184,173,172,155,123,140,128,172,144,162,153,133,150,145,140,124,147,147,158,161,182,165,177,179,195,178,171,143,168,132,147,164,152,167,152,158,174,173,134,112,128},
				new double[] {0.165473735,0.161712969,0.162926119,0.15358486,0.166929516,0.094625743,0.094747058}), 
//		OR ("Ordinary(Electives)", 
//				new double [] {0.017,0.258,0.011,0.184,0,0.147,0.11,0.126,0.032,0.01,0.003,0.014,0.015,0.02,0,0.053},
//				new int[] {75,57,87,93,85,88,83,80,67,86,84,86,73,78,77,76,85,75,57,57,44,68,90,79,72,75,91,88,76,49,86,68,81,60,65,68,66,52,33,47,46,55,50,79,68,77,76,78,87,85,82,75},
//				new double[] {0.16174921,0.238145416,0.181506849,0.223656481,0.172550053,0.012908325,0.009483667});
				OR ("Ordinary(Electives)", 
						new double [] {0.017,0.258,0.011,0.184,0,0.147,0.11,0.126,0.032,0.01,0.003,0.014,0.015,0.02,0,0.053},
						new int[] {23,14,17,11,1,0,0,7,15,19,22,9,0,10,17,19,21,20,4,1,5,17,21,21,20,9,1,0,7,
						18,22,18,7,0,9,40,16,9,13,24,0,10,9,12,26,15,7,1,11,23,16,16,17,6,0,1,13,9,
						21,11,6,0,7,22,13,17,17,12,1,2,28,24,12,16,20,1,5,25,16,18,16,7,1,6,21,15,
						20,16,7,0,9,23,9,19,21,12,0,10,18,20,15,10,6,1,11,21,17,20,13,11,1,6,21,20,
						23,15,11,0,3,25,10,9,13,8,0,6,25,11,15,16,4,0,3,23,25,17,15,6,0,8,16,11,17,
						7,3,0,0,6,21,28,14,13,1,6,24,12,19,16,11,0,6,20,16,17,20,9,0,8,16,13,22,16,
						7,1,10,21,14,21,15,13,0,8,28,14,12,5,3,0,9,17,13,19,35,10,0,8,19,18,27,26,
						14,0,10,14,21,15,18,11,0,9,24,19,27,21,10,0,10,18,11,19,17,7,0,8,24,16,24,
						19,7,1,8,18,14,21,19,16,0,1,25,13,22,19,9,0,8,21,11,17,23,9,1,6,18,18,15,16,
						10,0,7,22,12,16,8,0,0,0,2,5,9,7,3,0,0,6,16,21,18,9,0,4,28,17,25,17,8,0,9,24,
						15,14,16,11,0,5,19,15,15,15,14,2,10,20,9,19,11,7,0,7,18,19,22,16,10,1,7,18,
						9,17,19,16,1,7,18,13,16,20,10,0,15,24,5,17,19,6,1,7,13,13,19,15,5,2,8,17,15,
						15,14,2,0,16,21,15,21,2,0,0,0,5,10,7,3,0,0,0},
						new double[] {1.0});
		
		private final String name;
		private final double [] prob;
		private final int []patientsWeek;
		private final double []patientsDaily;
		private AdmissionMethod(String name, double []prob, int []patientsWeek, double []patientsDaily) {
			this.name = name;
			this.prob = prob;
			this.patientsWeek = patientsWeek;
			this.patientsDaily = patientsDaily;
		}
		public String getName() { return name; }
		public double[] getProb() { return prob; }
		public int[] getPatientsWeek() { return patientsWeek; }
		public double[] getPatientsDaily() { return patientsDaily; }
	}
	
	public enum Specialty {
		$300 ("GENERAL MEDICINE", 
				new double[][] {{0.069230769,0.002325581,0.079785331,0.000536673,0.001252236,0.000536673,0.846332737,0},
				{0.090257413,0.052460085,0.010752688,0.007494298,0.068752036,0.020853698,0.00325839,0.746171391},
				{0.033950617,0.086419753,0.021604938,0.00308642,0.037037037,0.015432099,0,0.802469136},
				{0.357267951,0.024518389,0.015761821,0,0.007005254,0,0.031523643,0.563922942},
				{0.135135135,0.054054054,0,0.135135135,0.027027027,0,0,0.648648649},
				{0.042918455,0.017167382,0.008583691,0,0.087982833,0,0.008583691,0.834763948},
				{0.010309278,0.030927835,0,0,0.010309278,0.041237113,0,0.907216495},
				{0.525069341,0.024962663,0.018775336,0.002560273,0.052485598,0.005547258,0.012374653,0.358224877}}, 
				new double[][] {{0.794871795,0.025641026,0.051282051,0.025641026,0.076923077,0,0.025641026,0},
				{0.237288136,0.016949153,0.016949153,0.016949153,0,0,0,0.711864407},
				{0,0.25,0,0,0,0,0,0.75},
				{0.333333333,0,0,0,0,0,0,0.666666667},
				{0.5,0,0,0,0,0,0,0.5},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0.5,0.25,0,0,0,0,0,0.25}}),
		$100 ("GENERAL SURGERY",
				new double[][] {{0.000312695,0.026579112,0,0.01438399,0,0.007817386,0.950906817,0},
				{0.058823529,0.029411765,0,0,0.058823529,0.029411765,0,0.823529412},
				{0.002967359,0.175667656,0.032047478,0.005341246,0.017210682,0.018397626,0.004747774,0.743620178},
				{0.02970297,0.712871287,0,0,0,0,0.01980198,0.237623762},
				{0.002427184,0.199029126,0.002427184,0.055825243,0.002427184,0.002427184,0.046116505,0.689320388},
				{0,0.128205128,0,0,0.051282051,0,0.025641026,0.794871795},
				{0,0.035087719,0,0,0,0.087719298,0.00877193,0.868421053},
				{0.008447044,0.450292398,0.015919428,0.112735543,0.002923977,0.01754386,0.010071475,0.382066277}}, 
				new double[][] {{0,0.899441341,0,0.037243948,0,0.054003724,0.009310987,0},
				{0,0.222222222,0,0,0,0,0,0.777777778},
				{0.002764977,0.08202765,0.070967742,0.002764977,0.003686636,0.007373272,0,0.830414747},
				{0.064935065,0.909090909,0,0,0,0,0,0.025974026},
				{0,0.166666667,0,0.104166667,0,0,0,0.729166667},
				{0,0.333333333,0,0,0,0,0,0.666666667},
				{0,0.028985507,0,0,0,0.057971014,0,0.913043478},
				{0,0.545454545,0,0.090909091,0,0,0,0.363636364}}),
		$420 ("PAEDIATRICS",
				new double[][] {{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0}},
				new double[][] {{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0.025641026,0,0.974358974},
				{0,0,0,0,0,0,0,0}}),
		$110 ("TRAUMA & ORTHOPAEDICS",
				new double[][] {{0,0.860841424,0,0.003236246,0,0.122052705,0.013869626,0},
				{0,0.111111111,0,0,0.111111111,0,0,0.777777778},
				{0.004206099,0.086225026,0.009989485,0.001577287,0.067823344,0.006834911,0.001051525,0.822292324},
				{0,0.75,0.05,0,0,0,0,0.2},
				{0,0.357142857,0,0.071428571,0,0,0,0.571428571},
				{0,0.05,0,0,0.058333333,0,0.008333333,0.883333333},
				{0,0.007272727,0,0,0,0,0,0.992727273},
				{0.029411765,0.676470588,0.029411765,0.088235294,0,0,0.029411765,0.147058824}},
				new double[][] {{0,0.933250927,0,0.002472188,0,0.064276885,0,0},
				{0,0.666666667,0,0,0.333333333,0,0,0},
				{0.003942181,0.014454665,0.011826544,0.00131406,0.002628121,0,0,0.965834428},
				{0,1,0,0,0,0,0,0},
				{0,0.666666667,0,0,0,0,0,0.333333333},
				{0,0,0,0,0.25,0,0,0.75},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0}}),
		$430 ("GERIATRIC MEDICINE",
				new double[][] {{0.023662551,0.004115226,0.018518519,0,0.051440329,0,0.902263374,0},
				{0.088095238,0.021428571,0,0.004761905,0.404761905,0.019047619,0.002380952,0.45952381},
				{0.051282051,0.051282051,0.025641026,0,0.384615385,0.051282051,0,0.435897436},
				{0.541666667,0.041666667,0,0,0,0,0.083333333,0.333333333},
				{0,0.3,0,0,0.1,0,0,0.6},
				{0.021276596,0.010638298,0.00177305,0.00177305,0.136524823,0.00177305,0.005319149,0.820921986},
				{0.083333333,0,0,0,0.083333333,0.083333333,0,0.75},
				{0.442285714,0.019428571,0.004571429,0.008,0.426285714,0.001142857,0.005714286,0.092571429}},
				new double[][] {{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}}),
		$502 ("GYNAECOLOGY",
				new double[][] {{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0.666666667,0,0,0,0.333333333,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0.001048218,0.002096436,0.003144654,0,0,0.192872117,0,0.800838574},
				{0,0,0,0,0,0,0,0}},
				new double[][] {{0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1},
				{0,0.25,0.25,0,0,0.25,0.25,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0.004580153,0,0,0.048854962,0.001526718,0.945038168},
				{0.5,0.5,0,0,0,0,0,0}}),
		$101 ("UROLOGY",
				new double [][] {{0,0.027586207,0,0.44137931,0,0,0.531034483,0},
				{0.333333333,0,0,0,0,0,0,0.666666667},
				{0,0.166666667,0.027777778,0.055555556,0.027777778,0,0,0.722222222},
				{0,0.333333333,0,0.666666667,0,0,0,0},
				{0.010152284,0.020304569,0.005076142,0.076142132,0.010152284,0,0.035532995,0.842639594},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0.166666667,0,0,0,0.833333333},
				{0.00621118,0.130434783,0.00621118,0.409937888,0,0.037267081,0.01242236,0.397515528}},
				new double[][] {{0,0.303687636,0,0.650759219,0,0.017353579,0.028199566,0},
				{0,0,0,0,0,0,0,0},
				{0,0.095238095,0.017857143,0.029761905,0,0,0.005952381,0.851190476},
				{0,0.636363636,0,0.272727273,0,0,0,0.090909091},
				{0,0.035502959,0.023668639,0.091715976,0,0,0.020710059,0.828402367},
				{0,0,0,0,0,0,0,0},
				{0,0.111111111,0,0,0,0,0,0.888888889},
				{0,0.130434783,0,0.608695652,0,0.043478261,0,0.217391304}}),
		$120 ("ENT",
				new double[][] {{0,0.004016064,0,0.891566265,0,0.072289157,0.032128514,0},
				{0,0,0,0.25,0.25,0,0,0.5},
				{0.222222222,0,0,0.444444444,0,0,0,0.333333333},
				{0.5,0,0,0,0,0,0,0.5},
				{0.009009009,0.036036036,0.009009009,0.009009009,0.004504505,0.009009009,0.009009009,0.914414414},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0.1,0,0,0,0.9},
				{0.1,0.1,0,0.7,0,0,0,0.1}},
				new double [][] {{0,0.020072993,0,0.75729927,0,0.22080292,0.001824818,0},
				{0,0,0,0,0,0,0,0},
				{0,0.0625,0,0.1875,0,0,0,0.75},
				{0,0,0,0,0,0,0,0},
				{0,0.00952381,0,0.002380952,0,0,0,0.988095238},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,1,0,0,0,0}}),
		$370 ("MEDICAL ONCOLOGY",
				new double[][] {{0.101010101,0,0,0,0,0,0.898989899,0},
				{0,0,0,0,0.083333333,0,0,0.916666667},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0.482758621,0,0.068965517,0,0,0,0,0.448275862}},
				new double[][] {{1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}}),
		$303 ("CLINICAL HAEMATOLOGY",
				new double[][] {{0.096774194,0.016129032,0,0,0,0,0.887096774,0},
				{0.15,0.15,0,0,0.05,0,0,0.65},
				{0,0.333333333,0,0,0,0,0,0.666666667},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0.766666667,0,0,0,0.033333333,0,0,0.2}},
				new double[][] {{1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}}),
		$190 ("ANAESTHETICS",
				new double[][] {{0,0,1,0,0,0,0,0},
				{0.03125,0.03125,0.03125,0,0.03125,0,0,0.875},
				{0.052631579,0.052631579,0.052631579,0.052631579,0,0,0,0.789473684},
				{0.340909091,0.204545455,0,0.011363636,0,0.011363636,0.034090909,0.397727273},
				{0.333333333,0,0,0,0,0,0,0.666666667},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1},
				{0.333333333,0.333333333,0,0.333333333,0,0,0,0}},
				new double[][] {{0,0.777777778,0,0,0,0.222222222,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0}}),
		$800 ("CLINICAL ONCOLOGY",
				new double[][] {{0.043478261,0,0,0,0,0,0.956521739,0},
				{0,0.166666667,0,0,0.166666667,0,0,0.666666667},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1},
				{0.5625,0.0625,0.0625,0,0,0.0625,0,0.25}},
				new double[][] {{0,1,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}}),
		$130 ("OPHTHALMOLOGY",
				new double[][] {{0,0.227272727,0,0.5,0,0.227272727,0.045454545,0},
				{0,0,0,0,0,0,0,0},
				{0,0.055555556,0,0.444444444,0,0.055555556,0,0.444444444},
				{0,0,0,0,0,0,0,0},
				{0,0.205882353,0,0.088235294,0,0.058823529,0,0.647058824},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0.181818182,0,0,0,0.818181818},
				{0,0.5,0,0,0,0,0,0.5}},
				new double[][] {{0,0,0,1,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0.25,0,0.75},
				{0.5,0,0.5,0,0,0,0,0},
				{0,0,0,0,0,0,0.111111111,0.888888889},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1}}),
		$140 ("ORAL SURGERY",
				new double[][] {{0,0,0,0.75,0,0.136363636,0.113636364,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0.333333333,0,0,0,0.666666667},
				{0,0,0,0,0,0,0,0},
				{0,0.051282051,0,0.025641026,0,0.025641026,0,0.897435897},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0.142857143,0.857142857},
				{0,0.166666667,0,0.666666667,0,0,0,0.166666667}},
				new double[][] {{0,0.084337349,0,0.831325301,0,0.072289157,0.012048193,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0.3,0,0,0,0.7},
				{0,0,0,1,0,0,0,0},
				{0,0.040540541,0.013513514,0.013513514,0,0,0,0.932432432},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1}}),
		$410 ("RHEUMATOLOGY",
				new double[][] {{0.027027027,0,0,0,0.783783784,0,0.189189189,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0.5,0,0,0,0,0,0,0.5},
				{0,0,0,0,0,0,0,0},
				{0,0,0.03030303,0,0.03030303,0,0,0.939393939},
				{0,0,0,0,0,0,0,0},
				{0.166666667,0,0.166666667,0,0.666666667,0,0,0}},
				new double[][] {{0,0,0,0,0.875,0,0.125,0},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0.125,0,0,0,0.125,0,0,0.75},
				{0,0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0,0}}),
		$330 ("DERMATOLOGY",
				new double[][] {{0.045454545,0,0,0,0.727272727,0.136363636,0.090909091,0},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1},
				{0.5,0,0,0,0.5,0,0,0}},
				new double[][] {{0,0,0,0,0.965811966,0.034188034,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0},
				{0.004405286,0.004405286,0.004405286,0,0.008810573,0.004405286,0,0.973568282},
				{0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0}});

		private final String name;
		private double[][] emerMatrix;
		private double[][] elecMatrix;
		private Specialty(String name, double [][]emerMatrix, double [][]elecMatrix) {
			this.name = name;
			this.emerMatrix = emerMatrix;
			this.elecMatrix = elecMatrix;
		}
		public String getName() { return name; }
		public double[][] getEmerMatrix() { return emerMatrix; }
		public double[][] getElecMatrix() { return elecMatrix; }
	}	

	public enum WardType {
		GMED ("Medical Wards", 78, "ExponentialVariate", new Object[] {6.68207498715973}, 
				"ExponentialVariate", new Object[] {5.16279069767442}),
		GSUR ("Surgical Wards", 129, "HyperExponentialVariate", new Object[] {2.74927036043212, 11.2119097638434, 0.5}, 
				"HyperExponentialVariate", new Object[] {2.48524136957044, 6.97821244583691, 0.5}),
		GCRI ("Critical Unit Wards", 14, "HyperExponentialVariate", new Object[] {1.9703721932111, 5.9346481887035, 0.5}, 
				"ExponentialVariate", new Object[] {3.451923076923}),
		GSPE ("Specialty Wards", 18, "HyperExponentialVariate", new Object[] {1.33775809823615, 4.69047208845052, 0.5}, 
				"HyperExponentialVariate", new Object[] {1.38755707856734, 2.94697962422653, 0.5}),
		GELD ("Elderly Wards", 84, "HyperExponentialVariate", new Object[] {15.9337887001623, 15.933783099126, 0.5}, 
				"HyperExponentialVariate", new Object[] {9.51647126376783, 18.8046296552964, 0.5}),
		GWOC ("Women and Children Wards", 72, "ExponentialVariate", new Object[] {2.35314155942468}, 
				"ExponentialVariate", new Object[] {3.49945593035909}),
		GASM ("Assessment Wards", 32, 
				"EmpiricalVariate", new Object[] {
				new double[] {0.439744513065383, 0.430288500920493, 0.0922327595124107, 0.0262690140089955, 0.0114652124927178},
				new double[] {0.25, 0.8, 2.1, 3.1, 4.1}},
//				new RandomVariate[] {
//						RandomVariateFactory.getInstance("UniformVariate", 0, 1),
//						RandomVariateFactory.getInstance("UniformVariate", 1, 2),
//						RandomVariateFactory.getInstance("UniformVariate", 2, 3),
//						RandomVariateFactory.getInstance("UniformVariate", 3, 4),
//						RandomVariateFactory.getInstance("UniformVariate", 4, 5)
//				}},
				"EmpiricalVariate", new Object[] {
				new double[] {0.439744513065383, 0.430288500920493, 0.0922327595124107, 0.0262690140089955, 0.0114652124927178},
				new double[] {0.25, 0.8, 2.1, 3.1, 4.1}});
//				new RandomVariate[] {
//						RandomVariateFactory.getInstance("UniformVariate", 0, 1),
//						RandomVariateFactory.getInstance("UniformVariate", 1, 2),
//						RandomVariateFactory.getInstance("UniformVariate", 2, 3),
//						RandomVariateFactory.getInstance("UniformVariate", 3, 4),
//						RandomVariateFactory.getInstance("UniformVariate", 4, 5)
//				}});

		private final String name;
		private final int beds;
		private final String emerDist;
		private final Object []emerParam;
		private final String elecDist;
		private final Object []elecParam;
		
		private WardType(String name, int beds, String emerDist,
				Object[] emerParam, String elecDist, Object[] elecParam) {
			this.name = name;
			this.beds = beds;
			this.emerDist = emerDist;
			this.emerParam = emerParam;
			this.elecDist = elecDist;
			this.elecParam = elecParam;
		}

		public String getName() { return name;	}
		public int getBeds() { return beds;	}
		public String getEmerDist() { return emerDist; }
		public Object[] getEmerParam() { return emerParam; }
		public String getElecDist() { return elecDist; }
		public Object[] getElecParam() { return elecParam; }
	}

	private ArrayList<InfoReceiver> viewers = new ArrayList<InfoReceiver>(); 
	
	public RLIIP7GSimulation(int id) {
		super(id, "RLI Inpatient Model", SimulationTimeUnit.DAY, 0.0, (double)NDAYS);
	}
	
	protected void testModel() {
		// CURIOSO: Con este generador no genera todo, ¿por qué?
//		RandomVariateFactory.setDefaultRandomNumber(new NSSrng());
		// Defines the patient types
		for (Specialty spec : Specialty.values()) {
			new ElementType(AdmissionMethod.AE.ordinal() * Specialty.values().length + spec.ordinal(), this, "" + AdmissionMethod.AE + "/" + spec, 0);
			new ElementType(AdmissionMethod.GP.ordinal() * Specialty.values().length + spec.ordinal(), this, "" + AdmissionMethod.GP + "/" + spec, 0);
			new ElementType(AdmissionMethod.OR.ordinal() * Specialty.values().length + spec.ordinal(), this, "" + AdmissionMethod.OR + "/" + spec, 1);
		}

		// Defines resource types, resources and Workgroups
		int resCount = 0;
		SimulationCycle c = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", endTs), 0);
		WorkGroup wgs[] = new WorkGroup[WardType.values().length];
		int count = 0;
		for (WardType ward : WardType.values()) {
			ResourceType rt = new ResourceType(ward.ordinal(), this, "W" + ward);
			for (int i = 0; i < ward.getBeds(); i++)
				new Resource(resCount++, this, "BED" + i + "_W" + ward).addTimeTableEntry(c, endTs, rt);
			wgs[count++] = new WorkGroup(rt, 1);
		}

		// Element creators
		ElementCreator [] creators = new ElementCreator[AdmissionMethod.values().length];
//		for (AdmissionMethod adm : AdmissionMethod.values())
//			creators[adm.ordinal()] = new ElementCreator(new ConstantFunction(1));
		creators[AdmissionMethod.AE.ordinal()] = new ElementCreator(this, new ConstantFunction(1));
		creators[AdmissionMethod.GP.ordinal()] = new ElementCreator(this, new ConstantFunction(1));
		creators[AdmissionMethod.OR.ordinal()] = new ElementCreator(this, new PeriodicProportionFunction(AdmissionMethod.OR.getPatientsWeek(), AdmissionMethod.OR.getPatientsDaily(), 1.0));

//		ElementCreator AECreator = new ElementCreator(new PeriodicProportionFunction(AdmissionMethod.AE.getPatientsWeek(), AdmissionMethod.AE.getPatientsDaily(), 1.0));
//		ElementCreator GPCreator = new ElementCreator(new PeriodicProportionFunction(AdmissionMethod.GP.getPatientsWeek(), AdmissionMethod.GP.getPatientsDaily(), 1.0));
//		ElementCreator ORCreator = new ElementCreator(new PeriodicProportionFunction(AdmissionMethod.OR.getPatientsWeek(), AdmissionMethod.OR.getPatientsDaily(), 1.0));

		// Defines activities, flows and generators
		for (Specialty spec : Specialty.values()) {
			// First activities and flows
			TransitionActivity actEmer = new TransitionActivity(spec.ordinal(), this, "EMER " + spec + ": Stay in bed", 0);
			SingleFlow fEmer = new SingleFlow(this, actEmer);
			TransitionActivity actElec = new TransitionActivity(Specialty.values().length + spec.ordinal(), this, "ELEC" + spec + ": Stay in bed", 1);
			SingleFlow fElec = new SingleFlow(this, actElec);
			for (WardType ward : WardType.values()) {
				actEmer.addWorkGroup(new SimulationTimeFunction(this, ward.getEmerDist(), ward.getEmerParam()), wgs[ward.ordinal()]);
				actElec.addWorkGroup(new SimulationTimeFunction(this, ward.getElecDist(), ward.getElecParam()), wgs[ward.ordinal()]);
			}
			// Defines transitions
			actEmer.addTransitions(spec.getEmerMatrix());
			// FIXME: These activities shouldn't be created
			if (!actEmer.checkTransitions() && 
					((AdmissionMethod.AE.getProb()[spec.ordinal()] > 0.0) || (AdmissionMethod.GP.getProb()[spec.ordinal()] > 0.0)))
				throw new RuntimeException("INVALID MATRIX. ACTIVITY " + actEmer.getDescription());
			actElec.addTransitions(spec.getElecMatrix());
			if (!actElec.checkTransitions() && (AdmissionMethod.OR.getProb()[spec.ordinal()] > 0.0))
				throw new RuntimeException("INVALID MATRIX. ACTIVITY " + actElec.getDescription());
			// Adds type of patients to creators
			creators[AdmissionMethod.AE.ordinal()].add(getElementType(AdmissionMethod.AE.ordinal() * Specialty.values().length + spec.ordinal()), fEmer, AdmissionMethod.AE.prob[spec.ordinal()]);			
			creators[AdmissionMethod.GP.ordinal()].add(getElementType(AdmissionMethod.GP.ordinal() * Specialty.values().length + spec.ordinal()), fEmer, AdmissionMethod.GP.prob[spec.ordinal()]);			
			creators[AdmissionMethod.OR.ordinal()].add(getElementType(AdmissionMethod.OR.ordinal() * Specialty.values().length + spec.ordinal()), fElec, AdmissionMethod.OR.prob[spec.ordinal()]);			
//			AECreator.add(getElementType(AdmissionMethod.AE.ordinal() * Specialty.values().length + spec.ordinal()), fEmer, AdmissionMethod.AE.prob[spec.ordinal()]);			
//			GPCreator.add(getElementType(AdmissionMethod.GP.ordinal() * Specialty.values().length + spec.ordinal()), fEmer, AdmissionMethod.GP.prob[spec.ordinal()]);			
//			ORCreator.add(getElementType(AdmissionMethod.OR.ordinal() * Specialty.values().length + spec.ordinal()), fElec, AdmissionMethod.OR.prob[spec.ordinal()]);			
		}
			
		// Defines main cycle (starting at 8h) and element generators
		AdmissionMethod adm = AdmissionMethod.AE;
		SimulationTimeFunction[] part = new SimulationTimeFunction[adm.patientsWeek.length * adm.patientsDaily.length];
		int ind = 0;
		for (int pw : adm.patientsWeek)
			for (double p : adm.patientsDaily)
				part[ind++] = new SimulationTimeFunction(this, "ExponentialVariate", 1.0 / (pw * p));
		SimulationCycle subCycle = new SimulationPeriodicCycle(this, 0.0, new SimulationUniformlyDistributedSplitFunction(this, part, 1.0), 0); 
		SimulationCycle dailyCycle = new SimulationPeriodicCycle(this, 0.0001, new SimulationTimeFunction(this, "ConstantVariate", 1.0), 0, subCycle);
		new TimeDrivenGenerator(this, creators[adm.ordinal()], dailyCycle);
		
		adm = AdmissionMethod.GP;
		part = new SimulationTimeFunction[adm.patientsWeek.length * adm.patientsDaily.length];
		ind = 0;
		for (int pw : adm.patientsWeek)
			for (double p : adm.patientsDaily)
				part[ind++] = new SimulationTimeFunction(this, "ExponentialVariate", 1.0 / (pw * p));
		subCycle = new SimulationPeriodicCycle(this, 0.0, new SimulationUniformlyDistributedSplitFunction(this, part, 1.0), 0); 
		dailyCycle = new SimulationPeriodicCycle(this, 0.0001, new SimulationTimeFunction(this, "ConstantVariate", 1.0), 0, subCycle);
		new TimeDrivenGenerator(this, creators[adm.ordinal()], dailyCycle);
		
		adm = AdmissionMethod.OR;
		dailyCycle = new SimulationPeriodicCycle(this, 0.0001, new SimulationTimeFunction(this, "ConstantVariate", 1.0), 0);
		new TimeDrivenGenerator(this, creators[adm.ordinal()], dailyCycle);

//		for (AdmissionMethod adm : AdmissionMethod.values()) {
//			TimeFunction[] part = new TimeFunction[adm.patientsWeek.length * adm.patientsDaily.length];
//			int ind = 0;
//			for (int pw : adm.patientsWeek)
//				for (double p : adm.patientsDaily)
//					part[ind++] = TimeFunctionFactory.getInstance("ExponentialVariate", 1.0 / (pw * p));
//			Cycle subCycle = new PeriodicCycle(0.0, new UniformlyDistributedSplitFunction(part, 1.0), 0); 
//			Cycle dailyCycle = new PeriodicCycle(0.0001, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 0, subCycle);
//			new TimeDrivenGenerator(this, creators[adm.ordinal()], dailyCycle);
//		}

//		Cycle dailyCycle = new PeriodicCycle(1.0 / 3.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 0);
//		new TimeDrivenGenerator(this, AECreator, dailyCycle);
//		new TimeDrivenGenerator(this, GPCreator, dailyCycle);
//		new TimeDrivenGenerator(this, ORCreator, dailyCycle);
	}
	
	@Override
	protected void createModel() {
		testModel();
	}

	@Override
	public void addInfoReceiver(InfoReceiver receiver) {
		super.addInfoReceiver(receiver);
		viewers.add(receiver);
	}
	
	@Override
	public void end() {
		
	}
}
