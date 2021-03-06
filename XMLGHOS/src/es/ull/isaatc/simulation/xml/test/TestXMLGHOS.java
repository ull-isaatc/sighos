package es.ull.isaatc.simulation.xml.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.XMLExperiment;
import es.ull.isaatc.simulation.xml.XMLWrapper;

class PruebaExp extends XMLExperiment {

	public PruebaExp(XMLWrapper xmlWrapper) {
		super(xmlWrapper);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ull.isaatc.simulation.XMLExperiment#getSimulation(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = super.getSimulation(ind);
//		SelectableActivityTimeListener listener = new SelectableActivityTimeListener();
//		listener.setPeriod(24);
//		listener.listenAct(1);
//		sim.getListenerController().addListener(listener);
//		listenerController.add(ind, listener);
		return sim;
	}

}

class Prueba {
	public enum TestMode {
		FILE, STREAM
	};

//	private static String modelFileName = "C:/Documents and Settings/yuyu/sighosws/XMLGHOS/src/es/ull/isaatc/simulation/xml/test/model.xml";
//	private static String experimentFileName = "C:/Documents and Settings/yuyu/sighosws/XMLGHOS/src/es/ull/isaatc/simulation/xml/test/experiment.xml";
//	private static String modelFileName = "C:\\Documents and Settings\\Ordenador2\\workspace\\XMLGHOS\\src\\es\\ull\\isaatc\\simulation\\xml\\test\\model.xml";
//	private static String experimentFileName = "C:\\Documents and Settings\\Ordenador2\\workspace\\XMLGHOS\\src\\es\\ull\\isaatc\\simulation\\xml\\test\\experiment.xml";
	private static String modelFileName = "C:\\Documents and Settings\\Ordenador2\\Escritorio\\Presidencia\\Proc1 model.xml";
	private static String experimentFileName = "C:\\Documents and Settings\\Ordenador2\\Escritorio\\Presidencia\\Proc1 experiment.xml";

	public Prueba(TestMode mode) {
		XMLWrapper xmlModel = null;

		try {
			switch (mode) {
			case FILE:
				xmlModel = new XMLWrapper(modelFileName, null,
						experimentFileName);
				break;
			case STREAM:
				xmlModel = new XMLWrapper(new StringReader(
						getContent(modelFileName)), null, new StringReader(
						getContent(experimentFileName)));
				break;
			}

			PruebaExp exp = new PruebaExp(xmlModel);

			exp.start();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getContent(String fileName) {
		StringBuffer result = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {
				result.append(str);
				result.append(System.getProperty("line.separator"));
			}
			in.close();
		} catch (IOException e) {

		}
		return result.toString();
	}

}

public class TestXMLGHOS {

	public static void main(String arg[]) {

		new Prueba(Prueba.TestMode.valueOf(arg[0]));
	}
}
