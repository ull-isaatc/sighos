package es.ull.isaatc.simulation.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

import es.ull.isaatc.simulation.xml.Activity;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.simulation.xml.WorkGroup;
import es.ull.isaatc.simulation.xml.WorkGroup.Role;
import es.ull.isaatc.simulation.xml.util.MarshallModelUtil;
import es.ull.isaatc.simulation.xml.validation.ModelException;
import es.ull.isaatc.simulation.xml.validation.ModelValidator;

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
public class XMLWrapper {

    /** Model data */
    private Model model;

    /** Experiement data */
    private Experiment experiment;

    /** Mapping table */
    private MappingTableManager modelList = new MappingTableManager();

    /** Scenario definition */
    private ModelMappingTable scenario;

    /** XML model validator */
    private ModelValidator modelValidator;

    /** Counter for FLOW_ID */
    private static int FLOW_ID = 0;

    /**
     * Load a model and a scenario stored in a XML file and merge the
     * content to create a model stored in memory.
     * 
     * @param xmlModelFileName
     * @param xmlScenarioFileName
     * @param xmlExperimentFileName
     */
    public XMLWrapper(String xmlModelFileName, String xmlScenarioFileName, String xmlExperimentFileName) {
		modelValidator = new ModelValidator(modelList.getModelList());
	
		experiment = MarshallModelUtil.unMarshallExperiment(xmlExperimentFileName);
		Model model = MarshallModelUtil.unMarshallModel(xmlModelFileName); 
		modelList.add(new ModelMappingTable(model));
		if (xmlScenarioFileName == null)
		    scenario = null;
		else
		    scenario = new ModelMappingTable(MarshallModelUtil.unMarshallModel(xmlScenarioFileName));
	
		updateIncludes(model);
		updateComponentDefinitions();
		updateScenario();
		updateComponentsReferences();
		mergeModels();
		// create a log with the mappings
		createMappingLogFile("mappings.log");
    }

    /**
     * Adds the referenced models
     * @param model the top model
     */
    private void updateIncludes(Model model) {

	for (String modelFileName : model.getInclude()) {
	    Model includeModel = MarshallModelUtil.unMarshallModel(modelFileName);
	    // prevent cycle include
	    if (includeModel.getId() != model.getId()) {
			modelList.add(new ModelMappingTable(includeModel));
			updateIncludes(includeModel);
	    }
	}
    }

    private void updateComponentDefinitions() {
	
		// update components definition
		try {
		    for (ModelMappingTable includeModel : modelList) {
				for (ResourceType rt : includeModel.getModel().getResourceType()) {
				    includeModel.addComponent(rt);
				    modelValidator.validate(rt);
				}
				for (WorkGroup wg : includeModel.getModel().getWorkGroup()) {
				    includeModel.addComponent(wg);
				    modelValidator.validate(wg);
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
    }
    
    private void updateComponentsReferences() {

		for (ModelMappingTable includeModel : modelList) {
		    // resource type reference in time table entries
		    for (Resource res : includeModel.getModel().getResource())
			for (TimeTable tt : res.getTimeTable())
			    updateTimeTableReferences(tt);
	
		    // resource type references in workgroups 
		    for (Activity act : includeModel.getModel().getActivity())
				for (es.ull.isaatc.simulation.xml.Activity.WorkGroup wg : act.getWorkGroup())
					if (wg.getWorkGroup() != null)
						updateWorkGroupReferences(wg.getWorkGroup());
					else
						updateResourceTypeReferences(wg.getDefinition());
	
		    // work groups references in activities
		    
		    
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
		} else {
		    ComponentRef ref = probTree.getElementType();
		    ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getElementType(ref.getId()).getId());
		    ref = probTree.getMetaFlow();
		    ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getRootFlow(ref.getId()).getId());
		}
    }

    private void updateWorkGroupReferences(ComponentRef wgRef) {

    	wgRef.setId(modelList
    			.get(Integer.valueOf(wgRef.getModelId()))
			    .getResourceType(wgRef.getId()).getId());
    }
    
    private void updateResourceTypeReferences(WorkGroup wg) {

		for (Role role : wg.getRole()) {
		    role.getRtRef().setId(
			    modelList
				    .get(Integer.valueOf(role.getRtRef().getModelId()))
				    .getResourceType(role.getRtRef().getId()).getId());
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
		    SingleFlow sf = (SingleFlow) flow;
		    sf.getActRef().setId(
			    modelList.get(Integer.valueOf(sf.getActRef().getModelId())).getActivity(sf.getActRef().getId()).getId());
		} else if (flow instanceof PackageFlow) {
		    PackageFlow pf = (PackageFlow) flow;
		    pf.getRootFlowRef().setId(
			    modelList.get(
				    Integer.valueOf(pf.getRootFlowRef().getModelId())).getRootFlow(pf.getRootFlowRef().getId()).getId());
		} else if (flow instanceof DecisionOption) {
		    DecisionOption opt = (DecisionOption) flow;
		    updateFlowReferences(FlowChoiceUtility.getSelectedFlow(opt));
		} else if (flow instanceof TypeBranch) {
		    TypeBranch branch = (TypeBranch) flow;
		    for (ComponentRef ref : branch.getElementType())
			ref.setId(modelList.get(Integer.valueOf(ref.getModelId())).getElementType(ref.getId()).getId());
		    updateFlowReferences(FlowChoiceUtility.getSelectedFlow(branch));
		} else if (flow instanceof SequenceFlow) {
		    SequenceFlow seq = (SequenceFlow) flow;
		    for (Flow f : seq.getSingleOrPackageOrSequence())
			updateFlowReferences(f);
		} else if (flow instanceof SimultaneousFlow) {
		    SimultaneousFlow sim = (SimultaneousFlow) flow;
		    for (Flow f : sim.getSingleOrPackageOrSequence())
			updateFlowReferences(f);
		} else if (flow instanceof DecisionFlow) {
		    DecisionFlow dec = (DecisionFlow) flow;
		    for (Flow f : dec.getOption())
			updateFlowReferences(f);
		} else if (flow instanceof TypeFlow) {
		    TypeFlow type = (TypeFlow) flow;
		    for (Flow f : type.getBranch())
			updateFlowReferences(f);
		}
    }

    /**
     * Includes the scenario modifications to the model
     *
     */
    private void updateScenario() {
		/* en este punto ya se han cargado todos los modelos que se encuentran en modelList
		 * a partir de ahora se debería almacenar el escenario, realizando los cambios oportunos
		 * Todavía no es necesario actualizar las referencias, mejor es hacerlo cuando el modelo
		 * esté actualizado con el escenario */
		if (scenario == null)
		    return;
		// insert the scenario as a new model
		modelList.add(scenario);
		try {
		    // update resource types
		    ListIterator it;
		    it = scenario.getModel().getResourceType().listIterator();
		    while (it.hasNext()) {
			if (processScenarioComponent((ResourceType)it.next()))
			    it.remove();
		    }
		    // update resources
		    it = scenario.getModel().getResource().listIterator();
		    while (it.hasNext()) {
			if (processScenarioComponent((Resource)it.next()))
			    it.remove();
	
		    }
		    // update activities
		    it = scenario.getModel().getActivity().listIterator();
		    while (it.hasNext()) {
			if (processScenarioComponent((Activity)it.next()))
			    it.remove();
		    }
		    // update element types
		    it = scenario.getModel().getElementType().listIterator();
		    while (it.hasNext()) {
			if (processScenarioComponent((ElementType)it.next()))
			    it.remove();
		    }
		    // update root flows
		    it = scenario.getModel().getRootFlow().listIterator();
		    while (it.hasNext()) {
			if (processScenarioComponent((RootFlow)it.next()))
			    it.remove();
		    }
		} catch (ModelException e) {
		    modelValidator.showErrors();
		    System.exit(-1);
		}
    }

    /**
     * Process a resource type definition in a scenario. If the component defined in the 
     * scenario already exist, the original component is modified. In other case the component
     * is added to the model. 
     * @param component resource type to process
     * @throws ModelException
     * @return true if the component was already in the model 
     */
    private boolean processScenarioComponent(ResourceType component)
	    throws ModelException {
		// check if the component is new or an update
		if (component.getModelId() == null) {
		    scenario.addComponent(component);
		    modelValidator.validate(component);
		    return false;
		} else {
		    ModelMappingTable referencedModel = modelList.get(Integer.valueOf(component.getModelId()));
		    ResourceType modelComponent = referencedModel.getResourceType(component.getId());
		    if (modelComponent != null) { // update the component definition
		    	modelComponent.setDescription(component.getDescription());
		    }
		    return true;
		}
    }

    /**
     * Process a resource definition in a scenario. If the component defined in the 
     * scenario already exist, the original component is modified. In other case the component
     * is added to the model. 
     * @param component resource to process
     * @throws ModelException 
     * @return true if the component was already in the model 
     */
    private boolean processScenarioComponent(Resource component)
	    throws ModelException {
		// check if the component is new or an update
		if (component.getModelId() == null) {
		    scenario.addComponent(component);
		    modelValidator.validate(component);
		    return false;
		} else {
		    ModelMappingTable referencedModel = modelList.get(Integer
			    .valueOf(component.getModelId()));
		    Resource modelComponent = referencedModel.getResource(component.getId());
		    if (modelComponent != null) { // update the component definition
				modelComponent.setDescription(component.getDescription());
				// FIXED : 30/11/06
				// controlar si el número es menor o mayor al actual para reasignar ids
				// nuevos y separar la asignación en varios recursos
				int oldUnits = modelComponent.getUnits();
				int newUnits = component.getUnits();
				modelComponent.getTimeTable().clear();
				if (newUnits == oldUnits) { // update (old - new) resources tte to empty
				    modelComponent.getTimeTable().addAll(component.getTimeTable());
				}
				else if (newUnits < oldUnits) { // update (old - new) resources tte to empty 
				    modelComponent.setUnits(oldUnits - newUnits);
				    Resource auxRes = new Resource();
				    auxRes.setDescription(component.getDescription());
				    auxRes.setUnits(newUnits);
				    auxRes.getTimeTable().addAll(component.getTimeTable());
				    referencedModel.addComponent(auxRes);
				    referencedModel.getModel().getResource().add(auxRes);
				}
				else if (newUnits > oldUnits) {// add (new - old) resources
		    		// FIXME : al crear nuevos recursos si se asignan las mismas entradas horarias
		    		// (el mismo objeto) a varios recursos , en el momento de actualizar las referencias
				// se intentará actualizar más de una vez, con lo que dará un error
				    modelComponent.getTimeTable().addAll(component.getTimeTable());
				    Resource auxRes = new Resource();
				    auxRes.setDescription(component.getDescription());
				    auxRes.setUnits(newUnits - oldUnits);
				    // copy the tte for the new resource
				    // each tte is duplicated, but the only things that should be created are
				    // the ttes and the roles in each tte
				    for (TimeTable ttComp : component.getTimeTable()) {
					    TimeTable tt = new Resource.TimeTable();
					    tt.setDur(ttComp.getDur());
					    tt.setCycle(ttComp.getCycle());
					    for (ComponentRef refComp : ttComp.getRtRef()) {
							ComponentRef ref = new ComponentRef();
							ref.setId(refComp.getId());
							ref.setModelId(refComp.getModelId());
							tt.getRtRef().add(ref);
					    }
					    auxRes.getTimeTable().add(tt);
				    }
				    referencedModel.addComponent(auxRes);
				    referencedModel.getModel().getResource().add(auxRes);
				}
		    }
		    return true;
		}
    }
    
    /**
     * Process an activity definition in a scenario. If the component defined in the 
     * scenario already exist, the original component is modified. In other case the component
     * is added to the model. 
     * @param component activity to process
     * @throws ModelException 
     * @return true if the component was already in the model 
     */
    private boolean processScenarioComponent(Activity component)
	    throws ModelException {
		// check if the component is new or an update
		if (component.getModelId() == null) {
		    scenario.addComponent(component);
		    modelValidator.validate(component);
		    return false;
		} else {
		    ModelMappingTable referencedModel = modelList.get(Integer.valueOf(component.getModelId()));
		    Activity modelComponent = referencedModel.getActivity(component.getId());
		    if (modelComponent != null) { // update the component definition
				modelComponent.setDescription(component.getDescription());
				modelComponent.setPresential(component.isPresential());
				modelComponent.setPriority(component.getPriority());
				// FIXME : comprobar que existan los mismos id en los workgroups
				// antes verificar que es necesario guardar los wg para el estado
				modelComponent.getWorkGroup().clear();
				modelComponent.getWorkGroup().addAll(component.getWorkGroup());
		    }
		    return true;
		}
    }
    
    /**
     * Process an element type definition in a scenario. If the component defined in the 
     * scenario already exist, the original component is modified. In other case the component
     * is added to the model. 
     * @param component element type to process
     * @throws ModelException 
     * @return true if the component was already in the model 
     */
    private boolean processScenarioComponent(ElementType component)
	    throws ModelException {
		// check if the component is new or an update
		if (component.getModelId() == null) {
		    scenario.addComponent(component);
		    modelValidator.validate(component);
		    return false;
		} else {
		    ModelMappingTable referencedModel = modelList.get(Integer
			    .valueOf(component.getModelId()));
		    ElementType modelComponent = referencedModel.getElementType(component.getId());
		    if (modelComponent != null) { // update the component definition
				modelComponent.setDescription(component.getDescription());
				modelComponent.setPriority(component.getPriority());
		    }
		    return true;
		}
    }
    
    /**
     * Process a root flow definition in a scenario. If the component defined in the 
     * scenario already exist, the original component is modified. In other case the component
     * is added to the model. 
     * @param component root flow to process
     * @throws ModelException 
     * @return true if the component was already in the model 
     */
    private boolean processScenarioComponent(RootFlow component)
	    throws ModelException {
		// check if the component is new or an update
		if (component.getModelId() == null) {
		    scenario.addComponent(component);
		    modelValidator.validate(component);
		    return false;
		} else {
		    ModelMappingTable referencedModel = modelList.get(Integer
			    .valueOf(component.getModelId()));
		    RootFlow modelComponent = referencedModel.getRootFlow(component.getId());
		    if (modelComponent != null) { // update the component definition
				modelComponent.setDescription(component.getDescription());
				modelComponent.setFlow(component.getFlow());
		    }
		    return true;
		}
    }
    
    /**
     * Find a component in a list with a specified identifier
     * @param componentList the list to search into
     * @param component the component to look for
     * @return the component if found or null if there isn't a component with the correct id
     */
    private BaseComponent findComponent(List componentList,
	    BaseComponent component) {
		List<BaseComponent> bcList = componentList;
		for (BaseComponent bc : bcList) {
		    if (bc.getId() == component.getId())
			return bc;
		}
		return null;
    }

    private ResourceType findComponent(Model model, ResourceType component) {
    	return (ResourceType) findComponent(model.getResourceType(), component);
    }

    private WorkGroup findComponent(Model model, WorkGroup component) {
    	return (WorkGroup) findComponent(model.getWorkGroup(), component);
    }

    private Resource findComponent(Model model, Resource component) {
    	return (Resource) findComponent(model.getResource(), component);
    }

    private Activity findComponent(Model model, Activity component) {
    	return (Activity) findComponent(model.getActivity(), component);
    }

    private ElementType findComponent(Model model, ElementType component) {
    	return (ElementType) findComponent(model.getElementType(), component);
    }

    private RootFlow findComponent(Model model, RootFlow component) {
    	return (RootFlow) findComponent(model.getRootFlow(), component);
    }

    private void mergeModels() {
		ModelMappingTable first = modelList.remove(0);
		model = first.getModel();
		for (ModelMappingTable includeModel : modelList) {
		    // append model components
		    model.resourceType
			    .addAll(includeModel.getModel().getResourceType());
		    model.resource.addAll(includeModel.getModel().getResource());
		    model.activity.addAll(includeModel.getModel().getActivity());
		    model.elementType.addAll(includeModel.getModel().getElementType());
		    model.rootFlow.addAll(includeModel.getModel().getRootFlow());
		}
		modelList.add(0, first);
    }

    /**
     * Returns the Output debug mode
     * 
     * @return
     */
    public boolean getDebugMode() {
    	return experiment.debugMode;
    }

    /**
     * @return the modelList
     */
    public MappingTableManager getModelList() {

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
    
    public void createMappingLogFile(String fileName) {
	    try {
		FileOutputStream logFile = new FileOutputStream(fileName);
		PrintWriter log = new PrintWriter(new OutputStreamWriter(logFile));
		for (ModelMappingTable model : modelList) {
		    log.print(model.toString());
//		    System.out.println(model.toString());
		}
		log.close();
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
    }
}
