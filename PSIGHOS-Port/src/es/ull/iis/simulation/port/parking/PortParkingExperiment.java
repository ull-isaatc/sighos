/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;
import es.ull.iis.simulation.port.parking.json.PortParkingJSONListener;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final long ENDTS = TimeUnit.MINUTE.convert(10, TimeUnit.DAY);
	private static final String FILE_NAME = System.getProperty("user.dir") + "\\resources\\bootstrap_data.csv";
	final int testId;
	/**
	 */
	public PortParkingExperiment() {
		this(-1);
	}
	
	public PortParkingExperiment(int testId) {
		super("Basic experiment with parking", NEXP);
		this.testId = testId;
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		PortParkingModel sim = null;
		final ArrayList<VesselTransshipmentOrder> operations = new ArrayList<>();
		switch(testId) {
		case 0:
			operations.add(new VesselTransshipmentOrder(OperationType.LOAD, WaresType.CHEMICAL, PortParkingModel.TONES_PER_TRUCK * 2));
			sim =  new PortParkingModel(ind, ENDTS, operations);
			sim.addInfoReceiver(new PortParkingListener());
			break;
		case 1:
			operations.add(new VesselTransshipmentOrder(OperationType.UNLOAD, WaresType.CHEMICAL, PortParkingModel.TONES_PER_TRUCK * 2));
			sim =  new PortParkingModel(ind, ENDTS, operations);
			sim.addInfoReceiver(new PortParkingListener());
			break;
		case 2:
			operations.add(new VesselTransshipmentOrder(OperationType.LOAD, WaresType.CHEMICAL, PortParkingModel.TONES_PER_TRUCK * 2));
			operations.add(new VesselTransshipmentOrder(OperationType.UNLOAD, WaresType.AGRO_LIVESTOCK_FOOD, PortParkingModel.TONES_PER_TRUCK * 2));
			sim =  new PortParkingModel(ind, ENDTS, operations);
			sim.addInfoReceiver(new PortParkingListener());
			break;
		case -1:
		default:
			sim = new PortParkingModel(ind, ENDTS, FILE_NAME);
			sim.addInfoReceiver(new PortParkingJSONListener());
			break;
		}
		return sim;
	}


	public static void main(String[] args) {
		new PortParkingExperiment().start();
	}
	
}
