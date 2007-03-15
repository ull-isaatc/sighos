/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.fuzzy.xml.ProgrammedTasks;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler.Task;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationTimeListener;
import es.ull.isaatc.simulation.xml.ModelCreator;
import es.ull.isaatc.simulation.xml.XMLExperiment;
import es.ull.isaatc.simulation.xml.XMLModel;
import es.ull.isaatc.util.Cycle;

/**
 * Creates a simulation model and uses fuzzy controllers for simulating
 * the behaviour of the management staff.
 * @author Roberto Mu�oz
 */
public class FuzzyExperiment extends XMLExperiment {

	protected ProgrammedTasks xmlProgTask;
	
	/**
	 * Initialize the experiment 
	 * @param xmlModel XML model description
	 */
	public FuzzyExperiment(XMLFuzzyModel xmlModel) {
		super(xmlModel);
		this.xmlProgTask = xmlModel.getXmlProgTasks();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.xml.XMLExperiment#getSimulation(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		ModelCreator simul = (ModelCreator) super.getSimulation(ind);
		createSamplers(simul, xmlModel);
		simul.addListener(new SimulationTimeListener() {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				// TODO Auto-generated method stub
				super.infoEmited(info);
				System.out.println(this);
			}
		});
		return simul;
	}
	
	/**
	 * Creates and adds the listeners that sample the simulation
	 * @param simul the simulation
	 * @param xmlModel the model described in XML
	 */
	public void createSamplers(ModelCreator simul, XMLModel xmlModel) {
		for (Sampler xmlSampler : xmlProgTask.getSampler())
			simul.addListener(createSampler(simul, xmlSampler));
	}
	
	/**
	 * Creates a simulation listener for the fuzzy controller.
	 * @param simul the simulation
	 * @param xmlSampler the sampler described in XML
	 * @return the FuzzyControllerListener
	 */
	public SimulationListener createSampler(ModelCreator simul, Sampler xmlSampler) {
		Cycle cycle = simul.createCycle(xmlSampler.getCycle()); 
		FuzzyControllerListener listener = new FuzzyControllerListener(
				simul,
				cycle.iterator(simul.getStartTs(), simul.getEndTs()),
				xmlSampler.getPeriod(),
				xmlSampler.getActQueue().getId(),
				xmlSampler.getFilename());
		for (Task task : xmlSampler.getTask()) {
			listener.addTask(task.getDescription(),
					task.getElementType().getId(),
					task.getMetaFlow().getId(),
					simul.createCycle(task.getCycle()),
					task.getQos());
		}
		new TimeDrivenGenerator(simul, listener, cycle.iterator(simul.getStartTs(), simul.getEndTs()));
		return listener;
	}
}
