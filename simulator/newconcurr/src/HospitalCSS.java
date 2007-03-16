import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.random.Normal;
import es.ull.isaatc.random.Uniform;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationTimeListener;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * 
 */

class CSSSimulation extends Simulation {
	int npac;

	public CSSSimulation(double startTs, double endTs, int npac) {
		super("Hospital CSS Simulation", startTs, endTs, new Output(true));
		this.npac = npac;
	}

	@Override
	protected void createModel() {
		Activity actFun = new Activity(0, this, "Functional Out. App.");
		Activity actOnc = new Activity(1, this, "Oncological Out. App.");
		
		ResourceType crDoc = new ResourceType(0, this, "Doctor");
		ResourceType crSur = new ResourceType(1, this, "Surgery");
		
		WorkGroup wgAux;
		wgAux = actFun.getNewWorkGroup(0, new Normal(20.0, 5.0));
		wgAux.add(crDoc, 1);
		wgAux.add(crSur, 1);

		wgAux = actOnc.getNewWorkGroup(0, new Normal(18.0, 5.0));
		wgAux.add(crDoc, 1);
		wgAux.add(crSur, 1);
		
		PeriodicCycle c = new PeriodicCycle(480.0, new Fixed(1440.0), 0);
		new Resource(1, this, "Doctor 1").addTimeTableEntry(c, 360.0, crDoc);
		new Resource(2, this, "Doctor 2").addTimeTableEntry(c, 360.0, crDoc);
		new Resource(3, this, "Surgery 1").addTimeTableEntry(c, 360.0, crSur);
		new Resource(4, this, "Surgery 2").addTimeTableEntry(c, 360.0, crSur);
		
		ElementType etDiag = new ElementType(0, this, "Diagnosed Patient");
		ElementType etUndiag = new ElementType(1, this, "Undiagnosed Patient");
		
		TypeMetaFlow meta = new TypeMetaFlow(0, new Fixed(1));
		
		TypeBranchMetaFlow tbm1 = new TypeBranchMetaFlow(1, meta, etUndiag);
		SimultaneousMetaFlow sim = new SimultaneousMetaFlow(2, tbm1, new Fixed(1));
		new SingleMetaFlow(3, sim, new Fixed(1), actFun);
		new SingleMetaFlow(4, sim, new Fixed(1), actOnc);

		TypeBranchMetaFlow tbm2 = new TypeBranchMetaFlow(5, meta, etDiag);
		DecisionMetaFlow dm = new DecisionMetaFlow(6, tbm2, new Fixed(1));
		OptionMetaFlow om1 = new OptionMetaFlow(7, dm, 0.5);
		new SingleMetaFlow(10, om1, new Uniform(1, 4), actFun);
		OptionMetaFlow om2 = new OptionMetaFlow(11, dm, 0.5);
		new SingleMetaFlow(14, om2, new Uniform(1, 4), actOnc);
		
		ElementCreator ec = new ElementCreator(new Fixed(npac));
		ec.add(etDiag, meta, 0.5);
		ec.add(etUndiag, meta, 0.5);
		new TimeDrivenGenerator(this, ec, new PeriodicCycle(0.0, new Fixed(1440.0), 0).iterator(startTs, endTs));
	}
	
}

class CSSExperiment extends Experiment {
	private static int NEXP = 1;
	int ndays;
	int npac;

	public CSSExperiment(int ndays, int npac) {
		super("Hospital CSS", NEXP);
		this.ndays = ndays;
		this.npac = npac;
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		CSSSimulation sim = new CSSSimulation(0.0, ndays * 1440.0, npac);
		sim.addListener(new SimulationTimeListener() {			
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.println((endT - iniT) + " milliseconds");
			}			
		});
		return sim;
	}	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HospitalCSS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CSSExperiment(30, 15).start();
	}

}
