
import es.ull.isaatc.simulation.XMLExperiment;
import es.ull.isaatc.simulation.fuzzy.FuzzyExperiment;
import es.ull.isaatc.simulation.fuzzy.XMLFuzzyModel;

public class CAUFuzzySimulation {

	private static int NORMAL_PARAMS = 3;

	private static int SCENARIO_PARAMS = 4;

	public static void main(String arg[]) {
		XMLFuzzyModel xmlModel = null;

		// check parameters
		if (arg.length == NORMAL_PARAMS) {
			xmlModel = new XMLFuzzyModel(arg[0], null, arg[1], arg[2]);
		} else if (arg.length == SCENARIO_PARAMS) {
			xmlModel = new XMLFuzzyModel(arg[0], arg[1], arg[2], arg[2]);
		}

		if (xmlModel == null)
			System.err.println("ERROR : Parameters error");
		else {
			XMLExperiment exp = new FuzzyExperiment(xmlModel);
			exp.start();
		}
	}
}
