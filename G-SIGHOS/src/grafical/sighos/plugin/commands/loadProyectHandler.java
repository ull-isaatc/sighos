package grafical.sighos.plugin.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.Model;
import grafical.sighos.plugin.tree.model.handler.MovingBox;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;
import grafical.sighos.plugin.view.comando;
import grafical.sighos.plugin.view.inicial;

import java.io.*;
import java.util.ArrayList;

public class loadProyectHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell1 = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		boolean loading = true;
		if ( Application.initialized_proyect == true){
			loading = false;
			if ( MessageDialog.openConfirm(shell1, "Confirm", "The unsaved proyect must be lost") )
				loading = true;
		}


		if ( loading == true) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			// File standard dialog

			FileDialog fileDialog = new FileDialog( shell );
			// Set the text
			fileDialog.setText("Select File");
			// Set filter on .txt files
			fileDialog.setFilterExtensions(new String[] { "*.gdata" });
			// Put in a readable name for the filter
			fileDialog.setFilterNames(new String[] { "GSighosFile(*.gdata)" });
			// Open Dialog and save result of selection

			String selected = fileDialog.open();

			System.out.println(selected);



			FileInputStream fis = null;
			ObjectInputStream in = null;
			
			inicial.treeViewer.setInput(inicial.getInitalInput());
			Application.initialized_proyect = true;


			try {
				// Apertura del fichero y creacion de BufferedReader para poder
				// hacer una lectura comoda (disponer del metodo readLine()).

				fis = new FileInputStream(selected);
				in = new ObjectInputStream(fis);


				// Lectura del fichero
				Application.name_proyect = (String)in.readObject();
				Application.author_proyect = (String)in.readObject();
				Application.descripcion_proyect  = (String)in.readObject();
				Application.time_proyect  = (String)in.readObject();
				Application.loop_exp  = (Integer)in.readObject();				
				//Tipos de recursos
				RT_property.rt_List  = new ArrayList<RT_property>();
				ET_property.et_List  = new ArrayList<ET_property>();
				C_property.c_List  = new ArrayList<C_property>();
				R_property.r_List  = new ArrayList<R_property>();
				W_property.w_List  = new ArrayList<W_property>();
				A_property.a_List  = new ArrayList<A_property>();
				//lectura de fichero
				RT_property.rt_List  = (ArrayList<RT_property>)in.readObject();
				ET_property.et_List  = (ArrayList<ET_property>)in.readObject();
				C_property.c_List  = (ArrayList<C_property>)in.readObject();
				R_property.r_List  = (ArrayList<R_property>)in.readObject();
				W_property.w_List  = (ArrayList<W_property>)in.readObject();
				A_property.a_List  = (ArrayList<A_property>)in.readObject();				
										


			}
			catch(Exception e){
				e.printStackTrace();
			}finally{
				// En el finally cerramos el fichero, para asegurarnos
				// que se cierra tanto si todo va bien como si salta 
				// una excepcion.
				try{                    
					if( null != fis ){   
						fis.close();     
					}                  
				}catch (Exception e2){ 
					e2.printStackTrace();
				}

				//Para cerrar el editor			
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();			
				page.closeAllEditors(false);	

				//Para reiniciar todo			


				//Para los Resource Type			
				for ( int j = 0; j < RT_property.rt_List.size(); j++) {


					RT_property objectt = new RT_property( RT_property.rt_List.get(j).getName(),
							RT_property.rt_List.get(j).getVarName(),
							RT_property.rt_List.get(j).getObj_Number(),
							RT_property.rt_List.get(j).getCode());


					if ( !RT_property.rt_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.rt_branch.add(objectt );		      
					}
				}
				//Para los Element type		
				for ( int j = 0; j < ET_property.et_List.size(); j++) {

					System.out.println(ET_property.et_List.get(j).getName());
					System.out.println(ET_property.et_List.get(j).getClass());				

					ET_property objectt = new ET_property( ET_property.et_List.get(j).getName(),
							ET_property.et_List.get(j).getVarName(),
							ET_property.et_List.get(j).getObj_Number(),
							ET_property.et_List.get(j).getCode());


					if ( !ET_property.et_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.et_branch.add(objectt );		      
					}
				}
				//Para los Cycle	
				for ( int j = 0; j < C_property.c_List.size(); j++) {

					System.out.println(C_property.c_List.get(j).getName());
					System.out.println(C_property.c_List.get(j).getClass());				

					C_property objectt = new C_property( C_property.c_List.get(j).getName(),
							C_property.c_List.get(j).getVarName(),
							C_property.c_List.get(j).getObj_Number(),
							C_property.c_List.get(j).getTipo(),
							C_property.c_List.get(j).getCycle(),							
							C_property.c_List.get(j).getVals());

					if ( !C_property.c_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.c_branch.add(objectt );		      
					}
				}	
				//Para los Resource	
				for ( int j = 0; j < R_property.r_List.size(); j++) {

					System.out.println(R_property.r_List.get(j).getName());
					System.out.println(R_property.r_List.get(j).getClass());				

					R_property objectt = new R_property( R_property.r_List.get(j).getName(),
							R_property.r_List.get(j).getVarName(),
							R_property.r_List.get(j).getObj_Number(),
							R_property.r_List.get(j).getR_resource(),
							R_property.r_List.get(j).getR_resource2(),
							R_property.r_List.get(j).getR_resource_n()							
							);


					if ( !R_property.r_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.r_branch.add(objectt );		      
					}
				}	
				//Para los Workgroup	
				for ( int j = 0; j < W_property.w_List.size(); j++) {

					System.out.println(W_property.w_List.get(j).getName());
					System.out.println(W_property.w_List.get(j).getClass());				

					W_property objectt = new W_property( W_property.w_List.get(j).getName(),
							W_property.w_List.get(j).getVarName(),
							W_property.w_List.get(j).getObj_Number(),
							W_property.w_List.get(j).getW_resource(),
							W_property.w_List.get(j).getW_resource_n());


					if ( !W_property.w_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.w_branch.add(objectt );		      
					}
				}	
				//Para los Activity	
				for ( int j = 0; j < A_property.a_List.size(); j++) {

					System.out.println(A_property.a_List.get(j).getName());
					System.out.println(A_property.a_List.get(j).getClass());				

					A_property objectt = new A_property( A_property.a_List.get(j).getName(),
							A_property.a_List.get(j).getVarName(),
							A_property.a_List.get(j).getObj_Number(),
							A_property.a_List.get(j).getA_work(),
							A_property.a_List.get(j).getA_func(),
							A_property.a_List.get(j).getA_var_n());


					if ( !A_property.a_List.get(j).getName().equals("$$$$$trash$$$$$")  ) {
						inicial.a_branch.add(objectt );		      
					}
				}					



				Application.initialized_proyect = true;
				inicial.refresh();
				comando.refresh();



			}
		}

		return null;

	}

}