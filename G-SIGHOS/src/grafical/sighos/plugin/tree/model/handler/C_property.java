package grafical.sighos.plugin.tree.model.handler;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class C_property extends Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<C_property> c_List = new ArrayList<C_property>();
	
	private String c_code;	
	private String tipo;
	private String cycle;
	private ArrayList<Integer> vals;
	
	protected static int cursor = 0;
		
	
	public C_property(String name, String varName, Integer obj_Number, String tip, String cycl,ArrayList<Integer> val ) {
		super(name, varName, obj_Number);
		this.tipo = tip; 
		this.cycle = cycl;
		this.vals = val;
		code_generator();
	}				

	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitC_property(this, passAlongArgument);
	}

	
    public void code_generator() {
    	
    	c_code = "SimulationPeriodicCycle " + varName +
		" = new SimulationPeriodicCycle( this, " + vals.get(0) 
		+ ", new SimulationTimeFunction( this, \"ConstantVariate\"," + vals.get(1)+"), " 
		+ vals.get(2); 

		if ( !tipo.equals("Compuesto")) {
			c_code += ");";
		}
		else {
			c_code += "," + cycle + ");";
		}
  	
    }
	
	
	
	public String getCode() {
		return c_code;
	}
	
	public String getCycle() {
		return cycle;
	}	
	
	public String getTipo() {
		return tipo;
	}	

	public ArrayList<Integer> getVals() {
		return vals;
	}		
	
	public void setCode( String c_cod) {
		c_code = c_cod;
	}	
	
	public String toString() {
		return name;
	}
	@Override
	public int hashCode() {
		return obj_Number;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		C_property other = (C_property) obj;
		if (obj_Number != other.getObj_Number()) {
				return false;
		} 
		return true;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
    throws IOException
{
    stream.writeObject(name );
    stream.writeObject(varName );
    stream.writeObject(obj_Number );
    stream.writeObject(c_code );
    stream.writeObject(tipo );
    stream.writeObject(cycle );
    stream.writeObject(vals );    
		
}
	
	
	private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException
{

	    name = (String)stream.readObject( );
	    varName = (String)stream.readObject( );
	    obj_Number = (Integer)stream.readObject( );
	    c_code = (String)stream.readObject( ); 
	    tipo = (String)stream.readObject( ); 
	    cycle = (String)stream.readObject( ); 
	    vals = (ArrayList<Integer>)stream.readObject( ); 	    

		
}
	
	
	
	
	
}
