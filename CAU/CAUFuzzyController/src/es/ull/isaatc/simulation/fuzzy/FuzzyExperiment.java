/**
 * 
 */
package es.ull.isaatc.simulation.fuzzy;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.XMLExperiment;
import es.ull.isaatc.simulation.XMLSimulation;
import es.ull.isaatc.simulation.XMLSimulationFactory;
import es.ull.isaatc.simulation.fuzzy.xml.ProgrammedTasks;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler;
import es.ull.isaatc.simulation.fuzzy.xml.Sampler.Task;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationTimeListener;
import es.ull.isaatc.simulation.xml.XMLWrapper;
import es.ull.isaatc.util.Cycle;

/**
 * Creates a simulation model and uses fuzzy controllers for simulating
 * the behaviour of the management staff.
 * @author Roberto Muñoz
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
		XMLSimulation simul = (XMLSimulation) super.getSimulation(ind);
		createSamplers(simul, xmlWrapper);
		return simul;
	}
	
	/**
	 * Creates and adds the listeners that sample the simulation
	 * @param simul the simulation
	 * @param xmlModel the model described in XML
	 */
	public void createSamplers(XMLSimulation simul, XMLWrapper xmlModel) {
		for (Sampler xmlSampler : xmlProgTask.getSampler())
			simul.addListener(createSampler(simul, xmlSampler));
	}
	
	/**
	 * Creates a simulation listener for the fuzzy controller.
	 * @param simul the simulation
	 * @param xmlSampler the sampler described in XML
	 * @return the FuzzyControllerListener
	 */
	public SimulationListener createSampler(XMLSimulation simul, Sampler xmlSampler) {
		Cycle cycle = XMLSimulationFactory.createCycle(xmlSampler.getCycle(), simul.getBaseTimeIndex());
		int[] actId = new int[xmlSampler.getActQueue().size()];
		
		for (int i = 0; i < xmlSampler.getActQueue().size(); i++)
			actId[i] = xmlSampler.getActQueue().get(i).getId();
		
		FuzzyControllerListener listener = new FuzzyControllerListener(
				simul,
				xmlSampler.getFilename(),
				cycle.iterator(startTs, endTs),
				xmlSampler.getPeriod(),
				actId);
		for (Task task : xmlSampler.getTask()) {
			listener.addTask(task.getDescription(),
					task.getElementType().getId(),
					task.getMetaFlow().getId(),
					XMLSimulationFactory.createCycle(task.getCycle(), simul.getBaseTimeIndex()),
					task.getQos());
		}
		new TimeDrivenGenerator(simul, listener, cycle);
		return listener;
	}
}
