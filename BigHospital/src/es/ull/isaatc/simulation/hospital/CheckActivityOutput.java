/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CheckActivityOutput {
	private static String filePath = "D:\\temp resultados\\seq\\";
//	private static String filePath = "C:\\";
	private static String actFileName = filePath + "act.txt";
	private static String queueFileName = filePath + "queue.txt";
	private static Map<Integer, String> lastAct = new HashMap<Integer, String>();
	private static Map<String, Integer> acts = new HashMap<String, Integer>();
	private static String[] actNames;
	private static int[][] actGraph;

	private static void printActGraph() {
		for (String name : actNames)
			System.out.print("\t" + name);
		System.out.println();
		for (int i = 0; i < actGraph.length; i++) {
			System.out.print(actNames[i]);
			for (int j = 0; j < actGraph[i].length; j++) {
				System.out.print("\t" + actGraph[i][j]);
			}
			System.out.println();						
		}
	}
	
	private static void createActStructure() {
		int value = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(queueFileName));
			actNames = br.readLine().split("\t");
			for (String act : actNames)
				acts.put(act, value++);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		actGraph = new int[acts.size()][acts.size()];
	}
	
	public static void main(String[] args) {
		createActStructure();
		try {
			BufferedReader br = new BufferedReader(new FileReader(actFileName));
			String line;
			String[] columns;
			// Skips first line
			br.readLine();
			while((line = br.readLine())!=null){
				columns = line.split("\t");
				int elem = Integer.parseInt(columns[0]);
				String act = columns[2];
				int actId = acts.get(act);
				String last = lastAct.get(elem);
				lastAct.put(elem, act);
				if (last != null) {
					actGraph[acts.get(last)][actId]++;
				}
			}			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printActGraph();
	}
}
