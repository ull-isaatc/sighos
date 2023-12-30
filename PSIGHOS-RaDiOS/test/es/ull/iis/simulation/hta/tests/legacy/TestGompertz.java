package es.ull.iis.simulation.hta.tests.legacy;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.GompertzVariate;

public class TestGompertz {

	public TestGompertz() {
	}

	public static void main(String[] args) {
		GompertzVariate gv = new GompertzVariate();
		long endT, iniT;
		
		gv.setParameters(Math.exp(-11.41320441),0.097865047, 30);
		iniT = System.nanoTime();

		for (int i = 0; i < 5000; i++) {
			System.out.println(gv.generate());
		}
		endT = System.nanoTime();
		System.out.println("" + ((endT - iniT) / 1000000) + "ms");

		System.out.println(TimeUnit.DAY.convert(1, TimeUnit.MONTH));
	}

}
