package es.ull.isaatc.simulation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationListener;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;
import es.ull.isaatc.simulation.listener.xml.XMLListenerController;
import es.ull.isaatc.simulation.state.processor.NullStateProcessor;
import es.ull.isaatc.util.ClassPathHacker;
import es.ull.isaatc.util.Output;

/**
 * Creates an experiment from a description in XML.
 * @author Roberto Mu�oz
 */
public class XMLExperiment extends PooledExperiment {

	protected es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper;

	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;

	private ArrayList<SimulationListener> listenerList = new ArrayList<SimulationListener>();

	private XMLListenerController listenerController = new XMLListenerController();
	
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
		setProcessor(new NullStateProcessor());
		out = new Output(xmlWrapper.getDebugMode());
	}

	protected void createListeners() {

		Iterator<es.ull.isaatc.simulation.xml.ClassReference> listenerIt = xmlExperiment
				.getSimulationListener().iterator();
		listenerList.clear();
		while (listenerIt.hasNext()) {
			listenerList.add((SimulationListener) processClassReference(
					listenerIt.next(), SimulationListener.class));
		}
	}

	protected Object processClassReference(
			es.ull.isaatc.simulation.xml.ClassReference sp, Class c) {

		try {
			if (sp.getClasspath() != null) {
				try {
					ClassPathHacker.addFile(sp.getClasspath());
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("ERROR : classpath not found");
				}
			}
			Object instance = c.cast(Class.forName(sp.getName()).newInstance());
			Iterator<es.ull.isaatc.simulation.xml.ClassReference.Param> paramIt = sp
					.getParam().iterator();
			while (paramIt.hasNext()) {
				es.ull.isaatc.simulation.xml.ClassReference.Param param = paramIt
						.next();
				Class pType[] = new Class[1];
				Object pValue[] = new Object[1];
				String type = param.getType();
				if (type.equals("int")) {
					pType[0] = int.class;
					pValue[0] = new Integer(param.getValue());
				}
				if (type.equals("float")) {
					pType[0] = float.class;
					pValue[0] = new Float(param.getValue());
				}
				if (type.equals("double")) {
					pType[0] = double.class;
					pValue[0] = new Double(param.getValue());
				}
				if (type.equals("String")) {
					pType[0] = String.class;
					pValue[0] = param.getValue();
				}
				Method setter = instance.getClass().getMethod(
						"set" + param.getName(), pType);
				setter.invoke(instance, pValue);
			}
			return instance;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = XMLSimulationFactory.getSimulation(ind, xmlWrapper);
		sim.setStartTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment.getStartTs().getValue(), xmlExperiment.getStartTs().getTimeUnit(), ((XMLSimulation)sim).getBaseTimeIndex()));
		sim.setEndTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment.getEndTs().getValue(), xmlExperiment.getEndTs().getTimeUnit(), ((XMLSimulation)sim).getBaseTimeIndex()));
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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Experiment#end()
	 */
	@Override
	protected void end() {
		super.end();
		System.out.println(listenerController);
	}
}
