/*
 * Time.java
 *
 * Created on 9 de noviembre de 2005, 13:56
 */

package es.ull.cyc.util;

/**
 *
 * @author Iv�n Castilla Rodr�guez
 */
public class Time {
    
    /** Creates a new instance of Time */
    public Time() {
    }

    /**
     * Devuelve una cadena representando un instante de simulaci�n
     * @param ts Tiempo de simulacion
     * @return Cadena de caracteres con el tiempo de simulaci�n en el formato 
     * "d�a"d:"hora"h:"min"min
     */
	public static String toDayHourMinute(double ts) {
		int intTs = (int) ts;
		int day = intTs / 1440;
		int hour = (intTs - day * 1440) / 60;
		int min = intTs - day * 1440 - hour * 60;
		return (new String(day+"d:"+hour+"h:"+min+"min"));
	}


}
