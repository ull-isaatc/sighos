package es.ull.isaatc.simulation.benchmark.matrix;
/**
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.ull.isaatc.util.StandardThreadPool;
import es.ull.isaatc.util.ThreadPool;

/**
 * Ejemplo de multiplicacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilosX2 
{
	ExecutorService tp;
	double [][]m1;
	double [][]m2;

	public MultiplicaMatricesConHilosX2(int nThreads, double [][] m1, double [][] m2) {
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
		
		Collection<HiloMultiplicador> hilos = new ArrayList<HiloMultiplicador>(filas * columnas);
		
		// Para cada elemento de la matriz resultado, se lanza el hilo
		// correspondiente.
		for (int fila=0; fila<filas; fila++)
			for (int columna=0; columna<columnas; columna++)
				hilos.add(new HiloMultiplicador(m1, m2, resultado, fila, columna)); 

		try {
			tp.invokeAll(hilos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tp.shutdown();		
		// se devuelve el resultado obtenido	
		return resultado;
	}

	/**
	 * Calcula uno de los elementos de la matriz resultado
	 * @author chuidiang
	 */
	class HiloMultiplicador implements Callable<Integer>
	{
		private double[][] m1;
		private double[][] m2;
		private double[][] resultado;
		private int fila;
		private int columna;
		
		/**
		 * Guarda los parmetros que se le pasan 
		 * @param m1 primer operando
		 * @param m2 segundo operando
		 * @param resultado matriz donde dejar el resultado
		 * @param fila fila que debe calcular
		 * @param columna columna que debe calcular
		 */
		public HiloMultiplicador (double[][] m1, double[][]m2, double[][]resultado, int fila, int columna)
		{
			this.m1 = m1;
			this.m2 = m2;
			this.resultado = resultado;
			this.fila = fila;
			this.columna = columna;
		}

		/**
		 * Calcula el elemento fila,columna de la matriz resultado
		 */
		public void run()
		{
		}

		@Override
		public Integer call() throws Exception {
			resultado[fila][columna]=0.0;
			for (int i=0;i<m2.length;i++)
				resultado[fila][columna]+=m1[fila][i]*m2[i][columna];
			return 0;
		}
	}
}

/**
 * Ejemplo de multiplicacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilosX 
{
	ExecutorService tp;
	double [][]m1;
	double [][]m2;

	public MultiplicaMatricesConHilosX(int nThreads, double [][] m1, double [][] m2) {
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
		
		// Para cada elemento de la matriz resultado, se lanza el hilo
		// correspondiente.
		for (int fila=0; fila<filas; fila++)
			for (int columna=0; columna<columnas; columna++)
				tp.execute(new HiloMultiplicador(m1, m2, resultado, fila, columna));

		tp.shutdown();
		try {
			tp.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// se devuelve el resultado obtenido	
		return resultado;
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
		
		/**
		 * Guarda los parmetros que se le pasan 
		 * @param m1 primer operando
		 * @param m2 segundo operando
		 * @param resultado matriz donde dejar el resultado
		 * @param fila fila que debe calcular
		 * @param columna columna que debe calcular
		 */
		public HiloMultiplicador (double[][] m1, double[][]m2, double[][]resultado, int fila, int columna)
		{
			this.m1 = m1;
			this.m2 = m2;
			this.resultado = resultado;
			this.fila = fila;
			this.columna = columna;
		}

		/**
		 * Calcula el elemento fila,columna de la matriz resultado
		 */
		public void run()
		{
			resultado[fila][columna]=0.0;
			for (int i=0;i<m2.length;i++)
				resultado[fila][columna]+=m1[fila][i]*m2[i][columna];
		}
	}
}
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

}

/**
 * Ejemplo de multipliacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilosPool 
{
	ThreadPool<HiloMultiplicador> tp;
	CountDownLatch doneSignal;
	double [][]m1;
	double [][]m2;

	public MultiplicaMatricesConHilosPool(int nThreads, double [][] m1, double [][] m2) {
		tp = new StandardThreadPool<HiloMultiplicador>(nThreads);
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

}

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
}


/**
 * Ejemplo de multipliacin de matrices usando hilos.
 * @author chuidiang
 */
class MultiplicaMatricesConHilos2 
{
	double [][]m1;
	double [][]m2;

	public MultiplicaMatricesConHilos2(double [][] m1, double [][] m2) {
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
		
		LinkedList<Thread> hilos = new LinkedList<Thread>();
		// Para cada elemento de la matriz resultado, se lanza el hilo
		// correspondiente.
		for (int fila=0; fila<filas; fila++)
			for (int columna=0; columna<columnas; columna++) {
				Thread hilo = new Thread(new HiloMultiplicador2(m1, m2, resultado, fila, columna));
				hilos.add(hilo);
				hilo.start();
			}

		for (Thread hilo : hilos)
			try {
				hilo.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		// se devuelve el resultado obtenido	
		return resultado;
	}

	/**
	 * Calcula uno de los elementos de la matriz resultado
	 * @author chuidiang
	 */
	class HiloMultiplicador2 implements Runnable
	{
		private double[][] m1;
		private double[][] m2;
		private double[][] resultado;
		private int fila;
		private int columna;
		
		/**
		 * Guarda los parmetros que se le pasan 
		 * @param m1 primer operando
		 * @param m2 segundo operando
		 * @param resultado matriz donde dejar el resultado
		 * @param fila fila que debe calcular
		 * @param columna columna que debe calcular
		 */
		public HiloMultiplicador2 (double[][] m1, double[][]m2, double[][]resultado, int fila, int columna)
		{
			this.m1 = m1;
			this.m2 = m2;
			this.resultado = resultado;
			this.fila = fila;
			this.columna = columna;
		}

		/**
		 * Calcula el elemento fila,columna de la matriz resultado
		 */
		public void run()
		{
			resultado[fila][columna]=0.0;
			for (int i=0;i<m2.length;i++)
				resultado[fila][columna]+=m1[fila][i]*m2[i][columna];
		}
	}
}



/**
 * An standard benchmark using a matrix.
 * @author Ivn Castilla Rodrguez
 *
 */
public class MatrixBenchmark {
	
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
		int size = 512;
		int nThreads = 1;
		char type = 'i';
		if (args.length > 0) {
			type = args[0].charAt(0);
			size = Integer.parseInt(args[1]);
			if (args.length > 2)
				nThreads = Integer.parseInt(args[2]);
			
		}
		// Dos matrices para multiplicar 
//		double [][] m1 = new double[][] {{1,2,3},{1,2,3},{1,2,3}};
//		double [][] m2 = new double[][] {{1,2,3},{1,2,3},{1,2,3}};
		
		double [][] m1 = crearMatrizCuadrada(size, 100);
		double [][] m2 = crearMatrizCuadrada(size, 100);
	
		long t1 = System.currentTimeMillis();
		// Se multiplican
		
		switch(type) {
		case 'n':		new MultiplicaMatricesConHilos(nThreads, m1, m2).multiplica(); break;
		case 'p':		new MultiplicaMatricesConHilosPool(nThreads, m1, m2).multiplica(); break;
		case 's':		new MultiplicaMatricesConHilosSimple(nThreads, m1, m2).multiplica(); break;
		case 't':		new MultiplicaMatricesConHilos2(m1, m2).multiplica(); break;
		case 'x':		new MultiplicaMatricesConHilosX(nThreads, m1, m2).multiplica(); break;
		case 'i':		new MultiplicaMatricesConHilosX2(nThreads, m1, m2).multiplica(); break;
		}

		
		long t2 = System.currentTimeMillis();
		System.out.println(nThreads + "\t" + (t2 - t1));
		
		// Se saca por pantalla el resultado.
//		for (int i=0;i<resultado.length; i++)
//		{
//			for (int j=0;j<resultado[0].length;j++)
//				System.out.print(resultado[i][j]+" ");
//			System.out.println(" ");
//		}

	}

}
