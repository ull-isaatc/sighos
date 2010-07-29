package grafical.sighos.plugin.editor.provider;



import grafical.sighos.plugin.editor.model.MiModelo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class MiProveedorContenido implements IStructuredContentProvider,
PropertyChangeListener {
private final Viewer viewer;
public MiProveedorContenido(Viewer viewer) {
this.viewer = viewer;
}
@Override
public Object[] getElements(Object inputElement) {
	MiModelo content = (MiModelo) inputElement;
	return content.getPersons().toArray();
	}
	@Override
	public void dispose() {
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
	viewer.refresh();
	}
	}

