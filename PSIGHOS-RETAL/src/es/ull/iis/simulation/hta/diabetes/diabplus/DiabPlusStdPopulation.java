/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.diabplus;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesStdPopulation;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author icasrod
 *
 */
public class DiabPlusStdPopulation extends DiabetesStdPopulation {
	final private boolean man;
	final private double hba1c;
	final private double age;
	final private double durationOfDiabetes;
	
	public DiabPlusStdPopulation(boolean man, double hba1c, double age, double durationOfDiabetes) {
		super(DiabetesType.T1);
		this.age = age;
		this.durationOfDiabetes = durationOfDiabetes;
		this.hba1c = hba1c;
		this.man = man;
	}

	@Override
	protected double getPMan() {
		return man ? 1.0 : 0.0;
	}

	@Override
	protected RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", hba1c);
	}

	@Override
	protected RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", age);
	}

	@Override
	protected RandomVariate getBaselineDurationOfDiabetes() {
		return RandomVariateFactory.getInstance("ConstantVariate", durationOfDiabetes);
	}

}
