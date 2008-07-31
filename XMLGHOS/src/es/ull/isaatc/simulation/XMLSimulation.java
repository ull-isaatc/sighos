package es.ull.isaatc.simulation;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * @author Roberto Muñoz
 *
 */
public class XMLSimulation extends StandAloneLPSimulation {

	/** XML model description in memory */
	private es.ull.isaatc.simulation.xml.Model xmlModel;
	/** XML experiment description in memory */
	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;
	/** Base time index in timeRelations */
	private int baseTimeIndex = 0;
	/** Generator generator */
	private GeneratorTransformer genTransformer;
	
	private TreeMap<Integer, WorkGroup> wgList;
	
	/**
	 * Creates the simulation from the data stored in <code>xmlWrapper<code>.
	 * @param xmlWrapper This object stores the XML description of the model and the experiment.
	 * @param out
	 */
	public XMLSimulation(int id, es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper) {

		super(id, xmlWrapper.getModel().getDescription());
		xmlModel = xmlWrapper.getModel();
		xmlExperiment = xmlWrapper.getExperiment();
		baseTimeIndex = XMLSimulationFactory.getTimeUnit(xmlModel.getBaseTimeUnit(), baseTimeIndex);
		genTransformer = new GeneratorTransformer(this);
		wgList = new TreeMap<Integer, WorkGroup>();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		createResourceTypes();
		createWorkGroups();
		createResources();
		createActivities();
		createElementTypes();
		createGenerators();
	}

	/**
	 * Creates the resource types of the model
	 */
	protected void createResourceTypes() {
		for (es.ull.isaatc.simulation.xml.ResourceType rtXML : xmlModel.getResourceType())
			XMLSimulationFactory.getResourceType(this, rtXML);
	}

	/**
	 * Creates the work groups of the model
	 */
	protected void createWorkGroups() {
		for (es.ull.isaatc.simulation.xml.WorkGroup wgXML : xmlModel.getWorkGroup()) {
			WorkGroup wg = XMLSimulationFactory.getWorkGroup(this, wgXML);
			wgList.put(wg.getIdentifier(), wg);
		}
	}
	
	/**
	 * Creates the resources of the model
	 */
	protected void createResources() {
		for (es.ull.isaatc.simulation.xml.Resource resXML : xmlModel.getResource()) {
			XMLSimulationFactory.getResource(this, resXML, baseTimeIndex);
		}
	}
	
	/**
	 * Creates the activities of the model
	 */
	protected void createActivities() {
		for (es.ull.isaatc.simulation.xml.Activity actXML : xmlModel.getActivity()) {
			XMLSimulationFactory.getActivity(this, actXML, baseTimeIndex);
		}
	}
	
	/**
	 * Creates the model ElementTypes.
	 */
	void createElementTypes() {
		for (es.ull.isaatc.simulation.xml.ElementType etXML : xmlModel.getElementType())
			XMLSimulationFactory.getElementType(this, etXML);
		}

	/**
	 * Creates the generators for the simulation
	 */
	protected void createGenerators() {
		genTransformer.createGenerators();
	}
	
	/**
	 * @return the xmlModel
	 */
	public es.ull.isaatc.simulation.xml.Model getXmlModel() {
		return xmlModel;
	}

	/**
	 * @return the xmlExperiment
	 */
	public es.ull.isaatc.simulation.xml.Experiment getXmlExperiment() {
		return xmlExperiment;
	}

	public WorkGroup getWorkGroup(int id) {
		return wgList.get(id);
	}
	
	/**
	 * @return the baseTimeIndex
	 */
	public int getBaseTimeIndex() {
		return baseTimeIndex;
	}
	
	public HashMap<Integer, MetaFlow> getFlowList() {
		return genTransformer.getFlowList();
	}
}
