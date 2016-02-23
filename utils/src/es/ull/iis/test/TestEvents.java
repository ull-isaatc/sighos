package es.ull.iis.test;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

class Escuchador implements EventListener {
	
}
class Evento extends EventObject {
	int id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Evento(int id, Object source) {
		super(source);
		this.id = id;
	}	
}

class LanzaEventos {
	ArrayList<Evento> lista;
	int id;
	int contador;
	
	LanzaEventos(int id) {
		lista = new ArrayList<Evento>();
		this.id = id;
	}
	
	void addEvento() {
		lista.add(new Evento(contador++, this));
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestEvents {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LanzaEventos(0);
		
	}

}
