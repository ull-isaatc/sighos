package es.ull.isaatc.simulation.sequential.test;


class Exp extends ClassLoader{
	static final int NEXP = 100000000;
	Exp(){};
	
	public void run() {
		VarContainer cl = new VarContainer();
		@SuppressWarnings("unused")
		Integer temp;
		
		long init = System.currentTimeMillis();
		for (int i = 0; i < NEXP; i++) {
			temp = cl.integer1;
		}
		long time1 = System.currentTimeMillis();
		System.out.println ("Acceso directo: " + (time1 - init) + " milisegundos");
		for (int i = 0; i < NEXP; i++) {
			temp = cl.getInteger1();
		}
		long time2 = System.currentTimeMillis();
		System.out.println ("Acceso llamada: " + (time2 - time1) + " milisegundos");
		for (int i = 0; i < NEXP; i++) {
			temp = cl.integer2.get("I");
		}
		long time3 = System.currentTimeMillis();
		System.out.println ("Acceso mapa: " + (time3 - time2) + " milisegundos");
		for (int i = 0; i < NEXP; i++) {
			temp = (Integer) cl.getVar("integer1").intValue();
		}
		long time4 = System.currentTimeMillis();
		System.out.println ("Acceso metodo: " + (time4 - time3) + " milisegundos");
		for (int i = 0; i < NEXP; i++) {
			Class<?> className;
			try {
				className = loadClass("es.ull.isaatc.simulation.test.VarContainer");
				temp = ((VarContainer)className.cast(cl)).integer1;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long time5 = System.currentTimeMillis();
		System.out.println ("Acceso class loader: " + (time5 - time4) + " milisegundos");	
	}
}

public class AccessTimeCost {

	public static void main(String[] args) {
		Exp e = new Exp();
		e.run();
	}
}
