package grafical.sighos.plugin.view;



import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.tree.model.MovingBoxContentProvider;
import grafical.sighos.plugin.tree.model.MovingBoxLabelProvider;
import grafical.sighos.plugin.tree.model.TreeViewerPlugin;

import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.G_property;
import grafical.sighos.plugin.tree.model.handler.Model;
import grafical.sighos.plugin.tree.model.handler.MovingBox;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;


/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class inicial extends ViewPart {
	public static TreeViewer treeViewer;
	protected Text text;
	protected MovingBoxLabelProvider labelProvider;

	
	public static final String ID = "grafical.sighos.plugin.view.inicial";
	
	
	
	protected Action onlyBoardGamesAction, atLeatThreeItems;
	protected Action booksBoxesGamesAction, noArticleAction;
	protected Action addBookAction, removeAction;
	protected ViewerFilter onlyBoardGamesFilter, atLeastThreeFilter;
	protected ViewerSorter booksBoxesGamesSorter, noArticleSorter;
	
	protected static MovingBox root;
	public static MovingBox rt_branch;
	public static MovingBox r_branch;
	public static MovingBox c_branch;
	public static MovingBox w_branch;
	public static MovingBox a_branch;
	public static MovingBox et_branch;
	public static MovingBox g_branch;
	
	private static ArrayList<String> non_delete;
	
	
	
	
	/**
	 * The constructor.
	 */
	public inicial() {
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 21;
		parent.setLayout(layout);
		

		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		treeViewer.setContentProvider(new MovingBoxContentProvider());
		labelProvider = new MovingBoxLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setUseHashlookup(true);
		
		// layout the tree viewer below the text field
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		// Create menu, toolbars, filters, sorters.
		
		createFiltersAndSorters();
		
		createActions();
		createMenus();
		createToolbar();
		hookListeners();
		
		
		//treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
		getSite().setSelectionProvider(treeViewer);
		hookDoubleClickCommand();
		
	}
	


	
	private void hookDoubleClickCommand() {
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite()
				.getService(IHandlerService.class);
				try {
					handlerService.executeCommand(
							"grafical.sighos.plugin.commands.LlamadaEditor", null);
				} catch (Exception ex) {
					throw new RuntimeException(
					"grafical.sighos.plugin.commands.LlamadaEditor not found")
					;
				}
				
			}
		});

	}
	
	protected void createFiltersAndSorters() {
	//	atLeastThreeFilter = new ThreeItemFilter();
	//	onlyBoardGamesFilter = new BoardgameFilter();
	//	booksBoxesGamesSorter = new BookBoxBoardSorter();
	//	noArticleSorter = new NoArticleSorter();
	}
	
	protected void hookListeners() {
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = (Model) iterator.next();
						String value = labelProvider.getText(domain);
						toShow.append(value);
						toShow.append(", ");
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(toShow.toString());
				}
			}
		});
		
	}
	
	protected void createActions() {
		onlyBoardGamesAction = new Action("Only Board Games") {
			public void run() {
				updateFilter(onlyBoardGamesAction);
			}
		};
		onlyBoardGamesAction.setChecked(false);
		
		atLeatThreeItems = new Action("Boxes With At Least Three Items") {
			public void run() {
				updateFilter(atLeatThreeItems);
			}
		};
		atLeatThreeItems.setChecked(false);
		
		booksBoxesGamesAction = new Action("Books, Boxes, Games") {
			public void run() {
				updateSorter(booksBoxesGamesAction);
			}
		};
		booksBoxesGamesAction.setChecked(false);
		
		noArticleAction = new Action("Ignoring Articles") {
			public void run() {
				updateSorter(noArticleAction);
			}
		};
		noArticleAction.setChecked(false);
		
//		addBookAction = new Action("Add Book") {
//			public void run() {
//				addNewBook();
//			}			
//		};
//addBookAction.setToolTipText("Add a New Book");
//addBookAction.setImageDescriptor(TreeViewerPlugin.getImageDescriptor("newBook.gif"));

		removeAction = new Action("Delete") {
			public void run() {
				removeSelected();
			}			
		};
		removeAction.setToolTipText("Delete");
		removeAction.setImageDescriptor(TreeViewerPlugin.getImageDescriptor("remove.gif"));		
	}
	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */

	
	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}


	protected void fillMenu(IMenuManager rootMenuManager) {
		IMenuManager filterSubmenu = new MenuManager("Filters");
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(onlyBoardGamesAction);
		filterSubmenu.add(atLeatThreeItems);
		
		IMenuManager sortSubmenu = new MenuManager("Sort By");
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(booksBoxesGamesAction);
		sortSubmenu.add(noArticleAction);
	}
	
	
	
	protected void updateSorter(Action action) {
		if(action == booksBoxesGamesAction) {
			noArticleAction.setChecked(!booksBoxesGamesAction.isChecked());
			if(action.isChecked()) {
				treeViewer.setSorter(booksBoxesGamesSorter);
			} else {
				treeViewer.setSorter(null);
			}
		} else if(action == noArticleAction) {
			booksBoxesGamesAction.setChecked(!noArticleAction.isChecked());
			if(action.isChecked()) {
				treeViewer.setSorter(noArticleSorter);
			} else {
				treeViewer.setSorter(null);
			}
		}
			
	}
	
	/* Multiple filters can be enabled at a time. */
	protected void updateFilter(Action action) {
		if(action == atLeatThreeItems) {
			if(action.isChecked()) {
				treeViewer.addFilter(atLeastThreeFilter);
			} else {
				treeViewer.removeFilter(atLeastThreeFilter);
			}
		} else if(action == onlyBoardGamesAction) {
			if(action.isChecked()) {
				treeViewer.addFilter(onlyBoardGamesFilter);
			} else {
				treeViewer.removeFilter(onlyBoardGamesFilter);
			}
		}
	}
	
	protected void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	//	toolbarManager.add(addBookAction);
		toolbarManager.add(removeAction);
	}

	
	/** Add a new book to the selected moving box.
	 * If a moving box is not selected, use the selected
	 * obect's moving box. 
	 * 
	 * If nothing is selected add to the root. */
	protected void addNewBook() {
		MovingBox receivingBox;
		if (treeViewer.getSelection().isEmpty()) {
			receivingBox = root;
		} else {
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
			Model selectedDomainObject = (Model) selection.getFirstElement();
			if (!(selectedDomainObject instanceof MovingBox)) {
				receivingBox = selectedDomainObject.getParent();
			} else {
				receivingBox = (MovingBox) selectedDomainObject;
			}
		}
		//receivingBox.add(ET_property.newBook());
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		/* Tell the tree to not redraw until we finish
		 * removing all the selected children. */
		treeViewer.getTree().setRedraw(false);
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			Model model = (Model) iterator.next();
			MovingBox parent = model.getParent();
			if ( !non_delete.contains(model.getName()) ) {
			   parent.remove(model);
			   if ( model instanceof RT_property ) {
				  RT_property borrar = RT_property.rt_List.get(model.getObj_Number());
				  borrar.setName("$$$$$trash$$$$$");
				   
			   }
			   else if ( model instanceof ET_property ) {
					  ET_property borrar = ET_property.et_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }
			   else if ( model instanceof W_property ) {
					  W_property borrar = W_property.w_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }
			   else if ( model instanceof R_property ) {
					  R_property borrar = R_property.r_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }
			   else if ( model instanceof C_property ) {
					  C_property borrar = C_property.c_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }
			   else if ( model instanceof A_property ) {
					  A_property borrar = A_property.a_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }	
			   else if ( model instanceof G_property ) {
					  G_property borrar = G_property.g_List.get(model.getObj_Number());
					  borrar.setName("$$$$$trash$$$$$");				   
			   }			   

			}
		}
		treeViewer.getTree().setRedraw(true);
	}
	
	
	public static MovingBox getInitalInput() {
		
		non_delete = new ArrayList<String>();
		non_delete.add("Resource Type");
		non_delete.add("Resource");
		non_delete.add("Cycles");
		non_delete.add("WorkGroup");
		non_delete.add("Activity");
		non_delete.add("Element Type");
		non_delete.add("Generators");		

		root = new MovingBox();
		rt_branch = new MovingBox("Resource Type");
		r_branch = new MovingBox("Resource");
		c_branch = new MovingBox("Cycles");
		w_branch = new MovingBox("WorkGroup");
		a_branch = new MovingBox("Activity");
		et_branch = new MovingBox("Element Type");
		g_branch = new MovingBox("Generators");		
		
		root.add(rt_branch);
		root.add(r_branch);
		root.add(c_branch);
		root.add(w_branch);
		root.add(a_branch);
		root.add(et_branch);
		root.add(g_branch);	
		
		//
		//rt_branch.add(new RT_property("Prueba 2", "Reiner", 2, "sd"));		
			
		return root;
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
		treeViewer.getControl().setFocus();
		
	}
	
	
	public static void refresh () {
		if ( Application.initialized_proyect == false)
			treeViewer.setInput(getInitalInput());
			
		treeViewer.refresh();
	}
	

}
