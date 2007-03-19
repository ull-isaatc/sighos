/*
 * FicheroConcurrente.java
 *
 * Created on 18 de noviembre de 2004, 12:24
 */
import java.io.*;

/**
 * "Ranchito" para conseguir un fichero en el que pueda controlar el acceso desde
 * varios "threads" que están accediendo a él de forma concurrente
 * De paso voy a aprovecharlo para llevar el tiempo de simulación
 * @author Iván Castilla Rodríguez
 */
public class FicheroConcurrente {
    FileWriter fichero;
    private int contador;
    
    /**
     * Constructor del fichero
     * @param file Nombre del fichero
     */
    FicheroConcurrente(String file) throws IOException {
        fichero = new FileWriter(file);
        contador = 0;
    }

    /**
     * Añade un nuevo elemento que usará el fichero
     */
    public synchronized void incContador() {
        contador++;
    }
    
    /**
     * Descuenta un elemento de los que usaban el fichero. Si el contador llega 
     * a 0 se cierra el fichero.
     */
    public synchronized void decContador() throws IOException {
        contador--;
        if (contador == 0)
            fichero.close();
    }
    
    /** 
     * Imprime un mensaje en el fichero
     * @param str Mensaje a imprimir
     */
    public void print(String str) throws IOException {
        fichero.write(str);        
        fichero.flush();
    }
    
    /** 
     * Imprime un mensaje en el fichero añadiéndole un salto de línea
     * @param str Mensaje a imprimir
     */
    public void println(String str) throws IOException {
        fichero.write(str + "\r\n");
        fichero.flush();
    }
    
}
