/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla
 *
 */
public class TestLoadOWL {

	/**
	 * 
	 */
	public TestLoadOWL() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			final SecondOrderParamsRepository secParams = new OSDiGenericRepository(1, 1000, System.getProperty("user.dir") + "\\resources\\OSDi.owl", "#PBD_ProfoundBiotinidaseDeficiency", "#PBD_BasePopulation", DisutilityCombinationMethod.ADD);
			secParams.registerAllSecondOrderParams();
			for (Disease disease : secParams.getRegisteredDiseases()) {
				System.out.println(disease.prettyPrint(""));
			}
			for (Intervention interv : secParams.getRegisteredInterventions()) {
				System.out.println(interv.prettyPrint(""));
			}
			System.out.println(secParams.prettyPrint(""));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TranspilerException e) {
			e.printStackTrace();
		}
	}

}
