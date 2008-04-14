package es.ull.isaatc.HUNSC.quiBasico;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariateFactory;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.*;
import es.ull.isaatc.simulation.listener.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

class SimSurgeries extends StandAloneLPSimulation {
	public enum PatientType {
//		ACMF ("ACMF", "LogisticVariate", new Object[] {35.6895, 9.9821}, 2032),
		ACMF ("ACMF", "GammaVariate", new Object[] {4.29512, 8.76201}, 2032),
		AUDO ("AUDO", "GeneralizedExtremeValueVariate", new Object[] {0.454607, 14.8539, 33.0558}, 356),
		URO ("AURO_HURO", "InverseGaussian2Variate", new Object[] {76.1942, 103.581}, 2462),
		HACV ("HACV", "InverseGaussian2Variate", new Object[] {112.336, 138.588}, 873),
		HCAR ("HCAR", "GeneralizedExtremeValueVariate", new Object[] {0.243972, 41.4997, 74.8847}, 1071),
		HCG ("HCG1-4", "InverseGaussian2Variate", new Object[] {133.92, 109.848}, 4648),
//		HCMF ("HCMF", "FileResampleVariate", new Object[] {"resources\\HCMF.res"}, 1192),
		HCMFl80 ("HCMF lower 80", "InverseGaussian2Variate", new Object[] {52.84, 171.541}, 100),
		HCMFh80 ("HCMF higher 80", "GeneralizedExtremeValueVariate", new Object[] {0.547589, 45.0124, 69.9042}, 1092),
		HCPE ("HCPE", "GeneralizedExtremeValueVariate", new Object[] {0.292465, 24.8478, 46.2618}, 2218),
		HCTO ("HCTO", "InverseGaussian2Variate", new Object[] {133.737, 127.75}, 604),
		HDER ("HDER", "LogLogisticVariate", new Object[] {3.29634, 0.292091}, 2084),
//		HGIN ("HGIN", "FileResampleVariate", new Object[] {"resources\\HGIN.res"}, 4739),
		HGINl50 ("HGIN lower 50", "LogLogisticVariate", new Object[] {3.57109, 0.258699}, 2499),
		HGIN50_100 ("HGIN higher 50 lower 100", "InverseGaussian2Variate", new Object[] {70.7972, 203.265}, 1124),
		HGINh100 ("HGIN higher 100", "LogLogisticVariate", new Object[] {4.69034, 0.250766}, 1116),
		HNCR ("HNCR", "GammaVariate", new Object[] {2.4353, 80.411}, 708),
		HOBS ("HOBS", "LogLogisticVariate", new Object[] {4.32637, 0.184009}, 1839),
		HOC1h50 ("HOC1 higher 50", "LogisticVariate", new Object[] {78.3602, 13.2464}, 975),
		HOC1l50 ("HOC1 lower 50", "LogLogisticVariate", new Object[] {2.98869, 0.204257}, 2225),
		HOF ("HOFT_HOFI", "GammaVariate", new Object[] {2.60921, 23.6465}, 2285),
		HOOF366 ("HOOF366", "LogLogisticVariate", new Object[] {4.31175, 0.092785}, 1341),
		HOOFrem ("HOOFrem", "LogNormalVariate", new Object[] {3.36325, 0.380817}, 516),
		HOR ("HORL_HORI", "LogLogisticVariate", new Object[] {4.21749, 0.348936}, 1439),
		HTR ("HTR1-4_HTRI", "GammaVariate", new Object[] {2.32862, 36.7782}, 5602);
//		HTR ("HTR1-4_HTRI", "FileResampleVariate", new Object[] {"resources\\HTR.res"}, 5602);
//		HTRl50 ("HTR lower 50", "LogLogisticVariate", new Object[] {3.5025, 0.30018}, 1163),
//		HTR50_100 ("HTR50-100", "GeneralizedExtremeValueVariate", new Object[] {0.0453183, 30.5575, 57.954}, 2507),
//		HTRh100 ("HTR higher 100", "LogLogisticVariate", new Object[] {4.71793, 0.32415}, 1932);
				
		private final String name;
		private final String dist;
		private final Object []param;
		private final int samples;
		private PatientType(String name, String dist, Object []param, int samples) {
			this.name = name;
			this.dist = dist;
			this.param = param;
			this.samples = samples;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the dist
		 */
		public String getDist() {
			return dist;
		}
		/**
		 * @return the param
		 */
		public Object[] getParam() {
			return param;
		}
		/**
		 * @return the samples
		 */
		public int getSamples() {
			return samples;
		}
	}
	// Tiempo total del estudio: 2 años en minutos (un año bisiesto)
	public static final double TOTALTIME = 731 * 1440.0;
	// Número de quirófanos disponibles
	public static final int NSURGERIES = 9;
	// Número de quirófanos con horario de tarde disponibles
	public static final int NXSURGERIES = 1;
	
	public SimSurgeries(int id) {
		super(id, "HUNSC Surgeries (" + id +")", 0.0, TOTALTIME);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		// Primero suponemos un único tipo de quirófano
		ResourceType surgery = new ResourceType(0, this, "Surgery");
		WorkGroup wgSurgery = new WorkGroup(0, this, "Surgery WG");
		wgSurgery.add(surgery, 1);
		
		// Activities, Element types, Generators
		for (PatientType pt : PatientType.values()) {
			Activity act = new Activity(pt.ordinal(), this, pt.getName());
			act.addWorkGroup(TimeFunctionFactory.getInstance(pt.getDist(), pt.getParam()), wgSurgery);
			ElementType et = new ElementType(pt.ordinal(), this, "Patient " + pt.getName());
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio (2 años)
			TimeFunction expo = TimeFunctionFactory.getInstance("ExponentialVariate", TOTALTIME / pt.getSamples());
			Cycle c = new PeriodicCycle(expo.getValue(0.0), expo, 0);
			ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, new SingleMetaFlow(pt.ordinal(), RandomVariateFactory.getInstance("ConstantVariate", 1), act));
	        new TimeDrivenGenerator(this, ec, c);
		}
		
		// Resources: Surgeries. Start at 8 am and finish at 3 pm
		PeriodicCycle c1 = new PeriodicCycle(480.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0);
		for (int i = 0; i < NSURGERIES; i++) {
			Resource r = new Resource(i, this, "Surgery " + i);
			r.addTimeTableEntry(c1, 420.0, surgery);
		}
		// Extra resources: Surgeries. Start at 4 pm finish after 165 minutes every 2 days
		PeriodicCycle c2 = new PeriodicCycle(960.0, TimeFunctionFactory.getInstance("ConstantVariate", 2880.0), 0);
		for (int i = 0; i < NXSURGERIES; i++) {
			Resource r = new Resource(i + NSURGERIES, this, "XSurgery " + i);
			r.addTimeTableEntry(c2, 165.0, surgery);
		}
	}
}

class HUNSCListener extends StatisticListener {
	FileWriter fileRes = null;

	public HUNSCListener(double period, FileWriter fileRes) {
		super(period);
		this.fileRes = fileRes;
	}

	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		try {
			fileRes.write((getEndT() - getIniT()) + "\t" + getNStartedElem() + "\r\n");
			for (int i = 4; i < getNPeriods(); i = i + 8) {
				for (int[] values: getActQueues().values()) {
					fileRes.write(values[i] + "\t");
					fileRes.flush();					
				}
				fileRes.write("\r\n");
				fileRes.flush();
			}
//			for (int[] values : getActQueues().values()) {
//				for (int val : values) {
//					fileRes.write(val + " ");
//					fileRes.flush();
//				}
//				fileRes.write("\r\n");
//				fileRes.flush();
//			}			
			fileRes.write("\r\n");
			fileRes.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class HUNSCListener2 extends ActivityTimeListener {
	FileWriter fileRes1 = null;

	public HUNSCListener2(double period, FileWriter fileRes1) {
		super(period);
		this.fileRes1 = fileRes1;
	}

	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		double result[] = new double[getNumberOfPeriods()];
		try {
			for (double[] values : getActUsage().values())
				for (int j = 0; j < values.length; j++)
					result[j] += values[j];
			for (double val : result) {
				fileRes1.write(val + " ");
				fileRes1.flush();
			}
			fileRes1.write("\r\n");
			fileRes1.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class HUNSCListener3 implements SimulationListener, SimulationObjectListener {
	FileWriter fileRes = null;
	HashMap<Integer, ElementEntry> list;
	
	public HUNSCListener3 (String fileName) {
		list = new HashMap<Integer, ElementEntry>();		
		try {
			this.fileRes = new FileWriter(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ElementEntry {
		int etId;
		double reqActTs = Double.NaN;
		double staActTs = Double.NaN;
		double endActTs = Double.NaN;
		
		/**
		 * @param etId
		 */
		public ElementEntry(int etId) {
			this.etId = etId;
		}
		
	}

	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo eInfo = (ElementInfo)info;
			switch(eInfo.getType()) {
				case START:
					list.put(new Integer(eInfo.getIdentifier()), new ElementEntry(eInfo.getValue()));
					break;
				case REQACT:
					list.get(eInfo.getIdentifier()).reqActTs = eInfo.getTs();
					break;
				case STAACT:
					list.get(eInfo.getIdentifier()).staActTs = eInfo.getTs();
					break;
				case ENDACT:
					list.get(eInfo.getIdentifier()).endActTs = eInfo.getTs();
					break;
			}
		}
	}

	public void infoEmited(SimulationStartInfo info) {
		// Nothing special to do
	}

	public void infoEmited(SimulationEndInfo info) {
		try {
			fileRes.write("E\tET\tREQ\tSTA\tEND\r\n");
			fileRes.flush();
			for (Map.Entry<Integer, ElementEntry> entry : list.entrySet()) {
				fileRes.write(entry.getKey() + "\t" + entry.getValue().etId + "\t" + entry.getValue().reqActTs
						 + "\t" + entry.getValue().staActTs + "\t" + entry.getValue().endActTs + "\r\n");
				fileRes.flush();
			}
			fileRes.write("\r\n");
			fileRes.flush();			
			fileRes.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void infoEmited(TimeChangeInfo info) {
	}

}

// Escucha cada 3 horas, pero solo muestra el resultado a las 15 y a las 24
// Además muestra directamente la suma de las actividades
class HUNSCListener4 extends ActivityListener {
	FileWriter fileRes = null;

	public HUNSCListener4(double period, String fileName) {
		super(period);
		try {
			this.fileRes = new FileWriter(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		try {
			for (int i = 7; i < getNumberOfPeriods(); i = i + 24) {
				int res15 = 0; 
				int res8 = 0; 
				for (int[] values: getActQueues().values()) {
					res8 += values[i];
					res15 += values[i + 7];
				}
				fileRes.write(res8 + "\t" + res15 + "\r\n");
				fileRes.flush();
			}
			fileRes.write("\r\n");
			fileRes.flush();
			fileRes.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class HUNSCListener5 extends ResourceUsageListener implements SimulationListener {

	public HUNSCListener5() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.listener.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		System.out.println(this.toString());		
	}
	
}
/**
 * Experimentos de validación. La unidad de tiempo es el minuto.
 */
class ExpSurgeries extends Experiment {
	final static int NEXP = 10;
	
	ExpSurgeries() {
		super("Validation HOFT", NEXP);
	}
	
	public Simulation getSimulation(int ind) {		
		SimSurgeries sim = new SimSurgeries(ind);
		ListenerController cont = new ListenerController() {
			@Override
			public void end() {
				super.end();
				for (String str : getListenerResults()) {
					System.out.println(str);
				}
			}
		};
		sim.setListenerController(cont);
//		sim.setOutput(new Output(true));
//		cont.addListener(new StdInfoListener());
//		cont.addListener(new HUNSCListener5());
//		cont.addListener(new HUNSCListener4(1, "C:\\Users\\Iván\\Documents\\HC\\res" + (ind) + ".txt"));
//		cont.addListener(new HUNSCListener3("C:\\Users\\Iván\\Documents\\HC\\Elements" + ind + ".txt"));
		cont.addListener(new ElementTypeTimeListener(SimSurgeries.TOTALTIME));
//		cont.addListener(new HUNSCListener5());
		return sim;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Experiment#start()
	 */
	@Override
	public void start() {
		for (int i = 0; i < NEXP; i++) {
			Simulation sim = getSimulation(i);
			sim.call();
		}
			
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HUNSCValidation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpSurgeries().start();
	}

}
