/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.WorkToken;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.MergeFlow;
import es.ull.iis.simulation.model.flow.MergeFlowControl;

/**
 * @author Iván Castilla
 *
 */
public class MergeFlowEngine extends EngineObject implements es.ull.iis.simulation.model.engine.MergeFlowEngine {
	final private MergeFlow modelFlow;
	
	/**
	 * @param simul
	 * @param objTypeId
	 */
	public MergeFlowEngine(SimulationEngine simul, MergeFlow modelFlow) {
		super(modelFlow.getIdentifier(), simul, modelFlow.getObjectTypeIdentifier());
		this.modelFlow = modelFlow;
	}

	/**
	 * @return the modelFlow
	 */
	public MergeFlow getModelFlow() {
		return modelFlow;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.engine.MergeFlowEngine#getControlStructureInstance()
	 */
	@Override
	public Map<Element, MergeFlowControl> getControlStructureInstance() {
		return new TreeMap<Element, MergeFlowControl>();
	}

	@Override
	public Map<Flow, LinkedList<WorkToken>> getGeneralizedBranchesControlInstance() {
		return new TreeMap<Flow, LinkedList<WorkToken>>();
	}

}
