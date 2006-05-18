package es.ull.cyc.simulation.bind;

/**
 * ModelCreator.java
 * 
 * Created on 6 February 2006
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import es.ull.cyc.simulation.bind.validation.ModelException;
import es.ull.cyc.simulation.bind.validation.ModelValidator;
import es.ull.cyc.simulation.results.SimulationResults;
import es.ull.cyc.util.Output;

/**
 * Extends the Simulation class. Creates all the needed components to model a
 * system from a description stored in a XML in memory structure.
 * 
 * @author Roberto Muñoz González
 */
public class ModelCreator extends es.ull.cyc.simulation.Simulation {
	/** XML model description in memory */
	es.ull.cyc.simulation.bind.Model xmlModel;

	/** XML experiement description in memory */
	es.ull.cyc.simulation.bind.Experiment xmlExperiment;

	/** List that matches a model ResourceType with an identifier */
	ArrayList<ResourceType> resType;

	/** List containing the model root flows */
	ArrayList<es.ull.cyc.simulation.MetaFlow> flowList;

	/** Relation between time periods (second, minute, hour, day, month, year) */
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
	public ModelCreator(es.ull.cyc.simulation.bind.XMLModel xmlModel, Output out) {
		super(xmlModel.getModel().getDescription(), 0.0, xmlModel
				.getExperiment().getEndTs(), out);
		this.xmlModel = xmlModel.getModel();
		this.xmlExperiment = xmlModel.getExperiment();
		this.modelValidator = new ModelValidator();
	}

	/**
	 * Creates the simulation from the data stored in <code>xmlModel<code>. Continues a previously
	 * saved simulation.
	 * @param xmlModel XML representation of the model
	 * @param 
	 */
	public ModelCreator(es.ull.cyc.simulation.bind.XMLModel xmlModel, Output out, SimulationResults res) {
		super(xmlModel.getModel().getDescription(), xmlModel.getExperiment().getEndTs(), out, res);
		this.xmlModel = xmlModel.getModel();
		this.xmlExperiment = xmlModel.getExperiment();
		this.modelValidator = new ModelValidator();
	}
	
	/**
	 * Creates de simulation model.
	 */
	protected void createModel() {
		baseTimeIndex = getTimeUnit(xmlModel.getBaseTimeUnit());
		createResourceTypes();
		createResources();
		createActivities();
	}

	/**
	 * Creates the simulation ResourceTypes.
	 */
	void createResourceTypes() {
		boolean hasError = false;
		Iterator<es.ull.cyc.simulation.bind.ResourceType> resTypeIt = xmlModel.getResourceType()
				.iterator();

		resType = new ArrayList<ResourceType>();
		while (resTypeIt.hasNext()) {
			es.ull.cyc.simulation.bind.ResourceType rt = resTypeIt.next();
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
	 * Creates the simulation Resources.
	 */
	void createResources() {
		boolean hasError = false;
		Iterator<es.ull.cyc.simulation.bind.Resource> resIt = xmlModel.getResource().iterator();
		Iterator<es.ull.cyc.simulation.bind.Resource.TimeTable> timeTableIt;

		while (resIt.hasNext()) {
			es.ull.cyc.simulation.bind.Resource res = resIt.next();
			try {
				if (modelValidator.validate(res)) {
					for (int i = 0; i < res.getNelem(); i++) {
						if (res.getTimeTable().size() > 0) {
							es.ull.cyc.simulation.Resource resource = new es.ull.cyc.simulation.Resource(
									res.getId() + i, this, res.getDescription());
							timeTableIt = res.getTimeTable().iterator();
							// Insert each time table entry as several roles entries
							while (timeTableIt.hasNext()) {
								es.ull.cyc.simulation.bind.Resource.TimeTable timeTable = timeTableIt
										.next();
								ArrayList<es.ull.cyc.simulation.ResourceType> roles = new ArrayList<es.ull.cyc.simulation.ResourceType>();
								Iterator<Integer> resTypeIdIt = timeTable.getRId()
										.iterator();
								while (resTypeIdIt.hasNext())
									roles.add(findResourceType(resTypeIdIt.next()));
								resource.addTimeTableEntry(createCycle(timeTable.getPeriod()), (int)(timeTable.getDur().getValue() * getTimeRelation(getTimeUnit(timeTable.getDur().getTimeUnit()), baseTimeIndex)), roles);
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
	 * Creates the simulation Activities
	 */
	protected void createActivities() {
		boolean hasError = false;
		Iterator<es.ull.cyc.simulation.bind.Activity> actListIt = xmlModel.getActivity().iterator();
		es.ull.cyc.simulation.bind.Activity act;

		while (actListIt.hasNext()) {
			try {
				act = actListIt.next();
				if (modelValidator.validate(act))
					createWorkGroups(act, new es.ull.cyc.simulation.Activity(
							act.getId(), this, act.getDescription(), act
									.getPriority(), act.isPresencial()));
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
	 *            xml Activity
	 * @param actModel
	 *            simulation Activity
	 */
	void createWorkGroups(es.ull.cyc.simulation.bind.Activity actXML,
			es.ull.cyc.simulation.Activity actModel) {
		Iterator<es.ull.cyc.simulation.bind.Activity.WorkGroup> wgListIt = actXML.getWorkGroup()
				.iterator();
		Iterator<es.ull.cyc.simulation.bind.Activity.WorkGroup.ResourceType> rtListIt;
		es.ull.cyc.simulation.bind.Activity.WorkGroup wgXML;
		es.ull.cyc.simulation.bind.Activity.WorkGroup.ResourceType rtXML;
		es.ull.cyc.simulation.WorkGroup wgModel;

		while (wgListIt.hasNext()) {
			wgXML = wgListIt.next();
			wgModel = actModel.getNewWorkGroup(wgXML.getId(), createPeriod(
					wgXML.getDuration(), getTimeUnit(actXML.getTimeUnit()),
					baseTimeIndex), wgXML.getPriority(), wgXML.getCost());
			rtListIt = wgXML.getResourceType().iterator();
			// Insert each resource type entry for this WorkGroup
			while (rtListIt.hasNext()) {
				rtXML = rtListIt.next();
				wgModel.add(this.findResourceType(rtXML.getRtId()), rtXML
						.getNeeded());
			}
		}
	}

	/**
	 * Creates the simulation meta flows
	 * 
	 */
	protected void createMetaFlows() {
		boolean hasError = false;
		Iterator<es.ull.cyc.simulation.bind.Flow> flowListIt = xmlModel.getFlow().iterator();

		flowList = new ArrayList<es.ull.cyc.simulation.MetaFlow>();
		while (flowListIt.hasNext()) {
			try {
				es.ull.cyc.simulation.bind.Flow flow = flowListIt.next();
				if (modelValidator.validate(flow))
					flowList.add(createMetaFlow(flow, null));
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
	 * Creates the generators for the simulation
	 * 
	 */
	protected void createGenerators() {
		createMetaFlows();
		Iterator<es.ull.cyc.simulation.bind.Generator> genIt = xmlExperiment.getGenerator()
				.iterator();
		while (genIt.hasNext()) {
			es.ull.cyc.simulation.bind.Generator gen = genIt.next();
			createGeneration(gen.getToGenerate()).createGenerators(this, createCycle(gen.getCycle()));
		}
	}

	/**
	 * Returns a simulation metaflow
	 * 
	 * @param XMLFlow
	 *            flow xml description
	 * @param parent
	 *            Metaflows parent
	 */
	es.ull.cyc.simulation.MetaFlow createMetaFlow(es.ull.cyc.simulation.bind.Flow XMLFlow,
			es.ull.cyc.simulation.MetaFlow parent) {
		es.ull.cyc.random.RandomNumber rn = createRandomNumber(XMLFlow
				.getIterations());

		if (XMLFlow.getType().equals(es.ull.cyc.simulation.bind.FlowType.SINGLE)) {
			if (parent instanceof es.ull.cyc.simulation.GroupMetaFlow)
				return new es.ull.cyc.simulation.SingleMetaFlow(
						XMLFlow.getId(),
						(es.ull.cyc.simulation.GroupMetaFlow) parent, rn,
						getActivity(XMLFlow.getActId()));
			else if (parent instanceof es.ull.cyc.simulation.OptionMetaFlow)
				return new es.ull.cyc.simulation.SingleMetaFlow(
						XMLFlow.getId(),
						(es.ull.cyc.simulation.OptionMetaFlow) parent, rn,
						getActivity(XMLFlow.getActId()));
			else
				return new es.ull.cyc.simulation.SingleMetaFlow(
						XMLFlow.getId(), rn, getActivity(XMLFlow.getActId()));
		}
		if (XMLFlow.getType().equals(es.ull.cyc.simulation.bind.FlowType.SEQUENCE)) {
			es.ull.cyc.simulation.SequenceMetaFlow seq = null;
			if (parent instanceof es.ull.cyc.simulation.OptionMetaFlow)
				seq = new es.ull.cyc.simulation.SequenceMetaFlow(XMLFlow
						.getId(),
						(es.ull.cyc.simulation.OptionMetaFlow) parent, rn);
			else if (parent instanceof es.ull.cyc.simulation.GroupMetaFlow)
				seq = new es.ull.cyc.simulation.SequenceMetaFlow(XMLFlow
						.getId(),
						(es.ull.cyc.simulation.GroupMetaFlow) parent, rn);
			else
				seq = new es.ull.cyc.simulation.SequenceMetaFlow(XMLFlow
						.getId(), rn);

			Iterator<es.ull.cyc.simulation.bind.Flow> flowListIt = XMLFlow.getFlow().iterator();
			while (flowListIt.hasNext()) {
				createMetaFlow(flowListIt.next(), seq);
			}
			return seq;
		}
		if (XMLFlow.getType().equals(es.ull.cyc.simulation.bind.FlowType.SIMULTANEOUS)) {
			es.ull.cyc.simulation.SimultaneousMetaFlow sim = null;
			if (parent instanceof es.ull.cyc.simulation.OptionMetaFlow)
				sim = new es.ull.cyc.simulation.SimultaneousMetaFlow(XMLFlow
						.getId(),
						(es.ull.cyc.simulation.OptionMetaFlow) parent, rn);
			else if (parent instanceof es.ull.cyc.simulation.GroupMetaFlow)
				sim = new es.ull.cyc.simulation.SimultaneousMetaFlow(XMLFlow
						.getId(),
						(es.ull.cyc.simulation.GroupMetaFlow) parent, rn);
			else
				sim = new es.ull.cyc.simulation.SimultaneousMetaFlow(XMLFlow
						.getId(), rn);
			Iterator<es.ull.cyc.simulation.bind.Flow> flowListIt = XMLFlow.getFlow().iterator();
			while (flowListIt.hasNext()) {
				createMetaFlow(flowListIt.next(), sim);
			}
			return sim;
		}
		if (XMLFlow.getType().equals(es.ull.cyc.simulation.bind.FlowType.DECISION)) {
			es.ull.cyc.simulation.DecisionMetaFlow dec = null;
			if (parent instanceof es.ull.cyc.simulation.OptionMetaFlow)
				dec = new es.ull.cyc.simulation.DecisionMetaFlow(XMLFlow
						.getId(),
						(es.ull.cyc.simulation.OptionMetaFlow) parent, rn);
			else if (parent instanceof es.ull.cyc.simulation.GroupMetaFlow)
				dec = new es.ull.cyc.simulation.DecisionMetaFlow(XMLFlow
						.getId(), (es.ull.cyc.simulation.GroupMetaFlow) parent,
						rn);
			else
				dec = new es.ull.cyc.simulation.DecisionMetaFlow(XMLFlow
						.getId(), rn);
			Iterator<es.ull.cyc.simulation.bind.Flow> flowListIt = XMLFlow.getFlow().iterator();
			while (flowListIt.hasNext()) {
				createOption(flowListIt.next(), dec);
			}
			return dec;
		}
		if (XMLFlow.getType().equals(es.ull.cyc.simulation.bind.FlowType.PACKED)) {
			es.ull.cyc.simulation.MetaFlow mf = createMetaFlow(dfs(xmlModel
					.getFlow(), XMLFlow.getFlowId()), parent);
			if (!parent.equals(mf))
				mf.setId(XMLFlow.getId());
			return mf;
		}
		return null;
	}

	/**
	 * Returns an option metaflow from its definition in XML
	 * 
	 * @param XMLFlow
	 * @param parent
	 */
	es.ull.cyc.simulation.OptionMetaFlow createOption(es.ull.cyc.simulation.bind.Flow XMLFlow,
			es.ull.cyc.simulation.DecisionMetaFlow parent) {
		es.ull.cyc.simulation.OptionMetaFlow opt = new es.ull.cyc.simulation.OptionMetaFlow(
				XMLFlow.getId(), parent, XMLFlow.getProb());
		createMetaFlow(XMLFlow, opt);
		return opt;
	}

	/**
	 * Creates a generation class object
	 * 
	 * @param xmlGeneration
	 */
	es.ull.cyc.simulation.Generation createGeneration(
			es.ull.cyc.simulation.bind.Generation xmlGeneration) {
		ArrayList<GenerationPair> probVector = compactProbTree(xmlGeneration
				.getProbTree());
		es.ull.cyc.simulation.Generation gen = new es.ull.cyc.simulation.Generation(
				createRandomNumber(xmlGeneration.getNElem()));

		Iterator<GenerationPair> probVectorIt = probVector.iterator();
		while (probVectorIt.hasNext()) {
			GenerationPair gp = probVectorIt.next();
			gen.add(gp.getMetaFlow(), gp.getProb());
		}
		return gen;
	}

	/**
	 * Returns a CompoundCycle or a Cycle from a definition in XML
	 * 
	 * @param xmlCycle
	 */
	es.ull.cyc.util.Cycle createCycle(es.ull.cyc.simulation.bind.Cycle xmlCycle) {

		int cycleTimeUnitIndex = getTimeUnit(xmlCycle.getTimeUnit());
		es.ull.cyc.random.RandomNumber rn = createPeriod(xmlCycle.getPeriod(),
				cycleTimeUnitIndex, baseTimeIndex);
		double relationTime = getTimeRelation(cycleTimeUnitIndex, baseTimeIndex);
		double startTs = xmlCycle.getStartTs() * relationTime;

		if (xmlCycle.getEndTs() != null) {
			double endTs = xmlCycle.getEndTs() * relationTime;
			if (xmlCycle.getSubCycle() == null) {
				return new es.ull.cyc.util.Cycle(startTs, rn, endTs);
			} else {
				return new es.ull.cyc.util.Cycle(startTs, rn, endTs,
						createCycle(xmlCycle.getSubCycle()));
			}
		} else {
			if (xmlCycle.getSubCycle() == null) {
				return new es.ull.cyc.util.Cycle(startTs, rn, xmlCycle
						.getIterations());
			} else {
				return new es.ull.cyc.util.Cycle(startTs, rn, xmlCycle
						.getIterations(), createCycle(xmlCycle.getSubCycle()));
			}
		}
	}

	/**
	 * Compact the probability tree and returns an ArrayList with the final
	 * probability for each root meta flow
	 * 
	 * @param probTree
	 */
	ArrayList<GenerationPair> compactProbTree(es.ull.cyc.simulation.bind.ProbTree probTree) {
		ArrayList<GenerationPair> probVector = new ArrayList<GenerationPair>();

		// if the node is hoja then return its probability
		if (probTree.getSubTree().size() == 0) {
			probVector.add(new GenerationPair(findMetaFlowById(probTree
					.getMetaFlow()), probTree.getProb()));
			return probVector;
		}
		Iterator<es.ull.cyc.simulation.bind.ProbTree> nodeIt = probTree.getSubTree().iterator();
		while (nodeIt.hasNext()) {
			probVector.addAll(compactProbTree(nodeIt.next()));
		}

		// Calculate the probability of each type
		for (int i = 0; i < probVector.size(); i++) {
			GenerationPair gp = probVector.get(i);
			gp.setProb(gp.getProb() * probTree.getProb());
			probVector.set(i, gp);
		}
		return probVector;
	}

	public boolean hasErrors() {
		return modelValidator.hasErrors();
	}

	/**
	 * Creates an object that represents a probability distribution
	 * 
	 * @param rnXML
	 *            probabiblity distribution expressed in XML
	 * @return RandomNumber subclass
	 */
	es.ull.cyc.random.RandomNumber createRandomNumber(es.ull.cyc.simulation.bind.RandomNumber rnXML) {
		// It isn't any data integrity check, data in rnXML should be correct
		if (rnXML == null)
			return new es.ull.cyc.random.Fixed(1);
		if (rnXML.getDist().equals(Distribution.BETA))
			return new es.ull.cyc.random.Beta(new Double(rnXML.getP1())
					.intValue(), new Double(rnXML.getP2()).intValue());
		if (rnXML.getDist().equals(Distribution.CHISQUARE))
			return new es.ull.cyc.random.ChiSquare(new Double(rnXML.getP1())
					.intValue());
		if (rnXML.getDist().equals(Distribution.ERLANG))
			return new es.ull.cyc.random.Erlang(rnXML.getP1(), rnXML.getP2());
		if (rnXML.getDist().equals(Distribution.EXPONENTIAL))
			return new es.ull.cyc.random.Exponential(rnXML.getP1());
		if (rnXML.getDist().equals(Distribution.FIXED))
			return new es.ull.cyc.random.Fixed(rnXML.getP1());
		if (rnXML.getDist().equals(Distribution.BETA))
			return new es.ull.cyc.random.Geometric(rnXML.getP1());
		if (rnXML.getDist().equals(Distribution.NORMAL))
			return new es.ull.cyc.random.Normal(rnXML.getP1(), rnXML.getP2());
		if (rnXML.getDist().equals(Distribution.POISSON))
			return new es.ull.cyc.random.Poisson(rnXML.getP1());
		if (rnXML.getDist().equals(Distribution.TRIANGULAR))
			return new es.ull.cyc.random.Triangular(rnXML.getP1(), rnXML
					.getP2(), rnXML.getP3());
		if (rnXML.getDist().equals(Distribution.UNIFORM))
			return new es.ull.cyc.random.Uniform(rnXML.getP1(), rnXML.getP2());
		return null;
	}

	/**
	 * 
	 * @param rn
	 * @param freqIndex
	 * @param baseIndex
	 * @return
	 */
	es.ull.cyc.random.RandomNumber createPeriod(es.ull.cyc.simulation.bind.RandomNumber rn,
			int periodIndex, int baseIndex) {
		double value = getTimeRelation(periodIndex, baseIndex);

		es.ull.cyc.simulation.bind.RandomNumber newRn = new es.ull.cyc.simulation.bind.RandomNumber();
		newRn.setDist(rn.getDist());
		newRn.setP1(rn.getP1() * value);
		newRn.setP2(rn.getP2() * value);
		newRn.setP3(rn.getP3() * value);

		return createRandomNumber(newRn);
	}

	/**
	 * Finds and returns a ResourceType.
	 * 
	 * @param id
	 *            ResourceType identifier
	 * @return simulation ResourceType
	 */
	es.ull.cyc.simulation.ResourceType findResourceType(int id) {
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
	 * Returns the meta flow that matches with the id
	 * 
	 * @param id
	 */
	es.ull.cyc.simulation.MetaFlow findMetaFlowById(int id) {
		es.ull.cyc.simulation.MetaFlow mf = null;
		Iterator<es.ull.cyc.simulation.MetaFlow> mfIt = flowList.iterator();

		while (mfIt.hasNext()) {
			mf = mfIt.next();
			if (mf.getId() == id)
				return mf;
		}
		return null;
	}

	/**
	 * Implements a depth first search in the flows structure
	 * 
	 * @param flowList
	 *            flow descendants
	 * @param id
	 *            searched flow identifier
	 * @return searched flow or null if its not found
	 */
	protected es.ull.cyc.simulation.bind.Flow dfs(List<es.ull.cyc.simulation.bind.Flow> flowList, int id) {
		if (flowList.size() == 0)
			return null;
		Iterator<es.ull.cyc.simulation.bind.Flow> flowIt = flowList.iterator();
		while (flowIt.hasNext()) {
			es.ull.cyc.simulation.bind.Flow flow = flowIt.next();
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
	 * Returns the index in the timeRelations vector for the value
	 */
	int getTimeUnit(es.ull.cyc.simulation.bind.CommonFreq value) {

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

	/**
	 * Matches an identifier with a simulation ResourceType. This class is used
	 * by some methods in ModelCreator to find a ResourceType.
	 * 
	 * @author Roberto Muñoz
	 */
	private class ResourceType {
		/** ResourceType identifier */
		int id;

		/** Simulation ResourceType */
		es.ull.cyc.simulation.ResourceType resourceType;

		/**
		 * Creates a simulation ResourceType from the description in XML. Assign
		 * an identifier to it.
		 * 
		 * @param rt
		 *            XML ResourceType description
		 * @param mod
		 *            simulation model
		 */
		public ResourceType(es.ull.cyc.simulation.bind.ResourceType rt,
				es.ull.cyc.simulation.Simulation mod) {
			super();
			resourceType = new es.ull.cyc.simulation.ResourceType(rt.getId(),
					mod, rt.getDescription());
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
		public es.ull.cyc.simulation.ResourceType getResourceType() {
			return resourceType;
		}
	}

	/**
	 * This class pair a metaflow with a probability
	 */
	private class GenerationPair {
		double prob;

		es.ull.cyc.simulation.MetaFlow metaFlow;

		/**
		 * @param prob
		 * @param metaFlowId
		 */
		public GenerationPair(es.ull.cyc.simulation.MetaFlow metaFlow,
				double prob) {
			super();
			this.prob = prob;
			this.metaFlow = metaFlow;
		}

		/**
		 * @return the metaFlowId
		 */
		public es.ull.cyc.simulation.MetaFlow getMetaFlow() {
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
		 *            the metaFlowId to set
		 */
		public void setMetaFlow(es.ull.cyc.simulation.MetaFlow metaFlow) {
			this.metaFlow = metaFlow;
		}

		/**
		 * @param prob the prob to set
		 */
		public void setProb(double prob) {
			this.prob = prob;
		}
	}
}
