package es.ull.cyc.simulation.bind;
/**
 * XMLModel.java
 * 
 * Created on 13 February 2006
 */

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import es.ull.cyc.util.Output;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * Extends the bind.Model class. Load a model and a scenario stored in a XML file and
 * merge the content to create a model stored in memory.   
 * @author Roberto Muñoz
 */
public class XMLModel {
	/** Model loaded from a XML file */
	Model model;
	/** Experiement data */
	Experiment experiment;
		
	/**
	 * Load a model and a scenario stored in a XML file and
	 * merge the content to create a model stored in memory.
	 * @param xmlModelFileName
	 * @param xmlScenarioFileName
	 */
	public XMLModel(String xmlModelFileName, String xmlScenarioFileName, String xmlExperimentFileName) {
	
		try {
			JAXBContext jc = JAXBContext.newInstance("es.ull.cyc.simulation.bind");
			Unmarshaller u = jc.createUnmarshaller();
			this.model = (Model)u.unmarshal(new FileInputStream(xmlModelFileName));
			if (xmlScenarioFileName != null) {
				loadScenario((Scenario)u.unmarshal(new FileInputStream(xmlScenarioFileName)));
			}
			if (xmlExperimentFileName != null) {
				experiment = (Experiment)u.unmarshal(new FileInputStream(xmlExperimentFileName));
			}
			else {
				return;
			}
		}
		catch (JAXBException je) { je.printStackTrace(); System.exit(-1); }
		catch (IOException ioe) { ioe.printStackTrace(); System.exit(-1); }
	}
	
	/**
	 * Merge the information describing a scenario with the loaded model
	 * @param scenario
	 */
	protected void loadScenario(Scenario scenario) {
		
		model.setDescription(scenario.getDescription());
		extendResources(scenario.getResource());
		extendFlows(scenario.getFlowExtension());
	}

	/**
	 * Returns the Output debug mode 
	 * @return
	 */
	public int getDebugMode() {
		String debugMode = experiment.getDebugMode();
		if (debugMode.equals("NO"))
			return Output.NODEBUG;
		if (debugMode.equals("DEBUG"))
			return Output.DEBUGLEVEL;
		if (debugMode.equals("XDEBUG"))
			return Output.XDEBUGLEVEL;
		return Output.NODEBUG;
	}

	/**
	 * Find a resource by its description
	 * @param description Resource description
	 * @return resource or null if the resource isn't found
	 */
	protected Resource findResource(String description) {
		Iterator<Resource> resIt = model.getResource().iterator();
		while (resIt.hasNext()) {
			Resource res = resIt.next();
			if (res.description.equals(description))
				return res;
		}
		return null;
	}
	
	/**
	 * Add, modify or delete model resources
	 * @param resList scenario Resource list
	 */
	protected void extendResources(List<Resource> resList) {
		Iterator<Resource> resIt = resList.iterator();
		
		while (resIt.hasNext()) {
			Resource res = resIt.next();
			Resource modelRes = findResource(res.getDescription());
			if (modelRes == null) {             // add a resource to the model
				model.getResource().add(res);
			}
			else {
				modelRes.timeTable = res.getTimeTable();  // modify a model resource
			}
		}		
	}
	
	/**
	 * Search for a flow in the model
	 * @param id searched flow identifier
	 * @return searched flow or null if its not found
	 */
	protected Flow findFlow(int id) {
		return dfs(model.getFlow(), id);		
	}
	
	/**
	 * Implements a depth first search in the flows structure
	 * @param flowList flow descendants
	 * @param id searched flow identifier
	 * @return searched flow or null if its not found
	 */
	protected Flow dfs(List<Flow> flowList, int id) {
		if (flowList.size() == 0) return null;
		Iterator<Flow> flowIt = flowList.iterator();
		while (flowIt.hasNext()) {
			Flow flow = flowIt.next();
			if (flow.getId() == id)
				return flow;
			else {
				flow = dfs(flow.getFlow(), id);
				if (flow != null)
					return flow;
			}			
		}
		return null;
	}
	
	/**
	 * Modify the model flows characteristics
	 * @param flowList Scenario flows extensions
	 */
	protected void extendFlows(List<FlowExtension> flowList) {
		Iterator<FlowExtension> flowIt = flowList.iterator();
		while (flowIt.hasNext()) {
			FlowExtension flow = flowIt.next();
			Flow modelFlow = findFlow(flow.getId());
			if (modelFlow != null) {
				if (flow.getIterations() != null) {
					modelFlow.setIterations(flow.getIterations());
				}
				if (flow.getProb()!= null) {
					modelFlow.setProb(flow.getProb());				
				}
			}
		}
	}

	/**
	 * @return XML model stored in memory 
	 */
	public Model getModel() {
		return model;
	}
	
	/**
	 * @return XML experiement stored in memory
	 */
	public Experiment getExperiment() {
		return experiment; 
	}
}
