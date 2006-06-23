package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.util.*;

/**
 * Las instancias de esta clase representa los diferentes tipos de recursos que
 * hay en el sistema, o lo que es lo mismo, los diferentes roles que tienen los
 * recursos. Los recursos activos podrán adquirir diferentes roles en cada una
 * de sus entradas de horario.
 * @author Carlos Martin Galan
 */
public class ResourceType extends DescSimulationObject {
    /** Gestor de actividades relacionado con esta clase de recurso */
    protected ActivityManager manager;
    /** Cola de recursos activos con horarios solapados en distintas Clases de 
     recurso */
    protected ResourceList availableResourceQueue;

    /**
     * Crea una nueva clase de recurso con una descripción
     * @param modelRT Clase de recurso del modelo
     * @param simul Associated simulation
     */
	public ResourceType(int id, Simulation simul, String description) {
		super(id, simul, description);
        availableResourceQueue = new ResourceList();
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
	 * Books all the available resources and gets the total amount.
     * @param e Element looking for available elements.
	 * @return An array that contains the total amount of available resources without 
	 * any conflicts (0) and the total amount of resources which are booked for, at least, 
	 * other resource type in the same activity (1). 
	 */
    protected int[] getAvailable(Element e) {
        int total[] = new int[2];
        for (int i = 0; i < availableResourceQueue.size(); i++) {
            Resource res = availableResourceQueue.get(i);
            // First, I check if the resource is being used
            if (res.getCurrentElement() == null) {
	            if (res.addBook(e))
	            	total[0]++;
	            else
	            	total[1]++;
            }
        }
        return total;
    }

    /**
     * Esta función permite "liberar" aquellos recursos que se reservaron en una
     * llamada a getDisponibles desde una actividad NO realizable
     * @param e El elemento que preguntó por los recursos
     */
    protected void resetAvailable(Element e) {
        for (int i = 0; i < availableResourceQueue.size(); i++)
        	availableResourceQueue.get(i).removeBook(e);
    }

    /**
     * Permite acceder a la entrada de rol múltiple de la cola de recursos
     * disponibles con índice ind
     * @param ind Indice del recurso
     * @return Entrada de rol múltiple correspondiente al índice o null si no 
     * existe una entrada correspondiente a ese índice.
     */
    protected Resource getResource(int ind) {
        if (ind >= availableResourceQueue.size())
            return null;
        return availableResourceQueue.get(ind);
    }
    
    /**
     * Busca el primer recurso con roles solapados de la cola de recursos
     * disponibles que esté reservado (o en uso) por el elemento e y no ha sido
     * ya reservado para otra clase de recurso (solapado). Comienza la
     * búsqueda a partir del recurso con índice ind.
     * @param ind Indice a partir del cual comienza la búsqueda.
     * @param e Elemento que tiene reservado (o en uso) al recurso buscado.
     * @return El índice del recurso o -1 si no encontró ninguno.
     */
    protected int getNextAvailableResource(int ind, BasicElement e) {
        for (; ind < availableResourceQueue.size(); ind++) {
            Resource res = availableResourceQueue.get(ind);
            // Checks if the resource is busy (taken by other element or conflict in the same activity)
            // FIXME Debería bastar con preguntar por el RT
            if ((res.getCurrentElement() == null) && (res.getCurrentResourceType() == null))
            	return ind;
        }
        return -1;
    }
    
	/**
	 * Decrementa el número de recursos disponibles de esta clase de recurso en
	 * una cantidad dada.
	 * @param n Resources needed
     * @param e element catching the resources
	 */
    protected void catchResources(int n, Element e) {
        print(Output.MessageType.DEBUG, "Decrease amount\t" + n,
        		"Decrease amount\t" + n + "\t" + e);
        
        // When this point is reached, it is suppose that there are enough resources
        for (int i = 0; i < availableResourceQueue.size(); i++) {
            Resource res = availableResourceQueue.get(i);
            // Checks the availability of the resource
            if (res.getCurrentElement() == null) {
            	// The resource has no conflict
            	if (res.getCurrentResourceType() == null) {
	            	if (n > 0) {
	            		res.catchResource(e, this);
	            		n--;
	                    print(Output.MessageType.DEBUG, "Resource taken\t" + res,
	                    		"Resource taken\t" + res + "\t " + n + "\t" + e);
	            	}
	            	else {
	            		res.removeBook(e);
	            	}
            	}
            	// Conflict (in the same activity)
            	// Theoretically, I have no need of check "n"
            	else if (res.getCurrentResourceType() == this) {
            		res.catchResource(e, this);
            		n--;
                    print(Output.MessageType.DEBUG, "Resource taken\t" + res,
                    		"Resource taken\t" + res + "\t " + n + "\t" + e);
                    // This check should be unneeded
                    if (n < 0) {
                    	print(Output.MessageType.ERROR, "UNEXPECTED ERROR: More resources than expected", 
                    			"UNEXPECTED ERROR: More resources than expected\t"+ n + "\t" + e);
                    }
            	}
            }
        }
        // This check should be unneeded
        if (n > 0)
        	print(Output.MessageType.ERROR, "UNEXPECTED ERROR: Less resources than expected", 
        			"UNEXPECTED ERROR: Less resources than expected\t"+ n + "\t" + e);
    }

    /**
     * Adds a resource as available
     * @param res New available resource.
     */
    protected void incAvailable(Resource res) {
    	print(Output.MessageType.DEBUG, "Resource added\t" + res);
        availableResourceQueue.add(res);
        // If the resource was being used in a previous "availability period", it was marked as
        // "timeOut". This mark can be removed.
        if ((res.getCurrentResourceType() == this) && res.isTimeOut())
        	res.setTimeOut(false);
    }
    
    /**
     * Removes a resource 
     * @param res New unavailable resource.
     */
    protected void decAvailable(Resource res) {
    	print(Output.MessageType.DEBUG, "Resource removed\t" + res);
        // If the resource is being used for this resource type, it's marked as "timeOut"
        if (!availableResourceQueue.remove(res) && (res.getCurrentResourceType() == this))
        	res.setTimeOut(true);
    }
    
	public String getObjectTypeIdentifier() {
		return "RT";
	}

	public double getTs() {
		return manager.getTs();
	}

	class ResourceList {
	    protected ArrayList<Resource> resources;
	    protected ArrayList<Integer> counter;
	    
	    ResourceList() {
	    	resources = new ArrayList<Resource>();
	    	counter = new ArrayList<Integer>();
	    }
	    
	    void add(Resource res) {
	    	int pos = resources.indexOf(res);
	    	if (pos == -1) {
	    		resources.add(res);
	    		counter.add(new Integer(1));
	    	}
	    	else
	    		counter.set(pos, new Integer(counter.get(pos).intValue() + 1)); 
	    }
	    
	    /**
	     * Removes a resource. The resource can have more than one appearance in the list. In 
	     * this case, it's no t really removed.
	     * @param res
	     * @return True if the resource is completely removed from the list. False in other case.
	     */
	    boolean remove(Resource res) {
	    	int pos = resources.indexOf(res);
	    	// FIXME Debería crearme un tipo personalizado de excepción
	    	if (pos == -1)
	    		throw new RuntimeException("Unexpected error: Resource not found in resource type");
	    	if (counter.get(pos).intValue() > 1) {
	    		counter.set(pos, new Integer(counter.get(pos).intValue() - 1));
	    		return false;
	    	}
			resources.remove(pos);
			counter.remove(pos);
	    	return true;
	    }
	    
	    Resource get(int index) {
	    	return resources.get(index);
	    }
	    
	    int size() {
	    	return resources.size();
	    }
	}

} 
