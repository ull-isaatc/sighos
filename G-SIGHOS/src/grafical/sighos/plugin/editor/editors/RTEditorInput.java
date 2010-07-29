package grafical.sighos.plugin.editor.editors;



import grafical.sighos.plugin.tree.model.handler.RT_property;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


public class RTEditorInput implements IEditorInput {
private final RT_property rt_obj;

public RTEditorInput(RT_property rt_ob) {
this.rt_obj = rt_ob;
}
public RT_property getRT_property() {
return rt_obj;
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
return rt_obj.toString();
}
@Override
public IPersistableElement getPersistable() {
return null;
}
@Override
public String getToolTipText() {
return rt_obj.toString();
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
if (obj instanceof RTEditorInput) {
return rt_obj.equals(((RTEditorInput) obj).getRT_property());
}
return false;
}
@Override
public int hashCode() {
return rt_obj.hashCode();
	}

}