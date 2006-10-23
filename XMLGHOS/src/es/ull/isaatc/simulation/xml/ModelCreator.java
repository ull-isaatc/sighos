package es.ull.isaatc.simulation.xml;

/**
 * ModelCreator.java
 * 
 * Created on 6 February 2006
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.xml.validation.ModelException;
import es.ull.isaatc.simulation.xml.validation.ModelValidator;
import es.ull.isaatc.util.Output;

/**
 * Extends the Simulation class. Creates all the components required to model a
 * system from a description stored in XML.
 * 
 * @author Roberto Muñoz González
 */
public class ModelCreator extends es.ull.isaatc.simulation.Simulation {

    /** XML model description in memory */
    es.ull.isaatc.simulation.xml.Model xmlModel;

    /** XML experiment description in memory */
    es.ull.isaatc.simulation.xml.Experiment xmlExperiment;

    /** List that matches a model ResourceType with an identifier */
    ArrayList<ResourceType> resType;

    /** List containing the model root flows */
    HashMap<Integer, es.ull.isaatc.simulation.MetaFlow> flowList;

    /**
     * Relation between time periods (second, minute, hour, day, month,
     * year)
     */
    double timeRelations[] = { 1, 60, 60, 24, 30, 12, 1 };

    /** Base time index in timeRelations */
    int baseTimeIndex = 0;

    /** XML model validator */
    ModelValidator modelValidator;

    /**
     * Creates the simulation from the data stored in <code>xmlModel<code>.
     * @param xmlModel XML representation of the model
     * @param out
     */
    public ModelCreator(es.ull.isaatc.simulation.xml.XMLModel xmlModel,
	    Output out) {

	super(xmlModel.getModel().getDescription(), xmlModel.getExperiment()
		.getStartTs(), xmlModel.getExperiment().getEndTs(), out);
	this.xmlModel = xmlModel.getModel();
	this.xmlExperiment = xmlModel.getExperiment();
	this.modelValidator = new ModelValidator();
    }

    /**
     * Creates the simulation model.
     */
    protected void createModel() {

	baseTimeIndex = getTimeUnit(xmlModel.getBaseTimeUnit());
	createResourceTypes();
	createResources();
	createActivities();
	createElementTypes();
	createGenerators();
    }

    /**
     * Creates the model ResourceTypes.
     */
    void createResourceTypes() {

	boolean hasError = false;
	Iterator<es.ull.isaatc.simulation.xml.ResourceType> resTypeIt = xmlModel
		.getResourceType().iterator();

	resType = new ArrayList<ResourceType>();
	while (resTypeIt.hasNext()) {
	    es.ull.isaatc.simulation.xml.ResourceType rt = resTypeIt.next();
	    try {
		if (modelValidator.validate(rt))
		    resType.add(new ResourceType(rt, this));
	    } catch (ModelException me) {
		hasError = true;
		System.err.println(me);
	    }
	}
	if (hasError) {
	    modelValidator.showResTypeErrors();
	    System.exit(-1);
	}
    }

    /**
     * Creates the model Resources
     */
    protected void createResources() {

	boolean hasError = false;
	Iterator<es.ull.isaatc.simulation.xml.Resource> resIt = xmlModel
		.getResource().iterator();
	Iterator<es.ull.isaatc.simulation.xml.Resource.TimeTable> timeTableIt;

	while (resIt.hasNext()) {
	    es.ull.isaatc.simulation.xml.Resource res = resIt.next();
	    try {
		if (modelValidator.validate(res)) {
		    for (int i = 0; i < res.getUnits(); i++) {
			if (res.getTimeTable().size() > 0) {
			    es.ull.isaatc.simulation.Resource resource = new es.ull.isaatc.simulation.Resource(
				    res.getId() + i, this, res.getDescription());
			    timeTableIt = res.getTimeTable().iterator();
			    // Insert each time table entry as several roles
			    // entries
			    while (timeTableIt.hasNext()) {
				es.ull.isaatc.simulation.xml.Resource.TimeTable timeTable = timeTableIt
					.next();
				ArrayList<es.ull.isaatc.simulation.ResourceType> roles = new ArrayList<es.ull.isaatc.simulation.ResourceType>();
				Iterator<Integer> resTypeIdIt = timeTable
					.getRtId().iterator();
				while (resTypeIdIt.hasNext())
				    roles.add(findResourceType(resTypeIdIt
					    .next()));
				resource
					.addTimeTableEntry(
						createCycle(timeTable
							.getCycle()),
						(int) (timeTable.getDur()
							.getValue() * getTimeRelation(
							getTimeUnit(timeTable
								.getDur()
								.getTimeUnit()),
							baseTimeIndex)), roles);
			    }
			}
		    }
		}
	    } catch (ModelException me) {
		hasError = true;
		System.err.println(me);
	    }
	}
	if (hasError) {
	    modelValidator.showResourceErrors();
	    System.exit(-1);
	}
    }

    /**
     * Creates the model Activities
     */
    protected void createActivities() {

	boolean hasError = false;
	Iterator<es.ull.isaatc.simulation.xml.Activity> actListIt = xmlModel
		.getActivity().iterator();
	es.ull.isaatc.simulation.xml.Activity act;

	while (actListIt.hasNext()) {
	    try {
		act = actListIt.next();
		if (modelValidator.validate(act))
		    createWorkGroups(act,
			    new es.ull.isaatc.simulation.Activity(act.getId(),
				    this, act.getDescription(), act
					    .getPriority(), act.isPresential()));
	    } catch (ModelException me) {
		hasError = true;
		System.err.println(me);
	    }
	}
	if (hasError) {
	    modelValidator.showActivityErrors();
	    System.exit(-1);
	}
    }

    /**
     * Creates all the WorkGroups of an Activity.
     * 
     * @param actXML
     *                xml Activity
     * @param actModel
     *                simulation Activity
     */
    void createWorkGroups(es.ull.isaatc.simulation.xml.Activity actXML,
	    es.ull.isaatc.simulation.Activity actModel) {

	Iterator<es.ull.isaatc.simulation.xml.Activity.WorkGroup> wgListIt = actXML
		.getWorkGroup().iterator();
	Iterator<es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role> rtListIt;
	es.ull.isaatc.simulation.xml.Activity.WorkGroup wgXML;
	es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role rtXML;
	es.ull.isaatc.simulation.WorkGroup wgModel;

	while (wgListIt.hasNext()) {
	    wgXML = wgListIt.next();
	    wgModel = actModel.getNewWorkGroup(wgXML.getId(), createPeriod(
		    wgXML.getDuration(), getTimeUnit(wgXML.getTimeUnit()),
		    baseTimeIndex), wgXML.getPriority());
	    rtListIt = wgXML.getRole().iterator();
	    // Insert each resource type entry for this WorkGroup
	    while (rtListIt.hasNext()) {
		rtXML = rtListIt.next();
		wgModel.add(this.findResourceType(rtXML.getRtId()), rtXML
			.getUnits());
	    }
	}
    }

    /**
     * Creates the model ElementTypes.
     */
    void createElementTypes() {

	boolean hasError = false;
	Iterator<es.ull.isaatc.simulation.xml.ElementType> elemTypeIt = xmlModel
		.getElementType().iterator();

	while (elemTypeIt.hasNext()) {
	    es.ull.isaatc.simulation.xml.ElementType et = elemTypeIt.next();
	    try {
		if (modelValidator.validate(et))
		    new es.ull.isaatc.simulation.ElementType(et.getId(), this,
			    et.getDescription(), et.getPriority());
	    } catch (ModelException me) {
		hasError = true;
		System.err.println(me);
	    }
	}
	if (hasError) {
	    modelValidator.showElementTypeErrors();
	    System.exit(-1);
	}
    }

    /**
     * Creates the generators for the simulation
     */
    protected void createGenerators() {

	createMetaFlows();
	Iterator<es.ull.isaatc.simulation.xml.Generator> genIt = xmlExperiment
		.getGenerator().iterator();
	while (genIt.hasNext()) {
	    createGenerator(genIt.next());
	}
    }

    /**
     * Creates the simulation meta flows
     */
    protected void createMetaFlows() {

	boolean hasError = false;
	Iterator<es.ull.isaatc.simulation.xml.RootFlow> rootFlowListIt = xmlModel
		.getRootFlow().iterator();

	flowList = new HashMap<Integer, es.ull.isaatc.simulation.MetaFlow>();
	while (rootFlowListIt.hasNext()) {
	    try {
		es.ull.isaatc.simulation.xml.RootFlow rf = rootFlowListIt
			.next();
		if (modelValidator.validate(rf))
		    flowList.put(rf.getId(), createMetaFlow(FlowChoiceUtility
			    .getSelectedFlow(rf.getFlow()), null));
	    } catch (ModelException me) {
		hasError = true;
		System.err.println(me);
	    }
	}
	if (hasError) {
	    modelValidator.showFlowErrors();
	    System.exit(-1);
	}
    }

    /**
     * Returns a simulation metaflow
     * 
     * @param XMLFlow
     *                flow xml description
     * @param parent
     *                Metaflows parent
     */
    es.ull.isaatc.simulation.MetaFlow createMetaFlow(
	    es.ull.isaatc.simulation.xml.Flow XMLFlow,
	    es.ull.isaatc.simulation.MetaFlow parent) {

	es.ull.isaatc.random.RandomNumber rn = createCompoundRandomNumber(
		XMLFlow.getIterations(), 1);

	if (XMLFlow instanceof SingleFlow) {
	    SingleFlow XMLsf = (SingleFlow) XMLFlow;
	    if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		return new es.ull.isaatc.simulation.SingleMetaFlow(XMLFlow
			.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent, rn,
			getActivity(XMLsf.getActId()));
	    else if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		return new es.ull.isaatc.simulation.SingleMetaFlow(XMLFlow
			.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent, rn,
			getActivity(XMLsf.getActId()));
	    else if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		return new es.ull.isaatc.simulation.SingleMetaFlow(XMLFlow
			.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent,
			rn, getActivity(XMLsf.getActId()));
	    else
		return new es.ull.isaatc.simulation.SingleMetaFlow(XMLFlow
			.getId(), rn, getActivity(XMLsf.getActId()));
	}
	if (XMLFlow instanceof PackageFlow) {
	    PackageFlow XMLpf = (PackageFlow) XMLFlow;
	    es.ull.isaatc.simulation.MetaFlow mf = createMetaFlow(
		    rootFlowSearch(xmlModel.getRootFlow(), XMLpf
			    .getRootFlowId()), parent);
	    if (!parent.equals(mf))
		mf.setId(XMLpf.getId());
	    return mf;
	}
	if (XMLFlow instanceof ExitFlow) {
	    ExitFlow XMLef = (ExitFlow) XMLFlow;
	    if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		return new es.ull.isaatc.simulation.ExitMetaFlow(XMLef.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent);
	    if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		return new es.ull.isaatc.simulation.ExitMetaFlow(XMLef.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent);
	    if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		return new es.ull.isaatc.simulation.ExitMetaFlow(XMLef.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent);
	    else
		return new es.ull.isaatc.simulation.ExitMetaFlow(XMLef.getId());
	}
	if (XMLFlow instanceof SequenceFlow) {
	    SequenceFlow XMLsf = (SequenceFlow) XMLFlow;
	    es.ull.isaatc.simulation.SequenceMetaFlow seq = null;
	    if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		seq = new es.ull.isaatc.simulation.SequenceMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent, rn);
	    else if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		seq = new es.ull.isaatc.simulation.SequenceMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent,
			rn);
	    else if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		seq = new es.ull.isaatc.simulation.SequenceMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent, rn);
	    else
		seq = new es.ull.isaatc.simulation.SequenceMetaFlow(XMLsf
			.getId(), rn);

	    Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = XMLsf
		    .getSingleOrPackageOrSequence().iterator();
	    while (flowListIt.hasNext()) {
		createMetaFlow(flowListIt.next(), seq);
	    }
	    return seq;
	}
	if (XMLFlow instanceof SimultaneousFlow) {
	    SimultaneousFlow XMLsf = (SimultaneousFlow) XMLFlow;
	    es.ull.isaatc.simulation.SimultaneousMetaFlow sim = null;
	    if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		sim = new es.ull.isaatc.simulation.SimultaneousMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent, rn);
	    else if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		sim = new es.ull.isaatc.simulation.SimultaneousMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent,
			rn);
	    else if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		sim = new es.ull.isaatc.simulation.SimultaneousMetaFlow(XMLsf
			.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent, rn);
	    else
		sim = new es.ull.isaatc.simulation.SimultaneousMetaFlow(XMLsf
			.getId(), rn);
	    Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = XMLsf
		    .getSingleOrPackageOrSequence().iterator();
	    while (flowListIt.hasNext()) {
		createMetaFlow(flowListIt.next(), sim);
	    }
	    return sim;
	}
	if (XMLFlow instanceof DecisionFlow) {
	    DecisionFlow XMLdf = (DecisionFlow) XMLFlow;
	    es.ull.isaatc.simulation.DecisionMetaFlow dec = null;
	    if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		dec = new es.ull.isaatc.simulation.DecisionMetaFlow(XMLdf
			.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent,
			new Fixed(1));
	    else if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		dec = new es.ull.isaatc.simulation.DecisionMetaFlow(XMLdf
			.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent,
			new Fixed(1));
	    else if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		dec = new es.ull.isaatc.simulation.DecisionMetaFlow(XMLdf
			.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent,
			new Fixed(1));
	    else
		dec = new es.ull.isaatc.simulation.DecisionMetaFlow(XMLdf
			.getId(), new Fixed(1));
	    Iterator<es.ull.isaatc.simulation.xml.DecisionOption> optionListIt = XMLdf
		    .getOption().iterator();
	    while (optionListIt.hasNext()) {
		createOption(optionListIt.next(), dec);
	    }
	    return dec;
	}
	if (XMLFlow instanceof TypeFlow) {
	    TypeFlow XMLtf = (TypeFlow) XMLFlow;
	    es.ull.isaatc.simulation.TypeMetaFlow type = null;
	    if (parent instanceof es.ull.isaatc.simulation.OptionMetaFlow)
		type = new es.ull.isaatc.simulation.TypeMetaFlow(XMLtf.getId(),
			(es.ull.isaatc.simulation.OptionMetaFlow) parent,
			new Fixed(1));
	    else if (parent instanceof es.ull.isaatc.simulation.TypeBranchMetaFlow)
		type = new es.ull.isaatc.simulation.TypeMetaFlow(XMLtf.getId(),
			(es.ull.isaatc.simulation.TypeBranchMetaFlow) parent,
			new Fixed(1));
	    else if (parent instanceof es.ull.isaatc.simulation.GroupMetaFlow)
		type = new es.ull.isaatc.simulation.TypeMetaFlow(XMLtf.getId(),
			(es.ull.isaatc.simulation.GroupMetaFlow) parent,
			new Fixed(1));
	    else
		type = new es.ull.isaatc.simulation.TypeMetaFlow(XMLtf.getId(),
			new Fixed(1));
	    Iterator<es.ull.isaatc.simulation.xml.TypeBranch> branchListIt = XMLtf
		    .getBranch().iterator();
	    while (branchListIt.hasNext()) {
		createTypeBranch(branchListIt.next(), type);
	    }
	    return type;
	}
	return null;
    }

    /**
     * Returns an option metaflow from its definition in XML
     * 
     * @param XMLFlow
     * @param parent
     */
    es.ull.isaatc.simulation.OptionMetaFlow createOption(
	    es.ull.isaatc.simulation.xml.DecisionOption XMLOption,
	    es.ull.isaatc.simulation.DecisionMetaFlow parent) {

	es.ull.isaatc.simulation.OptionMetaFlow opt = new es.ull.isaatc.simulation.OptionMetaFlow(
		XMLOption.getId(), parent, XMLOption.getProb());
	createMetaFlow(FlowChoiceUtility.getSelectedFlow(XMLOption), opt);
	return opt;
    }

    /**
     * Returns a type branch metaflow from its definition in XML
     * 
     * @param XMLFlow
     * @param parent
     */
    es.ull.isaatc.simulation.TypeBranchMetaFlow createTypeBranch(
	    es.ull.isaatc.simulation.xml.TypeBranch XMLBranch,
	    es.ull.isaatc.simulation.TypeMetaFlow parent) {

	ArrayList<es.ull.isaatc.simulation.ElementType> elementTypes = new ArrayList<es.ull.isaatc.simulation.ElementType>();
	String elemTypes[] = XMLBranch.getElemTypes().split(",");
	for (String strId : elemTypes)
	    elementTypes.add(getElementType(Integer.parseInt(strId)));
	es.ull.isaatc.simulation.TypeBranchMetaFlow branch = new es.ull.isaatc.simulation.TypeBranchMetaFlow(
		XMLBranch.getId(), parent, elementTypes);
	createMetaFlow(FlowChoiceUtility.getSelectedFlow(XMLBranch), branch);
	return branch;
    }

    /**
     * Creates a generation class object
     * 
     * @param xmlGeneration
     */
    protected es.ull.isaatc.simulation.ElementGenerator createGenerator(
	    es.ull.isaatc.simulation.xml.Generator xmlGenerator) {

	es.ull.isaatc.util.Cycle cycle = createCycle(xmlGenerator.getCycle());
	es.ull.isaatc.simulation.xml.Generation xmlGeneration = xmlGenerator
		.getToGenerate();
	ArrayList<GenerationTrio> probVector = compactProbTree(xmlGeneration
		.getProbTree());
	es.ull.isaatc.simulation.ElementGenerator gen = new es.ull.isaatc.simulation.ElementGenerator(
		this, createCompoundRandomNumber(xmlGeneration.getNElem(), 1),
		cycle.iterator(getStartTs(), getEndTs()));
	Iterator<GenerationTrio> probVectorIt = probVector.iterator();
	while (probVectorIt.hasNext()) {
	    GenerationTrio gt = probVectorIt.next();
	    gen.add(gt.getElementType(), gt.getMetaFlow(), gt.getProb());
	}
	return gen;
    }

    /**
     * Returns a CompoundCycle or a Cycle from a definition in XML
     * 
     * @param xmlCycle
     */
    es.ull.isaatc.util.Cycle createCycle(
	    es.ull.isaatc.simulation.xml.Cycle xmlCycle) {

	int cycleTimeUnitIndex = getTimeUnit(xmlCycle.getTimeUnit());
	es.ull.isaatc.random.RandomNumber rn = createPeriod(xmlCycle
		.getPeriod(), cycleTimeUnitIndex, baseTimeIndex);
	double relationTime = getTimeRelation(cycleTimeUnitIndex, baseTimeIndex);
	double startTs = xmlCycle.getStartTs() * relationTime;

	if (xmlCycle.getEndTs() != null) {
	    double endTs = xmlCycle.getEndTs() * relationTime;
	    if (xmlCycle.getSubCycle() == null) {
		return new es.ull.isaatc.util.Cycle(startTs, rn, endTs);
	    } else {
		return new es.ull.isaatc.util.Cycle(startTs, rn, endTs,
			createCycle(xmlCycle.getSubCycle()));
	    }
	} else {
	    if (xmlCycle.getSubCycle() == null) {
		return new es.ull.isaatc.util.Cycle(startTs, rn, xmlCycle
			.getIterations());
	    } else {
		return new es.ull.isaatc.util.Cycle(startTs, rn, xmlCycle
			.getIterations(), createCycle(xmlCycle.getSubCycle()));
	    }
	}
    }

    /**
     * Compact the probability tree and returns an <code>ArrayList</code>
     * with the final probability for each root meta flow
     * 
     * @param probTree
     */
    ArrayList<GenerationTrio> compactProbTree(
	    es.ull.isaatc.simulation.xml.ProbTree probTree) {

	ArrayList<GenerationTrio> probVector = new ArrayList<GenerationTrio>();

	if (probTree.getSubTree().size() == 0) {
	    probVector.add(new GenerationTrio(getElementType(probTree
		    .getElementType()), flowList.get(probTree.getMetaFlow()),
		    probTree.getProb()));
	    return probVector;
	}

	Iterator<es.ull.isaatc.simulation.xml.ProbTree> nodeIt = probTree
		.getSubTree().iterator();
	while (nodeIt.hasNext()) {
	    probVector.addAll(compactProbTree(nodeIt.next()));
	}

	// Calculate the probability of each type
	for (int i = 0; i < probVector.size(); i++) {
	    GenerationTrio gp = probVector.get(i);
	    gp.setProb(gp.getProb() * probTree.getProb());
	    probVector.set(i, gp);
	}
	return probVector;
    }

    /**
     * Creates an object that represents a probability distribution
     * 
     * @param crnXML
     *                probabiblity distribution expressed in XML
     * @return RandomNumber subclass
     */
    es.ull.isaatc.random.RandomNumber createCompoundRandomNumber(
	    es.ull.isaatc.simulation.xml.RandomNumber crnXML, double k) {

	// FIXME: Currently there is no data integrity check
	if (crnXML == null)
	    return new es.ull.isaatc.random.Fixed(1);
	if (crnXML.getDist() != null)
	    return createRandomNumber(crnXML, k);
	if (crnXML.getOp().equals(Operation.ADDITION))
	    return new es.ull.isaatc.random.AddRandomNumber(
		    createCompoundRandomNumber(crnXML.getOperand().get(0), k),
		    createCompoundRandomNumber(crnXML.getOperand().get(1), k));
	if (crnXML.getOp().equals(Operation.MULTIPLICATION))
	    return new es.ull.isaatc.random.MultRandomNumber(
		    createCompoundRandomNumber(crnXML.getOperand().get(0), k),
		    createCompoundRandomNumber(crnXML.getOperand().get(1), k));
	return null;
    }

    es.ull.isaatc.random.RandomNumber createRandomNumber(
	    es.ull.isaatc.simulation.xml.RandomNumber rnXML, double k) {

	if (rnXML == null)
	    return new es.ull.isaatc.random.Fixed(1);

	es.ull.isaatc.simulation.xml.RandomNumber newRn = new es.ull.isaatc.simulation.xml.RandomNumber();
	newRn.setDist(rnXML.getDist());
	newRn.setP1(rnXML.getP1() * k);
	newRn.setP2(rnXML.getP2() * k);
	newRn.setP3(rnXML.getP3() * k);

	if (rnXML.getDist().equals(Distribution.BETA))
	    return new es.ull.isaatc.random.Beta(new Double(newRn.getP1())
		    .intValue(), new Double(newRn.getP2()).intValue());
	if (rnXML.getDist().equals(Distribution.CHISQUARE))
	    return new es.ull.isaatc.random.ChiSquare(new Double(newRn.getP1())
		    .intValue());
	if (rnXML.getDist().equals(Distribution.ERLANG))
	    return new es.ull.isaatc.random.Erlang(newRn.getP1(), newRn.getP2());
	if (rnXML.getDist().equals(Distribution.EXPONENTIAL))
	    return new es.ull.isaatc.random.Exponential(newRn.getP1());
	if (rnXML.getDist().equals(Distribution.FIXED))
	    return new es.ull.isaatc.random.Fixed(newRn.getP1());
	if (rnXML.getDist().equals(Distribution.BETA))
	    return new es.ull.isaatc.random.Geometric(newRn.getP1());
	if (rnXML.getDist().equals(Distribution.NORMAL))
	    return new es.ull.isaatc.random.Normal(newRn.getP1(), newRn.getP2());
	if (rnXML.getDist().equals(Distribution.POISSON))
	    return new es.ull.isaatc.random.Poisson(newRn.getP1());
	if (rnXML.getDist().equals(Distribution.TRIANGULAR))
	    return new es.ull.isaatc.random.Triangular(newRn.getP1(), newRn
		    .getP2(), newRn.getP3());
	if (rnXML.getDist().equals(Distribution.UNIFORM))
	    return new es.ull.isaatc.random.Uniform(newRn.getP1(), newRn
		    .getP2());
	return null;
    }

    /**
     * @param rn
     * @param freqIndex
     * @param baseIndex
     * @return
     */
    es.ull.isaatc.random.RandomNumber createPeriod(
	    es.ull.isaatc.simulation.xml.RandomNumber rn, int periodIndex,
	    int baseIndex) {

	double value = getTimeRelation(periodIndex, baseIndex);

	return createCompoundRandomNumber(rn, value);
    }

    /**
     * Finds and returns a ResourceType.
     * 
     * @param id
     *                ResourceType identifier
     * @return simulation ResourceType
     */
    es.ull.isaatc.simulation.ResourceType findResourceType(int id) {

	Iterator<ResourceType> resTypeIt = resType.iterator();
	ResourceType rt;

	while (resTypeIt.hasNext()) {
	    rt = resTypeIt.next();
	    if (rt.getId() == id) {
		return rt.getResourceType();
	    }
	}
	return null;
    }

    /**
     * Returns the flow of a root flow
     */
    protected es.ull.isaatc.simulation.xml.Flow rootFlowSearch(
	    List<es.ull.isaatc.simulation.xml.RootFlow> flowList, int id) {

	if (flowList.size() == 0)
	    return null;
	Iterator<es.ull.isaatc.simulation.xml.RootFlow> rfIt = flowList
		.iterator();
	while (rfIt.hasNext()) {
	    es.ull.isaatc.simulation.xml.RootFlow rf = rfIt.next();
	    if (rf.getId() == id)
		return FlowChoiceUtility.getSelectedFlow(rf.getFlow());
	}
	return null;
    }

    /**
     * Returns the index in the timeRelations vector for the value
     */
    int getTimeUnit(es.ull.isaatc.simulation.xml.CommonFreq value) {

	// if the value is null then uses the baseTime
	if (value == null)
	    return baseTimeIndex;
	if (value.equals(CommonFreq.YEAR))
	    return 6;
	if (value.equals(CommonFreq.MONTH))
	    return 5;
	if (value.equals(CommonFreq.DAY))
	    return 4;
	if (value.equals(CommonFreq.HOUR))
	    return 3;
	if (value.equals(CommonFreq.MINUTE))
	    return 2;
	if (value.equals(CommonFreq.SECOND))
	    return 1;
	return baseTimeIndex;
    }

    /**
     * Returns the relation value between the two parameters
     * 
     * @param valueIndex
     * @param baseIndex
     */
    double getTimeRelation(int valueIndex, int baseIndex) {

	double value = 1;
	for (int i = baseIndex; i < valueIndex; i++)
	    value *= timeRelations[i];
	return value;
    }

    public boolean hasErrors() {

	return modelValidator.hasErrors();
    }

    /**
     * Matches an identifier with a simulation ResourceType. This class is
     * used by some methods in ModelCreator to find a ResourceType.
     * 
     * @author Roberto Muñoz
     */
    private class ResourceType {

	/** ResourceType identifier */
	int id;

	/** Simulation ResourceType */
	es.ull.isaatc.simulation.ResourceType resourceType;

	/**
	 * Creates a simulation ResourceType from the description in XML. Assign
	 * an identifier to it.
	 * 
	 * @param rt
	 *                XML ResourceType description
	 * @param mod
	 *                simulation model
	 */
	public ResourceType(es.ull.isaatc.simulation.xml.ResourceType rt,
		es.ull.isaatc.simulation.Simulation mod) {

	    super();
	    resourceType = new es.ull.isaatc.simulation.ResourceType(
		    rt.getId(), mod, rt.getDescription());
	    id = rt.getId();
	}

	/**
	 * Getter for id field.
	 * 
	 * @return ResourceType identifier
	 */
	public int getId() {

	    return id;
	}

	/**
	 * Getter for resourceType field.
	 * 
	 * @return Simulation ResourceType
	 */
	public es.ull.isaatc.simulation.ResourceType getResourceType() {

	    return resourceType;
	}
    }

    /**
     * This class pair a metaflow with a probability
     */
    private class GenerationTrio {

	double prob;

	es.ull.isaatc.simulation.ElementType elementType;

	es.ull.isaatc.simulation.MetaFlow metaFlow;

	/**
	 * @param prob
	 * @param metaFlowId
	 */
	public GenerationTrio(es.ull.isaatc.simulation.ElementType elementType,
		es.ull.isaatc.simulation.MetaFlow metaFlow, double prob) {

	    super();
	    this.elementType = elementType;
	    this.prob = prob;
	    this.metaFlow = metaFlow;
	}

	/**
	 * @return the elementType
	 */
	public es.ull.isaatc.simulation.ElementType getElementType() {

	    return elementType;
	}

	/**
	 * @param elementType
	 *                the elementType to set
	 */
	public void setElementType(
		es.ull.isaatc.simulation.ElementType elementType) {

	    this.elementType = elementType;
	}

	/**
	 * @return the metaFlowId
	 */
	public es.ull.isaatc.simulation.MetaFlow getMetaFlow() {

	    return metaFlow;
	}

	/**
	 * @return the prob
	 */
	public double getProb() {

	    return prob;
	}

	/**
	 * @param metaFlowId
	 *                the metaFlowId to set
	 */
	public void setMetaFlow(es.ull.isaatc.simulation.MetaFlow metaFlow) {

	    this.metaFlow = metaFlow;
	}

	/**
	 * @param prob
	 *                the prob to set
	 */
	public void setProb(double prob) {

	    this.prob = prob;
	}
    }
}
