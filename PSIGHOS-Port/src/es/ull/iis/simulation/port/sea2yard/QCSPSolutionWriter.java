/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.IOException;

import com.kaizten.simulation.ports.qcsp.utilities.Utilities;

/**
 * @author Iván Castilla
 *
 */
public class QCSPSolutionWriter {

	/**
	 * 
	 * java -cp QuayCraneSchedulingProblem-1.0-SNAPSHOT.jar kaizten.opt.qcsp.main.Main "C:\Users\Iván Castilla\Dropbox\SimulationPorts\instances\k16.txt" "C:\Users\Iván Castilla\Dropbox\SimulationPorts\QCSPViewer\k16_12.qcsp" k16_12.png
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = System.getProperty("user.home") + "/Dropbox/SimulationPorts/for_validation/more/k21.sol";
        final StowagePlan[] plans = QCSP2StowagePlan.loadFromFile(fileName);
		for (int i = 0; i < 25; i++) {
			try {
				Utilities.writeFile(System.getProperty("user.home") +"/Dropbox/SimulationPorts/QCSPViewer/k21_" + i + ".qcsp", plans[i].getOriginalSolution().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
