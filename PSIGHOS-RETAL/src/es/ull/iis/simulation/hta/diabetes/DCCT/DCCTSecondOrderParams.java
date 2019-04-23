/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.DCCT;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatientGenerator.DiabetesPatientGenerationInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTConventionalIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.DCCTIntensiveIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.populations.DCCTPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.StandardSpainDeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParamsRepository {
	
	/**
	 * @param nPatients Number of patients to create
	 */
	public DCCTSecondOrderParams(int nPatients) {
		super(nPatients);
		registerPopulation(new DiabetesPatientGenerationInfo(new DCCTPopulation(this)));
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			registerComplication(new SimpleRETSubmodel());
			registerComplication(new SimpleNPHSubmodel());
		}
		else {
			registerComplication(new SheffieldRETSubmodel());
			registerComplication(new SheffieldNPHSubmodel());
		}
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());
		
		registerComplication(new DCCTSevereHypoglycemiaEvent());

		registerIntervention(new DCCTConventionalIntervention());
		registerIntervention(new DCCTIntensiveIntervention());
		setDiscountZero(true);
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(getRngFirstOrder(), nPatients);

		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			dModel.addIMR(SimpleNPHSubmodel.NPH, getIMR(SimpleNPHSubmodel.NPH));
			dModel.addIMR(SimpleNPHSubmodel.ESRD, getIMR(SimpleNPHSubmodel.ESRD));
		}
		else {
			dModel.addIMR(SheffieldNPHSubmodel.ALB2, getIMR(SheffieldNPHSubmodel.ALB2));
			dModel.addIMR(SheffieldNPHSubmodel.ESRD, getIMR(SheffieldNPHSubmodel.ESRD));			
		}
		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(DiabetesChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(DiabetesChronicComplications.CHD));
		return dModel;
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}


}
