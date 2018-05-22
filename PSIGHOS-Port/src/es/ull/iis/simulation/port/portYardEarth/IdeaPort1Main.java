package es.ull.iis.simulation.port.portYardEarth;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.Simulation;

public class IdeaPort1Main extends Experiment {
	final private static String DESCRIPTION = "Simulación zona patio-tierra del puerto de Santa Cruz de Tenerife";
	private static final TimeUnit PORT_TIME_UNIT = TimeUnit.SECOND;
	private static final long START_TS = 0;
	private static final long END_TS = 6*60*60;//segundos
	private int nGruas;
	private int nCallesA;
	private int nCamiones;
	private int nCallesInt1;
	private int nCallesInt2;
	private int nCallesInt3;
	private int nCallesInt4;
	private long tramo1;
	private long tramo2;
	private long tramo3;
	private long tramo4;
	private long descarga;
	private long transferencia;
	private long tramo1Vuelta;

	public IdeaPort1Main(int nGruas, int nCallesA,int nCamiones,int nCallesInt1,int nCallesInt2, 
			int nCallesInt3, int nCallesInt4, long tramo1, long tramo2,long tramo3, long tramo4, 
			long tramo1Vuelta, long descarga, long transferencia, int nSim, double error){
		super("Prueba PATIO-TIERRA", nSim);
		this.nGruas = nGruas;
		this.nCallesA = nCallesA;
		this.nCamiones = nCamiones;
		this.nCallesInt1 = nCallesInt1;
		this.nCallesInt2 = nCallesInt2;
		this.nCallesInt3 = nCallesInt3;
		this.nCallesInt4 = nCallesInt4;
		this.tramo1 = tramo1;
		this.tramo2 = tramo2;
		this.tramo3 = tramo3;
		this.tramo4 = tramo4;
		this.tramo1Vuelta = tramo1Vuelta;
		this.descarga = descarga;
		this.transferencia = transferencia;
		PortSimulation.setERROR(error);
		TiempoEstanciaListener.setnSim(nSim);
	}
	
	public Simulation getSimulation(int ind){
		
//Creamos la simulacion pasandole todos los parametros
	Simulation simul = new PortSimulation(ind, DESCRIPTION + ind, PORT_TIME_UNIT, START_TS, END_TS, 
				nGruas,nCamiones, nCallesA,nCallesInt1,nCallesInt2,nCallesInt3,nCallesInt4,tramo1,tramo2,
				tramo3, tramo4,tramo1Vuelta, descarga,transferencia);
	//Le pasamos los listener con los que obtenemos los resultados
		simul.addInfoReceiver(new StdInfoView());
		simul.addInfoReceiver(new TiempoEstanciaListener());
		simul.addInfoReceiver(new ConflictoListener(nExperiments));	
		
	return simul;
	}
}