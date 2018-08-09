/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	public final static boolean CANADA = false; 
	public final static double DEF_DISCOUNT_RATE = CANADA ? 0.015 : 0.03; 
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	public final static double YEAR_CONVERSION = SIMUNIT.convert(TimeStamp.getYear());

	public final static int NPATIENTS = 10000;

	public static final int N_COMPLICATIONS = Complication.values().length;

	public final static int MAX_AGE = 100;
	public final static int MAN = 0;
	public final static int WOMAN = 1;
	// FIXME: Should be related to simulation time unit 
	public final static long MIN_TIME_TO_EVENT = 30;

	private final ResourceUsageParam resourceUsage;
	private final SevereHypoglycemicEventParam hypoParam;
	private final RandomNumber rngSex;
	private final RandomNumber rngComplications;
	private final double[][] rndComplications;

	private final double invDNC_RET;
	private final double invDNC_NEU;
	private final double invDNC_NPH;
	private final double invDNC_CHD;
	private final double invNEU_CHD;
	private final double invNEU_LEA;
	private final double invNEU_NPH;
	private final double invNPH_CHD;
	private final double invNPH_ESRD;
	private final double invRET_BLI;
	private final double invRET_CHD;
	
	private final double[] rrCHD;
	private final double[] rrNPH;
	private final double[] rrNEU;
	private final double[] rrRET;
	
	private final double pMan;
	private final double initAge;
	private final long[] durationOfEffect;

	final private SecondOrderParams secParams;
	/**
	 * @param secondOrder
	 */
	public CommonParams(SecondOrderParams secParams) {
		super();
		this.secParams = secParams;
		rngSex = RandomNumberFactory.getInstance();
		rngComplications = RandomNumberFactory.getInstance();
		rndComplications = new double[N_COMPLICATIONS][NPATIENTS];
		for (int i = 0; i < N_COMPLICATIONS; i++)
			for (int j = 0; j < NPATIENTS; j++)
				rndComplications[i][j] = rngComplications.draw();
		resourceUsage = new ResourceUsageParam();
		hypoParam = new SevereHypoglycemicEventParam(NPATIENTS);
		invDNC_RET = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_RET);
		invDNC_NEU = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_NEU);
		invDNC_NPH = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_NPH);
		invDNC_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_CHD);
		invNEU_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_CHD);
		invNEU_LEA = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_LEA);
		invNEU_NPH = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_NPH);
		invNPH_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NPH_CHD);
		invNPH_ESRD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NPH_ESRD);
		invRET_BLI = -1 / secParams.getProbability(SecondOrderParams.STR_P_RET_BLI);
		invRET_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_RET_CHD);
		
		rrCHD =  secParams.getRR(Complication.CHD);
		rrNPH =  secParams.getRR(Complication.NPH);
		rrNEU =  secParams.getRR(Complication.NEU);
		rrRET =  secParams.getRR(Complication.RET);

		pMan = secParams.getProbability(SecondOrderParams.STR_P_MAN);
		initAge = secParams.getInitAge();
		durationOfEffect = secParams.getDurationOfEffect(SIMUNIT);
	}

	public int getSex(T1DMPatient pat) {
		return (rngSex.draw() < pMan) ? 0 : 1;
	}
	
	public double getInitAge() {
		return initAge;		
	}

	public long getDurationOfEffect(Intervention intervention) {
		return durationOfEffect[intervention.getId()];
	}
	
	public double getCostForState(T1DMPatient pat, double initAge, double endAge) {
		return resourceUsage.getResourceUsageCost(pat, initAge, endAge);
	}

	public SevereHypoglycemicEventParam.ReturnValue getTimeToSevereHypoglycemicEvent(T1DMPatient pat) {
		return hypoParam.getValue(pat);
	}
	
	public long getTimeToComplication(T1DMPatient pat, Complication complication) {
		final EnumSet<Complication> state = pat.getState();
		long time = Long.MAX_VALUE;
		final long time2Death = pat.getTimeToDeath();
		final double rnd = rndComplications[complication.ordinal()][pat.getIdentifier()];
		final boolean applyRiskReduction = (pat.getTs() < durationOfEffect[pat.getnIntervention()]);
		switch(complication) {
		case BLI:
			// Already at retinopathy
			if (state.contains(Complication.RET)) {
				final long time2BLI = getAnnualBasedTimeToEvent(pat, invRET_BLI, rnd, SecondOrderParams.NO_RR);
				if (time2BLI < time2Death)
					time = time2BLI;
			}
			break;
		case CHD:
			double minInv = invDNC_CHD;
			if (state.contains(Complication.NEU)) {
				if (minInv < invNEU_CHD) {
					minInv = invNEU_CHD;
				}
			}
			if (state.contains(Complication.NPH)) {
				if (minInv < invNPH_CHD) {
					minInv = invNPH_CHD;
				}
			}
			if (state.contains(Complication.RET)) {
				if (minInv < invRET_CHD) {
					minInv = invRET_CHD;
				}
			}

			final long time2CHD = getAnnualBasedTimeToEvent(pat, minInv, rnd, applyRiskReduction ? rrCHD : SecondOrderParams.NO_RR);
			if (time2CHD < time2Death)
				time = time2CHD;				
			break;
		case ESRD:
			// Already at nephropathy
			if (state.contains(Complication.NPH)) {
				final long time2ESRD = getAnnualBasedTimeToEvent(pat, invNPH_ESRD, rnd, SecondOrderParams.NO_RR);
				if (time2ESRD < time2Death)
					time = time2ESRD;
			}
			break;
		case LEA:
			// Already at neuropathy
			if (state.contains(Complication.NEU)) {
				final long time2LEA = getAnnualBasedTimeToEvent(pat, invNEU_LEA, rnd, SecondOrderParams.NO_RR);
				if (time2LEA < time2Death)
					time = time2LEA;
			}
			break;
		case NEU:
			final long time2NEU = getAnnualBasedTimeToEvent(pat, invDNC_NEU, rnd, applyRiskReduction ? rrNEU : SecondOrderParams.NO_RR);
			if (time2NEU < time2Death)
				time = time2NEU;
			break;
		case NPH:
			// Already at neuropathy: we're assuming that the risk from neuropathy is higher than from no complications
			if (state.contains(Complication.NEU)) {
				final long time2NPH = getAnnualBasedTimeToEvent(pat, invNEU_NPH, rnd, applyRiskReduction ? rrNPH : SecondOrderParams.NO_RR);
				if (time2NPH < time2Death)
					time = time2NPH;
			}
			else {
				final long time2NPH = getAnnualBasedTimeToEvent(pat, invDNC_NPH, rnd, applyRiskReduction ? rrNPH : SecondOrderParams.NO_RR);
				if (time2NPH < time2Death)
					time = time2NPH;				
			}
			break;
		case RET:
			final long time2RET = getAnnualBasedTimeToEvent(pat, invDNC_RET, rnd, applyRiskReduction ? rrRET : SecondOrderParams.NO_RR);
			if (time2RET < time2Death)
				time = time2RET;
			break;
		default:
			break;		
		}
		return time;
	}

	/**
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param minusAvgTimeToEvent -1/(annual risk of the event)
	 * @param rnd A random number
	 * @param interventionRR An array with the RR for each intervention, used to modify the final time.
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(T1DMPatient pat, double minusAvgTimeToEvent, double rnd, double[] interventionRR) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = (minusAvgTimeToEvent / interventionRR[pat.getnIntervention()]) * Math.log(rnd);
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	public void reset() {
		hypoParam.reset();
	}
	
}
