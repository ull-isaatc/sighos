package es.ull.cyc.simulation;
import java.util.ArrayList;
import es.ull.cyc.util.*;

/**
 * Las instancias de esta clase representa los diferentes tipos de recursos que
 * hay en el sistema, o lo que es lo mismo, los diferentes roles que tienen los
 * recursos. Los recursos activos podr�n adquirir diferentes roles en cada una
 * de sus entradas de horario.
 * @author Carlos Martin Galan
 */
public class ResourceType extends DescSimulationObject {
	/** n�mero de recursos disponibles */
    protected int available;
    /** Gestor de actividades relacionado con esta clase de recurso */
    protected ActivityManager manager;
    /** Cola de recursos activos con horarios solapados en distintas Clases de 
     recurso */
    protected ArrayList availableResourceQueue;

    /**
     * Crea una nueva clase de recurso con una descripci�n
     * @param modelRT Clase de recurso del modelo
     * @param simul Associated simulation
     */
	public ResourceType(int id, Simulation simul, String description) {
		super(id, simul, description);
        available = 0; // inicialmente ningun recurso se da de alta;
        availableResourceQueue = new ArrayList();
	}

    /**
     * Getter for property manager.
     * @return Value of property manager.
     */
    public ActivityManager getManager() {
        return manager;
    }
    
    /**
     * Setter for property manager.
     * @param manager New value of property manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
	/**
	 * Obtiene el n�mero de recursos disponibles de esta clase de recurso
     * @param e El elemento que pregunta por los recursos
	 * @return El n�mero de recursos disponibles
	 */
    protected int[] getAvailable(BasicElement e) {
        int total[] = new int[2];
        total[0] = available;
        total[1] = 0;
        for (int i = 0; i < availableResourceQueue.size(); i++) {
            MultipleRole erm = (MultipleRole) availableResourceQueue.get(i);
            int respuesta;
            // Esperamos por si otro elemento est� tratando de reservarlo
            while ((respuesta = erm.book(e)) == MultipleRole.BOOKED)
                ;
            // El recurso estaba libre y es reservado por este elemento
            if (respuesta == MultipleRole.FREE) {
                // MOD 5/10/04: Si el recurso ha llegado aqu� fuera de tiempo
                // debe eliminarse y no contabilizarse como recurso v�lido
                if (erm.isTimeOut()) {
                    erm.releaseBooking();
                    availableResourceQueue.remove(i--);
                }
                else
                    total[0]++;
                
            }
            // El recurso est� reservado para otra CR de la misma actividad
            else if (respuesta == MultipleRole.OVERLAPPED)
                total[1]++;
            // El recurso est� siendo usado por otro elemento
            else if (respuesta == MultipleRole.USED)
                ; // No se hace nada
        }
        return total;
    }

    /**
     * Devuelve �nicamente los recursos "simples" disponibles
     * @return El valor del atributo "disponibles"
     */
    protected int getAvailable() {
        return available;
    }
    
    /**
     * Esta funci�n permite "liberar" aquellos recursos que se reservaron en una
     * llamada a getDisponibles desde una actividad NO realizable
     * @param e El elemento que pregunt� por los recursos
     */
    protected void resetAvailable(BasicElement e) {
        for (int i = 0; i < availableResourceQueue.size(); i++) {
            MultipleRole erm = (MultipleRole) availableResourceQueue.get(i);
            if (erm.isBooked(e))
                erm.releaseBooking();
        }
    }
    
	/**
	 * Establece el n�mero de recursos disponibles de esta clase de recurso
	 * @param d El n�mero de recursos disponibles
	 */
    protected void setAvailable(int d) {
        available = d;
    }

    /**
     * Permite acceder a la entrada de rol m�ltiple de la cola de recursos
     * disponibles con �ndice ind
     * @param ind Indice del recurso
     * @return Entrada de rol m�ltiple correspondiente al �ndice o null si no 
     * existe una entrada correspondiente a ese �ndice.
     */
    protected MultipleRole getAvailableResource(int ind) {
        if (ind >= availableResourceQueue.size())
            return null;
        return (MultipleRole) availableResourceQueue.get(ind);
    }
    
    /**
     * Busca el primer recurso con roles solapados de la cola de recursos
     * disponibles que est� reservado (o en uso) por el elemento e y no ha sido
     * ya reservado para otra clase de recurso (solapado). Comienza la
     * b�squeda a partir del recurso con �ndice ind.
     * @param ind Indice a partir del cual comienza la b�squeda.
     * @param e Elemento que tiene reservado (o en uso) al recurso buscado.
     * @return El �ndice del recurso o -1 si no encontr� ninguno.
     */
    protected int getBookedResource(int ind, BasicElement e) {
        for (; ind < availableResourceQueue.size(); ind++) {
            MultipleRole erm = (MultipleRole) availableResourceQueue.get(ind);
            if ((erm.getBookedElement() == e) && (erm.getBookedResourceType() == null))
                return ind;
        }
        return -1;
    }
    
	/**
	 * Modifica el n�mero de recursos disponibles de esta clase de recurso, 
	 * sum�ndole o rest�ndole una cantidad.
	 * @param cantidad El incremento (si > 0) o decremento (si < 0) en el n�mero 
     * de recursos disponibles
	 */
    protected void modAvailable(int cantidad) {
        available += cantidad;
    }

	/**
     * MOD 28/10/04
	 * Incrementa el n�mero de recursos disponibles de esta clase de recurso en
	 * una cantidad dada.
	 * @param cantidad El incremento en el n�mero de recursos disponibles
     * @param e Elemento que est� cogiendo los recursos
     * @return Una lista con todos los Gestores de Actividad a los que hay que 
     * avisar por haber devuelto recursos que ten�an roles en otro GA
	 */
    protected ArrayList incAvailable(int cantidad, BasicElement e) {
        ArrayList listaGA = new ArrayList();

        print(Output.DEBUGMSG, "Increase amount\t" + cantidad,
        		"Increase amount\t" + cantidad + "\t" + e);
        
        // Primero se recorre la cola de recursos para comprobar si alguno lo
        // cogi� el elemento
        for (int i = 0; i < availableResourceQueue.size(); i++) {
            MultipleRole erm = (MultipleRole) availableResourceQueue.get(i);
            if (erm.getBookedElement() == e) {
                /* Si no entra en esta codici�n quiere decir que est� reservado para
                 * otra clase de recurso de la misma actividad, as� que ya se devolver�
                 * al llegar a ella
                 */
                //System.out.println(manager.getLp().getGVT() + "\tQuitando erm de " + getDescription() + " por [" + e.getIdentifier() + "]");
                if ((erm.getBookedResourceType() == this) || (erm.getBookedResourceType() == null)) {
                    cantidad--;
                    if (erm.isTimeOut()) {
                        // MOD 5/10/04: El recurso no se elimina aqu� sino que se devuelve de forma normal
                        // La pr�xima vez que se pregunte por este recurso desde la funci�n getAvailable
                        // ser� eliminado
                    	print(Output.DEBUGMSG, "Resource returned out of time\t" + erm.getResource(),
                    			"Resource returned out of time\t" + erm.getResource() + "\t" + e + "\t" + available);
                    }
                    else {
                    	print(Output.DEBUGMSG, "Resource returned normally\t" + erm.getResource(),
                    			"Resource returned normally\t" + erm.getResource() + "\t" + e + "\t" + available);
                        // MOD 2/11/04 
                        for (int j = 0; j < erm.getManagerList().size(); j++) {
                            ActivityManager ga = (ActivityManager) erm.getManagerList().get(j);
                            if (!listaGA.contains(ga))
                                listaGA.add(ga);
                        }
                    }
                    erm.setBookedResourceType(null);
                    erm.setBookedElement(null);
                }
            }
        }
        // Esta comprobaci�n no deber�a tener que hacerse
        if (cantidad < 0) {
        	print(Output.ERRORMSG, "UNEXPECTED ERROR: More resources than expected", 
        			"UNEXPECTED ERROR: More resources than expected\t"+ cantidad + "\t" + available + "\t" + e);
        }
        available += cantidad;
        return listaGA;
    }

	/**
	 * Decrementa el n�mero de recursos disponibles de esta clase de recurso en
	 * una cantidad dada.
	 * @param cantidad El decremento en el n�mero de recursos disponibles
     * @param e Elemento que est� cogiendo los recursos
	 */
    protected void decAvailable(int cantidad, BasicElement e) {
        print(Output.DEBUGMSG, "Decrease amount\t" + cantidad,
        		"Decrease amount\t" + cantidad + "\t" + e);
        
        if (available > cantidad) {
            available -= cantidad; 
        }
        else {
            cantidad -= available;
            available = 0;
            // Supongo que he controlado correctamente la llamada a esta funci�n
            // y ahora deber�a encontrar recursos suficientes
            for (int i = 0; i < availableResourceQueue.size(); i++) {
                MultipleRole erm = (MultipleRole) availableResourceQueue.get(i);
                // Todos los elementos de la cola que me hagan falta deben estar reservados
                if (erm.isBooked(e)) {
                    // Elementos no solapados en la misma actividad
                    if (erm.getBookedResourceType() == null) {
                        if (cantidad > 0) {
                            erm.setBooked(false);
                            cantidad--;
                            
                            print(Output.DEBUGMSG, "Resource taken\t" + erm.getResource(),
                            		"Resource taken\t" + erm.getResource() + "\t " + cantidad + "\t" + e);
                        }
                        else
                            erm.releaseBooking();                        
                    }
                    // Elementos solapados => Se indica la CR a la que se adjudicaron
                    // En teor�a la cantidad no me importa puesto que est�n perfectamente repartidos
                    else if (erm.getBookedResourceType() == this) {
                        erm.setBooked(false);
                        erm.setBookedResourceType(null);
                        cantidad--;                        
                        
                        print(Output.DEBUGMSG, "Overlapped Resource taken\t" + erm.getResource(),
                        		"Overlapped Resource taken\t" + erm.getResource() + "\t " + cantidad + "\t" + e);
                        
                        // Comprobaci�n chunga
                        if (cantidad < 0) {
                        	print(Output.ERRORMSG, "UNEXPECTED ERROR: More resources than expected", 
                        			"UNEXPECTED ERROR: More resources than expected\t"+ cantidad + "\t" + available + "\t" + e);
                        }
                    }
                }
            }
            // Comprobaci�n chunga
            if (cantidad > 0) {
            	print(Output.ERRORMSG, "UNEXPECTED ERROR: Less resources than expected", 
            			"UNEXPECTED ERROR: Less resources than expected\t"+ cantidad + "\t" + available + "\t" + e);
            }
        }
    }

    /**
     * Funci�n para a�adir un recurso activo disponible que tiene m�ltiples
     * roles solapados en el mismo horario
     * @param erm Entrada de rol m�ltiple de un recurso activo.
     */
    protected void incAvailable(MultipleRole erm) {
        availableResourceQueue.add(erm);
        // MOD 6/03/06 Se comprueba por si en una iteraci�n anterior se hab�a quedado
        // fuera de tiempo. De esta manera, el recurso ya no se considera "fuera de tiempo" 
        if (erm.isTimeOut())
        	erm.setTimeOut(false);
    }
    
    /**
     * Funci�n para quitar un recurso activo disponible que tiene m�ltiples
     * roles solapados en el mismo horario
     * @param erm Entrada de rol m�ltiple de un recurso activo.
     */
    protected void decAvailable(MultipleRole erm) {
        int respuesta;
        // Creo un elemento ficticio
        FictitiousElement e = new FictitiousElement(simul);
        // Esperamos por si otro elemento est� tratando de reservarlo
        while ((respuesta = erm.book(e)) == MultipleRole.BOOKED)
            ;
        // El recurso estaba libre y puede eliminarse
        if (respuesta == MultipleRole.FREE) {
        	print(Output.DEBUGMSG, "Overlapped Resource can be freed\t" + erm.getResource());
            availableResourceQueue.remove(erm);
            erm.releaseBooking();
        }
        // El recurso est� siendo usado por otro elemento
        else if (respuesta == MultipleRole.USED) {
            erm.setTimeOut(true);
        	print(Output.DEBUGMSG, "Overlapped Resource used out of time. Can't be freed\t" + erm.getResource(),
        			"Overlapped Resource used out of time. Can't be freed\t" + erm.getResource() + "\t" + available);
        }
        // El recurso est� reservado para otra CR de la misma actividad
        else if (respuesta == MultipleRole.OVERLAPPED) {
        	print(Output.ERRORMSG, "UNEXPECTED ERROR: Overlapped and booked role?\t" + erm.getResource(), 
        			"UNEXPECTED ERROR: Overlapped and booked role?\t" + available + "\t" + erm.getResource());
        }
    }
    
	public String getObjectTypeIdentifier() {
		return "RT";
	}

	public double getTs() {
		return manager.getTs();
	}
} // fin ResourceType
