package es.ull.iis.simulation.hta.T1DM.params;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

// For testing only
//	public static void main(String[] args) {
//		SecondOrderParamsRepository par = new SecondOrderParamsRepository(true) {
//		};
//		SecondOrderCostParam cost = par.new SecondOrderCostParam("MI", "MI", "", 2003, 19277.0); 
//		System.out.println(cost.getValue());
//	}
	public class SecondOrderParam {
		private final String name;
		private final String description;
		private final String source;
		private final double detValue;
		private final RandomVariate rnd;
		protected double lastGeneratedValue;

		public SecondOrderParam(String name, String description, String source, double detValue, RandomVariate rnd) {
			this.name = name;
			this.description = description;
			this.source = source;
			this.detValue = detValue;
			this.rnd = rnd;
			lastGeneratedValue = Double.NaN;
		}
		
		public SecondOrderParam(String name, String description, String source, double detValue, String rndFunction, Object... params) {
			this(name, description, source, detValue, RandomVariateFactory.getInstance(rndFunction, params));
		}
		
		public SecondOrderParam(String name, String description, String source, double detValue) {
			this(name, description, source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
		}
		
		public double getValue(boolean baseCase) {
			lastGeneratedValue = baseCase ? detValue : rnd.generate();
			return lastGeneratedValue;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}

		/**
		 * @return the generatedValues
		 */
		public double getLastGeneratedValue() {
			return lastGeneratedValue;
		}
	}