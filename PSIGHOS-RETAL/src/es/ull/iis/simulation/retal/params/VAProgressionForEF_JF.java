package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.params.VAParam;

final class VAProgressionForEF_JF extends Param {
	private static final double[] CONSTANT = {0.07, 0.188};
	private static final double[] BASELINE_VA = {0.919, 0.067};
	private static final double[] LOG_DAYS = {0.048, 0.038};
	private static final double[] LESION_TYPE = {-0.049, 0.03};
	private final double constantCoef;
	private final double baselineVACoef;
	private final double logDaysCoef;
	private final double lesionTypeCoef;
	
	public VAProgressionForEF_JF(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		constantCoef = CONSTANT[0];
		baselineVACoef = BASELINE_VA[0];
		logDaysCoef = LOG_DAYS[0];
		lesionTypeCoef = LESION_TYPE[0];
	}
	
	public VAParam.VAProgressionPair[] getVA(OphthalmologicPatient pat, int eyeIndex) {
		final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
		final double currentVA = pat.getVA(eyeIndex);
		final long timeSinceLastEvent = simul.getTs() - pat.getTimeToCNVStage(stage, eyeIndex);
		final double logDays = Math.log(TimeUnit.DAY.convert(timeSinceLastEvent, simul.getTimeUnit()));
		// Made to mimic Karnon
		final int lesion = (stage.getType() == CNVStage.Type.MC) ? 2 : 3;
		if (currentVA >= VisualAcuity.MAX_LOGMAR)
			return null;
		final double newVA = Math.min(VisualAcuity.MAX_LOGMAR, constantCoef + (logDays * logDaysCoef) + (currentVA * baselineVACoef) + (lesion * lesionTypeCoef));
		if (newVA < currentVA)
			return null;
		else
			return new VAParam.VAProgressionPair[] {new VAParam.VAProgressionPair(timeSinceLastEvent, newVA)};			
	}
}