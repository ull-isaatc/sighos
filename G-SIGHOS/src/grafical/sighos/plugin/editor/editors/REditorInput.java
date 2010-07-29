package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.R_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class REditorInput implements IEditorInput {
private final R_property r_obj;

public REditorInput(R_property r_ob) {
this.r_obj = r_ob;
}
public R_property getR_property() {
return r_obj;
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
return r_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return r_obj.toString();
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
if (obj instanceof REditorInput) {
return r_obj.equals(((REditorInput) obj).getR_property());
}
return false;
}
@Override
public int hashCode() {
return r_obj.hashCode();
	}

}
