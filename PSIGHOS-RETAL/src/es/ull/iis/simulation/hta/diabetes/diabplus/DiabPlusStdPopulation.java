/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams.Sex;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;

/**
 * TODO: Currently, we are not using smoker, attrial fibrilation, SBP or lipid ratio. If T2DM is implemented, this functionality should be added
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class DiabPlusStdPopulation implements DiabetesPopulation {
	final private Sex sex;
	final private double hba1c;
	final private double age;
	final private double durationOfDiabetes;
	final private double hypoRate;
	
	public DiabPlusStdPopulation(Sex sex, double hba1c, double age, double durationOfDiabetes, double hypoRate) {
		this.age = age;
		this.durationOfDiabetes = durationOfDiabetes;
		this.hba1c = hba1c;
		this.sex = sex;
		this.hypoRate = hypoRate;
	}

	@Override
	public DiabetesPatientProfile getPatientProfile() {
		return new DiabetesPatientProfile(age, sex, durationOfDiabetes, hba1c, 
				false, false, BasicConfigParams.DEFAULT_SBP, BasicConfigParams.DEFAULT_LIPID_RATIO);
	}

	/**
	 * @return the hypoRate
	 */
	public double getHypoRate() {
		return hypoRate;
	}

	@Override
	public DiabetesType getType() {
		return DiabetesType.T1;
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	@Override
	public int getMaxAge() {
		return BasicConfigParams.DEF_MAX_AGE;
	}

}
