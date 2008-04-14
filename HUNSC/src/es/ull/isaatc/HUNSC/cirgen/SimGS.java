/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.util.EnumSet;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.RoundedPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle;
import es.ull.isaatc.util.WeeklyPeriodicCycle.WeekDays;

/**
 * @author SYSTEM
 *
 */
public class SimGS extends StandAloneLPSimulation {
	// Tiempo total del estudio: 3 años en minutos (un año bisiesto)
	public static final double TOTALTIME = 1096 * 1440.0;
	// Tiempo de simulación
	public static final double ENDTIME = 1096 * 1440.0;
	// Tiempo de uso diario de los quirófanos (7 horas)
	public static final double AVAILABILITY = 7 * 60.0;
	// Hora de apertura de los quirófanos (mañana: 8 am)
	public static final double STARTTIME = 8 * 60.0;
	public enum OpTheatre {
		Q31 ("3-1", false, EnumSet.of(WeekDays.FRIDAY), 26850.0, 36110.0),
		AMB ("AMB", true, EnumSet.of(WeekDays.MONDAY), 25842.0, 51755.0),
		Q45 ("4-5", false, WeeklyPeriodicCycle.WEEKDAYS, 243235.0, 298125.0),
		Q46 ("4-6", false, WeeklyPeriodicCycle.WEEKDAYS, 211208.0, 249900.0);
		
		private final String name;
		private final WeeklyPeriodicCycle cycle;
		private final boolean daycase;
		private final double realUsage;
		private final double realAva;
		private OpTheatre(String name, boolean daycase, EnumSet<WeekDays> days, double realUsage, double realAva) {
			this.name = name;
			this.daycase = daycase;
			cycle = new WeeklyPeriodicCycle(days, 1440.0, STARTTIME, 0);
			this.realUsage = realUsage;
			this.realAva = realAva;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the daycase
		 */
		public boolean isDaycase() {
			return daycase;
		}

		/**
		 * @return the cycle
		 */
		public WeeklyPeriodicCycle getCycle() {
			return cycle;
		}

		/**
		 * @return the realUsage
		 */
		public double getRealUsage() {
			return realUsage;
		}

		/**
		 * @return the real availability
		 */
		public double getRealAva() {
			return realAva;
		}

	}

	public enum PatientType {
		D38 ("38", 1, 1, 90, 0, 0, 0),
		D78 ("78", 47, 0.021276596, 40, 0, 25.69565217, 10.37528327),
		D110 ("110", 1, 0, 0, 0, 40, 0),
		D122 ("122", 2, 1, 325, 15, 0, 0),
		D125 ("125", 1, 1, 20, 0, 0, 0),
		D150 ("150", 23, 1, 423.0434783, 180.8178229, 0, 0),
		D151 ("151", 48, 1, 348.4375, 148.3806909, 0, 0),
		D152 ("152", 5, 1, 386, 72.89718787, 0, 0),
		D153 ("153", 204, 0.995098039, 325.4876847, 100.8159988, 20, 0),
		D154 ("154", 153, 1, 350.2810458, 104.6217249, 0, 0),
		D155 ("155", 29, 1, 372.2413793, 163.8048998, 0, 0),
		D156 ("156", 20, 1, 340.65, 111.7408945, 0, 0),
		D157 ("157", 42, 1, 321.5476191, 111.837936, 0, 0),
		D158 ("158", 4, 1, 315, 14.57737974, 0, 0),
		D161 ("161", 3, 1, 86.66666667, 16.99673171, 0, 0),
		D171 ("171", 4, 1, 248.75, 174.7274664, 0, 0),
		D172 ("172", 51, 0.941176471, 142.875, 66.30379106, 51.66666667, 10.27402334),
		D173 ("173", 18, 0.333333333, 131.6666667, 62.76056795, 39.66666667, 9.65516557),
		D174 ("174", 2, 1, 87.5, 27.5, 0, 0),
		D175 ("175", 2, 1, 120, 10, 0, 0),
		D183 ("183", 9, 1, 297.7777778, 139.4057051, 0, 0),
		D186 ("186", 1, 1, 370, 0, 0, 0),
		D188 ("188", 1, 1, 120, 0, 0, 0),
		D189 ("189", 1, 1, 60, 0, 0, 0),
		D192 ("192", 1, 1, 145, 0, 0, 0),
		D193 ("193", 39, 1, 250.5128205, 88.03987203, 0, 0),
		D194 ("194", 4, 1, 396.25, 114.8572484, 0, 0),
		D195 ("195", 2, 1, 357.5, 92.5, 0, 0),
		D196 ("196", 7, 1, 318.5714286, 97.96917466, 0, 0),
		D197 ("197", 55, 1, 295.9090909, 106.9923146, 0, 0),
		D198 ("198", 6, 0.833333333, 260.6, 61.99548371, 55, 0),
		D199 ("199", 2, 1, 187.5, 77.5, 0, 0),
		D201 ("201", 1, 1, 225, 0, 0, 0),
		D202 ("202", 19, 0.894736842, 185.5882353, 87.41184388, 90.5, 40.5),
		D210 ("210", 1, 1, 180, 0, 0, 0),
		D211 ("211", 17, 1, 289.8823529, 96.19336063, 0, 0),
		D214 ("214", 70, 0.028571429, 82.5, 57.5, 41.5, 16.6817089),
		D215 ("215", 8, 0.25, 187.5, 82.5, 32.33333333, 9.44575154),
		D216 ("216", 64, 0.015625, 45, 0, 32.38095238, 15.35239868),
		D217 ("217", 2, 0, 0, 0, 90, 10),
		D226 ("226", 7, 1, 197.1428571, 67.81577687, 0, 0),
		D227 ("227", 8, 1, 276.25, 68.9995471, 0, 0),
		D228 ("228", 11, 0.454545455, 293, 66.67833231, 35.33333333, 23.45681611),
		D229 ("229", 10, 0.1, 200, 0, 18.22222222, 6.82768511),
		D235 ("235", 105, 0.952380952, 290.41, 126.3356715, 40.4, 20.01599361),
		D236 ("236", 4, 1, 161.25, 54.92893136, 0, 0),
		D237 ("237", 9, 1, 172.7777778, 47.55763235, 0, 0),
		D238 ("238", 37, 0.513513514, 151.9473684, 146.0780158, 42.38888889, 22.70129436),
		D240 ("240", 7, 1, 237.8571429, 64.63397729, 0, 0),
		D241 ("241", 78, 1, 215.5384615, 75.97750887, 0, 0),
		D242 ("242", 10, 1, 244.5, 54.42655602, 0, 0),
		D246 ("246", 1, 1, 135, 0, 0, 0),
		D252 ("252", 9, 1, 236.6666667, 87.30533902, 0, 0),
		D255 ("255", 6, 1, 248.3333333, 91.18052911, 0, 0),
		D259 ("259", 1, 1, 110, 0, 0, 0),
		D275 ("275", 1, 1, 425, 0, 0, 0),
		D276 ("276", 1, 1, 255, 0, 0, 0),
		D278 ("278", 15, 1, 346.6666667, 102.0239623, 0, 0),
		D282 ("282", 1, 1, 255, 0, 0, 0),
		D287 ("287", 4, 1, 268.75, 82.64192338, 0, 0),
		D289 ("289", 2, 1, 345, 75, 0, 0),
		D359 ("359", 19, 0.263157895, 36.4, 13.63231455, 51, 9.18072515),
		D380 ("380", 5, 0, 0, 0, 25.8, 19.26032191),
		D441 ("441", 1, 1, 430, 0, 0, 0),
		D442 ("442", 1, 1, 230, 0, 0, 0),
		D446 ("446", 8, 0.875, 56.71428571, 18.14004478, 75, 0),
		D447 ("447", 14, 0.285714286, 147.5, 132.1221026, 49.4, 9.56242647),
		D454 ("454", 2, 0, 0, 0, 65, 5),
		D455 ("455", 15, 0.266666667, 98.75, 19.8037244, 34.45454545, 11.13849701),
		D457 ("457", 1, 0, 0, 0, 37, 0),
		D478 ("478", 1, 1, 180, 0, 0, 0),
		D518 ("518", 1, 1, 120, 0, 0, 0),
		D527 ("527", 1, 0, 0, 0, 42, 0),
		D530 ("530", 12, 0.916666667, 206.3636364, 101.0645813, 45, 0),
		D531 ("531", 1, 1, 110, 0, 0, 0),
		D532 ("532", 1, 0, 0, 0, 10, 0),
		D535 ("535", 1, 1, 115, 0, 0, 0),
		D537 ("537", 10, 1, 185.5, 110.3958786, 0, 0),
		D540 ("540", 13, 1, 87.53846154, 20.87551154, 0, 0),
		D542 ("542", 6, 1, 111.6666667, 41.59994658, 0, 0),
		D543 ("543", 1, 1, 390, 0, 0, 0),
		D550 ("550", 52, 0.980769231, 119.5098039, 49.539595, 15, 0),
		D552 ("552", 1, 1, 105, 0, 0, 0),
		D553 ("553", 96, 0.96875, 177.4408602, 84.21522126, 76.66666667, 30.09245014),
		D555 ("555", 26, 1, 276.9230769, 137.4638199, 0, 0),
		D556 ("556", 7, 1, 333.5714286, 92.33921492, 0, 0),
		D558 ("558", 4, 1, 220.25, 69.60738107, 0, 0),
		D560 ("560", 30, 1, 210.5, 85.47270519, 0, 0),
		D562 ("562", 37, 1, 335.5945946, 94.2510906, 0, 0),
		D565 ("565", 20, 0.8, 95.4375, 51.60906988, 50.5, 20.69420209),
		D566 ("566", 2, 1, 47.5, 12.5, 0, 0),
		D567 ("567", 3, 1, 73.33333333, 30.91206165, 0, 0),
		D568 ("568", 5, 1, 200.4, 83.15431438, 0, 0),
		D569 ("569", 36, 0.861111111, 181.1290323, 112.7780412, 51, 27.45906044),
		D571 ("571", 3, 1, 406.6666667, 33.99346342, 0, 0),
		D573 ("573", 10, 1, 279, 76.67463727, 0, 0),
		D574 ("574", 293, 1, 144.8634812, 58.83518193, 0, 0),
		D575 ("575", 55, 1, 153.0909091, 63.65739912, 0, 0),
		D576 ("576", 36, 1, 282.5, 100.3708402, 0, 0),
		D577 ("577", 30, 1, 167.5, 84.33969014, 0, 0),
		D578 ("578", 1, 1, 210, 0, 0, 0),
		D585 ("585", 1, 1, 50, 0, 0, 0),
		D593 ("593", 1, 1, 360, 0, 0, 0),
		D596 ("596", 11, 1, 253.1818182, 102.4755565, 0, 0),
		D610 ("610", 1, 0, 0, 0, 10, 0),
		D611 ("611", 9, 0.666666667, 73.66666667, 13.98411798, 49.33333333, 11.72840806),
		D614 ("614", 1, 1, 70, 0, 0, 0),
		D617 ("617", 1, 1, 295, 0, 0, 0),
		D618 ("618", 2, 1, 207.5, 2.5, 0, 0),
		D619 ("619", 5, 1, 234, 109.4714575, 0, 0),
		D620 ("620", 1, 1, 180, 0, 0, 0),
		D681 ("681", 2, 0, 0, 0, 40, 10),
		D682 ("682", 12, 0.583333333, 56.42857143, 27.73783977, 32, 14.69693846),
		D685 ("685", 9, 0.444444444, 126.25, 56.72025652, 43.8, 20.35092136),
		D686 ("686", 14, 0.428571429, 89.16666667, 37.01538719, 52.125, 24.75094695),
		D692 ("692", 1, 1, 110, 0, 0, 0),
		D700 ("700", 1, 0, 0, 0, 9, 0),
		D701 ("701", 8, 0, 0, 0, 33, 14.25657743),
		D702 ("702", 4, 0, 0, 0, 34.25, 15.46568783),
		D703 ("703", 50, 0, 0, 0, 42.06, 15.81443644),
		D704 ("704", 1, 0, 0, 0, 22, 0),
		D705 ("705", 12, 0, 0, 0, 37, 13.26021619),
		D706 ("706", 119, 0.058823529, 63.14285714, 51.51262972, 38.94642857, 20.55647243),
		D707 ("707", 9, 0.666666667, 92.5, 64.46898479, 34, 7.87400787),
		D709 ("709", 33, 0.060606061, 100, 70, 44.09677419, 28.55030065),
		D710 ("710", 11, 0.090909091, 55, 0, 23, 9.2736185),
		D727 ("727", 1, 1, 95, 0, 0, 0),
		D728 ("728", 1, 0, 0, 0, 35, 0),
		D729 ("729", 10, 0.3, 55, 8.16496581, 41, 9.87059124),
		D740 ("740", 1, 0, 0, 0, 52, 0),
		D744 ("744", 1, 0, 0, 0, 30, 0),
		D757 ("757", 5, 0, 0, 0, 44.4, 26.67283262),
		D751 ("751", 1, 1, 300, 0, 0, 0),
		D752 ("752", 1, 1, 120, 0, 0, 0),
		D759 ("759", 1, 0, 0, 0, 60, 0),
		D782 ("782", 5, 0.2, 90, 0, 40.75, 5.62916512),
		D785 ("785", 98, 0.653061224, 80.8125, 55.30056368, 55.85294118, 25.220361),
		D787 ("787", 3, 1, 125, 28.57738033, 0, 0),
		D789 ("789", 18, 1, 202.5, 91.45809605, 0, 0),
		D879 ("879", 3, 0, 0, 0, 38.33333333, 26.56229575),
		D892 ("892", 1, 0, 0, 0, 25, 0),
		D897 ("897", 1, 1, 45, 0, 0, 0),
		D935 ("935", 1, 1, 475, 0, 0, 0),
		D936 ("936", 1, 1, 135, 0, 0, 0),
		D959 ("959", 3, 0.333333333, 170, 0, 27, 0),
		D996 ("996", 12, 0.666666667, 220.375, 96.38326813, 42.75, 11.58393284),
		D997 ("997", 6, 1, 279.1666667, 146.4415887, 0, 0),
		D998 ("998", 27, 0.592592593, 120.9375, 149.6268144, 41, 29.05480714),
		DV44 ("V44", 15, 1, 261.3333333, 117.6784697, 0, 0),
		DV55 ("V55", 47, 0.978723404, 248.673913, 116.0097549, 320, 0),
		DV58 ("V58", 7, 0.285714286, 260, 230, 23.4, 13.86506401);

		private final String name;
		private final int total;
		private final double noamb;
		private final double avgN;
		private final double stdN;
		private final double avgA;
		private final double stdA;
		private PatientType(String name, int total, double noamb, double avgN, double stdN, double avgA, double stdA) {
			this.name = name;
			this.total = total;
			this.noamb = noamb;
			this.avgN = avgN; 
			this.stdN = stdN; 
			this.avgA = avgA; 
			this.stdA = stdA; 
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the total
		 */
		public int getTotal() {
			return total;
		}
		/**
		 * @return the noamb
		 */
		public double getNoamb() {
			return noamb;
		}
		/**
		 * @return the avgN
		 */
		public double getAvgN() {
			return avgN;
		}
		/**
		 * @return the stdN
		 */
		public double getStdN() {
			return stdN;
		}
		/**
		 * @return the avgA
		 */
		public double getAvgA() {
			return avgA;
		}
		/**
		 * @return the stdA
		 */
		public double getStdA() {
			return stdA;
		}

	}
	
	public SimGS(int id) {
		super(id, "General Surgery (" + id +")", 0.0, ENDTIME);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		
		// Tipos de Quirófanos (ambulantes y no ambulantes)
		ResourceType rtNoAmb = new ResourceType(0, this, "OROT");
		WorkGroup wgNoAmb = new WorkGroup(0, this, "ORWG");
		wgNoAmb.add(rtNoAmb, 1);
		ResourceType rtAmb = new ResourceType(1, this, "DCOT");
		WorkGroup wgAmb = new WorkGroup(1, this, "DCWG");
		wgAmb.add(rtAmb, 1);
		
		// Quirófanos
		for (OpTheatre op : OpTheatre.values()) {
			Resource r = new Resource(op.ordinal(), this, op.getName());
			if (op.isDaycase())
				r.addTimeTableEntry(op.getCycle(), AVAILABILITY, rtAmb);
			else
				r.addTimeTableEntry(op.getCycle(), AVAILABILITY, rtNoAmb);
		}
		
		// Activities, Element types, Generators
		for (PatientType pt : PatientType.values()) {
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio
			TimeFunction expo = TimeFunctionFactory.getInstance("ExponentialVariate", TOTALTIME / pt.getTotal());
			Cycle c = new RoundedPeriodicCycle(expo.getValue(0.0), expo, 0, RoundedPeriodicCycle.Type.ROUND, 1440.0);
			ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));

			if (pt.getNoamb() > 0.0) {
				ElementType etNoAmb = new ElementType(pt.ordinal(), this, pt.getName() + " OR");
				Activity actNoAmb = new Activity(pt.ordinal(), this, pt.getName());
				actNoAmb.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAvgN(), pt.getStdN()}), wgNoAmb);
				ec.add(etNoAmb, new SingleMetaFlow(pt.ordinal(), RandomVariateFactory.getInstance("ConstantVariate", 1), actNoAmb), pt.getNoamb());			
			}
			if (pt.getNoamb() < 1.0) {
				ElementType etAmb = new ElementType(pt.ordinal() + PatientType.values().length, this, "Patient " + pt.getName() + " DC");
				Activity actAmb = new Activity(pt.ordinal() + PatientType.values().length, this, "A" + pt.getName());				
				actAmb.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAvgA(), pt.getStdA()}), wgAmb);
				ec.add(etAmb, new SingleMetaFlow(pt.ordinal() + PatientType.values().length, RandomVariateFactory.getInstance("ConstantVariate", 1), actAmb), 1 - pt.getNoamb());
			}				
	        new TimeDrivenGenerator(this, ec, c);
		}
		
	}

}
