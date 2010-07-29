package grafical.sighos.plugin.tree.model.handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




public class R_property extends Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<R_property> r_List = new ArrayList<R_property>();
	
	private ArrayList<String> r_resource = new ArrayList<String>();
	private ArrayList<String> r_resource2 = new ArrayList<String>();	
	private ArrayList<Integer> r_resource_n = new ArrayList<Integer>();	

	private ArrayList<String> r_code = new ArrayList<String>();	
	
	protected static int cursor = 0;
	
	public R_property(String name, String varName, int obj_Number, ArrayList<String> r_resourc,ArrayList<String> r_resourc2, ArrayList<Integer> r_resource_ ) {
		super(name, varName, obj_Number);
		this.r_resource = r_resourc; 
		this.r_resource2 = r_resourc2; 		
		this.r_resource_n = r_resource_;		
		code_generator();
		
	}	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitR_property(this, passAlongArgument);
	}
	
	
    public void code_generator() {
        r_code.clear();
    	
		r_code.add( "Resource " + varName +" = new Resource(" + obj_Number + ", this, \""+ name +"\" );");
	//	Resource rDoctor = new Resource(0, this, "Doctor Carlos");	
		
		for ( int i = 0; i < r_resource.size(); i++) {
				r_code.add(varName + ".addTimeTableEntry( "+r_resource.get(i)+", "+r_resource_n.get(i)+", "+r_resource2.get(i) +");");
			//	System.out.println(e.getKey() + " " + e.getValue());			
			
		}
		//rDoctor.addTimeTableEntry(jornadaSemanalMedico, 420,rtMedico);
		
		
    	
    }

	public ArrayList<String> getCode( ) {
		return r_code;
	}
    
	public String getCode( int pos) {
		return r_code.get(pos);
	}
	
	public void setCode( ArrayList<String> r_cod) {
		r_code = r_cod;
	}	
	
	public ArrayList<String > getR_resource() {
		return r_resource;
	}
	
	public void setR_resource( ArrayList<String > r_resourc) {
		r_resource = r_resourc;
	}	
	
	public ArrayList<String > getR_resource2() {
		return r_resource2;
	}
	
	public void setR_resource2( ArrayList<String > r_resourc2) {
		r_resource2 = r_resourc2;
	}	
	
	public ArrayList<Integer > getR_resource_n() {
		return r_resource_n;
	}
	
	public void setR_resource_n( ArrayList<Integer > r_resourc) {
		r_resource_n = r_resourc;
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
		R_property other = (R_property) obj;
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
    stream.writeObject(r_code );   
    stream.writeObject(r_resource);
    stream.writeObject(r_resource2);    
    stream.writeObject(r_resource_n);    
		
}
	
	
	private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException
{

	    name = (String)stream.readObject( );
	    varName = (String)stream.readObject( );
	    obj_Number = (Integer)stream.readObject( );
	    r_code = (ArrayList<String>)stream.readObject( ); 
	    r_resource = (ArrayList<String>) stream.readObject();
	    r_resource2 = (ArrayList<String>) stream.readObject();	    
	    r_resource_n = (ArrayList<Integer>) stream.readObject();	    
		
}
	
	
	
	
	

}