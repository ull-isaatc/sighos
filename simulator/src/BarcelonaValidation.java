import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;

class SimBarcelona extends Simulation {
	static final int NDAYS = 365 * 3;
	static final double START = 0.0;
//	static final double END = 24 * 60.0 * NDAYS;
	// PASADO A HORAS
	static final double END = 24 * NDAYS;

	SimBarcelona(String description, Output out) {
		super(description, START, END, out);
	}
	
	protected void createModel() {
		String coddiag[] = {"078", "173", "190", "216", "238", "360", "361", "362", "364",
				"365", "366", "370", "371", "372", "373", "374", "375", "376", "378", "379",
				"743", "870", "871", "921", "996", "998", "v58" 
		};
		// Todos los que usan una Beta están cambiados porque no admite double
		RandomNumber durac[] = {
				new Normal(1430, 236), 
				new AddRandomNumber(new Fixed(1200), new Exponential(1730)),
				new AddRandomNumber(new Fixed(1800), new Weibull(1190, 0.347)),
				new Normal(1410, 700),
				new AddRandomNumber(new Fixed(900), new Exponential(1200)),
				new Normal(4510, 2000),				
				new Normal(6270, 2710),
				new Uniform(600, 8700),
				new AddRandomNumber(new Fixed(900), new Exponential(1710)),
				new Normal(4590, 2040),
				new Normal(3690, 1430),
				new Triangular(2100, 2870, 12900),
				new Normal(6170, 2270),
				new AddRandomNumber(new Fixed(300), new LogNormal(1220, 1070)),
				new AddRandomNumber(new Fixed(600), new Weibull(1590, 1.04)),
				new Normal(3560, 2010),
				new Uniform(1200, 11700),
				new Uniform(1500, 5400),
				new Normal(4610, 1940),
				new Normal(4880, 2230),
				new Uniform(1200, 4800),
				new Uniform(1800, 7500),
				new Normal(4520, 2480),
				new Uniform(1200, 5700),
				new Triangular(900, 3410, 12600),
				new AddRandomNumber(new Fixed(600), new Exponential(1950)),
				new AddRandomNumber(new Fixed(900), new Exponential(859)),
		};
		Activity []acts = new Activity[coddiag.length];
		ResourceType rt = new ResourceType(0, this, "HOFTSurgery");
//		for (int i = 0; i < coddiag.length; i++) {
//			acts[i] = new Activity(i, this, "Act" + coddiag[i]);
//			acts[i].getNewWorkGroup(0, new MultRandomNumber(durac[i], new Fixed(1 / 60.0))).add(rt, 1);
//		}
//		Cycle c1 = new Cycle(480, new Fixed(1440.0), 5);
//		Cycle c2 = new Cycle(0, new Fixed(1440.0 * 7), 0, c1);
//		Resource sur = new Resource(0, this, "Surgery");
//		sur.addTimeTableEntry(c2, 420, rt);
		
		// PASADO A HORAS
		for (int i = 0; i < coddiag.length; i++) {
			acts[i] = new Activity(i, this, "Act" + coddiag[i]);
			acts[i].getNewWorkGroup(0, new MultRandomNumber(durac[i], new Fixed(1 / 3600.0))).add(rt, 1);
			new ElementType(i, this, "ET" + coddiag[i]);
		}

		// Resources
		Cycle c1 = new Cycle(8, new Fixed(24.0), 5);
		Cycle c2 = new Cycle(0, new Fixed(24.0 * 7), 0, c1);
		new Resource(0, this, "Surgery").addTimeTableEntry(c2, 7, getResourceType(0));
		
		// Generators
//		Esto es con errores en los periodos => NO VALIDO
//		double periods[] = {84.03361345, 84.03361345, 182.8153565, 9.20, 54.94505495,
//				31.34796238, 3.30, 25.5102041, 33.22259136, 7.194244604, 0.77, 45.66210046,
//				19.92031873, 1.69, 11.17, 4.06, 20.66115702, 182.8153565, 31.34796238, 
//				5.09, 156.4945227, 121.8026797, 18.90359168, 156.4945227, 21.92982456, 
//				32.25806452, 24.93765586
//		};
//		Manteniendo ajuste para algunos datos => NO VALIDO
//		double periods[] = {91.25, 84.03361345, 182.8153565, 9.52173913, 54.94505495, 31.34796238, 
//				3.662207358, 25.5102041, 33.22259136, 7.194244604, 1.091724826, 45.66210046, 19.92031873, 
//				3.067226891, 11.52631579, 4.451219512, 20.66115702, 182.8153565, 31.34796238, 5.918918919,  
//				156.4945227, 121.8026797, 18.90359168, 156.4945227, 21.92982456, 32.25806452, 24.93765586
//		};
//		Suponiendo llegada uniforme de pacientes
//		double periods[] = {91.25, 84.23076923, 219, 9.52173913, 54.75, 31.28571429, 3.662207358, 
//				25.46511628, 49.77272727, 7.203947368, 1.091724826, 45.625, 19.90909091, 3.067226891, 
//				11.52631579, 4.451219512, 20.66037736, 182.5, 31.28571429, 5.918918919, 219, 182.5, 
//				18.87931034, 156.4285714, 21.9, 32.20588235, 24.88636364
//		};
//		Supuestamente el mejor ajuste
		double periods[] = {91.74311927, 84.03361345, 219, 9.52380952, 54.94505495, 31.34796238, 
				3.663003663, 25.5102041, 49.77272727, 7.194244604, 1.092896175, 45.66210046, 19.92031873, 
				3.067484663, 11.53402537, 4.464285714, 20.66115702, 182.5, 31.34796238, 5.917159763,  
				219, 182.5, 18.90359168, 156.4285714, 21.92982456, 32.25806452, 24.93765586
		};
		
		for (int i = 0; i < periods.length; i++) {
//			Exponential expo = new Exponential(periods[i] * 1440.0);
			// PASADO A HORAS
			Exponential expo = new Exponential(periods[i] * 24.0);
			Cycle c = new Cycle(expo.samplePositiveDouble(), expo, 0);
	        new ElementGenerator(this, new Fixed(1), c.iterator(startTs, endTs), getElementType(i), new SingleMetaFlow(i, new Fixed(1), getActivity(i)));
		}
	}
}

class BarcelonaListener extends StatisticListener {
	FileWriter fileRes = null;

	public BarcelonaListener(double period) {
		super(period);
		try {
			fileRes = new FileWriter("C:\\res.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showResults() {
		try {
			fileRes.write((getEndT() - getIniT()) + "\t" + getNStartedElem() + "\r\n");
			for (Map.Entry<Integer,int[]> values : getActQueues().entrySet()) {
				for (int j = 0; j < values.getValue().length; j++) {
					fileRes.write(values.getValue()[j] + " ");
					fileRes.flush();
				}
				fileRes.write("\r\n");
				fileRes.flush();
			}
			fileRes.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try {
//			FileWriter file = new FileWriter("C:\\diaryUse.txt");
//			double perUse[][] = new double[results.length][];
//			for (int i = 0; i < results.length; i++)
//				perUse[i] = results[i].periodicUse(24.0);
//			for (int i = 0; i < perUse[0].length; i++) {			
//				for (int j = 0; j < results.length; j++) {
//					file.write(perUse[j][i] + "\t");
//					file.flush();
//				}
//				file.write("\r\n");
//			}			
//			file.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
}
/**
 * 
 */
class ExpBarcelona extends Experiment {
	final static int NEXP = 20;
	
	ExpBarcelona() {
//		super("Validation HOFT", NEXP, new BarcelonaResultProcessor(360 * 1440.0), new Output(Output.NODEBUG));
		// PASADO A HORAS
		super("Validation HOFT", NEXP);
	}
	
	public Simulation getSimulation(int ind) {
		SimBarcelona sim = new SimBarcelona(description + ind + "", new Output(Output.DebugLevel.NODEBUG));
		sim.addListener(new BarcelonaListener(24 * 365.0));
		return sim;
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BarcelonaValidation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExpBarcelona exp = new ExpBarcelona();
		exp.start();
	}

}
