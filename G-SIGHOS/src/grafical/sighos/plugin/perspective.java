package grafical.sighos.plugin;



import grafical.sighos.plugin.view.inicial;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class perspective implements IPerspectiveFactory {
public void createInitialLayout(IPageLayout layout) {
String editorArea = layout.getEditorArea();
layout.setEditorAreaVisible(true);
layout.setFixed(true);
layout.addStandaloneView(inicial.ID, false, IPageLayout.LEFT, 1.0f,
editorArea);


}
}
