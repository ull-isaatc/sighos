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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.kaizten.simulation.ports.qcsp.data.QCSPSolution;
import com.kaizten.simulation.ports.qcsp.data.QCSProblem;
import com.kaizten.simulation.ports.qcsp.evaluator.EvaluatorMeisel;
import com.kaizten.simulation.ports.qcsp.population.Population;
import com.kaizten.simulation.ports.qcsp.solver.eda.EDA;

/**
 * @author Iván Castilla
 *
 */
public class QCSP2StowagePlan {
	final private static int SAFETY_DISTANCE = 1;
	final private QCSProblem problem;
	final int safetyDistance;
	final double overlapLambda;
	final boolean keepOriginalDuration;
	final boolean debug;
	
	public QCSP2StowagePlan(QCSProblem problem, int safetyDistance, double overlapLambda, boolean keepOriginalDuration, boolean debug) {
		this.problem = problem;
		this.safetyDistance = safetyDistance;
		this.overlapLambda = overlapLambda;
		this.keepOriginalDuration = keepOriginalDuration;
		this.debug = debug;
	}
	
	public Population getSolutions(int maxGenerations, int populationSize) {
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
        final Vessel vessel = new Vessel(bays);
        // Creating stowage plan
        StowagePlan stowagePlan = null;
        final int planningHorizon = (int)(solution.getObjectiveFunctionValue() * 2 / 3);
        final int[][]dependenciesAtStart = new int[quayCranes][2];
        
        if (keepOriginalDuration) {
            for (int task = 1; task <= problem.getNumRealTasks(); task++) {
                int bay = problem.getTaskBay(task);
                vessel.add(task - 1, bay, solution.times[task] - problem.getTaskProcessingTime(task) + 1, problem.getTaskProcessingTime(task));
            }
            for (int qc = 0; qc < quayCranes; qc++) {
            	dependenciesAtStart[qc][0] = solution.getFicticiousTasks()[qc][0];
            	final int originalTask = solution.getFicticiousTasks()[qc][2];
            	dependenciesAtStart[qc][1] = (originalTask == -1) ? -1 : originalTask - 1;
            }
            stowagePlan = new StowagePlan(vessel, quayCranes, safetyDistance, (long)solution.getObjectiveFunctionValue() / 3, solution.getOverlap(overlapLambda), 
            		problem.getLowerBoundOverlapping(planningHorizon, overlapLambda), problem.getUpperBoundOverlapping(safetyDistance, overlapLambda), 
            		solution.leftToRight, dependenciesAtStart);
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
                final int bay = problem.getTaskBay(task);
                final int time = problem.getTaskProcessingTime(task);
                int startTime = solution.times[task] - problem.getTaskProcessingTime(task) + 1;
                for (int j = 0; j < time; j++) {
                    vessel.add(containerId, bay, startTime++, 1);
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
            	dependenciesAtStart[qc][0] = solution.getFicticiousTasks()[qc][0];
            	final int originalTask = solution.getFicticiousTasks()[qc][2];
            	// Takes the last "new" task associated to the original task
            	dependenciesAtStart[qc][1] = (originalTask == -1) ? -1 : tasksByOriginalTask.get(originalTask).get(tasksByOriginalTask.get(originalTask).size() - 1);
            }
            stowagePlan = new StowagePlan(vessel, quayCranes, safetyDistance, (long)solution.getObjectiveFunctionValue() / 3, solution.getOverlap(overlapLambda), 
            		problem.getLowerBoundOverlapping(planningHorizon, overlapLambda), problem.getUpperBoundOverlapping(safetyDistance, overlapLambda), 
            		solution.leftToRight, dependenciesAtStart);
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
	
	public void saveToFile(StowagePlan[] plans, final String outputFileName) {
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

	public static void testDependenciesAtStart() {
        final String instance = "C:\\Users\\Iván Castilla\\Dropbox\\SimulationPorts\\instances\\k45.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 4, 5, 6, 7, 9, 12}, 0);
        solution.assignTaskToQuayCrane(new int[]{11, 13, 15, 17, 18, 19}, 1);
        solution.assignTaskToQuayCrane(new int[]{8, 10, 14, 16, 20, 21, 22, 23, 24, 25}, 2);
        //
        solution.evaluator = new EvaluatorMeisel();
        solution.getObjectiveFunctionValue();
        System.out.println(solution);
        /**
         *
         */
        final int planningHorizon = 1000;
        final int securityDistance = 1;
        final double lambda = 1.0;
        System.out.println("Bays:              " + problem.getBays());
        System.out.println("Security Distance: " + securityDistance);
        System.out.println("Overlap:           " + solution.getOverlap(lambda));
        System.out.println("Lower Bound:       " + problem.getLowerBoundOverlapping(planningHorizon, lambda));
        System.out.println("Upper Bound:       " + problem.getUpperBoundOverlapping(securityDistance, lambda));
        System.out.println("Ficticious Tasks:  ");
        int[][] initialPositions = solution.getFicticiousTasks();
        for (int i = 0; i < problem.getQuayCranes(); i++) {
            int position = initialPositions[i][0];
            //int duration = initialPositions[i][1];
            int dependent = initialPositions[i][2];
            int id = initialPositions[i][3];
            int last = initialPositions[i][4];
            System.out.println("\tQC " + i + " -> id=" + id + ", pos=" + position + ", dep=" + dependent + ", last=" + last);
        }
		final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(problem, SAFETY_DISTANCE, lambda, true, false);
		final StowagePlan plan = planBuilder.getStowagePlanFromQCSP(solution);
		System.out.println("Vessel for best solution: ");
		System.out.println(plan.getVessel());
		System.out.println();
		System.out.println("Stowage plan for best solution:");
		System.out.println(plan);
		System.out.println("Fictitious tasks");
        for (int i = 0; i < problem.getQuayCranes(); i++) {
        	System.out.println("\tQC " + i + " -> pos=" + plan.getDependenciesAtStart(i)[0] + ", dep=" + plan.getDependenciesAtStart(i)[1]);
        }
		planBuilder.saveToFile(new StowagePlan[] {plan}, "C:\\Users\\Iván Castilla\\Dropbox\\SimulationPorts\\for_simulation\\k45_adhoc.sol");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testDependenciesAtStart();
//		final Arguments arguments = new Arguments();
//		try {
//			JCommander jc = JCommander.newBuilder()
//			  .addObject(arguments)
//			  .build();
//			jc.parse(args);
//			if (arguments.popSize > arguments.maxGen) {
//				ParameterException ex = new ParameterException("Wrong argument format: maxGeneration (" + arguments.maxGen + ") must be >= than populationSize (" + arguments.popSize + ")");
//				ex.setJCommander(jc);
//				throw ex;
//			}
//			final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(new QCSProblem(arguments.inputFileName), SAFETY_DISTANCE, arguments.overlapLambda, arguments.keep, arguments.debug);
//			final Population population = planBuilder.getSolutions(arguments.maxGen, arguments.popSize);
//			final StowagePlan[] plans = new StowagePlan[population.getSize()];
//			for (int i = 0; i < plans.length; i++) {
//				plans[i] = planBuilder.getStowagePlanFromQCSP(population.get(i));	
//			}
//			
//			if (arguments.debug) {				
//				System.out.println("Saved " + plans.length + " solutions");
//				System.out.println("Vessel for best solution: ");
//				System.out.println(plans[0].getVessel());
//				System.out.println();
//				System.out.println("Stowage plan for best solution:");
//				System.out.println(plans[0]);
//			}
//			if (arguments.outputFileName != null)
//				planBuilder.saveToFile(plans, arguments.outputFileName);
//		} catch (ParameterException ex) {
//			System.out.println(ex.getMessage());
//			ex.usage();
//			System.exit(-1);
//		}		
	}
	
	final private static class Arguments {
		@Parameter(names ={"--input", "-i"}, description = "Input instance file", required = true, order = 0)
		private String inputFileName;
		@Parameter(names ={"--output", "-o"}, description = "Output \"sol\" file with QCSP solutions", order = 1)
		private String outputFileName = null;		
		@Parameter(names ={"--maxgen", "-m"}, description = "MaxGeneration parameter for QCSP", order = 2)
		private int maxGen = 100;
		@Parameter(names ={"--popsize", "-p"}, description = "PopulationSize parameter for QCSP", order = 3)
		private int popSize = 100;
		@Parameter(names ={"--lambda", "-l"}, description = "Overlap lambda for QCSP", order = 4)
		private double overlapLambda = 1.0;
		@Parameter(names ={"--debug", "-d"}, description = "Print debug messages", order = 6)
		private boolean debug = false;
		@Parameter(names ={"--keep", "-k"}, description = "Keep original duration of tasks", order = 5)
		private boolean keep = false;
	}

}
