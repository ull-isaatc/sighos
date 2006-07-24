package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.Insets;

import es.ull.isaatc.simulation.editor.framework.actions.project.ValidateModelAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.*;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.CreateRootFlowAction;

public class ToolBarMenu extends SighosToolBar {
	
	private static final long serialVersionUID = 1L;

	private static ToolBarMenu INSTANCE = null;

	public static ToolBarMenu getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ToolBarMenu();
		}
		return INSTANCE;
	}
	
	public ToolBarMenu() {
		super("SighosDesigner ToolBar");
	}

	protected void initialize() {
		setMargin(new Insets(3, 2, 2, 0));
		
		add(new SighosToolBarButton(new CreateRootFlowAction()));
//		add(new SighosToolBarButton(new RemoveNetAction()));
		
		addSeparator();
		add(new SighosToolBarButton(new ValidateModelAction()));
		add(new SighosToolBarButton(new ValidateGraphModelAction()));
//		add(new SighosToolBarButton(new ExportToEngineFormatAction()));

		addSeparator();
		add(new SighosToolBarButton(DeleteAction.getInstance()));
		
		addSeparator();
		add(new SighosToolBarButton(AlignTopAction.getInstance()));
		add(new SighosToolBarButton(AlignMiddleAction.getInstance()));
		add(new SighosToolBarButton(AlignBottomAction.getInstance()));
		add(new SighosToolBarButton(AlignLeftAction.getInstance()));
		add(new SighosToolBarButton(AlignCentreAction.getInstance()));
		add(new SighosToolBarButton(AlignRightAction.getInstance()));
		
		addSeparator();
		add(new SighosToolBarButton(DecreaseSizeAction.getInstance()));
		add(new SighosToolBarButton(IncreaseSizeAction.getInstance()));
		addSeparator();
		add(new SighosToolBarButton(ZoomInAction.getInstance()));
		add(new SighosToolBarButton(ZoomOutAction.getInstance()));
		addSeparator();
		add(new SighosToolBarButton(ApplySugiyamaLayoutAction.getInstance()));
	}
}
