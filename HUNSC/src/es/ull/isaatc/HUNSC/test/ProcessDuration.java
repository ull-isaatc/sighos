package es.ull.isaatc.HUNSC.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 */

/**
 * @author Iván
 *
 */
public class ProcessDuration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader f = null;
		HashMap<String, ArrayList<Integer>> valores = new HashMap<String, ArrayList<Integer>>();
		try {
			f = new BufferedReader(new FileReader("C:\\Users\\Iván\\Documents\\HC\\test\\todosDiag.csv"));
			String cadena = "";
			while ((cadena = f.readLine()) != null) {
				String []strs = cadena.split(";");
				ArrayList<Integer> val = valores.get(strs[0]);
				if (val == null) {
					val = new ArrayList<Integer>();
					valores.put(strs[0], val);
				}
				val.add(new Integer(strs[1]));				
			}
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			for (String key : valores.keySet()) {
				BufferedWriter fout = new BufferedWriter(new FileWriter("C:\\Users\\Iván\\Documents\\HC\\test\\" + key + ".txt"));
				for (int val : valores.get(key))
					fout.write(val + "\r\n");
				fout.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 


	}

}
