/*
 * MultipleRole.java
 *
 * Created on 21 de octubre de 2004, 12:29
 */

package es.ull.cyc.simulation;

import java.util.ArrayList;

/**
 * Esta clase se emplea para representar los distintos recursos activos que 
 * tienen más de un rol (clase de recurso) en el mismo instante de tiempo.
 * @author Iván Castilla Rodríguez
 */
public class MultipleRole {
    /**
     * Indica que el recurso está reservado por otro elemento en otra actividad.
     */    
    protected static final int BOOKED = 1;
    /**
     * Indica que el recurso está disponible.
     */    
    protected static final int FREE = 2;
    /**
     * Indica que el recurso está siendo usado por otro elemento en otra actividad.
     */    
    protected static final int USED = 3;
    /**
     * Indica que el recurso está reservado por el mismo elemento en la misma actividad.
     */    
    protected static final int OVERLAPPED = 4;
    
    /** Lista de roles que tiene el recurso en el mismo horario */
    protected ArrayList roleList;
    /* MOD 30/10/04 Forma cutre de hacerlo */
    /** Lista de gestores de actividades implicados */
    protected ArrayList managerList;
    /** Recurso activo con múltiples roles solapados en horario */
    protected Resource res;
    /** Elemento que está utilizando este recurso actualmente */
    protected BasicElement bookedElement = null;
    /** Clase de recurso para la que está reservado actualmente */
    protected ResourceType bookedResourceType = null;
    /** Indica si el recurso activo ya terminó su horario de ejecución pero está
     ocupado atendiendo a algún elemento y no puede eliminarse */
    protected boolean timeOut = false;
    /** Indicador de que el recurso está siendo reservado para una actividad concreta */
    protected boolean booked = false;
    
    /**
     * Creates a new instance of MultipleRole
     * @param RoleList Lista de los roles para los que está disponible el recurso
     * simultáneamente.
     * @param res Recurso activo.
     */
    public MultipleRole(ArrayList roleList, Resource res) {
        this.res = res;
        this.roleList = roleList;
        /* MOD 30/10/04 Forma cutre de hacerlo */
        managerList = new ArrayList();
        for (int i = 0; i < roleList.size(); i++) {
            ResourceType rt = (ResourceType) roleList.get(i);
            if (!managerList.contains(rt.getManager()))
                managerList.add(rt.getManager());
        }
    }

    /**
     * Getter for property listaRoles.
     * @return Value of property listaRoles.
     */
    protected ArrayList getRoleList() {
        return roleList;
    }
    
    /**
     * Getter for property listaGA.
     * @return Value of property listaGA.
     */
    protected ArrayList getManagerList() {
        return managerList;
    }
    
    /**
     * Getter for property res.
     * @return Value of property res.
     */
    protected Resource getResource() {
        return res;
    }
    
    /**
     * Getter for property bookedElement.
     * @return Value of property bookedElement.
     */
    protected BasicElement getBookedElement() {
        return bookedElement;
    }
    
    /**
     * Setter for property bookedElement.
     * @param e New value of property bookedElement.
     */
    protected void setBookedElement(BasicElement e) {
        this.bookedElement = e;
    }
    
    /**
     * MOD 22/10/04
     * Permite saber si este recurso activo está siendo empleado ya.
     * @return Verdadero (true) si no tiene ningún elemento asociado, Falso (false)
     * en otro caso
     */
    protected boolean isAvailable() {
        return (bookedElement == null);
    }
    
    /**
     * Getter for property timeOut.
     * @return Value of property timeOut.
     */
    protected boolean isTimeOut() {
        return timeOut;
    }
    
    /**
     * Setter for property timeOut.
     * @param fueraTiempo New value of property timeOut.
     */
    protected void setTimeOut(boolean timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Pregunta y establece en una operación atómica el valor de reservado.
     * En caso de que el recurso esté libre se reserva desde esta misma función.
     * @param e Elemento con el que se realizan las comprobaciones.
     * @return El estado de esta entrada: SOLAPADO si el recurso está reservado
     * por el mismo elemento en la misma actividad; RESERVADO si el recurso está
     * reservado por otro elemento en otra actividad; USADO si el recurso está
     * siendo usado por otro elemento en otra actividad; LIBRE si el recurso
     * está disponible.
     */
    protected synchronized int book(BasicElement e) {
        // Recurso solapado
        if (this.bookedElement == e)
            return OVERLAPPED;
        // Recurso reservado por otro elemento
        if (booked) // && (this.e != e)
            return BOOKED;
        if (this.bookedElement == null) {
            booked = true;
            this.bookedElement = e;
            return FREE;
        }
        return USED;    
    }
    
    /**
     * Permite "liberar" a esta entrada de una reserva que no va a usarse
     */
    protected synchronized void releaseBooking() {
        bookedElement = null;
        bookedResourceType = null;
        booked = false;
    }
    
    /**
     * Getter for property booked.
     * @return Value of property booked.
     */
    protected synchronized boolean getBooked() {
        return booked;
    }

    /**
     * Me permite saber si el recurso está reservado por este elemento.
     * @return Value of property reservado.
     * @param e Elemento con el que se realiza la comprobación.
     */
    protected synchronized boolean isBooked(BasicElement e) {
        return (booked && (this.bookedElement == e));
    }

    /**
     * Setter for property booked.
     * @param reservado New value of property booked.
     */
    protected synchronized void setBooked(boolean reservado) {
        this.booked = reservado;
    }
    
    /**
     * Getter for property bookedResourceType.
     * @return Value of property bookedResourceType.
     */
    protected ResourceType getBookedResourceType() {
        return bookedResourceType;
    }
    
    /**
     * Setter for property bookedResourceType.
     * @param cr New value of property bookedResourceType.
     */
    protected void setBookedResourceType(ResourceType rt) {
        this.bookedResourceType = rt;
    }
    
    protected ResourceType getResourceType(int ind) {
        return (ResourceType) roleList.get(ind);
    }
    
    
}

