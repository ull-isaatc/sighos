/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;

import simkit.random.RandomVariateFactory;

import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.ElemIndispTimeListener;
import es.ull.isaatc.simulation.info.ElementStartFinishListener;
import es.ull.isaatc.simulation.info.PeriodicActivityQueueListener;
import es.ull.isaatc.simulation.info.PeriodicActivityUsageListener;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationTimeListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

class SimZaragoza extends StandAloneLPSimulation {
	int npatients;
	int nres;
	
	public SimZaragoza(double startTs, double endTs, int npatients, int nres, Output out) {
		super("Zaragoza Simulation", startTs, endTs, out);
		this.npatients = npatients;
		this.nres = nres;
	}

	@Override
	protected void createModel() {
		createEjemplo0();
	}
	protected void createEjemploRompeSimulador() {
		new ResourceType(0, this, "Doctor"); 
		WorkGroup wg1 = new WorkGroup(1, this, "");
		wg1.add(getResourceType(0), nres);
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 20), wg1);
		WorkGroup wg2 = new WorkGroup(2, this, ""); 
		wg2.add(getResourceType(0), 1);
		new Activity(1, this, "Subsequent Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 15), wg2);
		
		new ElementType(0, this, "Patient");
		
		PeriodicCycle c1 = new PeriodicCycle(480.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
		for (int i = 0; i < nres; i++)
			new Resource(i, this, "Doctor " + (i + 1)).addTimeTableEntry(c1, 399.0, getResourceType(0));
		
        PeriodicCycle c2 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", 1), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
        PeriodicCycle c3 = new PeriodicCycle(481.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))), c3);
	}
	protected void createEjemplo0() {
		new ResourceType(0, this, "Doctor"); 
		WorkGroup wg1 = new WorkGroup(1, this, "");
		wg1.add(getResourceType(0), 1);
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 20), wg1);
		new Activity(1, this, "Subsequent Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 15), wg1);
		
		new ElementType(0, this, "Patient");
		
		PeriodicCycle c1 = new PeriodicCycle(480.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
		for (int i = 0; i < nres; i++)
			new Resource(i, this, "Doctor " + (i + 1)).addTimeTableEntry(c1, 399.0, getResourceType(0));
		
		SequenceMetaFlow sec = new SequenceMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1));
		new SingleMetaFlow(1, sec, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
		new SingleMetaFlow(2, sec, RandomVariateFactory.getInstance("ConstantVariate", 2), getActivity(1));
        PeriodicCycle c2 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", npatients), getElementType(0), sec), c2);
	}
	
	protected void createEjemplo1() {
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("ConstantVariate", 10));
		new ElementType(0, this, "Patient");
        PeriodicCycle c2 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
	
	}
	protected void createEjemplo2() {
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(RandomVariateFactory.getInstance("NormalVariate", 20, 5));
		new ElementType(0, this, "Patient");
        PeriodicCycle c2 = new PeriodicCycle(0.0, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(RandomVariateFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
	
	}
}

class ZaragozaTimeListener extends SimulationTimeListener {
	FileWriter file;

	public ZaragozaTimeListener(FileWriter file) {
		super();
		this.file = file;
	}
	
	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		try {
			file.write(this.toString() + "\r\n");
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}						
	}
}

class ExpZaragoza extends Experiment {
	final static int NEXP = 1;
	final static int NDAYS = 10;
	final static double STARTTS = 0.0;
	final static double ENDTS = NDAYS * 1440.0;
	final static int NPATIENTS = 10;
	final static int NRES = 4;
	FileWriter file;
	ZaragozaTimeListener ztList;
//	ThreadedOutputStreamWriter out = null;
	
	public ExpZaragoza() {
		super("Zaragoza", NEXP);
		try {
			file = new FileWriter("C:\\ZaragozaTimes.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ztList = new ZaragozaTimeListener(file);
//		out = new ThreadedOutputStreamWriter(System.out);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new SimZaragoza(STARTTS, ENDTS, NPATIENTS, NRES, new Output());
//		sim.addListener(new StdInfoListener());
		sim.addListener(new SimulationTimeListener() {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.println(this);
			}
		});
//		sim.addListener(new PeriodicActivityUsageListener(1440.0) {
//		@Override
//		public void infoEmited(SimulationEndInfo info) {
//			super.infoEmited(info);
//			System.out.println(this);
//		}
//	});
//		sim.addListener(new PeriodicActivityUsageListener(1440.0) {
//			@Override
//			public void infoEmited(SimulationEndInfo info) {
//				super.infoEmited(info);
//				System.out.println(this);
//			}
//		});
//		sim.addListener(new ElemIndispTimeListener(1440.0) {
//			@Override
//			public void infoEmited(SimulationEndInfo info) {
//				super.infoEmited(info);
//				System.out.println(this);
//			}
//		});
//		sim.addListener(new ElementStartFinishListener() {
//			@Override
//			public void infoEmited(SimulationEndInfo info) {
//				super.infoEmited(info);
//				System.out.println(this);
//			}
//		});
		
//		sim.addListener(new SimulationTimeListener() {
//			@Override
//			public void infoEmited(SimulationEndInfo info) {
//				super.infoEmited(info);
//				System.out.println(this);
//			}
//		});
		
//		sim.addListener(ztList);
		return sim;
	}
	
	protected void end() {
//		if (out != null)
//			out.stop();
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Zaragoza {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpZaragoza().start();

	}

}
