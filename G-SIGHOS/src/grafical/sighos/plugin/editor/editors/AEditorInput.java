package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.A_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class AEditorInput implements IEditorInput {
private final A_property a_obj;

public AEditorInput(A_property a_ob) {
this.a_obj = a_ob;
}
public A_property getA_property() {
return a_obj;
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
return a_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return a_obj.toString();
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
if (obj instanceof AEditorInput) {
return a_obj.equals(((AEditorInput) obj).getA_property());
}
return false;
}
@Override
public int hashCode() {
return a_obj.hashCode();
	}

}