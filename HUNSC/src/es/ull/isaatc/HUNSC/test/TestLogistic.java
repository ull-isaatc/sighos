package es.ull.isaatc.HUNSC.test;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván
 *
 */
public class TestLogistic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		double val[] = LogNormalVariate.getLogNormalParameters(1220, 1070);
//		RandomVariate rnd = RandomVariateFactory.getInstance("LogNormalVariate", val[0], val[1]);
		
//		RandomVariate rnd = RandomVariateFactory.getInstance("LogisticVariate", 35.6895, 9.9821);
		
//		RandomVariate rndT = RandomVariateFactory.getInstance("StudentVariate", 2);
//		RandomVariate rnd = RandomVariateFactory.getInstance("ScaledVariate", rndT, 75.1815, 15.2415);
		
//		RandomVariate rnd = RandomVariateFactory.getInstance("InverseGaussian2Variate", 76.1942, 103.581);
		
//		RandomVariate rnd = RandomVariateFactory.getInstance("LogLogisticVariate", 4.01159, 0.441252);
		
		RandomVariate rnd = RandomVariateFactory.getInstance("GeneralizedExtremeValueVariate", 0.454607, 14.8539, 33.0558);
//		RandomVariate rnd1 = RandomVariateFactory.getInstance("InverseGaussian2Variate", 76.1942, 103.581);
//		TimeFunction rnd = TimeFunctionFactory.getInstance("RoundFunction", RoundFunction.Type.ROUND, new RandomFunction(rnd1), 5.0);
		for (int i = 0; i < 1000; i++)
//			System.out.println(rnd.getValue(0.0));		
			System.out.println(rnd.generate());		

	}

}
