package grafical.sighos.plugin.tree.model;

import grafical.sighos.plugin.tree.model.handler.DeltaEvent;
import grafical.sighos.plugin.tree.model.handler.IDeltaListener;
import grafical.sighos.plugin.tree.model.handler.Model;
import grafical.sighos.plugin.tree.model.handler.MovingBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;



public class MovingBoxContentProvider implements ITreeContentProvider, IDeltaListener, 
	PropertyChangeListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((MovingBox)oldInput);
		}
		if(newInput != null) {
			addListenerTo((MovingBox)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(MovingBox box) {
		box.removeListener(this);
		for (Iterator iterator = box.getBoxes().iterator(); iterator.hasNext();) {
			MovingBox aBox = (MovingBox) iterator.next();
			removeListenerFrom(aBox);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(MovingBox box) {
		box.addListener(this);
		for (Iterator iterator = box.getBoxes().iterator(); iterator.hasNext();) {
			MovingBox aBox = (MovingBox) iterator.next();
			addListenerTo(aBox);
		}
	}
	
	
	

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof MovingBox) {
			MovingBox box = (MovingBox)parentElement;
			return concat(box.getBoxes().toArray(), 
				box.getET_prop().toArray(), box.getRT_prop().toArray(), box.getR_prop().toArray(),
				box.getW_prop().toArray(), box.getC_prop().toArray(), box.getA_prop().toArray(),
				box.getG_prop().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] object, Object[] more, Object[] more2, Object[] more3, Object[] more4, Object[] more5, Object[] more6, Object[] more7) {
		Object[] both = new Object[object.length + more.length + more2.length + more3.length + more4.length + more5.length + more6.length + more7.length];
		System.arraycopy(object, 0, both, 0, object.length);
		System.arraycopy(more, 0, both, object.length, more.length);
		System.arraycopy(more2, 0, both, object.length + more.length, more2.length);
		System.arraycopy(more3, 0, both, object.length + more.length + more2.length, more3.length);	
		System.arraycopy(more4, 0, both, object.length + more.length+ more2.length + more3.length, more4.length);	
		System.arraycopy(more5, 0, both, object.length + more.length+ more2.length + more3.length + more4.length, more5.length);	
		System.arraycopy(more6, 0, both, object.length + more.length+ more2.length + more3.length + more4.length + more5.length, more6.length);	
		System.arraycopy(more7, 0, both, object.length + more.length+ more2.length + more3.length + more4.length + more5.length + more6.length, more7.length);			
		return both;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof Model) {
			return ((Model)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object movingBox = ((Model)event.receiver()).getParent();
		viewer.refresh(movingBox, false);
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
		
	}

}
