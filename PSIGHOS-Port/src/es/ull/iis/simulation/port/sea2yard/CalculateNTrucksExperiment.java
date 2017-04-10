/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.kaizten.simulation.ports.qcsp.data.QCSPSolution;
import com.kaizten.simulation.ports.qcsp.data.QCSProblem;
import com.kaizten.simulation.ports.qcsp.population.Population;
import com.kaizten.simulation.ports.qcsp.solver.eda.EDA;

import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * The main simulation class for a port. A port is divided into three areas: sea, yard and earth. Ships arrive at a 
 * specific berth, which counts on a fixed number of quay cranes to unload the charge. Each ship carries M containers, 
 * and quay cranes unload a container at a time. To unload a container, a truck must be available. Trucks lead the 
 * container to a specific block in the yard area. At his block, a yard crane puts the container in a free space. 
 * @author Iván Castilla
 *
 */
public class CalculateNTrucksExperiment extends Experiment {
	protected static final TimeUnit PORT_TIME_UNIT = TimeUnit.SECOND;
	protected static final long END_TS = 480 * 60 * 60;
	protected static final long T_OPERATION = 1L * 60;
	protected static final long T_TRANSPORT = 3 * T_OPERATION;
	protected static final long T_MOVE = T_OPERATION;
	private static final int SAFETY_DISTANCE = 0;
	private static final int MIN_N_TRUCKS = 1; 
	private static final int MAX_N_TRUCKS = 20;
	private static final double P_ERROR = 0.25; 
	private static final int NSIM = (P_ERROR == 0.0) ? 1 : 100;
//	private static final String INSTANCE = "C:\\Users\\Iván Castilla\\Dropbox\\SimulationPorts\\instances\\k40.txt";
	private static final String INSTANCE = "C:\\Users\\masbe_000\\Dropbox\\SimulationPorts\\instances\\k40.txt";
	private final StowagePlan plan; 
	private long currentSeed;
//	private final Listener[] experimentListeners;

	public CalculateNTrucksExperiment(StowagePlan plan) {
		super("PORTS", NSIM * (MAX_N_TRUCKS - MIN_N_TRUCKS + 1));
		this.plan = plan;
//		experimentListeners = new Listener[(MAX_N_TRUCKS == MIN_N_TRUCKS) ? NSIM : (NSIM * (MAX_N_TRUCKS - MIN_N_TRUCKS + 1))];
	}
	
	/**
	 * Creates a ship with 10 bays
	 * 		0
	 * 		1			4				8
	 * 		2		3	5		6	7	9
	 * ---------------------------------------
	 * 	0	1	2	3	4	5	6	7	8	9
	 * Creates a stowage plan for two cranes:
	 * - Crane 0 unloads 0, 1, 2, 5, 7
	 * - Crane 1 unloads 3, 4, 6, 8, 9
	 * @return A stowage plane for two cranes
	 */
	static StowagePlan fillTestPlan1() {
		final Vessel vessel = new Vessel(10, TimeUnit.MINUTE);
		vessel.add(0, 1, 14);
		vessel.add(1, 1, 18);
		vessel.add(2, 1, 6);
		vessel.add(3, 3, 10);
		vessel.add(4, 4, 17);
		vessel.add(5, 4, 21);
		vessel.add(6, 6, 10);
		vessel.add(7, 7, 9);
		vessel.add(8, 8, 7);
		vessel.add(9, 8, 19);
		final StowagePlan plan = new StowagePlan(vessel, 2, 1);
		plan.addAll(0, new int[]{0, 1, 2, 5, 7});
		plan.addAll(1, new int[]{3, 4, 6, 8, 9});
		plan.setInitialPosition(0, 2);
		plan.setInitialPosition(1, 6);
		return plan;
	}

	static StowagePlan fillTestPlan2() {
		final Vessel vessel = new Vessel(16, TimeUnit.MINUTE);
		vessel.add(0, 1, 33);
		vessel.add(1, 2, 2);
		vessel.add(2, 4, 44);
		vessel.add(3, 4, 59);
		vessel.add(4, 9, 60);
		vessel.add(5, 9, 6);
		vessel.add(6, 10, 60);
		vessel.add(7, 10, 52);
		vessel.add(8, 11, 56);
		vessel.add(9, 11, 41);
		vessel.add(10, 14, 38);
		vessel.add(11, 14, 22);
		vessel.add(12, 15, 54);
		vessel.add(13, 15, 16);
		vessel.add(14, 15, 34);
		final StowagePlan plan = new StowagePlan(vessel, 2, 0);
		plan.addAll(0, new int[]{0, 1, 2, 3, 7, 8, 9});
		plan.addAll(1, new int[]{4, 5, 6, 10, 11, 12, 13, 14});
		plan.setInitialPosition(0, 1);
		plan.setInitialPosition(1, 8);
		return plan;
	}

	@Override
	public Simulation getSimulation(int ind) {
		PortModel model = null;
		if (MAX_N_TRUCKS != MIN_N_TRUCKS) {
			int mod = (ind % (MAX_N_TRUCKS - MIN_N_TRUCKS + 1));
			if (mod == 0) {
				model = new PortModel(plan, ind, MIN_N_TRUCKS + mod, P_ERROR);
				currentSeed = model.getCurrentSeed();
			}
			else {
				model = new PortModel(plan, ind, MIN_N_TRUCKS + mod, P_ERROR, currentSeed);
			}
		}
		else {
			model = new PortModel(plan, ind, MIN_N_TRUCKS, P_ERROR);
		}
//		experimentListeners[ind] = new Sea2YardGeneralListener(plan, ind, TimeUnit.MINUTE);
//		model.addInfoReceiver(experimentListeners[ind]);
//		model.addInfoReceiver(new StdInfoView());
		model.addInfoReceiver(new Sea2YardGeneralListener(plan, ind, TimeUnit.SECOND));
//		model.addInfoReceiver(new ContainerTraceListener(TimeUnit.SECOND));
//		model.addInfoReceiver(new ContainerTimeLineListener(plan, TimeUnit.MINUTE));
		return model;
	}

	public static StowagePlan readPlanFromFile(QCSProblem problem) {
        // Configuring optimization technique
        final int maxGenerations = 100;
        final int populationSize = 100;
        final EDA solver = new EDA(problem, maxGenerations, populationSize, 20.0, 0.01);
        // Solving the instance
        solver.solve();
        // Showing results
        Population population = solver.getLargePopulation();
        for (int i = 0; i < population.getSize(); i++) {
            QCSPSolution solution = population.get(i);
            System.out.println("Solution " + i + ":");
            System.out.println(solution);
        }
        // Obtain best solution
        QCSPSolution bestSolution = population.get(0);
        System.out.println("Best Solution:");
        System.out.println(bestSolution);
        
        // Create aux structures
        final int[] craneDoTask = new int[problem.getNumRealTasks()];
        final int[][] tasksDoneByCrane = new int[problem.getQuayCranes()][]; 
        for (int qc = 0; qc < problem.getQuayCranes(); qc++) {
        	tasksDoneByCrane[qc] = new int[bestSolution.getTasksDoneByQC(qc).size() - 2];
            for (int i = 1; i < bestSolution.getTasksDoneByQC(qc).size() - 1; i++) {
            	final int task = bestSolution.getTasksDoneByQC(qc).get(i);
                tasksDoneByCrane[qc][i-1] = task;
            }
        }
        for (int i = 1; i <= problem.getNumRealTasks(); i++) {
            int time = problem.getTaskProcessingTime(i);
            System.out.println("Task " + i + ": " + time);
            craneDoTask[i-1] = bestSolution.getQuayCraneDoTask(i);
        }
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
        
        return readPlanFromFile(problem, craneDoTask, tasksDoneByCrane);
	}

	/**
	 * Returns a stowage plan based on a specific instance
	 * @param instance Path to the instance file
	 * @return A stowage plan based on the specified instance
	 */
	public static StowagePlan readPlanFromFile(QCSProblem problem, int[] craneDoTask, int[][] tasksDoneByCrane) {
        // Computing the number of bays
        int bays = problem.getMaximumBay() + 1;
        System.out.println("Bays: " + bays);
        // Creating vessel 
        int quayCranes = problem.getQuayCranes();
        int containerId = 0;
        Vessel vessel = new Vessel(bays, TimeUnit.SECOND);

        HashMap<Integer, ArrayList<Integer>> tasksByOriginalTask = new HashMap<>();
        for (int task = 1; task <= problem.getNumRealTasks(); task++) {
            int bay = problem.getTaskBay(task);
            int time = problem.getTaskProcessingTime(task);
            for (int j = 0; j < time; j++) {
                vessel.add(containerId, bay, T_OPERATION);
                ArrayList<Integer> tasks = tasksByOriginalTask.get(task);
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    tasksByOriginalTask.put(task, tasks);
                }
                tasks.add(containerId);
                containerId++;
            }
        }
        // Creating stowage plan
        StowagePlan stowagePlan = new StowagePlan(vessel, quayCranes, SAFETY_DISTANCE);
        for (int qc = 0; qc < quayCranes; qc++) {
            stowagePlan.setInitialPosition(qc, problem.getQuayCraneStartingPosition(qc));
            for (int task : tasksDoneByCrane[qc]) {
                int bay = problem.getTaskBay(task);
                int time = problem.getTaskProcessingTime(task);
                int quayCrane = craneDoTask[task-1];
                System.out.println("Task " + task + " with time " + time + " in bay " + bay + " is processed by " + quayCrane);
                stowagePlan.addAll(qc, tasksByOriginalTask.get(task));
            }
        }
		return stowagePlan;
	}
	
	public static void main(String[] args) {
		// For k40.txt
        final StowagePlan plan = readPlanFromFile(new QCSProblem(INSTANCE),
        		new int[] {0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 2, 2, 2, 1, 1, 2, 2, 2},
        		new int[][] {{1, 2, 3, 5, 6, 7, 9},{4, 8, 10, 11, 12, 16, 17},{13, 14, 15, 18, 19, 20}});
		
		// For k43.txt
//      final StowagePlan plan = readPlanFromFile(new QCSProblem(INSTANCE),
//    		  new int[] {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 1, 0, 0, 2, 1, 1, 1, 2, 2, 2, 2},
//    		  new int[][] {{1, 2, 3, 4, 5, 6, 7, 16, 17},{8, 9, 10, 11, 12, 15, 19, 20, 21},{13, 14, 18, 22, 23, 24, 25}});

		// for k101.txt
//      final StowagePlan plan = readPlanFromFile(new QCSProblem(INSTANCE),
//		new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 2, 2, 1, 2, 2, 1, 3, 2, 3, 2, 4, 2, 1, 3, 1, 4, 3, 5, 2, 5, 2, 2, 4, 3, 3, 4, 5, 5, 4, 4, 3, 5, 3, 5, 4, 4, 5},
//		new int[][] {{1, 2, 3, 4, 5, 7, 8, 9, 13},{6, 10, 11, 12, 16, 19, 26, 28},{14, 15, 17, 18, 21, 23, 25, 32, 34, 35},{20, 22, 27, 30, 37, 38, 44, 46},{24, 29, 36, 39, 42, 43, 48, 49},{31, 33, 40, 41, 45, 47, 50}});
//        final StowagePlan plan = readPlanFromFile(new QCSProblem(INSTANCE));
        
		System.out.println("Vessel: ");
		System.out.println(plan.getVessel());
		System.out.println();
		System.out.println("Stowage plan:");
		System.out.println(plan);
		new CalculateNTrucksExperiment(plan).start();

//		Simulation model = new PortModel(plan, 0, DESCRIPTION + " " + 0, 5, P_ERROR, -792807212L); // 288 - 309
//		Simulation model = new PortModel(plan, 0, DESCRIPTION + " " + 0, 5, P_ERROR, 1601344126L);// 297 - 308
//		model.addInfoReceiver(new Sea2YardGeneralListener(plan, 0, TimeUnit.MINUTE));
//		model.start();

	}

}
