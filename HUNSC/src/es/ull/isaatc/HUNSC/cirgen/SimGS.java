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
	// Duración del día en minutos
	public static final double MINUTESXDAY = 1440.0;
	// Tiempo total del estudio: 3 años en minutos (un año bisiesto)
	public static final double TOTALTIME = 1096 * MINUTESXDAY;
	// Tiempo de uso diario de los quirófanos (7 horas)
	public static final double AVAILABILITY = 7 * 60.0;
	// Hora de apertura de los quirófanos (mañana: 8 am)
	public static final double STARTTIME = 8 * 60.0;
	
	public enum OpTheatreType {
		OR ("No ambulante", 487578.0),
		DC ("Ambulante", 25518.0);
		
		private final String name;
		private final double realUsage;
		private OpTheatreType(String name, double realUsage) {
			this.name = name;
			this.realUsage = realUsage;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the realUs
		 */
		public double getRealUsage() {
			return realUsage;
		}
	}
	public enum OpTheatre {
		Q31 ("3-1", OpTheatreType.OR, EnumSet.of(WeekDays.FRIDAY), 26850.0, 36110.0),
		AMB ("AMB", OpTheatreType.DC, EnumSet.of(WeekDays.MONDAY), 25842.0, 51755.0),
		Q45 ("4-5", OpTheatreType.OR, WeeklyPeriodicCycle.WEEKDAYS, 243235.0, 298125.0),
		Q46 ("4-6", OpTheatreType.OR, WeeklyPeriodicCycle.WEEKDAYS, 211208.0, 249900.0);
		
		private final String name;
		private final WeeklyPeriodicCycle cycle;
		private final OpTheatreType type;
		private final double realUsage;
		private final double realAva;
		private OpTheatre(String name, OpTheatreType type, EnumSet<WeekDays> days, double realUsage, double realAva) {
			this.name = name;
			this.type = type;
			cycle = new WeeklyPeriodicCycle(days, MINUTESXDAY, STARTTIME, 0);
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
		 * @return the type
		 */
		public OpTheatreType getType() {
			return type;
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
		D38 ("38", 1, 0, 90, 0, 0, 0),
		D78 ("78", 1, 46, 40, 0, 25.69565217, 10.37528327),
		D110 ("110", 0, 1, 0, 0, 40, 0),
		D122 ("122", 2, 0, 325, 15, 0, 0),
		D125 ("125", 1, 0, 20, 0, 0, 0),
		D150 ("150", 23, 0, 423.0434783, 180.8178229, 0, 0),
		D151 ("151", 48, 0, 348.4375, 148.3806909, 0, 0),
		D152 ("152", 5, 0, 386, 72.89718787, 0, 0),
		D153 ("153", 203, 1, 325.4876847, 100.8159988, 20, 0),
		D154 ("154", 153, 0, 350.2810458, 104.6217249, 0, 0),
		D155 ("155", 29, 0, 372.2413793, 163.8048998, 0, 0),
		D156 ("156", 20, 0, 340.65, 111.7408945, 0, 0),
		D157 ("157", 42, 0, 321.5476191, 111.837936, 0, 0),
		D158 ("158", 4, 0, 315, 14.57737974, 0, 0),
		D161 ("161", 3, 0, 86.66666667, 16.99673171, 0, 0),
		D171 ("171", 4, 0, 248.75, 174.7274664, 0, 0),
		D172 ("172", 48, 3, 142.875, 66.30379106, 51.66666667, 10.27402334),
		D173 ("173", 6, 12, 131.6666667, 62.76056795, 39.66666667, 9.65516557),
		D174 ("174", 2, 0, 87.5, 27.5, 0, 0),
		D175 ("175", 2, 0, 120, 10, 0, 0),
		D183 ("183", 9, 0, 297.7777778, 139.4057051, 0, 0),
		D186 ("186", 1, 0, 370, 0, 0, 0),
		D188 ("188", 1, 0, 120, 0, 0, 0),
		D189 ("189", 1, 0, 60, 0, 0, 0),
		D192 ("192", 1, 0, 145, 0, 0, 0),
		D193 ("193", 39, 0, 250.5128205, 88.03987203, 0, 0),
		D194 ("194", 4, 0, 396.25, 114.8572484, 0, 0),
		D195 ("195", 2, 0, 357.5, 92.5, 0, 0),
		D196 ("196", 7, 0, 318.5714286, 97.96917466, 0, 0),
		D197 ("197", 55, 0, 295.9090909, 106.9923146, 0, 0),
		D198 ("198", 5, 1, 260.6, 61.99548371, 55, 0),
		D199 ("199", 2, 0, 187.5, 77.5, 0, 0),
		D201 ("201", 1, 0, 225, 0, 0, 0),
		D202 ("202", 17, 2, 185.5882353, 87.41184388, 90.5, 40.5),
		D210 ("210", 1, 0, 180, 0, 0, 0),
		D211 ("211", 17, 0, 289.8823529, 96.19336063, 0, 0),
		D214 ("214", 2, 68, 82.5, 57.5, 41.5, 16.6817089),
		D215 ("215", 2, 6, 187.5, 82.5, 32.33333333, 9.44575154),
		D216 ("216", 1, 63, 45, 0, 32.38095238, 15.35239868),
		D217 ("217", 0, 2, 0, 0, 90, 10),
		D226 ("226", 7, 0, 197.1428571, 67.81577687, 0, 0),
		D227 ("227", 8, 0, 276.25, 68.9995471, 0, 0),
		D228 ("228", 5, 6, 293, 66.67833231, 35.33333333, 23.45681611),
		D229 ("229", 1, 9, 200, 0, 18.22222222, 6.82768511),
		D235 ("235", 100, 5, 290.41, 126.3356715, 40.4, 20.01599361),
		D236 ("236", 4, 0, 161.25, 54.92893136, 0, 0),
		D237 ("237", 9, 0, 172.7777778, 47.55763235, 0, 0),
		D238 ("238", 19, 18, 151.9473684, 146.0780158, 42.38888889, 22.70129436),
		D240 ("240", 7, 0, 237.8571429, 64.63397729, 0, 0),
		D241 ("241", 78, 0, 215.5384615, 75.97750887, 0, 0),
		D242 ("242", 10, 0, 244.5, 54.42655602, 0, 0),
		D246 ("246", 1, 0, 135, 0, 0, 0),
		D252 ("252", 9, 0, 236.6666667, 87.30533902, 0, 0),
		D255 ("255", 6, 0, 248.3333333, 91.18052911, 0, 0),
		D259 ("259", 1, 0, 110, 0, 0, 0),
		D275 ("275", 1, 0, 425, 0, 0, 0),
		D276 ("276", 1, 0, 255, 0, 0, 0),
		D278 ("278", 15, 0, 346.6666667, 102.0239623, 0, 0),
		D282 ("282", 1, 0, 255, 0, 0, 0),
		D287 ("287", 4, 0, 268.75, 82.64192338, 0, 0),
		D289 ("289", 2, 0, 345, 75, 0, 0),
		D359 ("359", 5, 14, 36.4, 13.63231455, 51, 9.18072515),
		D380 ("380", 0, 5, 0, 0, 25.8, 19.26032191),
		D441 ("441", 1, 0, 430, 0, 0, 0),
		D442 ("442", 1, 0, 230, 0, 0, 0),
		D446 ("446", 7, 1, 56.71428571, 18.14004478, 75, 0),
		D447 ("447", 4, 10, 147.5, 132.1221026, 49.4, 9.56242647),
		D454 ("454", 0, 2, 0, 0, 65, 5),
		D455 ("455", 4, 11, 98.75, 19.8037244, 34.45454545, 11.13849701),
		D457 ("457", 0, 1, 0, 0, 37, 0),
		D478 ("478", 1, 0, 180, 0, 0, 0),
		D518 ("518", 1, 0, 120, 0, 0, 0),
		D527 ("527", 0, 1, 0, 0, 42, 0),
		D530 ("530", 11, 1, 206.3636364, 101.0645813, 45, 0),
		D531 ("531", 1, 0, 110, 0, 0, 0),
		D532 ("532", 0, 1, 0, 0, 10, 0),
		D535 ("535", 1, 0, 115, 0, 0, 0),
		D537 ("537", 10, 0, 185.5, 110.3958786, 0, 0),
		D540 ("540", 13, 0, 87.53846154, 20.87551154, 0, 0),
		D542 ("542", 6, 0, 111.6666667, 41.59994658, 0, 0),
		D543 ("543", 1, 0, 390, 0, 0, 0),
		D550 ("550", 51, 1, 119.5098039, 49.539595, 15, 0),
		D552 ("552", 1, 0, 105, 0, 0, 0),
		D553 ("553", 93, 3, 177.4408602, 84.21522126, 76.66666667, 30.09245014),
		D555 ("555", 26, 0, 276.9230769, 137.4638199, 0, 0),
		D556 ("556", 7, 0, 333.5714286, 92.33921492, 0, 0),
		D558 ("558", 4, 0, 220.25, 69.60738107, 0, 0),
		D560 ("560", 30, 0, 210.5, 85.47270519, 0, 0),
		D562 ("562", 37, 0, 335.5945946, 94.2510906, 0, 0),
		D565 ("565", 16, 4, 95.4375, 51.60906988, 50.5, 20.69420209),
		D566 ("566", 2, 0, 47.5, 12.5, 0, 0),
		D567 ("567", 3, 0, 73.33333333, 30.91206165, 0, 0),
		D568 ("568", 5, 0, 200.4, 83.15431438, 0, 0),
		D569 ("569", 31, 5, 181.1290323, 112.7780412, 51, 27.45906044),
		D571 ("571", 3, 0, 406.6666667, 33.99346342, 0, 0),
		D573 ("573", 10, 0, 279, 76.67463727, 0, 0),
		D574 ("574", 293, 0, 144.8634812, 58.83518193, 0, 0),
		D575 ("575", 55, 0, 153.0909091, 63.65739912, 0, 0),
		D576 ("576", 36, 0, 282.5, 100.3708402, 0, 0),
		D577 ("577", 30, 0, 167.5, 84.33969014, 0, 0),
		D578 ("578", 1, 0, 210, 0, 0, 0),
		D585 ("585", 1, 0, 50, 0, 0, 0),
		D593 ("593", 1, 0, 360, 0, 0, 0),
		D596 ("596", 11, 0, 253.1818182, 102.4755565, 0, 0),
		D610 ("610", 0, 1, 0, 0, 10, 0),
		D611 ("611", 6, 3, 73.66666667, 13.98411798, 49.33333333, 11.72840806),
		D614 ("614", 1, 0, 70, 0, 0, 0),
		D617 ("617", 1, 0, 295, 0, 0, 0),
		D618 ("618", 2, 0, 207.5, 2.5, 0, 0),
		D619 ("619", 5, 0, 234, 109.4714575, 0, 0),
		D620 ("620", 1, 0, 180, 0, 0, 0),
		D681 ("681", 0, 2, 0, 0, 40, 10),
		D682 ("682", 7, 5, 56.42857143, 27.73783977, 32, 14.69693846),
		D685 ("685", 4, 5, 126.25, 56.72025652, 43.8, 20.35092136),
		D686 ("686", 6, 8, 89.16666667, 37.01538719, 52.125, 24.75094695),
		D692 ("692", 1, 0, 110, 0, 0, 0),
		D700 ("700", 0, 1, 0, 0, 9, 0),
		D701 ("701", 0, 8, 0, 0, 33, 14.25657743),
		D702 ("702", 0, 4, 0, 0, 34.25, 15.46568783),
		D703 ("703", 0, 50, 0, 0, 42.06, 15.81443644),
		D704 ("704", 0, 1, 0, 0, 22, 0),
		D705 ("705", 0, 12, 0, 0, 37, 13.26021619),
		D706 ("706", 7, 112, 63.14285714, 51.51262972, 38.94642857, 20.55647243),
		D707 ("707", 6, 3, 92.5, 64.46898479, 34, 7.87400787),
		D709 ("709", 2, 31, 100, 70, 44.09677419, 28.55030065),
		D710 ("710", 1, 10, 55, 0, 23, 9.2736185),
		D727 ("727", 1, 0, 95, 0, 0, 0),
		D728 ("728", 0, 1, 0, 0, 35, 0),
		D729 ("729", 3, 7, 55, 8.16496581, 41, 9.87059124),
		D740 ("740", 0, 1, 0, 0, 52, 0),
		D744 ("744", 0, 1, 0, 0, 30, 0),
		D757 ("757", 0, 5, 0, 0, 44.4, 26.67283262),
		D751 ("751", 1, 0, 300, 0, 0, 0),
		D752 ("752", 1, 0, 120, 0, 0, 0),
		D759 ("759", 0, 1, 0, 0, 60, 0),
		D782 ("782", 1, 4, 90, 0, 40.75, 5.62916512),
		D785 ("785", 64, 34, 80.8125, 55.30056368, 55.85294118, 25.220361),
		D787 ("787", 3, 0, 125, 28.57738033, 0, 0),
		D789 ("789", 18, 0, 202.5, 91.45809605, 0, 0),
		D879 ("879", 0, 3, 0, 0, 38.33333333, 26.56229575),
		D892 ("892", 0, 1, 0, 0, 25, 0),
		D897 ("897", 1, 0, 45, 0, 0, 0),
		D935 ("935", 1, 0, 475, 0, 0, 0),
		D936 ("936", 1, 0, 135, 0, 0, 0),
		D959 ("959", 1, 2, 170, 0, 27, 0),
		D996 ("996", 8, 4, 220.375, 96.38326813, 42.75, 11.58393284),
		D997 ("997", 6, 0, 279.1666667, 146.4415887, 0, 0),
		D998 ("998", 16, 11, 120.9375, 149.6268144, 41, 29.05480714),
		DV44 ("V44", 15, 0, 261.3333333, 117.6784697, 0, 0),
		DV55 ("V55", 46, 1, 248.673913, 116.0097549, 320, 0),
		DV58 ("V58", 2, 5, 260, 230, 23.4, 13.86506401);

		private final String name;
		private final int []total;
		private final double percOR;
		private final double []avg;
		private final double []std;
		private PatientType(String name, int totalOR, int totalDC, double avgOR, double stdOR, double avgDC, double stdDC) {
			this.name = name;
			this.total = new int[2];
			this.total[OpTheatreType.OR.ordinal()] = totalOR;
			this.total[OpTheatreType.DC.ordinal()] = totalDC;
			percOR = (double)totalOR / (double)(totalOR + totalDC);
			this.avg = new double[2];
			this.std = new double[2];
			this.avg[OpTheatreType.OR.ordinal()] = avgOR; 
			this.avg[OpTheatreType.DC.ordinal()] = avgDC; 
			this.std[OpTheatreType.OR.ordinal()] = stdOR; 
			this.std[OpTheatreType.DC.ordinal()] = stdDC; 
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
			return total[0] + total[1];
		}

		public int getTotal(OpTheatreType type) {
			return total[type.ordinal()];
		}

		public double getTotalTime(OpTheatreType type) {
			return total[type.ordinal()] * avg[type.ordinal()];
		}

		/**
		 * Returns the probability of a patient type of being Ordinary
		 * or daycase.
		 * @param type Ordinary or daycase.
		 * @return The probability of a patient type of being Ordinary or daycase.
		 */
		public double getProbability(OpTheatreType type) {
			if (type == OpTheatreType.OR)
				return percOR;
			return 1 - percOR;
		}
		
		/**
		 * 
		 * @param type Ordinary or daycase.
		 * @return
		 */
		public double getAverage(OpTheatreType type) {
			return avg[type.ordinal()];
		}
		/**
		 * @param type Ordinary or daycase.
		 * @return
		 */
		public double getStdDev(OpTheatreType type) {
			return std[type.ordinal()];
		}
		
		public int ordinal(OpTheatreType type) {
			if (type == OpTheatreType.OR)						
				return ordinal();
			return ordinal() + values().length;
		}
	}
	
	public SimGS(int id, double startTs, double endTs) {
		super(id, "General Surgery (" + id +")", startTs, endTs);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		
		// Tipos de Quirófanos (ambulantes y no ambulantes)
		for (OpTheatreType type : OpTheatreType.values()) {
			ResourceType rt = new ResourceType(type.ordinal(), this, type.getName());
			new WorkGroup(type.ordinal(), this, type.getName()).add(rt, 1);
		}
		WorkGroup wgNoAmb = getWorkGroup(OpTheatreType.OR.ordinal());
		WorkGroup wgAmb = getWorkGroup(OpTheatreType.DC.ordinal());
			
//		ResourceType rtNoAmb = new ResourceType(0, this, "OROT");
//		WorkGroup wgNoAmb = new WorkGroup(0, this, "ORWG");
//		wgNoAmb.add(rtNoAmb, 1);
//		ResourceType rtAmb = new ResourceType(1, this, "DCOT");
//		WorkGroup wgAmb = new WorkGroup(1, this, "DCWG");
//		wgAmb.add(rtAmb, 1);
		
		// Quirófanos
		for (OpTheatre op : OpTheatre.values()) {
			Resource r = new Resource(op.ordinal(), this, op.getName());
			r.addTimeTableEntry(op.getCycle(), AVAILABILITY, getResourceType(op.getType().ordinal()));
		}
		
		// Activities, Element types, Generators
		for (PatientType pt : PatientType.values()) {
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio
			TimeFunction expo = TimeFunctionFactory.getInstance("ExponentialVariate", TOTALTIME / pt.getTotal());
			Cycle c = new RoundedPeriodicCycle(expo.getValue(0.0), expo, 0, RoundedPeriodicCycle.Type.ROUND, MINUTESXDAY);
			ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));

			if (pt.getTotal(OpTheatreType.OR) > 0) {
				ElementType etOR = new ElementType(pt.ordinal(OpTheatreType.OR), this, pt.getName() + " OR");
				Activity actOR = new Activity(pt.ordinal(OpTheatreType.OR), this, pt.getName() + " OR");
				actOR.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAverage(OpTheatreType.OR), pt.getStdDev(OpTheatreType.OR)}), wgNoAmb);
//				actNoAmb.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAvgOR(), pt.getStdOR()}), getWorkGroup(OpTheatreType.OR.ordinal()));
				ec.add(etOR, new SingleMetaFlow(pt.ordinal(OpTheatreType.OR), RandomVariateFactory.getInstance("ConstantVariate", 1), actOR), pt.getProbability(OpTheatreType.OR));
			}
			if (pt.getTotal(OpTheatreType.DC) > 0) {
				ElementType etDC = new ElementType(pt.ordinal(OpTheatreType.DC), this, "Patient " + pt.getName() + " DC");
				Activity actDC = new Activity(pt.ordinal(OpTheatreType.DC), this, "A" + pt.getName() + " DC");
				actDC.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAverage(OpTheatreType.DC), pt.getStdDev(OpTheatreType.DC)}), wgAmb);
//				actAmb.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", new Object[] {pt.getAvgDC(), pt.getStdDC()}), getWorkGroup(OpTheatreType.DC.ordinal()));
				ec.add(etDC, new SingleMetaFlow(pt.ordinal(OpTheatreType.DC), RandomVariateFactory.getInstance("ConstantVariate", 1), actDC), pt.getProbability(OpTheatreType.DC));
			}				
	        new TimeDrivenGenerator(this, ec, c);
		}
		
	}

}
