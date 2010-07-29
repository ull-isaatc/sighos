package grafical.sighos.plugin.tree.model.handler;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class RT_property extends Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<RT_property> rt_List = new ArrayList<RT_property>();
	
	private String rt_code;	
	
	protected static int cursor = 0;
	
	public RT_property(String name, String varName, Integer obj_Number, String rt_cod) {
		super(name, varName, obj_Number);
		this.rt_code = rt_cod;  
	}				

	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitRT_property(this, passAlongArgument);
	}

	
    public void code_generator() {
    	
		rt_code = "ResourceType " + varName +
		" = new ResourceType( " + obj_Number + ", this, \"" +
		name + "\" );";
    	
    }
	
	
	
	public String getCode() {
		return rt_code;
	}
	
	public void setCode( String rt_cod) {
		rt_code = rt_cod;
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
		RT_property other = (RT_property) obj;
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
    stream.writeObject(rt_code );    
		
}
	
	
	private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException
{

	    name = (String)stream.readObject( );
	    varName = (String)stream.readObject( );
	    obj_Number = (Integer)stream.readObject( );
	    rt_code = (String)stream.readObject( ); 

		
}
	
	
	
	
	
}
