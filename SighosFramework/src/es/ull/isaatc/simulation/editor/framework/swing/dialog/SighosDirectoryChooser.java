/*
 *  XNap Commons
 *
 *  Copyright (C) 2005  Felix Berger
 *  Copyright (C) 2005  Steffen Pingel
 *  Copyright (C) 2006  Roberto Muñoz
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package es.ull.isaatc.simulation.editor.framework.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.xnap.commons.gui.action.AbstractXNapAction;
import org.xnap.commons.gui.dnd.DefaultTreeFileTransferHandler;
import org.xnap.commons.gui.tree.FileCellRenderer;
import org.xnap.commons.gui.tree.FileNode;
import org.xnap.commons.gui.tree.FileTreeModel;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * Provides a dialog with a {@link JTree} that contains the local
 * directory structure for directory selection and a button for quick selection
 * of the user's home directory.
 * 
 * <p>The usage of this class is similar to {@link javax.swing.JFileChooser}.
 * 
 * TODO add auto completed input field for entering directory
 */
public abstract class SighosDirectoryChooser extends SighosDialog implements
		TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public static final int APPROVE_OPTION = 1;

	public static final int CANCEL_OPTION = 2;

	private JTree directoryTree;

	private FileTreeModel directoryTreeModel;

//	private HomeAction homeAction;

	/**
	 * Creates a directory chooser.
	 */
	public SighosDirectoryChooser(Component parent) {
		super(parent);

		initialize();
	}

	protected void initialize() {
		this.setSize(new Dimension(400, 400));
		setModal(true);		
		// center
		directoryTreeModel = new FileTreeModel("Folders", File
				.listRoots());
		directoryTree = new JTree(directoryTreeModel);
		directoryTree.setRootVisible(false);
		directoryTree.setCellRenderer(new FileCellRenderer());
		directoryTree.putClientProperty("JTree.lineStyle", "Angled");
		directoryTree.addTreeSelectionListener(this);
		directoryTree.setTransferHandler(new DefaultTreeFileTransferHandler());
		directoryTree.setDragEnabled(true);

		// bottom
//		homeAction = new HomeAction();
//		getButtonPanel().add(Builder.createButton(homeAction), 0);
		super.initialize();
		setTitle(ResourceLoader.getMessage("open_long"));
	}

	/**
	 * Returns the currently selected directory.
	 *  
	 * @return the selected directory; null, if no directory is selected 
	 */
	public File getSelectedDirectory() {
		Object selectedItem = directoryTree.getLastSelectedPathComponent();
		return (selectedItem instanceof File) ? (File) selectedItem : null;
	}

	/**
	 * Selects the specified directory in the tree. The tree is expanded
	 * as necessary and scrolled to ensure visibility of the directory. 
	 * 
	 * @param dir the directory to be selected
	 */
	public void setSelectedDirectory(File dir) {
		LinkedList<FileNode> files = new LinkedList<FileNode>();
		File parent = dir;
		while (parent != null) {
			files.addFirst(new FileNode(parent));
			parent = parent.getParentFile();
		}
		Object[] path = new Object[files.size() + 1];
		path[0] = directoryTreeModel.getRoot();
		System.arraycopy(files.toArray(), 0, path, 1, path.length - 1);
		TreePath tp = new TreePath(path);
		directoryTree.setSelectionPath(tp);
		directoryTree.scrollPathToVisible(tp);
	}

	public abstract void valueChanged(TreeSelectionEvent e);

	public class HomeAction extends AbstractXNapAction {

		private static final long serialVersionUID = 1L;

		public HomeAction() {
			putValue(Action.NAME, "Home");
			putValue(Action.SHORT_DESCRIPTION, "Jump to home folder");
			putValue(ICON_FILENAME, "folder_home.png");
		}

		public void actionPerformed(ActionEvent event) {
			setSelectedDirectory(new File(System.getProperty("user.home")));
		}

	}

	@Override
	protected boolean apply() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected JPanel getMainPanel() {
		JScrollPane jspDirectories = new JScrollPane(directoryTree);
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL }, { TableLayout.FILL } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		panel.setLayout(panelLayout);
		panel.add(jspDirectories, "0, 0");
		return panel;
	}

}
