package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.W_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class WEditorInput implements IEditorInput {
private final W_property w_obj;

public WEditorInput(W_property w_ob) {
this.w_obj = w_ob;
}
public W_property getW_property() {
return w_obj;
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
return w_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return w_obj.toString();
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
if (obj instanceof WEditorInput) {
return w_obj.equals(((WEditorInput) obj).getW_property());
}
return false;
}
@Override
public int hashCode() {
return w_obj.hashCode();
	}

}