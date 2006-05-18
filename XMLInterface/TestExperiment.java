import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;


import es.ull.cyc.simulation.Experiment;
import es.ull.cyc.simulation.Simulation;
import es.ull.cyc.simulation.bind.ModelCreator;
import es.ull.cyc.simulation.results.ResultProcessor;
import es.ull.cyc.simulation.results.SimulationResults;
import es.ull.cyc.util.Output;

/**
 * @author Roberto Muñoz
 *
 */
public class TestExperiment extends Experiment {

	class NullResultProcessor implements ResultProcessor {

		public void processStatistics(SimulationResults[] results) {
		}		
	}

	es.ull.cyc.simulation.bind.XMLModel xmlModel;
	es.ull.cyc.simulation.bind.Experiment xmlExperiment;
	double prevStart = 0.0, prevEnd = 0.0;
	
	public TestExperiment(es.ull.cyc.simulation.bind.XMLModel xmlModel) {
		super();
		this.xmlModel = xmlModel;
		this.xmlExperiment = xmlModel.getExperiment();
		this.prevStart = xmlExperiment.getPrevStart();
		this.prevEnd = xmlExperiment.getPrevEnd();
		initialize();
	}
	
	protected void initialize() {
		setDescription(xmlExperiment.getSimulation());
		setNExperiments(xmlExperiment.getExperiments());
		createResultClass();
		createOutput();
	}
	
	protected void createResultClass() {
		es.ull.cyc.simulation.bind.ResultClass rc = xmlExperiment.getResultClass();
		try {
			ResultProcessor proc = (ResultProcessor) Class.forName(rc.getName()).newInstance();
			Iterator<es.ull.cyc.simulation.bind.ResultClass.Param> paramIt = rc.getParam().iterator();
			while (paramIt.hasNext()) {
				es.ull.cyc.simulation.bind.ResultClass.Param param = paramIt.next();
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
				Method setter = proc.getClass().getMethod("set" + param.getName(), pType);
				setter.invoke(proc, pValue);
			}
			setProc(proc);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
		}
	}
	
	protected void createOutput() {
		setOut(new Output(xmlModel.getDebugMode()));
	}
	
	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.Experiment#getSimulation(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		if (Double.compare(prevEnd, 0.0) != 0)
			return new ModelCreator(xmlModel, out, new RecoverBackupSimulation(prevStart, prevEnd, ind, "C:"));
		return new ModelCreator(xmlModel, out);
	}

}
