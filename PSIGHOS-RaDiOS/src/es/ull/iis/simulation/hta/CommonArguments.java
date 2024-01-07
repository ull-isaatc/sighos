/**
 * 
 */
package es.ull.iis.simulation.hta;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Commonly used input arguments for creating a Health Technology Assessment application. These arguments can be used from any main to parse the input parameters.
 * If the user wants more specific arguments, this class may be extended or another similar class may be implemented. An example of the latter,, if the new class is called "ArgsExtra", 
 * the parsing of a String "args" can be configured by invoking:
 * <pre>{@code
 * JCommander.newBuilder()
    .addObject(new Object[] {new CommonArguments() , new ArgsExtra()})
    .build()
    .parse(args);
    }</pre>   
 * @author Iván Castilla
 *
 */
public class CommonArguments {

	@Parameter(names = { "--output", "-o" }, description = "Name of the output file name", order = 1)
	public String outputFileName = null;
	@Parameter(names = { "--patients", "-n" }, description = "Number of patients to simulate", order = 2)
	public int nPatients = HTAExperiment.DEF_N_PATIENTS;
	@Parameter(names = { "--runs", "-r" }, description = "Number of probabilistic runs", order = 3)
	public int nRuns = HTAExperiment.N_RUNS;
	@Parameter(names = { "--horizon", "-h" }, description = "Time horizon for the simulation (years)", order = 3)
	public int timeHorizon = -1;
	@Parameter(names = { "--discount",
			"-dr" }, variableArity = true, description = "The discount rate to be applied. If more than one value is provided, the first one is used for costs, and the second for effects. Default value is "
					+ HTAExperiment.DEF_DISCOUNT_RATE, order = 7)
	public List<Double> discount = new ArrayList<>();
	@Parameter(names = { "--single_patient_output", "-ps" }, description = "Enables printing the specified patient's output", order = 4)
	public int singlePatientOutput = -1;
	@Parameter(names = { "--epidem", "-ep" }, variableArity = true, description = "Enables printing epidemiologic results. Can receive several \"orders\". Each order consists of\r\n"
			+ "\t- The type of info to print {i: incidence, p:prevalence, c:cumulative incidence}\r"
			+ "\t- An optional argument of whether to print absolute ('a') or relative ('r') results (Default: relative)\r"
			+ "\t- An optional argument of whether to print information by age ('a') or by time from start ('t') results (Default: time from start)\r"
			+ "\t- An optional number that indicates interval size (in years) (Default: 1)", order = 9)
	public List<String> epidem = new ArrayList<>();

	@Parameter(names = { "--outcomes", "-po" }, description = "Enables printing individual outcomes", order = 9)
	public boolean individualOutcomes = false;
	@Parameter(names = { "--costs", "-pbc" }, description = "Enables printing breakdown of costs", order = 9)
	public boolean breakdownCost = false;
	@Parameter(names = { "--budget", "-pbi" }, description = "Enables printing budget impact", order = 9)
	public boolean bi = false;
	@Parameter(names = { "--parallel", "-p" }, description = "Enables parallel execution", order = 5)
	public boolean parallel = false;
	@Parameter(names = { "--quiet", "-q" }, description = "Quiet execution (does not print progress info)", order = 6)
	public boolean quiet = false;
	@Parameter(names = { "--year", "-y" }, description = "Modifies the year of the study (for cost updating))", order = 8)
	public int year = Year.now().getValue();

	/**
	 * 
	 */
	public CommonArguments() {
	}

}
