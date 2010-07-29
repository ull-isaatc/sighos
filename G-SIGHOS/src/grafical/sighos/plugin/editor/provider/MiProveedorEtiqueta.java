package grafical.sighos.plugin.editor.provider;

import grafical.sighos.plugin.editor.model.Person;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class MiProveedorEtiqueta implements ILabelProvider {
@Override
public Image getImage(Object element) {
return PlatformUI.getWorkbench().getSharedImages().getImage(
ISharedImages.IMG_OBJ_ELEMENT);
}
@Override
public String getText(Object element) {
Person person = (Person) element;
return (person.getLastName());
}
@Override
public void addListener(ILabelProviderListener listener) {
}
@Override
public void dispose() {
}
@Override
public boolean isLabelProperty(Object element, String property) {
return false;
}
@Override
public void removeListener(ILabelProviderListener listener) {
}
}