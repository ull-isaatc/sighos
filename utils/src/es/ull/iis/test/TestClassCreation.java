/**
 * 
 */
package es.ull.iis.test;


class MyIntegerObject implements Comparable<MyIntegerObject> {
	final int id;
	
	public MyIntegerObject(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(MyIntegerObject arg0) {
		return new Integer(id).compareTo(arg0.id);
	}
	
}

class MyIntObject implements Comparable<MyIntObject> {
	final int id;
	
	public MyIntObject(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(MyIntObject arg0) {
		if (id > arg0.id)
			return 1;
		if (id < arg0.id)
			return -1;
		return 0;
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestClassCreation {
	public static final int ITER = 10000000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyIntObject i1 = new MyIntObject(1);
		MyIntObject i2 = new MyIntObject(2);
		MyIntegerObject int1 = new MyIntegerObject(1);
		MyIntegerObject int2 = new MyIntegerObject(2);

		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ITER; i++) {
			i1.compareTo(i1);
			i1.compareTo(i2);
			i2.compareTo(i1);			
		}
		t1 = System.currentTimeMillis() - t1;
		System.out.println("Comparación int: " + t1);
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < ITER; i++) {
			int1.compareTo(int1);
			int1.compareTo(int2);
			int2.compareTo(int1);			
		}
		t1 = System.currentTimeMillis() - t1;
		System.out.println("Comparación Integer: " + t1);
	}

}
