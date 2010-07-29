package grafical.sighos.plugin.tree.model;


import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.G_property;
import grafical.sighos.plugin.tree.model.handler.MovingBox;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;



public class MovingBoxLabelProvider implements ILabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;
		if (element instanceof MovingBox) {
			descriptor = TreeViewerPlugin.getImageDescriptor("movingBox.gif");
		} else if (element instanceof ET_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("ET.gif");
		} else if (element instanceof RT_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("RT.gif");
		} else if (element instanceof R_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("r.gif");
		} else if (element instanceof W_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("w.gif");
		} else if (element instanceof C_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("C.gif");
		} else if (element instanceof A_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("A.gif");
		} else if (element instanceof G_property) {
			descriptor = TreeViewerPlugin.getImageDescriptor("GG.gif");			
		} else {
			throw unknownElement(element);
		}

		//obtain the cached image corresponding to the descriptor
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof MovingBox) {
			if(((MovingBox)element).getName() == null) {
				return "Box";
			} else {
				return ((MovingBox)element).getName();
			}
		} else if (element instanceof ET_property) {
			return ((ET_property)element).getTitle();
		} else if (element instanceof RT_property) {
			return ((RT_property)element).getTitle();
		} else if (element instanceof R_property) {
			return ((R_property)element).getTitle();
		} else if (element instanceof W_property) {
			return ((W_property)element).getTitle();
		} else if (element instanceof C_property) {
			return ((C_property)element).getTitle();
		} else if (element instanceof A_property) {
			return ((A_property)element).getTitle();
		} else if (element instanceof G_property) {
			return ((G_property)element).getTitle();			
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

}
