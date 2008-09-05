/**
 * 
 */
import java.io.FileWriter;
import java.io.IOException;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.SequenceMetaFlow;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;

class SimZaragoza extends StandAloneLPSimulation {
	int npatients;
	int nres;
	
	public SimZaragoza(int id, int npatients, int nres, SimulationTime startTs, SimulationTime endTs) {
		super(id, "Zaragoza Simulation", SimulationTimeUnit.MINUTE, startTs, endTs);
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
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 20), wg1);
		WorkGroup wg2 = new WorkGroup(2, this, ""); 
		wg2.add(getResourceType(0), 1);
		new Activity(1, this, "Subsequent Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 15), wg2);
		
		new ElementType(0, this, "Patient");
		
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		for (int i = 0; i < nres; i++)
			new Resource(i, this, "Doctor " + (i + 1)).addTimeTableEntry(c1, new SimulationTime(SimulationTimeUnit.MINUTE, 399.0), getResourceType(0));
		
//        SimulationCycle c2 = new SimulationPeriodicCycle(0.0, new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), 
        		new SimulationTimeFunction(this, "RandomFunction", RandomVariateFactory.getInstance("ConstantVariate", 1440.0))
        		, 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
        SimulationCycle c3 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 481.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))), c3);
	}
	protected void createEjemplo0() {
		new ResourceType(0, this, "Doctor"); 
		WorkGroup wg1 = new WorkGroup(1, this, "");
		wg1.add(getResourceType(0), 1);
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 20), wg1);
		new Activity(1, this, "Subsequent Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 15), wg1);
		
		new ElementType(0, this, "Patient");
		
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		for (int i = 0; i < nres; i++)
			new Resource(i, this, "Doctor " + (i + 1)).addTimeTableEntry(c1, new SimulationTime(SimulationTimeUnit.MINUTE, 399.0), getResourceType(0));
		
		SequenceMetaFlow sec = new SequenceMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1));
		new SingleMetaFlow(1, sec, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
		new SingleMetaFlow(2, sec, RandomVariateFactory.getInstance("ConstantVariate", 2), getActivity(1));
		SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", npatients), getElementType(0), sec), c2);
	}
	
	protected void createEjemplo1() {
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10));
		new ElementType(0, this, "Patient");
		SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
	
	}
	protected void createEjemplo2() {
		new Activity(0, this, "First Outpatient Appointment").addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 20, 5));
		new ElementType(0, this, "Patient");
		SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", npatients), getElementType(0), new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c2);
	
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

class ExpZaragoza extends PooledExperiment {
	final static int NEXP = 1;
	final static int NDAYS = 10;
	final static SimulationTime STARTTS = SimulationTime.getZero();
	final static SimulationTime ENDTS = new SimulationTime(SimulationTimeUnit.DAY, NDAYS);
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
		Simulation sim = new SimZaragoza(ind, NPATIENTS, NRES, STARTTS, ENDTS);
		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
		cont.addListener(new StdInfoListener());
		cont.addListener(new SimulationTimeListener() {
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
