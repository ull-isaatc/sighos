package es.ull.isaatc.simulation.xml;

import es.ull.isaatc.simulation.xml.Activity.WorkGroup;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.simulation.xml.util.MarshallModelUtil;
import es.ull.isaatc.simulation.xml.validation.ModelException;
import es.ull.isaatc.simulation.xml.validation.ModelValidator;
import es.ull.isaatc.util.OrderedList;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.Output.DebugLevel;

/**
 * XMLModel.java
 * 
 * Created on 13 February 2006
 */

/**
 * Load a model and a experiment stored in a XML file.
 * 
 * @author Roberto Muñoz
 */
public class XMLModel {

    /** Model data */
    Model model;
    
    /** Experiement data */
    Experiment experiment;
    
    /** Mapping table */
    OrderedList<ModelMappingTable> modelList = new OrderedList<ModelMappingTable>();
    
    /** XML model validator */
    private ModelValidator modelValidator;

    /** Counter for FLOW_ID */
    private static int FLOW_ID = 0;

    /**
     * Load a model and a scenario stored in a XML file and merge the
     * content to create a model stored in memory.
     * 
     * @param xmlModelFileName
     * @param xmlExperimentFileName
     */
    public XMLModel(String xmlModelFileName, String xmlExperimentFileName) {
	modelValidator = new ModelValidator(modelList);
	
	experiment = MarshallModelUtil.unMarshallExperiment(xmlExperimentFileName);
	modelList.add(new ModelMappingTable(MarshallModelUtil.unMarshallModel(xmlModelFileName)));
	
	updateIncludes(modelList.get(0).getModel());
	updateComponentsReferences();
	mergeModels();
    }

    /**
     * Adds the referenced models
     * @param model the top model
     */
    private void updateIncludes(Model model) {
	
	for (String modelFileName : model.getInclude()) {
	    Model includeModel = MarshallModelUtil.unMarshallModel(modelFileName);
	    if (includeModel.getId() != model.getId()) {
                modelList.add(new ModelMappingTable(includeModel));
                updateIncludes(includeModel);
	    }
	}
    }
    
    private void updateComponentsReferences() {

	// update components definition
	try {
	    for (ModelMappingTable includeModel : modelList) {    
                for (ResourceType rt : includeModel.getModel().getResourceType()) {
                    includeModel.addComponent(rt);
                    modelValidator.validate(rt);
                }
                for (Resource res : includeModel.getModel().getResource()) {
                    includeModel.addComponent(res);
                    modelValidator.validate(res);
                }
                for (Activity act : includeModel.getModel().getActivity()) {
                    includeModel.addComponent(act);
                    modelValidator.validate(act);
                }
                for (ElementType et : includeModel.getModel().getElementType()) {
                    includeModel.addComponent(et);
                    modelValidator.validate(et);
                }
                for (RootFlow rf : includeModel.getModel().getRootFlow()) {
                    includeModel.addComponent(rf);
                    modelValidator.validate(rf);
                }
	    }
	} catch (ModelException e) {
	    modelValidator.showErrors();
	    System.exit(-1);
	}
	
	for (ModelMappingTable includeModel : modelList) {
	    // resource type reference in time table entries
	    for (Resource res : includeModel.getModel().getResource())
		for (TimeTable tt : res.getTimeTable())
		    updateTimeTableReferences(tt);

	    // resource type references in workgroups 
	    for (Activity act : includeModel.getModel().getActivity())
		for (WorkGroup wg : act.getWorkGroup())
		    updateWorkgroupReferences(wg);

	    // flows
	    for (RootFlow rf : includeModel.getModel().getRootFlow())
		updateFlowReferences(FlowChoiceUtility.getSelectedFlow(rf.getFlow()));
	}
	
	// element type and root flow references 
	for (Generator gen : experiment.getGenerator()) {
	    updateProbTreeReferences(gen.getToGenerate().getProbTree());
	}
    }

    private void updateProbTreeReferences(ProbTree probTree) {
	
	if (probTree.getSubTree().size() != 0) {
	    for (ProbTree pt : probTree.getSubTree())
		updateProbTreeReferences(pt);
	}
	else {
	    ComponentRef ref = probTree.getElementType();
	    ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getElementType(ref.getId()).getId());
	    ref = probTree.getMetaFlow();
	    ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getRootFlow(ref.getId()).getId());
	}
    }
    
    private void updateWorkgroupReferences(WorkGroup wg) {
	
	for (Role role : wg.getRole()) {
	    role.getRtRef().setId(modelList.get(Integer.valueOf(role.getRtRef().getModelId())).getResourceType(role.getRtRef().getId()).getId());
	}
    }

    private void updateTimeTableReferences(TimeTable tt) {
	
	for (ComponentRef role : tt.getRtRef()) {
	    role.setId(modelList.get(Integer.valueOf(role.getModelId())).getResourceType(role.getId()).getId());
	}
    }
    
    private void updateFlowReferences(Flow flow) {
	flow.setId(FLOW_ID++);
	if (flow instanceof SingleFlow) {
	    SingleFlow sf = (SingleFlow)flow; 
	    sf.getActRef().setId(modelList.get(Integer.valueOf(sf.getActRef().getModelId())).getActivity(sf.getActRef().getId()).getId());
	}
	else if (flow instanceof PackageFlow) {
	    PackageFlow pf = (PackageFlow)flow; 
	    pf.getRootFlowRef().setId(modelList.get(Integer.valueOf(pf.getRootFlowRef().getModelId())).getRootFlow(pf.getRootFlowRef().getId()).getId());
	}
	else if (flow instanceof DecisionOption) {
	    DecisionOption opt = (DecisionOption)flow;
	    updateFlowReferences(FlowChoiceUtility.getSelectedFlow(opt));
	}
	else if (flow instanceof TypeBranch) {
	    TypeBranch branch = (TypeBranch)flow;
	    for (ComponentRef ref : branch.getElementType())
		ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getElementType(ref.getId()).getId());
	    updateFlowReferences(FlowChoiceUtility.getSelectedFlow(branch));
	}
	else if (flow instanceof SequenceFlow) {
	    SequenceFlow seq = (SequenceFlow)flow;
	    for (Flow f : seq.getSingleOrPackageOrSequence())
		updateFlowReferences(f);
	}
	else if (flow instanceof SimultaneousFlow) {
	    SimultaneousFlow sim = (SimultaneousFlow)flow;
	    for (Flow f : sim.getSingleOrPackageOrSequence())
		updateFlowReferences(f);
	}
	else if (flow instanceof DecisionFlow) {
	    DecisionFlow dec = (DecisionFlow)flow;
	    for (Flow f : dec.getOption())
		updateFlowReferences(f);
	}
	else if (flow instanceof TypeFlow) {
	    TypeFlow type = (TypeFlow)flow;
	    for (Flow f : type.getBranch())
		updateFlowReferences(f);
	}
    }
    
    private void mergeModels() {
	model = modelList.get(0).getModel();
	modelList.remove(0);
	for (ModelMappingTable includeModel : modelList) {
	    // append model components
	    model.resourceType.addAll(includeModel.getModel().getResourceType());
	    model.resource.addAll(includeModel.getModel().getResource());
	    model.activity.addAll(includeModel.getModel().getActivity());
	    model.elementType.addAll(includeModel.getModel().getElementType());
	    model.rootFlow.addAll(includeModel.getModel().getRootFlow());
	}		
    }
    
    /**
     * Returns the Output debug mode
     * 
     * @return
     */
    public DebugLevel getDebugMode() {

	String debugMode = experiment.getDebugMode();
	if (debugMode.equals("NO"))
	    return Output.DebugLevel.NODEBUG;
	if (debugMode.equals("DEBUG"))
	    return Output.DebugLevel.DEBUG;
	if (debugMode.equals("XDEBUG"))
	    return Output.DebugLevel.XDEBUG;
	return Output.DebugLevel.NODEBUG;
    }

    /**
     * @return the modelList
     */
    public OrderedList<ModelMappingTable> getModelList() {

	return modelList;
    }

    /**
     * @return XML model stored in memory
     */
    public Model getModel() {
//	MarshallModelUtil.marshallModel(model, "aux_model.xml");
	return model;
    }

    /**
     * @return XML experiement stored in memory
     */
    public Experiment getExperiment() {

	return experiment;
    }
}
