/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.kaizten.simulation.ports.qcsp.data.QCSPSolution;
import com.kaizten.simulation.ports.qcsp.data.QCSProblem;
import com.kaizten.simulation.ports.qcsp.population.Population;
import com.kaizten.simulation.ports.qcsp.solver.eda.EDA;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla
 *
 */
public class QCSP2StowagePlan {
	final private QCSProblem problem;
	final int safetyDistance;
	final int maxGenerations;
	final int populationSize;
	final String outputFileName;
	final boolean keepOriginalDuration;
	final boolean debug;
	
	public QCSP2StowagePlan(QCSProblem problem, int safetyDistance, int maxGenerations, int populationSize, String outputFileName, boolean keepOriginalDuration, boolean debug) {
		this.problem = problem;
		this.safetyDistance = safetyDistance;
		this.maxGenerations = maxGenerations;
		this.populationSize = populationSize;
		this.outputFileName = outputFileName;
		this.keepOriginalDuration = keepOriginalDuration;
		this.debug = debug;
	}
	
	public Population getSolutions() {
        // Configuring optimization technique
        final EDA solver = new EDA(problem, maxGenerations, populationSize, 20.0, 0.01);
        // Solving the instance
        solver.solve();
        // Showing results
        Population population = solver.getLargePopulation();
        if (debug) {
	        for (int i = 0; i < population.getSize(); i++) {
	            QCSPSolution solution = population.get(i);
	            System.out.println("Solution " + i + ":");
	            System.out.println(solution);
	        }
	        // Print best solution
            System.out.println("Best Solution:");
            System.out.println(population.get(0));
        }
        return population;
	}
	
	/**
	 * Returns a stowage plan based on a specific instance
	 * @param instance Path to the instance file
	 * @return A stowage plan based on the specified instance
	 */
	public StowagePlan getStowagePlanFromQCSP(QCSPSolution solution) {
        // Computing the number of bays
        final int bays = problem.getMaximumBay() + 1;
        // Creating vessel 
        final int quayCranes = problem.getQuayCranes();
        final Vessel vessel = new Vessel(bays, TimeUnit.SECOND);
        // Creating stowage plan
        final StowagePlan stowagePlan = new StowagePlan(vessel, quayCranes, safetyDistance, (long)solution.getObjectiveFunctionValue() / 3);
        
        if (keepOriginalDuration) {
            for (int task = 1; task <= problem.getNumRealTasks(); task++) {
                int bay = problem.getTaskBay(task);
                vessel.add(task - 1, bay, problem.getTaskProcessingTime(task));
            }
            for (int qc = 0; qc < quayCranes; qc++) {
                stowagePlan.setInitialPosition(qc, problem.getQuayCraneStartingPosition(qc));
                final ArrayList<Integer> tasks = new ArrayList<Integer>();
                for (int i = 1; i < solution.getTasksDoneByQC(qc).size() - 1; i++) {
                	tasks.add(solution.getTasksDoneByQC(qc).get(i) - 1);
                }
                stowagePlan.addAll(qc, tasks);
            }
        }
        else {
            HashMap<Integer, ArrayList<Integer>> tasksByOriginalTask = new HashMap<>();
            int containerId = 0;
            for (int task = 1; task <= problem.getNumRealTasks(); task++) {
                int bay = problem.getTaskBay(task);
                int time = problem.getTaskProcessingTime(task);
                for (int j = 0; j < time; j++) {
                    vessel.add(containerId, bay, PortModel.T_OPERATION);
                    ArrayList<Integer> tasks = tasksByOriginalTask.get(task);
                    if (tasks == null) {
                        tasks = new ArrayList<>();
                        tasksByOriginalTask.put(task, tasks);
                    }
                    tasks.add(containerId);
                    containerId++;
                }
            }
            for (int qc = 0; qc < quayCranes; qc++) {
                stowagePlan.setInitialPosition(qc, problem.getQuayCraneStartingPosition(qc));
                for (int i = 1; i < solution.getTasksDoneByQC(qc).size() - 1; i++) {
                	final int task = solution.getTasksDoneByQC(qc).get(i);
                    stowagePlan.addAll(qc, tasksByOriginalTask.get(task));
                }
            }        	
        }
		return stowagePlan;
	}
	
	@SuppressWarnings("unused")
	private void printAsArray(int[] craneDoTask, int[][] tasksDoneByCrane) {
        // Print for reuse        
        System.out.print("new int[] {" + craneDoTask[0]);
        for (int i = 1; i < problem.getNumRealTasks(); i++) {
        	System.out.print(", " + craneDoTask[i]);
        }
        System.out.println("};");

        System.out.print("new int[][] {");
        for (int qc = 0; qc < tasksDoneByCrane.length - 1; qc++) {
        	System.out.print("{" + tasksDoneByCrane[qc][0]);
            for (int i = 1; i < tasksDoneByCrane[qc].length; i++) {
            	System.out.print(", " + tasksDoneByCrane[qc][i]);
            }
            System.out.print("},");
        }
    	System.out.print("{" + tasksDoneByCrane[tasksDoneByCrane.length - 1][0]);
        for (int i = 1; i < tasksDoneByCrane[tasksDoneByCrane.length - 1].length; i++) {
        	System.out.print(", " + tasksDoneByCrane[tasksDoneByCrane.length - 1][i]);
        }
        System.out.println("}};");		
	}
	
	public void saveToFile(StowagePlan[] plans) {
		ObjectOutput output = null;
		try {
			output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(outputFileName)));
			output.writeObject(plans);
		}  
		catch(IOException ex) {
			System.err.println("Error creating file for Stowage plans");
			ex.printStackTrace();
		}
		finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static StowagePlan[] loadFromFile(String fileName) {
		ObjectInput input = null;
		StowagePlan[] plan = null;
		try {
			input = new ObjectInputStream (new BufferedInputStream(new FileInputStream(fileName)));
			plan = (StowagePlan[])input.readObject();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch(IOException ex){
			System.err.println("Could not open file " + fileName + " to load stowage plans");
			ex.printStackTrace();
		}
		finally{
			try {
				if (input != null) {
					input.close();
					return plan;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Wrong argument number");
			System.exit(-1);
		}
		else {
			final String inputFile = args[0];
			final String outputFile = args[1];
			final int maxGen = Integer.parseInt(args[2]);
			final int popSize = Integer.parseInt(args[3]);
			if (popSize > maxGen) {
				System.err.println("Wrong argument format: maxGeneration (" + maxGen + ") must be >= than populationSize (" + popSize + ")");
				System.exit(-1);
			}
			boolean keep = false; 
			boolean debug = false;
			if (args.length > 4) {
				if ((args[4].equalsIgnoreCase("--debug")) || (args[4].equalsIgnoreCase("-d"))){
					debug = true;
				}
				if ((args[4].equalsIgnoreCase("--keep")) || (args[4].equalsIgnoreCase("-k"))){
					keep = true;
				}
				if (args.length > 5) {				
					if ((args[5].equalsIgnoreCase("--debug")) || (args[5].equalsIgnoreCase("-d"))){
						debug = true;
					}
					if ((args[5].equalsIgnoreCase("--keep")) || (args[5].equalsIgnoreCase("-k"))){
						keep = true;
					}
				}
			}
			// FIXME: Be careful: null safety distance by now!!
			final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(new QCSProblem(inputFile), 0, maxGen, popSize, outputFile, keep, debug);
			final Population population = planBuilder.getSolutions();
			final StowagePlan[] plans = new StowagePlan[population.getSize()];
			for (int i = 0; i < plans.length; i++) {
				plans[i] = planBuilder.getStowagePlanFromQCSP(population.get(i));	
			}
			
			if (debug) {				
				System.out.println("Saved " + plans.length + " solutions");
				System.out.println("Vessel for best solution: ");
				System.out.println(plans[0].getVessel());
				System.out.println();
				System.out.println("Stowage plan for best solution:");
				System.out.println(plans[0]);
			}
			planBuilder.saveToFile(plans);
//			if (debug) {
//				StowagePlan[] loadedPlans = loadFromFile(outputFile);
//				System.out.println("Checked " + plans.length + " solutions");
//				System.out.println("Checking vessel for best solution: ");
//				System.out.println(loadedPlans[0].getVessel());
//				System.out.println();
//				System.out.println("Checcking Stowage plan for best solution:");
//				System.out.println(loadedPlans[0]);				
//			}
		}
	}

}
