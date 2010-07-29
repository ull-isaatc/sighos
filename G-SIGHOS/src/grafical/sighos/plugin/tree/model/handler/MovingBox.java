package grafical.sighos.plugin.tree.model.handler;

import java.util.ArrayList;
import java.util.List;

public class MovingBox extends Model {
	protected List boxes;
	protected List rt_props;
	protected List et_props;
	protected List w_props;
	protected List r_props;
	protected List c_props;
	protected List a_props;
	protected List g_props;	
	
	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();
	
	public MovingBox() {
		boxes = new ArrayList<MovingBox>();
		rt_props = new ArrayList<RT_property>();
		et_props = new ArrayList<ET_property>();
		w_props = new ArrayList<W_property>();
		r_props = new ArrayList<R_property>();
		c_props = new ArrayList<C_property>();
		a_props = new ArrayList<A_property>();
		g_props = new ArrayList<G_property>();		
	}
	
	private static class Adder implements IModelVisitor {


		public void visitET_property(ET_property e_prop, Object argument) {
			((MovingBox) argument).addET_property(e_prop);
		}


		public void visitMovingBox(MovingBox box, Object argument) {
			((MovingBox) argument).addBox(box);
		}

		@Override
		public void visitRT_property(RT_property rt_p, Object argument) {
			((MovingBox) argument).addRT_property(rt_p);
			
		}
		
		public void visitW_property(W_property w_prop, Object argument) {
			((MovingBox) argument).addW_property(w_prop);
		}
		
		public void visitR_property(R_property r_prop, Object argument) {
			((MovingBox) argument).addR_property(r_prop);
		}
		
		public void visitC_property(C_property c_prop, Object argument) {
			((MovingBox) argument).addC_property(c_prop);
		}

		public void visitA_property(A_property a_prop, Object argument) {
			((MovingBox) argument).addA_property(a_prop);
		}

		public void visitG_property(G_property g_prop, Object argument) {
			((MovingBox) argument).addG_property(g_prop);
		}
		
		

	}

	private static class Remover implements IModelVisitor {
		public void visitRT_property(RT_property boardgame, Object argument) {
			((MovingBox) argument).removeRT_property(boardgame);
		}


		public void visitET_property(ET_property et_prop, Object argument) {
			((MovingBox) argument).removeET_property(et_prop);
		}


		public void visitMovingBox(MovingBox box, Object argument) {
			((MovingBox) argument).removeBox(box);
			box.addListener(NullDeltaListener.getSoleInstance());
		}
		
		public void visitW_property(W_property w_prop, Object argument) {
			((MovingBox) argument).removeW_property(w_prop);
		}
		public void visitR_property(R_property r_prop, Object argument) {
			((MovingBox) argument).removeR_property(r_prop);
		}
		public void visitC_property(C_property c_prop, Object argument) {
			((MovingBox) argument).removeC_property(c_prop);
		}
		public void visitA_property(A_property a_prop, Object argument) {
			((MovingBox) argument).removeA_property(a_prop);
		}
		public void visitG_property(G_property g_prop, Object argument) {
			((MovingBox) argument).removeG_property(g_prop);
		}

	}
	
	public MovingBox(String name) {
		this();
		this.name = name;
	}
	
	public List getBoxes() {
		return boxes;
	}
	
	protected void addBox(MovingBox box) {
		boxes.add(box);
		box.parent = this;
		fireAdd(box);
	}
	
	protected void addET_property(ET_property et_prop) {
		et_props.add(et_prop);
		et_prop.parent = this;
		fireAdd(et_prop);
	}
	
	protected void addRT_property(RT_property rt_prop) {
		rt_props.add(rt_prop);
		rt_prop.parent = this;
		fireAdd(rt_prop);
	}	
	
	protected void addW_property(W_property w_prop) {
		w_props.add(w_prop);
		w_prop.parent = this;
		fireAdd(w_prop);
	}	
	
	protected void addR_property(R_property r_prop) {
		r_props.add(r_prop);
		r_prop.parent = this;
		fireAdd(r_prop);
	}	
	
	protected void addC_property(C_property c_prop) {
		c_props.add(c_prop);
		c_prop.parent = this;
		fireAdd(c_prop);
	}	
	
	protected void addA_property(A_property a_prop) {
		a_props.add(a_prop);
		a_prop.parent = this;
		fireAdd(a_prop);
	}	
	
	protected void addG_property(G_property g_prop) {
		g_props.add(g_prop);
		g_prop.parent = this;
		fireAdd(g_prop);
	}		
	

	
	public void remove(Model toRemove) {
		toRemove.accept(remover, this);
	}
	
	protected void removeRT_property(RT_property rt_prop) {
		rt_props.remove(rt_prop);
		rt_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(rt_prop);
	}
	
	protected void removeET_property(ET_property et_prop) {
		et_props.remove(et_prop);
		et_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(et_prop);
	}
	
	protected void removeW_property(W_property w_prop) {
		w_props.remove(w_prop);
		w_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(w_prop);
	}	
	
	protected void removeR_property(R_property r_prop) {
		r_props.remove(r_prop);
		r_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(r_prop);
	}
	
	protected void removeC_property(C_property c_prop) {
		c_props.remove(c_prop);
		c_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(c_prop);
	}
	
	protected void removeA_property(A_property a_prop) {
		a_props.remove(a_prop);
		a_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(a_prop);
	}
	
	protected void removeG_property(G_property g_prop) {
		g_props.remove(g_prop);
		g_prop.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(g_prop);
	}	
	

	
	protected void removeBox(MovingBox box) {
		boxes.remove(box);
		box.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(box);	
	}

	public void add(Model toAdd) {
		toAdd.accept(adder, this);
	}
	
	public List getRT_prop() {
		return rt_props;
	}
	
	public List getET_prop() {
		return et_props;
	}
	
	public List getW_prop() {
		return w_props;
	}
	
	public List getR_prop() {
		return r_props;
	}
	
	public List getC_prop() {
		return c_props;
	}
	
	public List getA_prop() {
		return a_props;
	}
	
	public List getG_prop() {
		return g_props;
	}	
	
	/** Answer the total number of items the
	 * receiver contains. */
	public int size() {
		return getET_prop().size() + getBoxes().size() + getRT_prop().size();
	}
	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitMovingBox(this, passAlongArgument);
	}



}

