import es.ull.cyc.simulation.bind.XMLModel;

public class ModelTest {

	public static void main(String arg[]) {
		XMLModel xmlModel = new XMLModel(arg[0], arg[1], arg[2]);
		TestExperiment exp = new TestExperiment(xmlModel);
		exp.start();
	}
}
