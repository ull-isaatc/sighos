/**
 * 
 */
package es.ull.iis.simulation.laundry;

import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class WashingUsageListener extends Listener {
	final TreeMap<Location, int[]> usage;
	final TreeMap<Location, Integer> lastGap;
	final long timeGap;
	final int nIntervals;
	/**
	 * @param description
	 */
	public WashingUsageListener(final LaundrySimulation simul, final long timeGap) {
		super("Listener for the usage of the different resources of the laundry");
		addEntrance(EntityLocationInfo.class);
		addEntrance(ResourceUsageInfo.class);
		addEntrance(SimulationStartStopInfo.class);
		usage = new TreeMap<>();
		lastGap = new TreeMap<>();
		this.timeGap = timeGap;
		nIntervals = (int) (simul.getEndTs() / timeGap) + 1;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(final SimulationInfo info) {
		if (info instanceof EntityLocationInfo) {
			final EntityLocationInfo lInfo = (EntityLocationInfo)info;
			final Location loc = lInfo.getLocation();
			final int gap = (int) (lInfo.getTs() / timeGap); 
			if (!usage.containsKey(loc)) {
				usage.put(loc, new int[nIntervals]);
				lastGap.put(loc, gap);
				switch(lInfo.getType()) {
				case ARRIVE:
				case START:
					usage.get(loc)[gap]++;
					break;
				case LEAVE:
					LaundrySimulation.error(lInfo.getEntity() + "\tcannot move from a location without having been placed there first");
				default:
					break;			
				}
			}
			else {
				final int[] use = usage.get(loc);
				switch(lInfo.getType()) {
				case ARRIVE:
				case START:
					for (int i = lastGap.get(loc) + 1; i <= gap; i++)
						use[i] = use[i - 1];
					use[gap]++;
					// TODO: Check if needed
//					usage.put(loc, use);
					break;
				case LEAVE:
					for (int i = lastGap.get(loc) + 1; i <= gap; i++)
						use[i] = use[i - 1];
					use[gap]--;
					// TODO: Check if needed
//					usage.put(loc, use);
					break;
				default:
					break;			
				}
				lastGap.put(loc, gap);
			}
				
		}
		else if (info instanceof ResourceUsageInfo) {
			
		}
		else if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
				final TreeMap<Location, double[]> summaryUse = new TreeMap<>(); 
				System.out.print("Time");
				for (final Location loc : usage.keySet()) {
					System.out.print("\t" + loc);
					final int[] use = usage.get(loc);
					for (int i = lastGap.get(loc) + 1; i < use.length; i++)
						use[i] = use[i - 1];
					summaryUse.put(loc, new double[] {0.0, 0.0});
				}
				System.out.println();
				for (int i = 0; i < nIntervals; i++) {
					System.out.print(i * timeGap);
					for (final int[] use : usage.values()) 
						System.out.print("\t" + use[i]);
					System.out.println();
				}
				System.out.print("Avg");
				for (final int[] use : usage.values()) 
					System.out.print("\t" + String.format(Locale.US, "%.2f", Statistics.average(use)));
				System.out.println();
				System.out.print("SD");
				for (final int[] use : usage.values()) 
					System.out.print("\t" + String.format(Locale.US, "%.2f", Statistics.stdDev(use)));
				System.out.println();
				System.out.print("Avg/%");
				for (final Entry<Location, int[]> entry : usage.entrySet()) 
					System.out.print("\t" + String.format(Locale.US, "%.2f", Statistics.average(entry.getValue()) / (double) entry.getKey().getCapacity()));
				System.out.println();
			}
		}
	}

}
