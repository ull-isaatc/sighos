package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.util.Cycle;


/**
 * Un recurso activo puede desempeñar diferentes roles, o lo que es lo mismo,
 * tener varias ClaseRecurso.
 * Por este motivo ya no hay un atributo. Si se desea saber que roles desempeña
 * es necesario recorrer su tabla horaria.
 * @author Carlos Martín Galán
 */
public class Resource extends DescSimulationObject {
	/** Timetable that contains all the pairs role - time-cycle */
    protected ArrayList timeTable;
    /** Resource managers that control the time table entries */
    protected TimeTableManager[] managerList;
    /** Associated Logical Process */
    protected LogicalProcess lp;

    /**
     * Creates a new instance of Resource
     * @param resModel Correspondign model resource
     * @param simul Associated simulation
     * @param lp Associated logical process
     */
	public Resource(int id, Simulation simul, String description) {
		super(id, simul, description);
        timeTable = new ArrayList();
	}

    /**
	 * @return Returns the lp.
	 */
	public LogicalProcess getLp() {
		return lp;
	}

	/**
	 * @param lp The lp to set.
	 */
	public void setLp(LogicalProcess lp) {
		this.lp = lp;
		lp.add(this);

	}

    /**
     * Arranca todos los timeManageres de actividad del recurso
     * Supone ya que se han introducido ya todas las entradas de su horario
     */
	public void start() {
		managerList = new TimeTableManager[getTimeTableSize()];
		for (int i = 0 ; i < managerList.length; i++) {
			managerList[i] = new TimeTableManager(i,this, getTimeTableEntry(i));
			managerList[i].start();
		}
	} // fin start

    /**
     * Add a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param r Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ResourceType r) {
        ArrayList roleList = new ArrayList();
        roleList.add(r);
        addTimeTableEntry(cycle, dur, roleList);
    }  

    /**
     * Add a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param r Roles that the resource plays during this cycle
     */
    public void addTimeTableEntry(Cycle cycle, double dur, ArrayList r) {
        TimeTableEntry entry = new TimeTableEntry(cycle,dur,r);
        timeTable.add(entry);
    }  
    
    /**
     * Returns the amount of time table entries.
     * @return Size of the time table.
     */
    public int getTimeTableSize() {
        return timeTable.size();
    }
    
    /**
     * Returns the TimeTableEntry object at the specified position of the time 
     * table.
     * @param index of object to return.
     * @return The TimeTableEntry object at the specified position.
     */
    public TimeTableEntry getTimeTableEntry(int index) {
        return (TimeTableEntry)timeTable.get(index);
    }
    
	public String getObjectTypeIdentifier() {
		return "RES";
	}

	public double getTs() {
		return lp.getTs();
	}
} // fin Resource
