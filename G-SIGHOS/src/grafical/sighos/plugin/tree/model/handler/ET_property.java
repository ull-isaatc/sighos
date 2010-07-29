package grafical.sighos.plugin.tree.model.handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ET_property extends Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<ET_property> et_List = new ArrayList<ET_property>();
	
	private String et_code;	
	
	protected static int cursor = 0;
	
	public ET_property(String name, String varName, int obj_Number, String et_cod) {
		super(name, varName, obj_Number);
		this.et_code = et_cod;  
	}	
	
	
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitET_property(this, passAlongArgument);
	}
	
	
    public void code_generator() {
    	
		et_code = "ElementType " + varName +
		" = new ElementType( " + obj_Number + ", this, \"" +
		name + "\" );";
    	
    }

	public String getCode() {
		return et_code;
	}
	
	public void setCode( String et_cod) {
		et_code = et_cod;
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
		ET_property other = (ET_property) obj;
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
    stream.writeObject(et_code );    
		
}
	
	
	private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException
{

	    name = (String)stream.readObject( );
	    varName = (String)stream.readObject( );
	    obj_Number = (Integer)stream.readObject( );
	    et_code = (String)stream.readObject( ); 

		
}
	
	
	
	
	

}
