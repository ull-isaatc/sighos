/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.fuzzy.xml.ProgrammedTasks;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler.Task;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.xml.ModelCreator;
import es.ull.isaatc.simulation.xml.XMLExperiment;
import es.ull.isaatc.simulation.xml.XMLModel;
import es.ull.isaatc.util.Cycle;

/**
 * @author Roberto Muñoz
 *
 */
public class FuzzyExperiment extends XMLExperiment {

	protected ProgrammedTasks xmlProgTask;
	/**
	 * @param xmlModel
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
