package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.listener.ActivityListener;
import es.ull.isaatc.simulation.listener.ActivityTimeListener;
import es.ull.isaatc.simulation.listener.ElementStartFinishListener;
import es.ull.isaatc.simulation.listener.ElementTypeTimeListener;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;
import es.ull.isaatc.simulation.listener.SelectableActivityListener;
import es.ull.isaatc.simulation.listener.SelectableActivityTimeListener;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;
import es.ull.isaatc.simulation.listener.xml.XMLListenerController;
import es.ull.isaatc.simulation.state.processor.NullStateProcessor;
import es.ull.isaatc.util.Output;

/**
 * Creates an experiment from a description in XML.
 * 
 * @author Roberto Muñoz
 */
public class XMLExperiment extends PooledExperiment {

	protected es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper;

	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;

	private ArrayList<SimulationListener> listenerList = new ArrayList<SimulationListener>();

	protected XMLListenerController listenerController = new XMLListenerController();

	private Output out;

	public XMLExperiment(es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper) {
		super();
		this.xmlWrapper = xmlWrapper;
		this.xmlExperiment = xmlWrapper.getExperiment();
		initialize();
	}

	protected void initialize() {
		tp = Executors.newSingleThreadExecutor();
		setDescription(xmlExperiment.getSimulation());
		setNExperiments(xmlExperiment.getExperiments());
		listenerController.setExperiments(this.xmlExperiment.getExperiments()); // Number of experiments
		setProcessor(new NullStateProcessor());
		out = new Output(xmlWrapper.getDebugMode());
	}

	/**
	 * Creates the simulation listeners from the description in XML
	 */
	protected void createListeners() {

		Iterator<es.ull.isaatc.simulation.xml.ClassReference> listenerIt = xmlExperiment.getSimulationListener().iterator();
		listenerList.clear();
		while (listenerIt.hasNext()) {
			listenerList.add((SimulationListener) XMLSimulationFactory.processClassReference(
					listenerIt.next(), SimulationListener.class));
		}

		for (es.ull.isaatc.simulation.xml.ListenerDescription listenerDesc : xmlExperiment
				.getListeners().getActivityListenerOrActivityTimeListenerOrElementStartFinishListener()) {
			listenerList.add(getSimulationListener(listenerDesc));
		}
	}

	protected SimulationListener getSimulationListener(
			es.ull.isaatc.simulation.xml.ListenerDescription listenerDesc) {
		
		if (listenerDesc instanceof es.ull.isaatc.simulation.xml.ActivityListener) {
			es.ull.isaatc.simulation.xml.ActivityListener xmlActListener = (es.ull.isaatc.simulation.xml.ActivityListener) listenerDesc;
			return new ActivityListener(xmlActListener.getPeriod());
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.ActivityTimeListener) {
			es.ull.isaatc.simulation.xml.ActivityTimeListener xmlListener = (es.ull.isaatc.simulation.xml.ActivityTimeListener) listenerDesc;
			return new ActivityTimeListener(xmlListener.getPeriod());
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.ElementStartFinishListener) {
			es.ull.isaatc.simulation.xml.ElementStartFinishListener xmlListener = (es.ull.isaatc.simulation.xml.ElementStartFinishListener) listenerDesc;
			return new ElementStartFinishListener(xmlListener.getPeriod());
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.ElementTypeTimeListener) {
			es.ull.isaatc.simulation.xml.ElementTypeTimeListener xmlListener = (es.ull.isaatc.simulation.xml.ElementTypeTimeListener) listenerDesc;
			return new ElementTypeTimeListener(xmlListener.getPeriod());
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.ResourceStdUsageListener) {
			es.ull.isaatc.simulation.xml.ResourceStdUsageListener xmlListener = (es.ull.isaatc.simulation.xml.ResourceStdUsageListener) listenerDesc;
			return new ResourceStdUsageListener(xmlListener.getPeriod());
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.SelectableActivityListener) {
			es.ull.isaatc.simulation.xml.SelectableActivityListener xmlListener = (es.ull.isaatc.simulation.xml.SelectableActivityListener) listenerDesc;
			SelectableActivityListener selListener = new SelectableActivityListener(xmlListener.getPeriod());
			for (int a : xmlListener.getActId())
				selListener.listenAct(a);
			return selListener;
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.SelectableActivityTimeListener) {
			es.ull.isaatc.simulation.xml.SelectableActivityTimeListener xmlListener = (es.ull.isaatc.simulation.xml.SelectableActivityTimeListener) listenerDesc;
			SelectableActivityTimeListener selListener = new SelectableActivityTimeListener(xmlListener.getPeriod());
			for (int a : xmlListener.getActId())
				selListener.listenAct(a);
			return selListener;
		} else if (listenerDesc instanceof es.ull.isaatc.simulation.xml.SimulationTimeListener)
			return new SimulationTimeListener();
		else {
			System.out.println("Selected listener not found");
			return null;
		}
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = XMLSimulationFactory.getSimulation(ind, xmlWrapper);
		sim.setStartTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment
				.getStartTs().getValue(), xmlExperiment.getStartTs()
				.getTimeUnit(), ((XMLSimulation) sim).getBaseTimeIndex()));
		sim.setEndTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment
				.getEndTs().getValue(), xmlExperiment.getEndTs().getTimeUnit(),
				((XMLSimulation) sim).getBaseTimeIndex()));
		sim.setOutput(out);

		createListeners();
		ListenerController cont = new ListenerController();
		SimulationListener timeListener = new SimulationTimeListener();
		listenerList.add(timeListener);

		listenerController.addAll(ind, listenerList);

		for (SimulationListener listener : listenerList)
			cont.addListener(listener);
		cont.addListener(timeListener);
		sim.setListenerController(cont);

		return sim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.Experiment#end()
	 */
	@Override
	protected void end() {
		super.end();
//		System.out.println(listenerController.getXML());
//		System.out.println(listenerController.toString());
	}
}
