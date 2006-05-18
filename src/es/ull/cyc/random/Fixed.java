/*
 * Fixed.java
 *
 * Created on 3 de septiembre de 2004, 11:53
 */

package es.ull.cyc.random;

/**
 * Fixed se usa para generar el mismo valor siempre
 * @author Iván Castilla Rodríguez
 */
public class Fixed extends RandomNumber {
    double value;
    
    /** Crea una nueva instancia de Fixed */
    public Fixed(double val) {
        value = val;
    }
    
    /** Crea una nueva instancia de Fixed */
    public Fixed(int val) {
        value = (double) val;
    }
    
    /************************************************************
     * Devuelve el valor fijo como double
     ************************************************************/
    public double sampleDouble() {
        return value;
    }
    
    /************************************************************
     * Devuelve el valor truncado a un entero
     ************************************************************/
    public int sampleInt() {
        return (int) value;
    }     
}
