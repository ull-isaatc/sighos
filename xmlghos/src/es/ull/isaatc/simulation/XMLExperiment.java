package es.ull.isaatc.simulation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.info.SimulationTimeListener;
import es.ull.isaatc.simulation.state.NullStateProcessor;
import es.ull.isaatc.util.ClassPathHacker;
import es.ull.isaatc.util.Output;

/**
 * Creates an experiment from a description in XML.
 * @author Roberto Muñoz
 */
public class XMLExperiment extends Experiment {

	protected es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper;

	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;

	private ArrayList<SimulationListener> listenerList = new ArrayList<SimulationListener>();

	private Output out;

	public XMLExperiment(es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper) {

		super();
		this.xmlWrapper = xmlWrapper;
		this.xmlExperiment = xmlWrapper.getExperiment();
		initialize();
	}

	protected void initialize() {

		setDescription(xmlExperiment.getSimulation());
		setNExperiments(xmlExperiment.getExperiments());
		setProcessor(new NullStateProcessor());
		createOutput();
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

	protected void createOutput() {

		out = new Output(xmlWrapper.getDebugMode());
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
		Simulation sim = XMLSimulationFactory.getSimulation(xmlWrapper);
		setStartTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment.getStartTs().getValue(), xmlExperiment.getStartTs().getTimeUnit(), ((XMLSimulation)sim).getBaseTimeIndex()));
		setEndTs(XMLSimulationFactory.getNormalizedTime(xmlExperiment.getEndTs().getValue(), xmlExperiment.getEndTs().getTimeUnit(), ((XMLSimulation)sim).getBaseTimeIndex()));
		sim.setOutput(out);
		
		createListeners();
		sim.addListener(new SimulationTimeListener() {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.println("CPU time :\t" + this);
			}
		});
		for (SimulationListener listener : listenerList)
			sim.addListener(listener);
		return sim;
	}
}
