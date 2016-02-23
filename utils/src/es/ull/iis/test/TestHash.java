package es.ull.iis.test;
import java.util.HashMap;
import java.util.TreeMap;


class Pair<T, U> {
	T obj1;
	U obj2;
	public Pair(T obj1, U obj2) {
		this.obj1 = obj1;
		this.obj2 = obj2;
	}
	@Override
	public String toString() {
		return "" + obj1 + ":" + obj2;
	}
}

class Pair1 extends Pair<Integer, Float> implements Comparable<Pair1> {
	public Pair1(Integer obj1, Float obj2) {
		super(obj1, obj2);
	}

	public int compareTo(Pair1 arg0) {
		int res1 = obj1.compareTo(arg0.obj1);
		if (res1 == 0)
			return (obj2.compareTo(arg0.obj2));
		return res1;
	}
}

public class TestHash {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<Pair<Integer, Float>, Integer> tree = new TreeMap<Pair<Integer,Float>, Integer>();
		HashMap<Pair<Integer, Float>, Integer> map = new HashMap<Pair<Integer,Float>, Integer>();
		Pair1 p1 = new Pair1(new Integer(1), new Float(2.0));
		Pair1 p2 = new Pair1(new Integer(1), new Float(3.0));
		Pair1 p1a = new Pair1(new Integer(1), new Float(2.0));
		Pair1 p3 = new Pair1(new Integer(2), new Float(2.0));
		System.out.println(p1 + " " + p1.hashCode());
		System.out.println(p2 + " " + p2.hashCode());
		System.out.println(p1a + " " + p1a.hashCode());
		System.out.println(p3 + " " + p3.hashCode());
		tree.put(p1, 1);
		System.out.println(tree);
		tree.put(p1a, 2);
		System.out.println(tree);
		tree.put(p2, 1);
		System.out.println(tree);
		tree.put(p3, 1);
		System.out.println(tree);
		map.put(p1, 1);
		System.out.println(map);
		map.put(p1a, 2);
		System.out.println(map);
		map.put(p2, 1);
		System.out.println(map);
		map.put(p3, 1);
		System.out.println(map);
	}

}
