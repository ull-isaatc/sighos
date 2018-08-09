package es.ull.iis.simulation.hta.retal.test;

final class VAProgressionForEF_JF {
	private static final double[] CONSTANT = {0.07, 0.188};
	private static final double[] BASELINE_VA = {0.919, 0.067};
	private static final double[] LOG_DAYS = {0.048, 0.038};
	private static final double[] LESION_TYPE = {-0.049, 0.03};
	private final double constantCoef;
	private final double baselineVACoef;
	private final double logDaysCoef;
	private final double lesionTypeCoef;
	
	protected VAProgressionForEF_JF() {
		constantCoef = CONSTANT[0];
		baselineVACoef = BASELINE_VA[0];
		logDaysCoef = LOG_DAYS[0];
		lesionTypeCoef = LESION_TYPE[0];
	}
	
	protected VAProgressionPair[] getVA(CNVStage stage, double currentVA, long daysSinceIncidence) {
		final double logDays = Math.log(daysSinceIncidence);
		// Made to mimic Karnon
		final int lesion = (stage.getType() == CNVStage.Type.MC) ? 2 : 3;
		if (currentVA >= 1.6)
			return null;
		final double newVA = Math.min(1.6, constantCoef + (logDays * logDaysCoef) + (currentVA * baselineVACoef) + (lesion * lesionTypeCoef));
		if (newVA < currentVA)
			return null;
		else
			return new VAProgressionPair[] {new VAProgressionPair(daysSinceIncidence, newVA)};			
	}
}