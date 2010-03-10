package es.ull.isaatc.simulation.bonn3Phase;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.isaatc.util.ExtendedMath;

/**
 * 
 */

/**
 * Java implementation of a tournament barrier (see C. Ball and M. Bull, "Barrier Synchronisation 
 * in Java," 2003. Available at http://www.ukhec.ac.uk/publications/reports/synch_java.pdf).
 * <p>
 * A tournament barrier makes the threads compete in a series of game "rounds", arranged in a
 * tournament structure. The winning thread "plays" against other winning threads until there is
 * only a "champion". The loser threads simply await to be awaken by the champion.<p>
 * Since this is not a real competition, winners and losers can be arbitrarily chosen in advance 
 * to improve performance.
 * @author Patrick Peschlow
 * @author Andreas Voss
 * @author Ivan Castilla (adaptation)
 *
 */
public class TournamentBarrier extends TPCBarrier {
	
	/**
	 * The barrier information associated to each competitor thread.
	 */
	private final HashMap<Runnable, BarrierThread> threadInfos_;
	
	/**
	 * The number of rounds used for the barrier.
	 */
	private final int numRounds_;

	/**
	 * Out flag set by the winner.
	 */
	private volatile boolean tourFlagOut = false;


	/**
	 * Creates a new Tournament Barrier.
	 * @param parties the threads that must invoke await()  before the barrier is tripped
	 * @param barrierAction the command to execute when the barrier is tripped, or <code>null</code>
	 * if there is no action
	 */
	public TournamentBarrier(Runnable[] parties, Runnable barrierAction) {
		super(parties, barrierAction);
		numRounds_ = (int) Math.ceil(Math.log(numThreads_) / Math.log(2.0));
		threadInfos_ = new HashMap<Runnable, BarrierThread>();

		for (int i = 0; i < parties.length; i++)
			threadInfos_.put(parties_[i], new BarrierThread(i));
		
		final int numVirtualThreads = ExtendedMath.nextHigherPowerOfTwo(numThreads_);
		for (BarrierThread bt : threadInfos_.values()) {
			bt.setupBarrier(numVirtualThreads);
//			bt.debug();
		}
	}

	/**
	 * Creates a new Tournament Barrier that will trip when the given number of parties 
	 * (threads) are waiting upon it, and does not perform a predefined action when the barrier 
	 * is tripped.
	 * @param parties the threads that must invoke {@link #await()} before the barrier is tripped
	 * @throws IllegalArgumentException if parties is less than 1
	 */	
	public TournamentBarrier(Runnable[] parties) {
		this(parties, null);
	}
	
	/* (non-Javadoc)
	 * @see TPCBarrier#barrier()
	 */
	/**
	 * The performance highly relies on the {@link Threads.currentThread()) performance
	 */
	@Override
	public void await() {
		threadInfos_.get(Thread.currentThread()).await();
	}

	/**
	 * Allows the barrier to be safely reused. 
	 * FIXME: Only for compatibility purposes with CyclicBarrier. Candidate to be removed
	 */
	public void reset() {
		tourFlagOut = false;
		for (BarrierThread bt : threadInfos_.values())
			bt.reset();
	}
	
	/**
	 * The inner representation of the information required by a thread to use the barrier.
	 * @author Patrick Peschlow
	 * @author Andreas Voss
	 * @author Ivan Castilla (Adaptation)
	 *
	 */
	private class BarrierThread {
		/**
		 * The unique id of this thread.
		 */
		private final int threadId_;
		
		/**
		 * Sense-reversing flag.
		 */
		private boolean tourSense_ = false;

		/**
		 * Pre-computed rounds.
		 */
		private final Round[] tourRounds_;

		/**
		 * Flags to be set during the barrier.
		 */
		private final AtomicBoolean[] tourFlag_;

		private BarrierThread(int threadId) {
			threadId_ = threadId;
			tourRounds_ = new Round[numRounds_];
			tourFlag_ = new AtomicBoolean[numRounds_];
		}

		protected int getThreadId() {
			return threadId_;
		}
		
		private void setupBarrier(int numVirtualThreads) {			
			for (int round = 0; round < numRounds_; round++) {
				final int partnerId = (threadId_ ^ ExtendedMath.powInt(2, round))
						% numVirtualThreads;
				final boolean isWinner = (threadId_ % ExtendedMath.powInt(2, round + 1) == 0);
				Role role;
				BarrierThread partner;
				if (partnerId >= numThreads_) {
					role = Role.WILDCARD;
					partner = null;
				} else {
					if (isWinner) {
						if (threadId_ == 0 && round == numRounds_ - 1) {
							role = Role.ROOT;
						} else {
							role = Role.WINNER;
						}
					} else {
						role = Role.LOSER;
					}
					partner = threadInfos_.get(parties_[partnerId]);
				}
				Round roundObj = new Round(partner, role);
				tourRounds_[round] = roundObj;
				tourFlag_[round] = new AtomicBoolean(false);
			}
		}
		
		private void reset() {
			tourSense_ = false;
			for (int round = 0; round < numRounds_; round++)
				tourFlag_[round].set(false);			
		}
		
		private void await() {
			tourSense_ = !tourSense_;
			int currentRound = 0;
			for (;;) {
				final Round roundObj = tourRounds_[currentRound];
				switch (roundObj.role_) {
				case WINNER:
					while (tourFlag_[currentRound].get() != tourSense_) {
					}
					++currentRound;
					// Continue to next round.
					continue;
				case WILDCARD:
					++currentRound;
					// Continue to next round.
					continue;
				case LOSER:
					roundObj.partner_.tourFlag_[currentRound].set(tourSense_);
					// Wait for the tournament winner (root) to complete the
					// barrier action.
					while (tourFlagOut != tourSense_) {
					}
					// Exit switch statement (and thus the for loop).
					break;
				case ROOT:
					while (tourFlag_[currentRound].get() != tourSense_) {
					}
					final Runnable command = barrierAction_;
					if (command != null)
						command.run();
					tourFlagOut = tourSense_;
					// Exit switch statement (and thus the for loop).
					break;
				}
				// Exit for loop.
				break;
			}			
		}
	}
	
	/**
	 * Roles of the different threads during tournament barriers.
	 * 
	 * @author Patrick Peschlow
	 */
	private static enum Role {
		WINNER, LOSER, WILDCARD, ROOT;
	}

	/**
	 * Helper object to pre-compute rounds of tournament barriers.
	 * 
	 * @author Patrick Peschlow
	 */
	private static class Round {
		/** The "competitor" thread. */
		private final BarrierThread partner_;
		/** The role of this thread in the current round. */
		private final Role role_;

		private Round(BarrierThread partner, Role role) {
			partner_ = partner;
			role_ = role;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			if (partner_ != null)
				return "PT:" + partner_.getThreadId() + "/" + role_;
			return "null/" + role_;
		}
	}
}
