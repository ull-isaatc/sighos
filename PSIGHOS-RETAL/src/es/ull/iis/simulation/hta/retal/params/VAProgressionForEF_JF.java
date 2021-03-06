package es.ull.iis.simulation.hta.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.model.TimeUnit;

final class VAProgressionForEF_JF extends VAProgressionParam {
	private static final double[] CONSTANT = {0.07, 0.188};
	private static final double[] BASELINE_VA = {0.919, 0.067};
	private static final double[] LOG_DAYS = {0.048, 0.038};
	private static final double[] LESION_TYPE = {-0.049, 0.03};
	private final double constantCoef;
	private final double baselineVACoef;
	private final double logDaysCoef;
	private final double lesionTypeCoef;
	
	public VAProgressionForEF_JF() {
		super();
		constantCoef = CONSTANT[0];
		baselineVACoef = BASELINE_VA[0];
		logDaysCoef = LOG_DAYS[0];
		lesionTypeCoef = LESION_TYPE[0];
	}
	
	@Override
	public ArrayList<VAProgressionPair> getVAProgression(RetalPatient pat, int eyeIndex, double expectedVA) {
		final ArrayList<VAProgressionPair> array = new ArrayList<VAProgressionPair>();
		final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
		final double currentVA = pat.getVA(eyeIndex);
		final long timeSinceLastEvent = pat.getSimulation().getTs() - pat.getTimeToCNVStage(stage, eyeIndex);
		final double logDays = Math.log(TimeUnit.DAY.convert(timeSinceLastEvent, pat.getSimulation().getTimeUnit()));
		// Made to mimic Karnon
		final int lesion = (stage.getType() == CNVStage.Type.MC) ? 2 : 3;
		final double newVA = Math.min(VisualAcuity.MAX_LOGMAR, constantCoef + (logDays * logDaysCoef) + (currentVA * baselineVACoef) + (lesion * lesionTypeCoef));
		array.add(new VAProgressionPair(timeSinceLastEvent, Math.max(expectedVA, newVA)));
		return array;
	}
}