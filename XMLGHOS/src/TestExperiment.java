import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.simulation.state.NullStateProcessor;
import es.ull.isaatc.simulation.state.StateProcessor;
import es.ull.isaatc.util.Output;

/**
 * @author Roberto Muñoz
 */
public class TestExperiment extends Experiment {
	
	
	private es.ull.isaatc.simulation.xml.XMLModel xmlModel;
	
	
	private es.ull.isaatc.simulation.xml.Experiment xmlExperiment;
	
	
	private ArrayList<SimulationListener> listenerList = new ArrayList<SimulationListener>();
	
	
	private Output out;
	
	
	
	public TestExperiment(es.ull.isaatc.simulation.xml.XMLModel xmlModel) {

		super();
		this.xmlModel = xmlModel;
		this.xmlExperiment = xmlModel.getExperiment();
		initialize();
	}
	
	
	protected void initialize() {

		setDescription(xmlExperiment.getSimulation());
		setNExperiments(xmlExperiment.getExperiments());
		setProcessor(new NullStateProcessor());
		createOutput();
		createListeners();
	}
	
	
	protected void createListeners() {

		Iterator<es.ull.isaatc.simulation.xml.ClassReference> listenerIt = xmlExperiment
				.getSimulationListener().iterator();
		while (listenerIt.hasNext()) {
			listenerList.add((SimulationListener) processClassReference(listenerIt
					.next(), SimulationListener.class));
		}
	}
	
	
	protected void createOutput() {

		out = new Output(xmlModel.getDebugMode());
	}
	
	
	protected Object processClassReference(
			es.ull.isaatc.simulation.xml.ClassReference sp, Class c) {

		try {
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
				Method setter = instance.getClass().getMethod("set" + param.getName(),
						pType);
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

		Simulation sim = new es.ull.isaatc.simulation.xml.ModelCreator(xmlModel,
				out);
		for (SimulationListener listener : listenerList)
			sim.addListener(listener);
		return sim;
	}
}
