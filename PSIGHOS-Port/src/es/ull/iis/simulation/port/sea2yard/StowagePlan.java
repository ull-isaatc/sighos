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
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Iván Castilla
 *
 */
public class StowagePlan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3247224781219705052L;
	private final ArrayList<Integer>[] plan;
	private final int[] initPosition;
	private final Vessel vessel;
	private int nContainers;
	private final int nCranes;
	private final int safetyDistance;
	private final long objectiveValue;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public StowagePlan(Vessel vessel, int nCranes, int safetyDistance, long objectiveValue) {
		plan = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
		for (int i = 0; i < nCranes; i++)
			plan[i] = new ArrayList<Integer>();
		initPosition = new int[nCranes];
		this.vessel = vessel;
		this.nCranes = nCranes;
		this.safetyDistance = safetyDistance;
		this.objectiveValue = objectiveValue;
		nContainers = 0;
	}

    public void addAll(int craneId, ArrayList<Integer> containers) {
        plan[craneId].addAll(containers);
        nContainers += containers.size();
    }

	public void addAll(int craneId, int[] containers) {
		nContainers += containers.length;
		for (int c : containers)			
			plan[craneId].add(c);
	}
	
	public ArrayList<Integer> get(int craneId) {
		return plan[craneId];
	}
	
	public void setInitialPosition(int craneId, int initPos) {
		initPosition[craneId] = initPos;
	}
	
	public int getInitialPosition(int craneId) {
		return initPosition[craneId];
	}
	
	/**
	 * @return the nContainers
	 */
	public int getNContainers() {
		return nContainers;
	}

	/**
	 * @return the nCranes
	 */
	public int getNCranes() {
		return nCranes;
	}

	/**
	 * @return the safetyDistance
	 */
	public int getSafetyDistance() {
		return safetyDistance;
	}

	/**
	 * @return the objectiveValue
	 */
	public long getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * @return the ship
	 */
	public Vessel getVessel() {
		return vessel;
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < plan.length; i++) {
			str.append("Crane " + i + " (INIT:" + initPosition[i] + "):");
			for (int containerId : plan[i]) {
				str.append("\t" + containerId);
			}
			str.append("\n");
		}
		return str.toString();
	}
	
	public void saveToFile(String fileName) {
		ObjectOutput output = null;
		try {
			output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			output.writeObject(this);
		}  
		catch(IOException ex) {
			System.err.println("Error creating file for Stowage plan. ");
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
	
	public static StowagePlan loadFromFile(String fileName) {
		ObjectInput input = null;
		StowagePlan plan = null;
		try {
			input = new ObjectInputStream (new BufferedInputStream(new FileInputStream(fileName)));
			plan = (StowagePlan)input.readObject();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch(IOException ex){
			System.err.println("Could not open file " + fileName + " to load stowage plan");
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

}
