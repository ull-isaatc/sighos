package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.C_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class CEditorInput implements IEditorInput {
private final C_property c_obj;

public CEditorInput(C_property c_ob) {
this.c_obj = c_ob;
}
public C_property getC_property() {
return c_obj;
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
return c_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return c_obj.toString();
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
if (obj instanceof CEditorInput) {
return c_obj.equals(((CEditorInput) obj).getC_property());
}
return false;
}
@Override
public int hashCode() {
return c_obj.hashCode();
	}

}
