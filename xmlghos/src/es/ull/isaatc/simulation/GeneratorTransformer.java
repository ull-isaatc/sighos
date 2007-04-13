/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

import es.ull.isaatc.simulation.xml.FlowChoiceUtility;
import es.ull.isaatc.simulation.xml.FunctionChoiceUtility;
import es.ull.isaatc.util.Cycle;

/**
 * @author Roberto
 * 
 */
public class GeneratorTransformer {
	/** List containing the model root flows */
	private HashMap<Integer, MetaFlow> flowList;
	/** The XML simulation instance */
	private XMLSimulation simul;
	/** XML model description in memory */
	private es.ull.isaatc.simulation.xml.Model xmlModel;
	/** XML experiment description in memory */
	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;
	

	
	/**
	 * @param simul The simulation
	 */
	public GeneratorTransformer(XMLSimulation simul) {
		super();
		this.simul = simul;
		this.xmlModel = simul.getXmlModel();
		this.xmlExperiment = simul.getXmlExperiment();
	}

	/**
	 * Creates the generators for the simulation
	 */
	protected void createGenerators() {
		createMetaFlows();
		for (es.ull.isaatc.simulation.xml.Generator genXML : xmlExperiment.getGenerator())
			createGenerator(genXML);
	}
	/**
	 * Creates the simulation meta flows
	 */
	protected void createMetaFlows() {
		flowList = new HashMap<Integer, es.ull.isaatc.simulation.MetaFlow>();
		for (es.ull.isaatc.simulation.xml.RootFlow rfXML : xmlModel.getRootFlow())
			flowList.put(rfXML.getId(), createMetaFlow(FlowChoiceUtility
					.getSelectedFlow(rfXML.getFlow()), null));
	}

	/**
	 * Returns a simulation metaflow
	 * @param xmlFlow xml flow description
	 * @param parent Metaflows parent
	 */
	protected MetaFlow createMetaFlow(es.ull.isaatc.simulation.xml.Flow xmlFlow, MetaFlow parent) {

		RandomVariate rn = XMLSimulationFactory.createCompoundRandomNumber(xmlFlow.getIterations(), 1);
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.SingleFlow) {
			es.ull.isaatc.simulation.xml.SingleFlow xmlSF = (es.ull.isaatc.simulation.xml.SingleFlow) xmlFlow;
			if (parent instanceof GroupMetaFlow)
				return new SingleMetaFlow(xmlFlow.getId(), (GroupMetaFlow) parent, rn,
						simul.getActivity(xmlSF.getActRef().getId()));
			else if (parent instanceof OptionMetaFlow)
				return new SingleMetaFlow(xmlFlow.getId(), (OptionMetaFlow) parent, rn,
						simul.getActivity(xmlSF.getActRef().getId()));
			else if (parent instanceof TypeBranchMetaFlow)
				return new SingleMetaFlow(xmlFlow.getId(), (TypeBranchMetaFlow) parent, rn,
						simul.getActivity(xmlSF.getActRef().getId()));
			else
				return new SingleMetaFlow(xmlFlow.getId(), rn,
						simul.getActivity(xmlSF.getActRef().getId()));
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.PackageFlow) {
			es.ull.isaatc.simulation.xml.PackageFlow xmlPF = (es.ull.isaatc.simulation.xml.PackageFlow) xmlFlow;
			MetaFlow mf = createMetaFlow(
					rootFlowSearch(xmlModel.getRootFlow(), xmlPF.getRootFlowRef().getId()), parent);
			if (!parent.equals(mf))
				mf.setId(xmlPF.getId());
			return mf;
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.ExitFlow) {
			es.ull.isaatc.simulation.xml.ExitFlow XMLef = (es.ull.isaatc.simulation.xml.ExitFlow) xmlFlow;
			if (parent instanceof GroupMetaFlow)
				return new ExitMetaFlow(XMLef.getId(), (GroupMetaFlow) parent);
			if (parent instanceof OptionMetaFlow)
				return new ExitMetaFlow(XMLef.getId(), (OptionMetaFlow) parent);
			if (parent instanceof TypeBranchMetaFlow)
				return new ExitMetaFlow(XMLef.getId(), (TypeBranchMetaFlow) parent);
			else
				return new ExitMetaFlow(XMLef.getId());
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.SequenceFlow) {
			es.ull.isaatc.simulation.xml.SequenceFlow XMLsf = (es.ull.isaatc.simulation.xml.SequenceFlow) xmlFlow;
			SequenceMetaFlow seq = null;
			if (parent instanceof OptionMetaFlow)
				seq = new SequenceMetaFlow(XMLsf.getId(), (OptionMetaFlow) parent, rn);
			else if (parent instanceof TypeBranchMetaFlow)
				seq = new SequenceMetaFlow(XMLsf.getId(), (TypeBranchMetaFlow) parent, rn);
			else if (parent instanceof GroupMetaFlow)
				seq = new SequenceMetaFlow(XMLsf.getId(), (GroupMetaFlow) parent, rn);
			else
				seq = new SequenceMetaFlow(XMLsf.getId(), rn);

			Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = XMLsf.getSingleOrPackageOrSequence().iterator();
			while (flowListIt.hasNext()) {
				createMetaFlow(flowListIt.next(), seq);
			}
			return seq;
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.SimultaneousFlow) {
			es.ull.isaatc.simulation.xml.SimultaneousFlow XMLsf = (es.ull.isaatc.simulation.xml.SimultaneousFlow) xmlFlow;
			SimultaneousMetaFlow sim = null;
			if (parent instanceof OptionMetaFlow)
				sim = new SimultaneousMetaFlow(XMLsf.getId(), (OptionMetaFlow) parent, rn);
			else if (parent instanceof TypeBranchMetaFlow)
				sim = new SimultaneousMetaFlow(XMLsf.getId(), (TypeBranchMetaFlow) parent, rn);
			else if (parent instanceof GroupMetaFlow)
				sim = new SimultaneousMetaFlow(XMLsf.getId(), (GroupMetaFlow) parent, rn);
			else
				sim = new SimultaneousMetaFlow(XMLsf.getId(), rn);
			Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = XMLsf.getSingleOrPackageOrSequence().iterator();
			while (flowListIt.hasNext()) {
				createMetaFlow(flowListIt.next(), sim);
			}
			return sim;
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.DecisionFlow) {
			es.ull.isaatc.simulation.xml.DecisionFlow XMLdf = (es.ull.isaatc.simulation.xml.DecisionFlow) xmlFlow;
			DecisionMetaFlow dec = null;
			if (parent instanceof OptionMetaFlow)
				dec = new DecisionMetaFlow(XMLdf.getId(), (OptionMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else if (parent instanceof TypeBranchMetaFlow)
				dec = new DecisionMetaFlow(XMLdf.getId(), (TypeBranchMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else if (parent instanceof GroupMetaFlow)
				dec = new DecisionMetaFlow(XMLdf.getId(), (GroupMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else
				dec = new DecisionMetaFlow(XMLdf.getId(), RandomVariateFactory.getInstance("ConstantVariate", 1));
			Iterator<es.ull.isaatc.simulation.xml.DecisionOption> optionListIt = XMLdf.getOption().iterator();
			while (optionListIt.hasNext()) {
				createOption(optionListIt.next(), dec);
			}
			return dec;
		}
		if (xmlFlow instanceof es.ull.isaatc.simulation.xml.TypeFlow) {
			es.ull.isaatc.simulation.xml.TypeFlow XMLtf = (es.ull.isaatc.simulation.xml.TypeFlow) xmlFlow;
			TypeMetaFlow type = null;
			if (parent instanceof OptionMetaFlow)
				type = new TypeMetaFlow(XMLtf.getId(), (OptionMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else if (parent instanceof TypeBranchMetaFlow)
				type = new TypeMetaFlow(XMLtf.getId(), (TypeBranchMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else if (parent instanceof GroupMetaFlow)
				type = new TypeMetaFlow(XMLtf.getId(), (GroupMetaFlow) parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
			else
				type = new TypeMetaFlow(XMLtf.getId(), RandomVariateFactory.getInstance("ConstantVariate", 1));
			Iterator<es.ull.isaatc.simulation.xml.TypeBranch> branchListIt = XMLtf.getBranch().iterator();
			while (branchListIt.hasNext()) {
				createTypeBranch(branchListIt.next(), type);
			}
			return type;
		}
		return null;
	}

	/**
	 * Returns an option metaflow from its definition in XML
	 * @param XMLFlow
	 * @param parent
	 */
	protected OptionMetaFlow createOption(es.ull.isaatc.simulation.xml.DecisionOption xmlOption,
			DecisionMetaFlow parent) {

		OptionMetaFlow opt = new OptionMetaFlow(xmlOption.getId(), parent, xmlOption.getProb());
		createMetaFlow(FlowChoiceUtility.getSelectedFlow(xmlOption), opt);
		return opt;
	}

	/**
	 * Returns a type branch metaflow from its definition in XML
	 * @param XMLFlow
	 * @param parent
	 */
	protected TypeBranchMetaFlow createTypeBranch(es.ull.isaatc.simulation.xml.TypeBranch XMLBranch,
			TypeMetaFlow parent) {
		ArrayList<ElementType> elementTypes = new ArrayList<ElementType>();
		for (es.ull.isaatc.simulation.xml.ComponentRef ref : XMLBranch.getElementType())
			elementTypes.add(simul.getElementType(ref.getId()));
		TypeBranchMetaFlow branch = new TypeBranchMetaFlow(XMLBranch.getId(), parent, elementTypes);
		createMetaFlow(FlowChoiceUtility.getSelectedFlow(XMLBranch), branch);
		return branch;
	}

	/**
	 * Creates a generation class object
	 * 
	 * @param xmlGeneration
	 */
	protected Generator createGenerator(
			es.ull.isaatc.simulation.xml.Generator xmlGenerator) {

		Cycle cycle = XMLSimulationFactory.createCycle(xmlGenerator.getCycle(), simul.getBaseTimeIndex());
		es.ull.isaatc.simulation.xml.Generation xmlGeneration = xmlGenerator.getToGenerate();
		ArrayList<GenerationTrio> probVector = compactProbTree(xmlGeneration.getProbTree());
		ElementCreator elementCreator = new ElementCreator(XMLSimulationFactory.createFunction(
				FunctionChoiceUtility.getSelectedFunction(xmlGeneration.getNElem()), 1));
		for (GenerationTrio gt : probVector) {
			elementCreator.add(gt.getElementType(), gt.getMetaFlow(), gt.getProb());
		}
		return new TimeDrivenGenerator(simul, elementCreator, cycle);

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
	 * Compact the probability tree and returns an <code>ArrayList</code>
	 * with the final probability for each root meta flow
	 * 
	 * @param probTree
	 */
	protected ArrayList<GenerationTrio> compactProbTree(
			es.ull.isaatc.simulation.xml.ProbTree probTree) {

		ArrayList<GenerationTrio> probVector = new ArrayList<GenerationTrio>();

		if (probTree.getSubTree().size() == 0) {
			probVector.add(new GenerationTrio(
					simul.getElementType(probTree.getElementType().getId()),
					flowList.get(probTree.getMetaFlow().getId()), probTree.getProb()));
			return probVector;
		}

		Iterator<es.ull.isaatc.simulation.xml.ProbTree> nodeIt = probTree.getSubTree().iterator();
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
	 * @return the flowList
	 */
	public HashMap<Integer, MetaFlow> getFlowList() {
		return flowList;
	}
	
	/**
	 * This class match a meta flow and an element type with a probability
	 */
	private class GenerationTrio {

		/** The probability of this trio. */
		private double prob;
		/** The element type. */
		private ElementType elementType;
		/** The meta flow. */
		private MetaFlow metaFlow;

		/**
		 * @param prob
		 * @param metaFlowId
		 */
		public GenerationTrio(ElementType elementType, MetaFlow metaFlow, double prob) {

			super();
			this.elementType = elementType;
			this.prob = prob;
			this.metaFlow = metaFlow;
		}

		/**
		 * @return the elementType
		 */
		public ElementType getElementType() {

			return elementType;
		}

		/**
		 * @param elementType the elementType to set
		 */
		public void setElementType(ElementType elementType) {

			this.elementType = elementType;
		}

		/**
		 * @return the metaFlow 
		 */
		public MetaFlow getMetaFlow() {

			return metaFlow;
		}

		/**
		 * @return the prob
		 */
		public double getProb() {

			return prob;
		}

		/**
		 * @param metaFlowId the metaFlow identifier to set
		 */
		public void setMetaFlow(MetaFlow metaFlow) {

			this.metaFlow = metaFlow;
		}

		/**
		 * @param prob the probability to set
		 */
		public void setProb(double prob) {

			this.prob = prob;
		}
	}
}
