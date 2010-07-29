package grafical.sighos.plugin.tree.model.handler;


public interface IModelVisitor {
	public void visitMovingBox(MovingBox box, Object passAlongArgument);
	public void visitET_property(ET_property rt_p, Object passAlongArgument);
	public void visitRT_property(RT_property rt_p, Object passAlongArgument);
	public void visitW_property(W_property e_p, Object passAlongArgument);
	public void visitR_property(R_property e_p, Object passAlongArgument);
	public void visitC_property(C_property e_p, Object passAlongArgument);
	public void visitA_property(A_property e_p, Object passAlongArgument);
	public void visitG_property(G_property e_p, Object passAlongArgument);

	
	
}
