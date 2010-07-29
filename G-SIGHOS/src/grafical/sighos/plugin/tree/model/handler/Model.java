package grafical.sighos.plugin.tree.model.handler;



public abstract class Model {
	protected MovingBox parent;
	protected String name;
	protected String varName;	
	protected Integer obj_Number;
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	
	public MovingBox getParent() {
		return parent;
	}
	
	/* The receiver should visit the toVisit object and
	 * pass along the argument. */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);
	
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public Model(String nam, String varNam, Integer obj_N) {
		this.name = nam;
		this.varName = varNam;
		this.obj_Number = obj_N;
	}
	
	public Model() {
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}

	public String getName() {
		return name;
	}

	public void setName( String new_Name ) {
		this.name = new_Name;
	}

	public String getVarName() {
		return varName;
	}

	public String getTitle() {
		return name;
	}
	
	public Integer getObj_Number() {
		return obj_Number;
	}



}