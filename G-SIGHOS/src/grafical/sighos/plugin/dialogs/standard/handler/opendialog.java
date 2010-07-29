package grafical.sighos.plugin.dialogs.standard.handler;

import javax.swing.Box;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;


public class opendialog extends AbstractHandler {
@Override
public Object execute(ExecutionEvent event) throws ExecutionException {
Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

// Directly standard selection
DirectoryDialog dirDialog = new DirectoryDialog(shell);
dirDialog.setText("Select your home directory");
String selectedDir = dirDialog.open();
System.out.println(selectedDir);

// Now a few messages
MessageDialog.openConfirm(shell, "Confirm", "Please confirm");
MessageDialog.openError(shell, "Error", "Error occured");
MessageDialog.openInformation(shell, "Info", "Info for you");
MessageDialog.openQuestion(shell, "Question", "Really, really?");
MessageDialog.openWarning(shell, "Warning", "I warn you");




return null;
}
}