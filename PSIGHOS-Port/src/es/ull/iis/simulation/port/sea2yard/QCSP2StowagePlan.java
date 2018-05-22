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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.kaizten.simulation.ports.qcsp.data.DummyTask;
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
	final private static String HOME = System.getProperty("user.home");
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
	
	/**
	 * Rescales a value between 0 and 1
	 * @param value Value to change scale
	 * @param min Min value of the original scale
	 * @param max Max value of the original scale
	 * @return The rescaled value
	 */
    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
    
	public Population getSolutions(int populationSize) {
        // Configuring optimization technique
        final EDA solver = new EDA(problem, 100, populationSize, 20.0, 0.01);
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
        final ArrayList<ArrayList<int []>> waits = new ArrayList<>(quayCranes);
        for (int craneId = 0; craneId < quayCranes; craneId++)
        	waits.add(new ArrayList<>());
		@SuppressWarnings("unchecked")
		final TreeMap<Integer, Integer>[] orderedTasks = (TreeMap<Integer, Integer>[])new TreeMap<?, ?>[quayCranes];
        for (int qc = 0; qc < quayCranes; qc++) {
            orderedTasks[qc]= new TreeMap<Integer, Integer>();
        }
        
        if (keepOriginalDuration) {
            for (int task = 1; task <= problem.getNumRealTasks(); task++) {
                int bay = problem.getTaskBay(task);
                vessel.add(task - 1, bay, solution.times[task] - problem.getTaskProcessingTime(task) + 1, problem.getTaskProcessingTime(task));
                orderedTasks[solution.getQuayCraneDoTask(task)].put(solution.times[task], task - 1);
            }
            final List<DummyTask> dummyTasks = solution.getDummyTasks();
            for (DummyTask dummyTask : dummyTasks) {
            	final int lastTask = (dummyTask.getLastTask() == -1) ? -1 : dummyTask.getLastTask() - 1;
            	waits.get(dummyTask.getQC()).add(new int[] {lastTask, dummyTask.getBay(), dummyTask.getOtherTask() - 1});
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
                    orderedTasks[solution.getQuayCraneDoTask(task)].put(startTime, containerId);                    
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
            final List<DummyTask> dummyTasks = solution.getDummyTasks();
            for (DummyTask dummyTask : dummyTasks) {
            	final int lastTask = (dummyTask.getLastTask() == -1) ? -1 : tasksByOriginalTask.get(dummyTask.getLastTask()).get(tasksByOriginalTask.get(dummyTask.getLastTask()).size() - 1);
            	final int otherTask = tasksByOriginalTask.get(dummyTask.getOtherTask()).get(tasksByOriginalTask.get(dummyTask.getOtherTask()).size() - 1);
            	waits.get(dummyTask.getQC()).add(new int[] {lastTask, dummyTask.getBay(), otherTask});
            }
        }
        stowagePlan = new StowagePlan(solution, vessel, quayCranes, safetyDistance, (long)solution.getObjectiveFunctionValue() / 3, 
        		normalize(solution.getOverlap(overlapLambda), problem.getLowerBoundOverlapping(planningHorizon, overlapLambda), problem.getUpperBoundOverlapping(safetyDistance, overlapLambda)),
        		solution.leftToRight, problem.getQuayCranesStartingPositions(), orderedTasks, waits);
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
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k45.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 4, 5, 6, 7, 9, 12}, 0);
        solution.assignTaskToQuayCrane(new int[]{11, 13, 15, 17, 18, 19}, 1);
        solution.assignTaskToQuayCrane(new int[]{8, 10, 14, 16, 20, 21, 22, 23, 24, 25}, 2);
		test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k45_adhoc_long.sol", false, false);
	}
	
	public static void testRightToLeft() {
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k30.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 4, 5, 9, 10}, 0);
        solution.assignTaskToQuayCrane(new int[]{6, 7, 8, 11, 12, 13, 14, 15}, 1);
        //
        solution.setLeftToRight(false);
        test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k30_adhoc_long.sol", false, false);
	}
	
	// FIXME: Simulation fails!!!
	public static void testRightToLeftDependent() {
//        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k30.txt";
//        QCSProblem problem = new QCSProblem(instance);
//        QCSPSolution solution = new QCSPSolution(problem, true);
//        //
//        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 4, 5, 6, 7, 11}, 0);
//        solution.assignTaskToQuayCrane(new int[]{8, 9, 10, 12, 13, 14, 15}, 1);
//        //
//        solution.setLeftToRight(false);
//        test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k30_dep_rl.sol", true, false);
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k21.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 5, 10}, 0);
        solution.assignTaskToQuayCrane(new int[]{4, 6, 7, 8, 9}, 1);
        //
        solution.setLeftToRight(false);
        test(solution, HOME + "/Dropbox/SimulationPorts/for_validation/more/k21_dep_rl.sol", true, true);
	}
	

	public static void testDependent() {
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k30.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{3, 4, 5, 6, 7, 8}, 0);
        solution.assignTaskToQuayCrane(new int[]{1, 2, 9, 10, 11, 12, 13, 14, 15}, 1);
        //
        test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k30_dep.sol", true, false);
	}
	
	public static void testMore() {
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k16.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{2, 3, 4, 6}, 0);
        solution.assignTaskToQuayCrane(new int[]{1, 5, 7, 8, 9, 10}, 1);
        //
        test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k16_dep1.sol", false, true);
	}
	
	public static void testMore2() {
        final String instance = HOME + "/Dropbox/SimulationPorts/instances/k100.txt";
        QCSProblem problem = new QCSProblem(instance);
        QCSPSolution solution = new QCSPSolution(problem, true);
        //
        solution.assignTaskToQuayCrane(new int[]{1, 2, 3, 5, 8, 9, 18, 25, 33, 42}, 0);
        solution.assignTaskToQuayCrane(new int[]{4, 6, 13, 15, 23, 28, 30, 32, 36, 44}, 1);
        solution.assignTaskToQuayCrane(new int[]{7, 10, 11, 16, 17, 20, 29, 39, 49}, 2);
        solution.assignTaskToQuayCrane(new int[]{12, 14, 19, 22, 24, 34, 38}, 3);
        solution.assignTaskToQuayCrane(new int[]{21, 26, 27, 37, 40, 41, 46, 50}, 4);
        solution.assignTaskToQuayCrane(new int[]{31, 35, 43, 45, 47, 48}, 5);
        //
//        solution.setLeftToRight(false);
        test(solution, HOME + "/Dropbox/SimulationPorts/for_simulation/k100_dep.sol", true, true);
	}
	
	public static void testInstancesOverlap() {
		final String instanceName = "qc6";
        final String instance = System.getProperty("user.home") + "/Dropbox/SimulationPorts/instances-overlap-analysis/" + instanceName + ".txt";
        for (int index = 0; index < 10; index++) {
            QCSProblem problem = new QCSProblem(instance);
            for (int qc = 0; qc < problem.getQuayCranes(); qc++) {
                problem.getQuayCranesStartingPositions()[problem.getQuayCranes() - qc - 1] = 100 - (qc * (2 + index));
            }
            QCSPSolution solution = new QCSPSolution(problem, true);
            int tasksPerQc = problem.getNumRealTasks() / problem.getQuayCranes();
            for (int qc = 0; qc < problem.getQuayCranes(); qc++) {
                int[] tasks = new int[tasksPerQc];
                for (int i = 0; i < tasksPerQc; i++) {
                    tasks[i] = 1 + (i * problem.getQuayCranes() + qc);
                }
                solution.assignTaskToQuayCrane(tasks, problem.getQuayCranes() - qc - 1);
            }
            test(solution, HOME + "/Dropbox/SimulationPorts/instances-overlap-analysis/" + instanceName + "_" + index + ".sol", true, true);
        }
		
	}
	public static void test(QCSPSolution solution, String outputFile, boolean keep, boolean debug) {
        solution.evaluator = new EvaluatorMeisel();
        solution.getObjectiveFunctionValue();
		QCSProblem problem = solution.getDataProblem();
        System.out.println(solution);
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
		final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(problem, SAFETY_DISTANCE, lambda, keep, debug);
		final StowagePlan plan = planBuilder.getStowagePlanFromQCSP(solution);
		System.out.println("Vessel for best solution: ");
		System.out.println(plan.getVessel());
		System.out.println();
		System.out.println("Stowage plan for best solution:");
		System.out.println(plan);
		System.out.println("Fictitious waits");
        List<DummyTask> dummyTasks = solution.getDummyTasks();
        for (DummyTask dummyTask : dummyTasks) {
            System.out.println(dummyTask);
        }
        if (outputFile != null) {
        	planBuilder.saveToFile(new StowagePlan[] {plan}, outputFile);
        }
	}
	
	/**
	 * Tests several solutions for the same problem
	 * @param problem
	 * @param solutions
	 * @param outputFile
	 * @param keep If true, keeps the original lenght of tasks; otherwise, creates a task per unit
	 * @param debug
	 */
	public static void test(QCSProblem problem, QCSPSolution[] solutions, String outputFile, boolean keep, boolean debug) {
        final int planningHorizon = 1000;
        final int securityDistance = 1;
        final double lambda = 1.0;
		final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(problem, SAFETY_DISTANCE, lambda, keep, debug);
		final StowagePlan[] plans = new StowagePlan[solutions.length];
		for (int index = 0; index < solutions.length; index++) {
			QCSPSolution solution = solutions[index];
	        solution.evaluator = new EvaluatorMeisel();
	        solution.getObjectiveFunctionValue();
	        System.out.println(solution);
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
			plans[index] = planBuilder.getStowagePlanFromQCSP(solution);
			System.out.println("Vessel for best solution: ");
			System.out.println(plans[index].getVessel());
			System.out.println();
			System.out.println("Stowage plan for best solution:");
			System.out.println(plans[index]);
			System.out.println("Fictitious waits");
	        List<DummyTask> dummyTasks = solution.getDummyTasks();
	        for (DummyTask dummyTask : dummyTasks) {
	            System.out.println(dummyTask);
	        }
		}
        if (outputFile != null) {
        	planBuilder.saveToFile(plans, outputFile);
        }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testInstancesOverlap();
//		testDependent();
//		testRightToLeft();
//		testDependenciesAtStart();
//		testMore2();

//		final Arguments arguments = new Arguments();
//		try {
//			JCommander jc = JCommander.newBuilder()
//			  .addObject(arguments)
//			  .build();
//			jc.parse(args);
//			final QCSP2StowagePlan planBuilder = new QCSP2StowagePlan(new QCSProblem(arguments.inputFileName), SAFETY_DISTANCE, arguments.overlapLambda, arguments.keep, arguments.debug);
//			final Population population = planBuilder.getSolutions(arguments.popSize);
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
