package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.ET_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ETEditorInput implements IEditorInput {
	private final ET_property et_obj;

	public ETEditorInput(ET_property et_ob) {
	this.et_obj = et_ob;
	}
	public ET_property getET_property() {
	return et_obj;
	}
	@Override
	public boolean exists() {
	return false;
	}
	@Override
	public ImageDescriptor getImageDescriptor() {
	return null;
	}
	@Override
	public String getName() {
	return et_obj.toString();
	}
	@Override
	public IPersistableElement getPersistable() {
	return null;
	}
	@Override
	public String getToolTipText() {
	return et_obj.toString();
	}
	@Override
	public Object getAdapter(Class adapter) {
	return null;
	}
	@Override
	public boolean equals(Object obj) {
	if (super.equals(obj)) {
	return true;
	}
	if (obj instanceof ETEditorInput) {
	return et_obj.equals(((ETEditorInput) obj).getET_property());
	}
	return false;
	}
	@Override
	public int hashCode() {
	return et_obj.hashCode();
		}
}
