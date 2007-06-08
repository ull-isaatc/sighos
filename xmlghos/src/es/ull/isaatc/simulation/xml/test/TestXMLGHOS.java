package es.ull.isaatc.simulation.xml.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import es.ull.isaatc.simulation.XMLExperiment;
import es.ull.isaatc.simulation.xml.XMLWrapper;

class Prueba {
	public enum TestMode {
		FILE, STREAM
	};

	private static String modelFileName = "d:/isaatc/workspace/XMLGHOS/src/es/ull/isaatc/simulation/xml/test/model.xml";

	private static String experimentFileName = "d:/isaatc/workspace/XMLGHOS/src/es/ull/isaatc/simulation/xml/test/experiment.xml";

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
			XMLExperiment exp = new XMLExperiment(xmlModel);
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
		
		Prueba test = new Prueba(Prueba.TestMode.valueOf(arg[0]));
	}
}
