package grafical.sighos.plugin.tree.model.handler;

import grafical.sighos.plugin.Application;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




public class A_property extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static ArrayList<A_property> a_List = new ArrayList<A_property>();

	private ArrayList<String> a_work = new ArrayList<String>();
	private ArrayList<String> a_func = new ArrayList<String>();	
	private ArrayList<Integer> a_var_n = new ArrayList<Integer>();	

	private ArrayList<String> a_code = new ArrayList<String>();	

	protected static int cursor = 0;

	public A_property(String name, String varName, int obj_Number, ArrayList<String> a_wor,ArrayList<String> a_fun, ArrayList<Integer> a_var_ ) {
		super(name, varName, obj_Number);
		this.a_work = a_wor; 
		this.a_func = a_fun; 		
		this.a_var_n = a_var_;		
		code_generator();

	}	


	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitA_property(this, passAlongArgument);
	}


	public void code_generator() {
		a_code.clear();

		a_code.add( "TimeDrivenActivity " + varName +" = new TimeDrivenActivity(" + obj_Number + ", this, \""+ name +"\" );");
		//	TimeDrivenActivity actOperacion = new TimeDrivenActivity(1, this, "Operacion" );
		int j = 0;
		for ( int i = 0; i < a_work.size(); i++) {

			String temp = varName + ".addWorkGroup(new SimulationTimeFunction(this,\""+a_func.get(i)+"\"";

			for (int f = 0;f< Application.functions.get(a_func.get(i));f++) {
				temp+=","+a_var_n.get(f+j);
			}
			j+=Application.functions.get(a_func.get(i));
			temp+="), "+a_work.get(i)+");";
			//actOperacion.addWorkGroup(new SimulationTimeFunction(this,"ConstantVariate",20),wgOpStandard );		

			a_code.add(temp);
		}
		//rDoctor.addTimeTableEntry(jornadaSemanalMedico, 420,rtMedico);



	}

	public ArrayList<String> getCode( ) {
		return a_code;
	}

	public String getCode( int pos) {
		return a_code.get(pos);
	}

	public void setCode( ArrayList<String> a_cod) {
		a_code = a_cod;
	}	

	public ArrayList<String > getA_work() {
		return a_work;
	}

	public void setA_work( ArrayList<String > a_wor) {
		a_work = a_wor;
	}	

	public ArrayList<String > getA_func() {
		return a_func;
	}

	public void setA_func( ArrayList<String > a_fun) {
		a_func = a_fun;
	}	

	public ArrayList<Integer > getA_var_n() {
		return a_var_n;
	}

	public void setA_var_n( ArrayList<Integer > a_var_) {
		a_var_n = a_var_;
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
		A_property other = (A_property) obj;
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
		stream.writeObject(a_code );   
		stream.writeObject(a_work);
		stream.writeObject(a_func);    
		stream.writeObject(a_var_n);    

	}


	private void readObject(java.io.ObjectInputStream stream)
	throws IOException, ClassNotFoundException
	{

		name = (String)stream.readObject( );
		varName = (String)stream.readObject( );
		obj_Number = (Integer)stream.readObject( );
		a_code = (ArrayList<String>)stream.readObject( ); 
		a_work = (ArrayList<String>) stream.readObject();
		a_func = (ArrayList<String>) stream.readObject();	    
		a_var_n = (ArrayList<Integer>) stream.readObject();	    

	}

}

	