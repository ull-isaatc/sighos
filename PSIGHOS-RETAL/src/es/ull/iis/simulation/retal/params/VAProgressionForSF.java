/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.params.VAParam;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class VAProgressionForSF extends Param {
	private final static int[] LETTERS_LOST = {5, 10, 15, 20};
	// Parameters for any VA level change for SF CNV lesions 
	// Source: Karnon model
	private final static double[] AGE_COEF = {0.010902535, 0.005114633};
	private final static double[] FUBASEVA_COEF = {0.018555672, 0.002278834};
	private final static double[] OCCULT_COEF = {-0.012045433, 0.080733806};
	private final static double[] MINCLASSIC_COEF = {-0.073349302, 0.079345419};
	private final static double[] CONS_COEF = {-8.531681419, 0.498242187};
	private final static double[] LN_P_COEF = {0.19237989, 0.025403131};

	// Parameters for multilogit regression to predict the magnitude of decrease of VA in subfoveal lesions.
	// Values for 10, 15 and 20 letters loss
	// Source: Karnon (Table 37, page 34)
	private final static double[][] MULTILOGIT_AGE = {{0.007, 0.013}, {-0.024, 0.015}, {0.002, 0.014}};
	private final static double[][] MULTILOGIT_FOLLOW_UP = {{-0.001, 0.001}, {0.000, 0.001}, {-0.001, 0.001}};
	private final static double[][] MULTILOGIT_FUBASEVA = {{0.013, 0.007}, {0.006, 0.008}, {0.050, 0.007}};
	private final static double[][] MULTILOGIT_OCCULT = {{0.201, 0.203}, {-0.137, 0.233}, {-0.673, 0.219}};
	private final static double[][] MULTILOGIT_MINCLASSIC = {{-0.454, 0.217}, {-0.522, 0.241}, {-0.958, 0.229}};
	private final static double[][] MULTILOGIT_CONS = {{-1.655, 1.075}, {0.716, 1.190}, {-2.832, 1.150}};

	private final double ageCoef;
	private final double fubasevaCoef;
	private final double occultCoef;
	private final double minclassicCoef;
	private final double consCoef;
	private final double gradientCoef;
	private final double[] multilogitAge = new double[3];
	private final double[] multilogitFollowUp = new double[3];
	private final double[] multilogitFubaseva = new double[3];
	private final double[] multilogitOccult = new double[3];
	private final double[] multilogitMinclassic = new double[3];
	private final double[] multilogitCons = new double[3];

	

	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAProgressionForSF(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		ageCoef = AGE_COEF[0];
		fubasevaCoef = FUBASEVA_COEF[0];
		occultCoef = OCCULT_COEF[0];
		minclassicCoef = MINCLASSIC_COEF[0];
		consCoef = CONS_COEF[0];
		gradientCoef = Math.exp(LN_P_COEF[0]);
		for (int i = 0; i < 3; i++) {
			multilogitAge[i] = MULTILOGIT_AGE[i][0];
			multilogitFollowUp[i] = MULTILOGIT_FOLLOW_UP[i][0];
			multilogitFubaseva[i] = MULTILOGIT_FUBASEVA[i][0];
			multilogitOccult[i] = MULTILOGIT_OCCULT[i][0];
			multilogitMinclassic[i] = MULTILOGIT_MINCLASSIC[i][0];
			multilogitCons[i] = MULTILOGIT_CONS[i][0];
		}
	}

	private double getMultiLogitCoef(CNVStage stage, double ageAtSF, long days, double fuva, int index) {
		return multilogitCons[index] + multilogitAge[index] * ageAtSF + multilogitFollowUp[index] * days +
			multilogitFubaseva[index] * fuva +
			((stage.getType() == CNVStage.Type.MC) ? multilogitMinclassic[index] : ((stage.getType() == CNVStage.Type.OCCULT) ? multilogitOccult[index] : 0));		
	}
	
	private int getMultiLogitResult(double rnd, CNVStage stage, double ageAtSF, long days, double fuva) {
		final double [] coef = new double[4];
		coef[0] = 1.0;
		for (int i = 0; i < 3; i++) {
			coef[i+1] = coef[i] + getMultiLogitCoef(stage, ageAtSF, days, fuva, i);
		}
		final double [] probabilities = new double[4];
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = coef[i] / coef[coef.length - 1];
		}
		for (int i = 0; i < probabilities.length - 1; i++) {
			if (probabilities[i] > rnd)
				return i;
		}
		return probabilities.length;
	}
	
	private double getTimeToChange(CNVStage stage, double ageAtSF, double fuva) {
		final double alpha = gradientCoef;
		final double beta = Math.exp(-(consCoef + ageAtSF * ageCoef + fuva * fubasevaCoef + 
				((stage.getType() == CNVStage.Type.MC) ? minclassicCoef : ((stage.getType() == CNVStage.Type.OCCULT) ? occultCoef : 0))) / alpha);
		final RandomVariate rnd = RandomVariateFactory.getInstance("WeibullVariate", alpha, beta);
		// FIXME: According to Karnon, this should return "days", but not sure yet
		return rnd.generate();		
	}
	
	public VAParam.VAProgressionPair[] getLevelChanges(OphthalmologicPatient pat, int eyeIndex) {
		final ArrayList<VAParam.VAProgressionPair> changes = new ArrayList<VAParam.VAProgressionPair>();
		final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
		double fuva = VisualAcuity.getLettersFromLogMAR(pat.getVA(eyeIndex));
		double ageAtSF = pat.getAgeAt(stage, eyeIndex);
		long timeToChange = (long)getTimeToChange(stage, ageAtSF, fuva);
		long daysSinceEvent = TimeUnit.DAY.convert(simul.getTs() - pat.getTimeToCNVStage(stage, eyeIndex), simul.getTimeUnit());
		while ((timeToChange < daysSinceEvent) && (fuva >= VisualAcuity.LETTERS_BLINDNESS)) {
			fuva -= LETTERS_LOST[getMultiLogitResult(pat.getRndLevelsLostSF(), stage, ageAtSF, daysSinceEvent, fuva)];
			if (fuva < 0.0)
				fuva = 0.0;
			changes.add(new VAParam.VAProgressionPair(timeToChange, VisualAcuity.getLogMARFromLetters(fuva)));
			ageAtSF += timeToChange / 365.0;
			daysSinceEvent -= timeToChange; 
			timeToChange = (long)getTimeToChange(stage, ageAtSF, fuva);
		}
		final VAParam.VAProgressionPair[] arrayChanges = new VAParam.VAProgressionPair[changes.size()];
		return (VAParam.VAProgressionPair[])changes.toArray(arrayChanges);
	}
}
