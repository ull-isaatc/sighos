package es.ull.isaatc.simulation;

import java.io.FileNotFoundException;

import es.ull.isaatc.simulation.xml.XMLWrapper;

public class XMLGHOS {

	private static int NORMAL_PARAMS = 2;

	private static int SCENARIO_PARAMS = 3;

	public static void main(String arg[]) {
		XMLWrapper xmlModel = null;

		try {
			// check parameters
			if (arg.length == NORMAL_PARAMS)
				xmlModel = new XMLWrapper(arg[0], null, arg[1]);
			else if (arg.length == SCENARIO_PARAMS)
				xmlModel = new XMLWrapper(arg[0], arg[1], arg[2]);

			if (xmlModel == null)
				System.err.println("ERROR : Parameters error");
			else {
				XMLExperiment exp = new XMLExperiment(xmlModel);
				exp.start();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
