import es.ull.isaatc.simulation.xml.XMLModel;

public class ModelTest {

	public static void main(String arg[]) {
		XMLModel xmlModel = new XMLModel(arg[0], arg[1]);
		TestExperiment exp = new TestExperiment(xmlModel);
		exp.start();
	}
}
