/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.random.Normal;
import es.ull.isaatc.random.Uniform;
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

class SimZaragoza extends Simulation {
	int npatients;
	int nres;
	
	public SimZaragoza(double startTs, double endTs, int npatients, int nres, Output out) {
		super("Zaragoza Simulation", startTs, endTs, out);
		this.npatients = npatients;
		this.nres = nres;
	}

	@Override
	protected void createModel() {
		createEjemplo2();
	}
	protected void createEjemplo0() {
		new ResourceType(0, this, "Doctor"); 
		WorkGroup wg1 = new Activity(0, this, "First Outpatient Appointment").getNewWorkGroup(0, new Fixed(20));
		wg1.add(getResourceType(0), 1);
		WorkGroup wg2 = new Activity(1, this, "Subsequent Outpatient Appointment").getNewWorkGroup(1, new Fixed(15));
		wg2.add(getResourceType(0), 1);
		
		new ElementType(0, this, "Patient");
		
		PeriodicCycle c1 = new PeriodicCycle(480.0, new Fixed(1440.0), 0);
		for (int i = 0; i < nres; i++)
			new Resource(i, this, "Doctor " + (i + 1)).addTimeTableEntry(c1, 399.0, getResourceType(0));
		
		SequenceMetaFlow sec = new SequenceMetaFlow(1, new Fixed(1));
		new SingleMetaFlow(1, sec, new Fixed(1), getActivity(0));
		new SingleMetaFlow(2, sec, new Fixed(2), getActivity(1));
        PeriodicCycle c2 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(npatients), getElementType(0), sec), c2.iterator(startTs, endTs));
	}
	
	protected void createEjemplo1() {
		new Activity(0, this, "First Outpatient Appointment").getNewWorkGroup(0, new Fixed(10));
		new ElementType(0, this, "Patient");
        PeriodicCycle c2 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(npatients), getElementType(0), new SingleMetaFlow(1, new Fixed(1), getActivity(0))), c2.iterator(startTs, endTs));
	
	}
	protected void createEjemplo2() {
		new Activity(0, this, "First Outpatient Appointment").getNewWorkGroup(0, new Normal(20, 25));
		new ElementType(0, this, "Patient");
        PeriodicCycle c2 = new PeriodicCycle(0.0, new Fixed(1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(new Fixed(npatients), getElementType(0), new SingleMetaFlow(1, new Fixed(1), getActivity(0))), c2.iterator(startTs, endTs));
	
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
	final static int NPATIENTS = 100;
	final static int NRES = 3;
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
