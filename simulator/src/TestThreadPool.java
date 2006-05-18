/**
 * 
 */

import es.ull.cyc.sync.*;
import java.io.*;

class newThreadPool extends ThreadPool {
	long tIni;
	FileWriter fichero;
	Lock lock;
	
	newThreadPool(long tIni, FileWriter fichero, Lock lock) {
		super();
		this.tIni = tIni;
		this.fichero = fichero;
		this.lock = lock;
	}
	
	newThreadPool(int nthreads, long tIni, FileWriter fichero, Lock lock) {
		super(nthreads);
		this.tIni = tIni;
		this.fichero = fichero;
		this.lock = lock;
	}
	
    protected void finishAll() {
    	super.finishAll();
		long tNext = System.currentTimeMillis();
		System.out.println("\r\nTerminados " + DumbPoolElement.cont + " eventos  en " + (tNext - tIni) + " con " + getNThreads() + " threads");
		try {
			fichero.write("\r\n" + (tNext - tIni) + "\t" + getNThreads());
			// DEBUG
//			for (int i = 0; i < getNThreads(); i++) {
//				//fichero.write("\t[" + i + "]\t" + getPoolElement(i).getNEvents() + "\t" + getPoolElement(i).getTAcc() + "\t");
//				fichero.write("\t" + getPoolElement(i).getNEvents());
//			}
//			fichero.write("\t ");
//			for (int i = 0; i < getNThreads(); i++) {
//				fichero.write("\t" + getPoolElement(i).getTAcc());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		lock.unlock();
    }
	
}
class DumbPoolElement extends Event {
	static int cont = 0;
	ThreadPool tp;
	long cuenta;
	int id;
	int levels;
	int tXlevel;
	
	DumbPoolElement(ThreadPool tp, long cuenta, int levels, int tXlevel) {
		super();
		this.tp = tp;
		this.cuenta = cuenta;
		this.levels = levels;
		this.tXlevel = tXlevel;
		id = getCont();
	}

	synchronized int getCont() {
		return cont++;
	}
	
	public void event() {
		double foo = 0.0;
		for (int i = 0; i < cuenta; i++) {
			foo += 1;
		}
		if (levels != 0)
			for (int j = 0; j < tXlevel; j++) {
				DumbPoolElement ee = new DumbPoolElement(tp, cuenta, levels - 1, tXlevel);
				tp.getThread(ee);				
			}
		//System.out.print("\tEvento " + id);
	}

}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestThreadPool {
	static ThreadPool tp;
	final static int NTESTS = 50;
	final static int NEVENTS = 3000;
	final static int NIVELES = 0;
	final static int HIJOS = 2;
	final static int CUENTA = 30000;
	final static int NTHREADS = 200;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileWriter fichero = null;
		Lock lock = new Lock();
	    try {
			fichero = new FileWriter("c:\\ttpNM"+ NEVENTS + "_" + CUENTA + "_" + NIVELES + "_" + HIJOS + "_" + NTHREADS + ".txt");
			fichero.write("Eventos: " + NEVENTS + " | Proceso: " + CUENTA + " | Niveles: " + NIVELES + " | Hijos: " + HIJOS + " | Lim. threads: " + NTHREADS + "\r\n");
			fichero.write("Tiempo\tThreads");

			for (int t = 0; t < NTESTS; t++) {
				System.gc();
				long tIni = System.currentTimeMillis();
				if (NTHREADS > 0)
					tp = new newThreadPool(NTHREADS, tIni, fichero, lock);
				else
					tp = new newThreadPool(tIni, fichero, lock);
				for (int i = 0; i < NEVENTS; i++) {
					DumbPoolElement ee = new DumbPoolElement(tp, CUENTA, NIVELES, HIJOS);
					tp.getThread(ee);
				}
				long tNext = System.currentTimeMillis();
				System.out.println("\r\nLanzados los eventos en " + (tNext - tIni));
				tp.finish();
				try {
					lock.lock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			fichero.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
