package grafical.sighos.plugin.tree.model.handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




public class W_property extends Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<W_property> w_List = new ArrayList<W_property>();
	
	private ArrayList<String> w_resource = new ArrayList<String>();
	private ArrayList<Integer> w_resource_n = new ArrayList<Integer>();	

	private ArrayList<String> w_code = new ArrayList<String>();	
	
	protected static int cursor = 0;
	
	public W_property(String name, String varName, int obj_Number, ArrayList<String> w_resourc, ArrayList<Integer> w_resource_ ) {
		super(name, varName, obj_Number);
		this.w_resource = w_resourc;  	
		this.w_resource_n = w_resource_;		
		code_generator();
		
	}	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitW_property(this, passAlongArgument);
	}
	
	
    public void code_generator() {
        w_code.clear();
    	
		w_code.add( "WorkGroup " + varName +" = new WorkGroup( );");
	//	wgOpStandard.add( rtMedico , 1);
		
		
		for ( int i = 0; i < w_resource.size(); i++) {
				w_code.add(varName + ".add( "+w_resource.get(i)+", "+w_resource_n.get(i)+");");
			//	System.out.println(e.getKey() + " " + e.getValue());			
			
		}
		
		
		//Iterator<java.util.Map.Entry<String, Integer>> it = w_resource.entrySet().iterator();
		//while (it.hasNext()) {
		//	Map.Entry<String, Integer> e = (Map.Entry<String, Integer>)it.next();
		//	w_code.add(varName + ".add( "+e.getKey()+", "+e.getValue()+");");
		//	System.out.println(e.getKey() + " " + e.getValue());
	//	}
		
    	
    }

	public ArrayList<String> getCode( ) {
		return w_code;
	}
    
	public String getCode( int pos) {
		return w_code.get(pos);
	}
	
	public void setCode( ArrayList<String> w_cod) {
		w_code = w_cod;
	}	
	
	public ArrayList<String > getW_resource() {
		return w_resource;
	}
	
	public void setW_resource( ArrayList<String > w_resourc) {
		w_resource = w_resourc;
	}	
	
	public ArrayList<Integer > getW_resource_n() {
		return w_resource_n;
	}
	
	public void setW_resource_n( ArrayList<Integer > w_resourc) {
		w_resource_n = w_resourc;
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
		W_property other = (W_property) obj;
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
    stream.writeObject(w_code );   
    stream.writeObject(w_resource);
    stream.writeObject(w_resource_n);    
		
}
	
	
	private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException
{

	    name = (String)stream.readObject( );
	    varName = (String)stream.readObject( );
	    obj_Number = (Integer)stream.readObject( );
	    w_code = (ArrayList<String>)stream.readObject( ); 
	    w_resource = (ArrayList<String>) stream.readObject();
	    w_resource_n = (ArrayList<Integer>) stream.readObject();	    
		
}
	
	
	
	
	

}