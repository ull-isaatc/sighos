/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariateFactory;
import es.ull.isaatc.HUNSC.cirgen.view.GSElementTypeTimeView;
import es.ull.isaatc.HUNSC.cirgen.view.GSElementTypeWaitView;
import es.ull.isaatc.HUNSC.cirgen.view.GSViewArray;
import es.ull.isaatc.HUNSC.cirgen.view.GSResourceStdUsageView;
import es.ull.isaatc.HUNSC.cirgen.view.GSResult;
import es.ull.isaatc.HUNSC.cirgen.view.GSView;
import es.ull.isaatc.HUNSC.cirgen.view.GSViewControllerArray;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationRoundedPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.ExcelTools;
import es.ull.isaatc.util.RoundedPeriodicCycle;

/**
 * Modelo de simulación del hospital construido a partir de los datos de un fichero de Excel
 * @author Iván Castilla Rodríguez
 *
 */
public class SimGS extends Simulation {
	/** "Envoltorio" del fichero Excel de entrada */
	private GSExcelInputWrapper input;
	/** Estructura que incluye las vistas de la simulación */
	private GSViewArray views;
	/** Estructura de control de la salida */
	private GSViewControllerArray listeners;
	/** Nombre del fichero de salida */
	private String filename;
	
	/**
	 * Inicializa la simulación, creando las vistas necesarias.
	 * @param id Identificador
	 * @param listeners Estructura de control de la salida
	 * @param input "Envoltorio" del fichero Excel de entrada
	 */
	public SimGS(int id, GSViewControllerArray listeners, GSExcelInputWrapper input) {
		super(id, "General Surgery (" + id +")", SimulationTimeUnit.MINUTE, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, input.getSimulationDays()));
		this.input = input;
		this.listeners = listeners;
		this.filename = input.getOutputPath() + input.getOutputFileName() + id + ExcelTools.EXT;
		views = new GSViewArray(id, new GSElementTypeTimeView(this, input), 
				new GSElementTypeWaitView(this, input), 
				new GSResourceStdUsageView(this, input));
		for (View v : views.getListeners()) {
			addInfoReceiver(v);
		}
		addInfoReceiver(new StdInfoView(this));
	}

	/**
	 * Crea el fichero Excel de resultado al terminar la simulación
	 */
	@Override
	public void end() {
		super.end();
		// Crea una nueva hoja
		HSSFWorkbook wb = new HSSFWorkbook();
		
		// Recupera los resultados de cada vista para escribirla en Excel
		ArrayList <GSResult> res = new ArrayList<GSResult>();
		for (View v : views.getListeners()) {
			if (v instanceof GSView) {
				((GSView)v).setResults(wb);
				res.add(((GSView)v).getResults());
			}
			// Si la vista no es de las que escriben a Excel, la muestra por pantalla
			else
				System.out.println(v.toString());
		}
		try {
			// Crea el fichero de salida
			FileOutputStream out = new FileOutputStream(filename);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listeners.notifyEnd(views);
		
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Simulation#createModel()
	 */
	@Override
	protected void createModel() {
		TreeMap<Integer, WorkGroup> wgList = new TreeMap<Integer, WorkGroup>();
		// Añado una semilla aleatoria
		RandomVariateFactory.setDefaultRandomNumber(RandomNumberFactory.getInstance(System.currentTimeMillis()));
		
		// Tipos de Quirófanos (ambulantes y no ambulantes; de urgencias o programados)
		// Se crea un tipo de recurso y un WG por cada configuración
		for (AdmissionType adm : AdmissionType.values())
			for (PatientType type : PatientType.values()) {
				ResourceType rt = new ResourceType(adm.ordinal()* 2 + type.ordinal(), this, adm.getName() + "_" + type.getName());
				WorkGroup wg = new WorkGroup();
				wg.add(rt, 1);
				wgList.put(adm.ordinal()* 2 + type.ordinal(), wg);
			}
			
		// Se crean los quirófanos como recursos
		for (OperationTheatre op : input.getOpTheatres()) {
			Resource r = new Resource(op.getIndex(), this, op.getName());
			for (OperationTheatre.TimetableEntry tte : op.getTimetableEntryList())
				r.addTimeTableEntry(tte.getCycle(this), tte.getOpenTime(), getResourceType(tte.getAdmissionType().ordinal() * 2 + tte.getPatientType().ordinal()));				
		}
		
		// Creo los pacientes, tipos de pacientes, creadores y generadores de pacientes
		for (PatientCategory pt : input.getPatientCategories()) {
			// Obtengo una exponencial que genere los pacientes usando como dato de partida
			// el total de pacientes llegados en el periodo de estudio
			SimulationTimeFunction expo = new SimulationTimeFunction(this, "ExponentialVariate", new SimulationTime(SimulationTimeUnit.DAY, input.getObservedDays() / (double)pt.getTotal()));
			SimulationCycle c = null;
			// Si es un paciente de tipo programado, se crea a las 0.00 horas de cada día
			if (pt.getAdmissionType() == AdmissionType.PROGRAMMED)
				c = new SimulationRoundedPeriodicCycle(this, expo.getFunction().getValue(0.0), expo, 0, 
						RoundedPeriodicCycle.Type.ROUND, new SimulationTime(SimulationTimeUnit.DAY, 1));
			// Si es un paciente de urgencias puede llegar en cualquier momento
			else
				c = new SimulationPeriodicCycle(this, expo.getFunction().getValue(0.0), expo, 0);
			ElementType et = new ElementType(pt.getIndex(), this, pt.getName() + "_" + pt.getAdmissionType().getName() + "_" + pt.getPatientType().getName());
			TimeDrivenActivity act = new TimeDrivenActivity(pt.getIndex(), this, pt.getName() + "_" + pt.getAdmissionType().getName() + "_" + pt.getPatientType().getName());
			act.addWorkGroup(new SimulationTimeFunction(this, pt.getDist() + "Variate", new Object[] {pt.getParam1(), pt.getParam2()}), wgList.get(pt.getAdmissionType().ordinal() * 2 + pt.getPatientType().ordinal()));
			ElementCreator ec = new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, new SingleFlow(this, act));
	        new TimeDrivenGenerator(this, ec, c);
		}
		
	}

}
