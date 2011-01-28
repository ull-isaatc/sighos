/**
 * 
 */
package es.ull.isaatc.rli;

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

/**
 * @author Iván
 *
 */
public class RLIIP8GSimulation extends Simulation {
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
				new double[][] {{0.840799572,0.001784758,0.081206497,0.001606282,0.070854899,0.000535427,0.002677137,0.000535427,0},
				{0.005012531,0.026106934,0.016708438,0.050543024,0.512113617,0.002506266,0.024018379,0.005430242,0.357560568},
				{0.011111111,0,0.011111111,0.061111111,0.033333333,0,0.011111111,0,0.872222222},
				{0.042517007,0.005102041,0.013605442,0.008503401,0.345238095,0,0.023809524,0,0.56122449},
				{0.010714286,0,0.007142857,0.089285714,0.033928571,0,0.016071429,0,0.842857143},
				{0.005789643,0.01318752,0.010292699,0.07204889,0.004824702,0.007397877,0.051141846,0.020585397,0.814731425},
				{0,0,0,0.051282051,0.128205128,0,0.051282051,0,0.769230769},
				{0.006060606,0.003030303,0.021212121,0.048484848,0.033333333,0.003030303,0.033333333,0.015151515,0.836363636},
				{0,0,0,0.010204082,0.010204082,0,0.030612245,0,0.948979592}},
				new double[][] {{0.057971014,0.014492754,0.028985507,0.057971014,0.797101449,0.014492754,0.028985507,0,0},
				{0,0,0,0,0.5,0,0.25,0,0.25},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0.333333333,0,0,0,0.666666667},
				{0,0,0,0,0,0,0,0,1},
				{0,0.016949153,0.016949153,0,0,0.016949153,0.016949153,0,0.93220339},
				{0,0,0,0,0.5,0,0,0,0.5},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0}}),
		$100 ("GENERAL SURGERY",
				new double[][] {{0.9446875,0,0.0003125,0,0.0003125,0.0175,0.02875,0.0084375,0},
				{0.001625488,0.001950585,0.016905072,0.003250975,0.008127438,0.10923277,0.450260078,0.017230169,0.391417425},
				{0,0,0,0,0.111111111,0,0,0,0.888888889},
				{0.028846154,0,0,0,0.028846154,0,0.692307692,0,0.25},
				{0.021276596,0,0,0.042553191,0,0.021276596,0.106382979,0,0.808510638},
				{0,0,0,0.111111111,0,0,0.027777778,0.027777778,0.833333333},
				{0.067146283,0,0.002398082,0.004796163,0.002398082,0.033573141,0.18705036,0.002398082,0.700239808},
				{0.007602339,0.001754386,0.029239766,0.016959064,0.002923977,0.005847953,0.042690058,0.018128655,0.874853801},
				{0.026548673,0,0,0,0,0,0.03539823,0,0.938053097}},
				new double[][] {{0.009033424,0,0,0,0.000903342,0.038843722,0.897922313,0.0532972,0},
				{0,0,0,0,0,0.090909091,0.545454545,0,0.363636364},
				{0,0,0.25,0,0,0,0,0,0.75},
				{0,0,0,0,0.064935065,0,0.909090909,0,0.025974026},
				{0,0,0,0,0,0,0.25,0,0.75},
				{0,0,0,0,0,0,0.222222222,0,0.777777778},
				{0,0,0,0,0,0.020833333,0.166666667,0,0.8125},
				{0.000879507,0.00351803,0.066842568,0.00351803,0.002638522,0.002638522,0.047493404,0.00703606,0.865435356},
				{0,0,0,0,0,0,0.029850746,0,0.970149254}}),
		$420 ("PAEDIATRICS",
				new double[][] {{0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1}},
				new double[][] {{0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1}}),
		$110 ("TRAUMA & ORTHOPAEDICS",
				new double[][] {{0.013831259,0,0,0,0,0.003227294,0.861226372,0.121715076,0},
				{0,0,0.029411765,0,0.058823529,0.088235294,0.676470588,0,0.147058824},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0.045454545,0,0,0,0.727272727,0,0.227272727},
				{0.006535948,0,0,0.104575163,0,0,0.039215686,0,0.849673203},
				{0,0,0.1,0.1,0,0,0.1,0,0.7},
				{0,0,0,0,0,0,0.384615385,0,0.615384615},
				{0.001459854,0.001459854,0.009245742,0.066180049,0.003892944,0.001459854,0.065206813,0.006326034,0.844768856},
				{0,0,0,0,0,0,0.007220217,0,0.992779783}},
				new double[][] {{0,0,0,0,0,0.002460025,0.929889299,0.067650677,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1,0,0},
				{0,0,0,0.25,0,0,0,0,0.75},
				{0,0,0,0.333333333,0,0,0.666666667,0,0},
				{0,0,0,0,0,0,0.666666667,0,0.333333333},
				{0,0,0.011553273,0.002567394,0.003851091,0.001283697,0.01283697,0,0.967907574},
				{0,0,0,0,0,0,0,0,1}}),
		$430 ("GERIATRIC MEDICINE",
				new double[][] {{0.899383984,0,0.018480493,0.052361396,0.025667351,0,0.004106776,0,0},
				{0.003378378,0.010135135,0.005630631,0.422297297,0.434684685,0.007882883,0.019144144,0.001126126,0.095720721},
				{0,0,0,0.083333333,0.083333333,0,0,0,0.833333333},
				{0.08,0,0,0,0.52,0,0.08,0,0.32},
				{0.005369128,0,0.001342282,0.140939597,0.016107383,0.001342282,0.008053691,0.001342282,0.825503356},
				{0.00678733,0.00678733,0,0.441176471,0.004524887,0.004524887,0.020361991,0.018099548,0.497737557},
				{0,0,0,0.1,0,0,0.3,0,0.6},
				{0,0,0.023809524,0.380952381,0.047619048,0,0.023809524,0.047619048,0.476190476},
				{0,0,0,0.083333333,0.083333333,0,0,0,0.833333333}},
				new double[][] {{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}}),
		$502 ("GYNAECOLOGY",
				new double[][] {{0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0.666666667,0.333333333,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0.001064963,0.003194888,0.001064963,0.001064963,0,0.002129925,0.008519702,0.982960596}},
				new double[][] {{0.00154321,0,0,0,0,0,0,0.99845679,0},
				{0,0,0,0,0.5,0,0,0.5,0},
				{0,0,0,0,0,0,0,0,0},
				{0.25,0,0.25,0,0,0,0.25,0.25,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0.004622496,0,0,0,0.001540832,0,0.993836672}}),
		$101 ("UROLOGY",
				new double[][] {{0.513793103,0,0,0,0,0.455172414,0.031034483,0,0},
				{0,0,0.00621118,0,0.00621118,0.385093168,0.130434783,0.037267081,0.434782609},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0.666666667,0.333333333,0,0},
				{0,0,0,0.2,0,0,0,0,0.8},
				{0,0,0,0.25,0.25,0,0,0,0.5},
				{0.054726368,0.004975124,0.004975124,0.009950249,0.009950249,0.009950249,0.019900498,0,0.885572139},
				{0.026315789,0,0.026315789,0.026315789,0,0.052631579,0.078947368,0,0.789473684},
				{0,0,0,0,0,0.166666667,0,0,0.833333333}},
				new double[][] {{0.033613445,0,0,0,0,0.649159664,0.300420168,0.016806723,0},
				{0,0.041666667,0,0,0,0.583333333,0.125,0.041666667,0.208333333},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0.272727273,0.636363636,0,0.090909091},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0.020348837,0,0.023255814,0,0,0.037790698,0.034883721,0,0.88372093},
				{0.00591716,0.00591716,0.017751479,0,0,0.029585799,0.017751479,0,0.923076923},
				{0,0,0,0,0,0,0.111111111,0,0.888888889}}),
		$120 ("ENT",
				new double[][] {{0.032128514,0,0,0,0,0.887550201,0.008032129,0.072289157,0},
				{0,0,0,0,0.1,0.7,0.1,0,0.1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0.5,0,0,0,0.5},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0.166666667,0,0.166666667,0,0,0.666666667},
				{0.008474576,0,0.008474576,0.004237288,0.008474576,0,0.029661017,0.008474576,0.93220339},
				{0,0,0,0,0.2,0.5,0,0,0.3},
				{0,0,0,0,0,0.1,0,0,0.9}},
				new double[][] {{0.001801802,0,0,0,0,0.74954955,0.021621622,0.227027027,0},
				{0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0.007125891,0,0.992874109},
				{0,0,0,0,0,0.25,0.0625,0,0.6875},
				{0,0,0,0,0,0,0,0,1}}),
		$370 ("MEDICAL ONCOLOGY",
				new double[][] {{0.449494949,0.5,0,0,0.050505051,0,0,0,0},
				{0,0.696629213,0,0,0.157303371,0,0,0,0.146067416},
				{0,0,0.011428571,0,0,0,0.005714286,0,0.982857143},
				{0,0.5,0,0,0,0,0,0,0.5},
				{0,0,0,0,0,0,0,0,1},
				{0,0.5,0,0.041666667,0,0,0,0,0.458333333},
				{0,0,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}},
				new double[][] {{0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0.007092199,0,0,0,0.992907801},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}}),
		$303 ("CLINICAL HAEMATOLOGY",
				new double[][] {{0.519230769,0.413461538,0,0,0.057692308,0,0.009615385,0,0},
				{0,0.436363636,0,0.018181818,0.418181818,0,0,0,0.127272727},
				{0.0125,0,0,0,0.0125,0,0,0,0.975},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0.4,0,0.033333333,0,0,0.1,0,0.466666667},
				{0,0,0,0,0,0,0,0,0},
				{0,0.25,0,0,0,0,0,0,0.75},
				{0,0,0,0,0,0,0,0,0}},
				new double[][] {{0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0.023255814,0,0,0,0.976744186},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}}),
		$190 ("ANAESTHETICS",
				new double[][] {{0.010989011,0,0.989010989,0,0,0,0,0,0},
				{0,0,0.333333333,0,0.333333333,0.333333333,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0.021505376,0,0,0,0.333333333,0.010752688,0.215053763,0.010752688,0.408602151},
				{0,0,0,0,0,0,0,0,1},
				{0,0.029411765,0.029411765,0.058823529,0,0,0.029411765,0,0.852941176},
				{0,0,0,0,0.333333333,0,0,0,0.666666667},
				{0,0,0.045454545,0.045454545,0.045454545,0.045454545,0.045454545,0,0.772727273},
				{0,0,0,0,0,0,0,0,1}},
				new double[][] {{0,0,0,0,0,0,0.777777778,0.222222222,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1}}),
		$800 ("CLINICAL ONCOLOGY",
				new double[][] {{0.34375,0.625,0,0,0.03125,0,0,0,0},
				{0,0.434782609,0,0,0.347826087,0,0,0.043478261,0.173913043},
				{0,0,0.018518519,0,0,0,0.018518519,0,0.962962963},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,1},
				{0.1,0.3,0,0.1,0,0,0.1,0,0.4},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,1,0,0,0,0,0,0,0}},
				new double[][] {{0,1,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0.016666667,0,0.983333333},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0}}),
		$130 ("OPHTHALMOLOGY",
				new double[][] {{0.042553191,0.021276596,0,0,0,0.510638298,0.212765957,0.212765957,0},
				{0,0,0,0,0,0,0.5,0,0.5},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0.205882353,0.058823529,0.735294118},
				{0,0,0,0,0,0.421052632,0.052631579,0.052631579,0.473684211},
				{0,0,0,0,0,0.153846154,0,0,0.846153846}},
				new double[][] {{0,0,0.023809524,0,0.023809524,0.214285714,0.357142857,0.380952381,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0.5,0,0.5,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0.111111111,0,0,0,0,0,0,0,0.888888889},
				{0,0,0,0,0,0,0,0.066666667,0.933333333},
				{0,0,0,0,0,0,0,0,1}}),
		$140 ("ORAL SURGERY",
				new double[][] {{0.113636364,0,0,0,0,0.75,0,0.136363636,0},
				{0,0,0,0,0,0.666666667,0.166666667,0,0.166666667},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0.025641026,0.051282051,0.025641026,0.897435897},
				{0,0,0,0,0,0.333333333,0,0,0.666666667},
				{0.142857143,0,0,0,0,0,0,0,0.857142857}},
				new double[][] {{0.011494253,0,0,0,0,0.816091954,0.08045977,0.091954023,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0.013333333,0,0,0,0.04,0,0.946666667},
				{0,0,0,0,0,0.3,0,0,0.7},
				{0,0,0,0,0,0,0,0,1}}),
		$410 ("RHEUMATOLOGY",
				new double[][] {{0.189189189,0,0,0.783783784,0.027027027,0,0,0,0},
				{0,0.142857143,0.142857143,0.571428571,0.142857143,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0.5,0,0,0,0.5},
				{0,0,0.03030303,0,0,0,0,0,0.96969697},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}},
				new double[][] {{0.125,0,0,0.875,0,0,0,0,0},
				{0,0,0,0,1,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0.111111111,0,0,0,0.888888889},
				{0,0,0,1,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0}}),
		$330 ("DERMATOLOGY",
				new double[][] {{0.090909091,0,0,0.727272727,0.045454545,0,0,0.136363636,0},
				{0,0.5,0,0,0.5,0,0,0,0},
				{0,0,0,1,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,1,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1}},
				new double[][] {{0,0,0,0.965811966,0,0,0,0.034188034,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0.004405286,0,0.004405286,0,0.004405286,0.004405286,0.982378855},
				{0,0,0,0,0,0,0,0,1},
				{0,0,0,0,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,1}});

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
				new double[] {0.25, 0.8, 2.1, 3.1, 4.1}}),
//				new RandomVariate[] {
//				RandomVariateFactory.getInstance("UniformVariate", 0, 1),
//				RandomVariateFactory.getInstance("UniformVariate", 1, 2),
//				RandomVariateFactory.getInstance("UniformVariate", 2, 3),
//				RandomVariateFactory.getInstance("UniformVariate", 3, 4),
//				RandomVariateFactory.getInstance("UniformVariate", 4, 5)
//		}});
		GCAN ("Cancer Ward", 10, "ExponentialVariate", new Object[] {4.4},
				"ExponentialVariate", new Object[] {2.5}),
		GCRI ("Critical Unit Wards", 14, "HyperExponentialVariate", new Object[] {1.9703721932111, 5.9346481887035, 0.5}, 
				"ExponentialVariate", new Object[] {3.451923076923}),
		GELD ("Elderly Wards", 100, "HyperExponentialVariate", new Object[] {15.9337887001623, 15.933783099126, 0.5}, 
				"HyperExponentialVariate", new Object[] {9.51647126376783, 18.8046296552964, 0.5}),
		GMED ("Medical Wards", 78, "ExponentialVariate", new Object[] {6.68207498715973}, 
				"ExponentialVariate", new Object[] {5.16279069767442}),
		GSPE ("Specialty Wards", 18, "HyperExponentialVariate", new Object[] {1.33775809823615, 4.69047208845052, 0.5}, 
				"HyperExponentialVariate", new Object[] {1.38755707856734, 2.94697962422653, 0.5}),
		GSUR ("Surgical Wards", 129, "HyperExponentialVariate", new Object[] {2.74927036043212, 11.2119097638434, 0.5}, 
				"HyperExponentialVariate", new Object[] {2.48524136957044, 6.97821244583691, 0.5}),
		GWOC ("Women and Children Wards", 72, "ExponentialVariate", new Object[] {2.35314155942468}, 
				"ExponentialVariate", new Object[] {3.49945593035909});

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

	
	public RLIIP8GSimulation(int id) {
		super(id, "RLI Inpatient Model", SimulationTimeUnit.DAY, 0.0, NDAYS);
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

}
