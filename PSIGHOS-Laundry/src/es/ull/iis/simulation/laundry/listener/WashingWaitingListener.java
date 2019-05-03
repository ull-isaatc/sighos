/**
 * 
 */
package es.ull.iis.simulation.laundry.listener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.laundry.Bag;
import es.ull.iis.simulation.laundry.LaundrySimulation;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class WashingWaitingListener extends Listener {
	final TreeMap<Location, ArrayList<Long>> waitingTime;
	final TreeMap<Bag, Pair> waitingStart;
	final double conversionFactor;
	/**
	 * @param description
	 */
	public WashingWaitingListener(final LaundrySimulation simul, TimeUnit unit) {
		super("Listener for the waiting time before entering into the different resources of the laundry");
		addEntrance(EntityLocationInfo.class);
		addEntrance(SimulationStartStopInfo.class);
		waitingTime = new TreeMap<>();
		waitingStart = new TreeMap<>();
		this.conversionFactor = unit.getConversionFactor(simul.getTimeUnit());
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(final SimulationInfo info) {
		if (info instanceof EntityLocationInfo) {
			final EntityLocationInfo lInfo = (EntityLocationInfo)info;
			final Location loc = lInfo.getLocation();
			if (!waitingTime.containsKey(loc)) {
				waitingTime.put(loc, new ArrayList<>());
			}
			final ArrayList<Long> wTime = waitingTime.get(loc);
			final Bag bag = (Bag)lInfo.getEntity();
			switch(lInfo.getType()) {
			case ARRIVE:
				if (waitingStart.containsKey(bag)) {
					final Pair initWait = waitingStart.remove(bag);
					wTime.add(lInfo.getTs() - initWait.waitStart);
				}
				else {
					wTime.add(0L);
				}
				break;
			case START:
				wTime.add(0L);
				break;
			case WAIT_FOR:
			case COND_WAIT:
				waitingStart.put(bag, new Pair(lInfo.getLocation(), lInfo.getTs()));
				break;
			case LEAVE:
			default:
				break;			
			}
		}
		else if (info instanceof SimulationStartStopInfo) {
			if (SimulationStartStopInfo.Type.END.equals(((SimulationStartStopInfo) info).getType())) {
				final long endTs = ((SimulationStartStopInfo) info).getTs();
				// Fill the time for non finished waitings
				while (!waitingStart.isEmpty()) {
					final Bag bag = waitingStart.firstKey();
					final Pair initWait = waitingStart.remove(bag);
					waitingTime.get(initWait.loc).add(endTs - initWait.waitStart);					
				}
				System.out.print("Location");
				for (final Location loc : waitingTime.keySet()) {
					System.out.print("\t" + loc);
				}
				System.out.println();
				System.out.print("Avg Wait");
				for (final Location loc : waitingTime.keySet()) {
					final ArrayList<Long> wTimes = waitingTime.get(loc);
					if (wTimes.isEmpty())
						System.out.print("\tNaN");
					else
						System.out.print("\t" + String.format(Locale.US, "%.2f", Statistics.average(wTimes) * conversionFactor));
				}
				System.out.println();
				System.out.print("SD Wait");
				for (final Location loc : waitingTime.keySet()) {
					final ArrayList<Long> wTimes = waitingTime.get(loc);
					if (wTimes.isEmpty())
						System.out.print("\tNaN");
					else
						System.out.print("\t" + String.format(Locale.US, "%.2f", Statistics.stdDev(wTimes) * conversionFactor));
				}
				System.out.println();
			}
		}
	}
	
	final class Pair {
		final Location loc;
		final long waitStart;
		
		public Pair(Location loc, long waitStart) {
			this.loc = loc;
			this.waitStart = waitStart;
		}
	}

}
