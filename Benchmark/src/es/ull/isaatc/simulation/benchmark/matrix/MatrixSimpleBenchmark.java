package es.ull.isaatc.simulation.benchmark.matrix;
/**
 * 
 */


/**
 * Ejemplo de multipliacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilosSimple 
{
	double [][]m1;
	double [][]m2;
	Thread []threads;

	public MultiplicaMatricesConHilosSimple(int nThreads, double [][] m1, double [][] m2) {
		threads = new Thread[nThreads];
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
		double [][] resultado = new double[m1.length][m2[0].length];
		int filasXthread = m1.length / threads.length;
		int columnasXthread = m2[0].length;
		
		// Para cada elemento de la matriz resultado, se lanza el hilo
		// correspondiente.
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new HiloMultiplicadorSimple(m1, m2, resultado, filasXthread * i, filasXthread * (i + 1) - 1, 0, columnasXthread - 1);
			threads[i].start();
		}
//		for (int fila=0; fila<filas; fila++)
//			for (int columna=0; columna<columnas; columna++) {
//				threads[] = new HiloMultiplicadorSimple(m1, m2, resultado, fila, columna);
//			}

		for (Thread hilo: threads)
			try {
				hilo.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		// se devuelve el resultado obtenido	
		return resultado;
	}

}

/**
 * Calcula uno de los elementos de la matriz resultado
 * @author chuidiang
 */
class HiloMultiplicadorSimple extends Thread
{
	private double[][] m1;
	private double[][] m2;
	private double[][] resultado;
	private int fIni;
	private int fFin;
	private int cIni;
	private int cFin; 
	
	/**
	 * Guarda los parmetros que se le pasan 
	 * @param m1 primer operando
	 * @param m2 segundo operando
	 * @param resultado matriz donde dejar el resultado
	 * @param fIni fila que debe calcular
	 * @param columna columna que debe calcular
	 */
	public HiloMultiplicadorSimple (double[][] m1, double[][]m2, double[][]resultado, int fIni, int fFin, int cIni, int cFin)
	{
		this.m1 = m1;
		this.m2 = m2;
		this.resultado = resultado;
		this.fIni = fIni;
		this.fFin = fFin;
		this.cIni = cIni;
		this.cFin = cFin;
	}

	/**
	 * Calcula el elemento fila,columna de la matriz resultado
	 */
	public void run()
	{
		for (int f = fIni; f <= fFin; f++) {
			for (int c = cIni; c <= cFin; c++) {
				resultado[f][c]=0.0;
				for (int i=0;i<m2.length;i++)
					resultado[f][c]+=m1[f][i]*m2[i][c];
			}
		}
	}
}

/**
 * An standard benchmark using a matrix.
 * @author Ivn Castilla Rodrguez
 *
 */
public class MatrixSimpleBenchmark {
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
	
	static void printMatriz(double[][] m) {
		for (int j=0;j<m.length; j++)
		{
			for (int k=0;k<m[0].length;k++)
				System.out.print(m[j][k]+" ");
			System.out.println(" ");
		}		
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
//		double [][] m1 = new double[][] {{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}};
//		double [][] m2 = new double[][] {{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}};
		
		double [][] m1 = crearMatrizCuadrada(size, 100);
		double [][] m2 = crearMatrizCuadrada(size, 100);
	
//		System.out.println("M1: ");
//		printMatriz(m1);
//		System.out.println("M2: ");
//		printMatriz(m2);
		for (int i = 0; i < NEXP; i++) {
			long t1 = System.currentTimeMillis();
			// Se multiplican
			double [][] resultado = new MultiplicaMatricesConHilosSimple(nThreads, m1, m2).multiplica();
			
			long t2 = System.currentTimeMillis();
			System.out.println(nThreads + "\t" + (t2 - t1));
			// Se saca por pantalla el resultado.
//			System.out.println("RES: ");
//			printMatriz(resultado);
		}
		

	}

}
