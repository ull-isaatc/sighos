package es.ull.isaatc.simulation.benchmark.matrix;
/**
 * 
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ejemplo de multipliacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilos 
{
	ExecutorService tp;
	CountDownLatch doneSignal;
	double [][]m1;
	double [][]m2;

	public MultiplicaMatricesConHilos(int nThreads, double [][] m1, double [][] m2) {
		tp = Executors.newFixedThreadPool(nThreads);
		this.m1 = m1;
		this.m2 = m2;
	}
	
	/**
	 * Realiza la multiplicacin de las dos matrices y devuelve el resultado
	 * @param m1 primer operando
	 * @param m2 segundo operando
	 * @return resultado de multiplicar m1xm2
	 */
	public double[][] multiplica ()
	{
		// condiciones que deben cumplirse y que se suponen ciertas
		// con los parmetros de entrada
		assert m1!=null;
		assert m2!=null;
		assert m1.length > 0;
		assert m1[0].length > 0;
		assert m2.length > 0;
		assert m2[0].length > 0;
		assert m1.length==m2[0].length;
		
		// Calculo de las dimensiones de la matriz resultado y
		// reserva de espacio para ella
		int filas = m1.length;
		int columnas = m2[0].length;
		double [][] resultado = new double[filas][columnas];
		
		doneSignal  = new CountDownLatch(filas * columnas);
		
		// Para cada elemento de la matriz resultado, se lanza el hilo
		// correspondiente.
		for (int fila=0; fila<filas; fila++)
			for (int columna=0; columna<columnas; columna++)
				tp.execute(new HiloMultiplicador(doneSignal, m1, m2, resultado, fila, columna));

		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			tp.shutdown();
		}
		
		// se devuelve el resultado obtenido	
		return resultado;
	}

}

/**
 * Calcula uno de los elementos de la matriz resultado
 * @author chuidiang
 */
class HiloMultiplicador implements Runnable
{
	private double[][] m1;
	private double[][] m2;
	private double[][] resultado;
	private int fila;
	private int columna;
	private CountDownLatch doneSignal;
	
	/**
	 * Guarda los parmetros que se le pasan 
	 * @param m1 primer operando
	 * @param m2 segundo operando
	 * @param resultado matriz donde dejar el resultado
	 * @param fila fila que debe calcular
	 * @param columna columna que debe calcular
	 */
	public HiloMultiplicador (CountDownLatch doneSignal, double[][] m1, double[][]m2, double[][]resultado, int fila, int columna)
	{
		this.m1 = m1;
		this.m2 = m2;
		this.resultado = resultado;
		this.fila = fila;
		this.columna = columna;
		this.doneSignal = doneSignal;
	}

	/**
	 * Calcula el elemento fila,columna de la matriz resultado
	 */
	public void run()
	{
		resultado[fila][columna]=0.0;
		for (int i=0;i<m2.length;i++)
			resultado[fila][columna]+=m1[fila][i]*m2[i][columna];
		doneSignal.countDown();
	}
}

/**
 * An standard benchmark using a matrix.
 * @author Ivn Castilla Rodrguez
 *
 */
public class MatrixBenchmark {
	private static int NEXP = 1;
	
	static double [][] crearMatrizCuadrada(int filas, int maxValue) {
		double [][]matriz = new double[filas][filas];
		int valor = 1;
		for (int i = 0; i < filas; i++) 
			for (int j = 0; j < filas; j++) {
				matriz[i][j] = valor;
				valor = (valor + 1) % maxValue;
			}
		return matriz;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 1024;
		int nThreads = 1;
		if (args.length > 0) {
			size = Integer.parseInt(args[0]);
			nThreads = Integer.parseInt(args[1]);
			
		}
		// Dos matrices para multiplicar 
//		double [][] m1 = new double[][] {{1,2,3},{1,2,3},{1,2,3}};
//		double [][] m2 = new double[][] {{1,2,3},{1,2,3},{1,2,3}};
		
		double [][] m1 = crearMatrizCuadrada(size, 100);
		double [][] m2 = crearMatrizCuadrada(size, 100);
	
		for (int i = 0; i < NEXP; i++) {
			long t1 = System.currentTimeMillis();
			// Se multiplican
			double [][] resultado = new MultiplicaMatricesConHilos(nThreads, m1, m2).multiplica();
			
			long t2 = System.currentTimeMillis();
			System.out.println(nThreads + "\t" + (t2 - t1));
		}
		
		// Se saca por pantalla el resultado.
//		for (int i=0;i<resultado.length; i++)
//		{
//			for (int j=0;j<resultado[0].length;j++)
//				System.out.print(resultado[i][j]+" ");
//			System.out.println(" ");
//		}

	}

}
