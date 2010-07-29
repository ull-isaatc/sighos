package grafical.sighos.plugin.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.swt.SWT;

import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;

import java.io.*;

public class guardarficherohandler extends AbstractHandler {
@Override
public Object execute(ExecutionEvent event) throws ExecutionException {
Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
// File standard dialog

FileDialog fileDialog = new FileDialog( shell, SWT.SAVE );
// Set the text
fileDialog.setText("Select File");
// Set filter on .txt files
fileDialog.setFilterExtensions(new String[] { "*.java" });
// Put in a readable name for the filter
fileDialog.setFilterNames(new String[] { "Javafiles(*.java)" });
// Open Dialog and save result of selection


String selected = fileDialog.open();

System.out.println(selected);
		
	

		

		FileWriter fichero = null;
		PrintWriter pw = null;
		try
		{
			fichero = new FileWriter(selected);
			pw = new PrintWriter(fichero);

			selected=selected.replace("\\", "/");
            String division2[] = selected.split("/");
            String division[] = division2[division2.length-1].split(".java");               
                
            
            pw.println("import java.util.EnumSet;");
			pw.println("import es.ull.isaatc.function.TimeFunction;");
			pw.println("import es.ull.isaatc.function.TimeFunctionFactory;");
			pw.println("import es.ull.isaatc.simulation.ElementCreator;");
			pw.println("import es.ull.isaatc.simulation.ElementType;");
			pw.println("import es.ull.isaatc.simulation.PooledExperiment;");
			pw.println("import es.ull.isaatc.simulation.Resource;");
			pw.println("import es.ull.isaatc.simulation.ResourceType;");
			pw.println("import es.ull.isaatc.simulation.SimulationPeriodicCycle;");
			pw.println("import es.ull.isaatc.simulation.SimulationTime;");
			pw.println("import es.ull.isaatc.simulation.SimulationTimeFunction;");
			pw.println("import es.ull.isaatc.simulation.SimulationTimeUnit;");
			pw.println("import es.ull.isaatc.simulation.Simulation;");
			pw.println("import es.ull.isaatc.simulation.TimeDrivenActivity;");
			pw.println("import es.ull.isaatc.simulation.TimeDrivenGenerator;");
			pw.println("import es.ull.isaatc.simulation.WorkGroup;");
			pw.println("import es.ull.isaatc.simulation.condition.PercentageCondition;");
			pw.println("import es.ull.isaatc.simulation.flow.ExclusiveChoiceFlow;");
			pw.println("import es.ull.isaatc.simulation.flow.ForLoopFlow;");
			pw.println("import es.ull.isaatc.simulation.flow.SingleFlow;");
			pw.println("import es.ull.isaatc.simulation.inforeceiver.StdInfoView;");
			pw.println("");
			pw.println("");
			pw.println("class Simulation_Model extends Simulation {");
			pw.println("int ndias;");
			pw.println("");
			pw.println("	public Simulation_Model (int id, int ndias) {");
			pw.println("		super( id,");
			pw.println("				\""+Application.name_proyect+"\","); 
			pw.println("				SimulationTimeUnit."+Application.time_proyect+"," );
			pw.println("				SimulationTime.getZero(),");
			pw.println("				new SimulationTime(SimulationTimeUnit.DAY, ndias));");
			pw.println("		this.ndias = ndias;");
			pw.println("	}");
			pw.println("");
			pw.println("	protected void createModel() {");
			pw.println("//Resource Type");
            for ( int i = 0; i < RT_property.rt_List.size();i++){
            	if ( !RT_property.rt_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		pw.println("		"+RT_property.rt_List.get(i).getCode());
            	}			
            }
			pw.println("");
			pw.println("");			
			pw.println("//Element Type");
            for ( int i = 0; i < ET_property.et_List.size();i++){
            	if ( !ET_property.et_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		pw.println("		"+ET_property.et_List.get(i).getCode());
            	}			
            }			
			pw.println("");		
			pw.println("");			
			pw.println("//Cycle");
            for ( int i = 0; i < C_property.c_List.size();i++){
            	if ( !C_property.c_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		pw.println("		"+C_property.c_List.get(i).getCode());
            	}			
            }
			pw.println("");
			pw.println("");			
			pw.println("//Resource");
            for ( int i = 0; i < R_property.r_List.size();i++){
            	if ( !R_property.r_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		for ( int j = 0; j < R_property.r_List.get(i).getCode().size();j++ )
            		pw.println("		"+R_property.r_List.get(i).getCode(j));
            	}			
            }	
			pw.println("");	
			pw.println("");			
			pw.println("//Workgroup");
            for ( int i = 0; i < W_property.w_List.size();i++){
            	if ( !W_property.w_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		for ( int j = 0; j < W_property.w_List.get(i).getCode().size();j++ )
            		pw.println("		"+W_property.w_List.get(i).getCode(j));
            	}			
            }		
			pw.println("");            
			pw.println("");			
			pw.println("//Activity");			
            for ( int i = 0; i < A_property.a_List.size();i++){
            	if ( !A_property.a_List.get(i).equals("$$$$$trash$$$$$")){
        			pw.println("");            		
            		for ( int j = 0; j < A_property.a_List.get(i).getCode().size();j++ )
            		pw.println("		"+A_property.a_List.get(i).getCode(j));
            	}			
            }			
		
			pw.println("        }");
			pw.println("");			



			pw.println("}");

			pw.println("class GSighos_Export extends PooledExperiment {");
			pw.println("	static final int NEXP = 1;");
			pw.println("	static final int NDIA = "+Application.loop_exp+";");
			pw.println("");
			pw.println("	public GSighos_Export() {");
			pw.println("		super(\""+Application.name_proyect+"\", NEXP);");
			pw.println("	}");
			pw.println("");
			pw.println("	public Simulation_Model getSimulation(int ind) {");
			pw.println("		Simulation_Model sim = null;");
			pw.println("		sim = new "+division[0]+"(ind, NDIA);	");
			pw.println("		StdInfoView debugVista = new StdInfoView(sim);");
			pw.println("		sim.addInfoReceiver(debugVista);");
			pw.println("		return sim;");
			pw.println("	}");		
			pw.println("}");
			pw.println("");			
			pw.println("public class "+division[0]+" {");
			pw.println("");			
			pw.println("	public static void main(String[] args) {");
			pw.println("		new GSighos_Export().start();");
			pw.println("	}");
			pw.println("}");			
				
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para 
				// asegurarnos que se cierra el fichero.
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}



		return null;
	}
}

