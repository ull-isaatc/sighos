package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.G_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class GEditorInput implements IEditorInput {
private final G_property g_obj;

public GEditorInput(G_property g_ob) {
this.g_obj = g_ob;
}
public G_property getG_property() {
return g_obj;
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
return g_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return g_obj.toString();
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
if (obj instanceof GEditorInput) {
return g_obj.equals(((GEditorInput) obj).getG_property());
}
return false;
}
@Override
public int hashCode() {
return g_obj.hashCode();
	}

}
