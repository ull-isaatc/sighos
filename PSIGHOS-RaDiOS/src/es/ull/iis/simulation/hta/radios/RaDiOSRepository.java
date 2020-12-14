/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;

/**
 * @author Iván Castilla
 *
 */
public class RaDiOSRepository extends SecondOrderParamsRepository {
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;

	/**
	 * @param nRuns
	 * @param nPatients
	 */
	public RaDiOSRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.ADD, BasicConfigParams.DEF_U_GENERAL_POP);
		
		// TODO: Rellenar registrando...
		
		// TODO: La población
		// registerPopulation(...)
		
		// TODO: La enfermedad
		// registerDisease(...)
		
		// TODO: La o las intervenciones (puedes empezar simulando solo una
		// registerIntervention(...)
		
		// El submodelo de mortalidad (por defecto podemos usar el que te pongo)
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}


}
