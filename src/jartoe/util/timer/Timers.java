package jartoe.util.timer;

import jartoe.concurrency.Nanos;
import jartoe.concurrency.Threads;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author Artoe
 */
public final class Timers {
	private static final Timers INSTANCE = new Timers();

	public static Timers getInstance() {
		return INSTANCE;
	}

	final Set<TimerContext> timers = new HashSet<>();
	final Object lock = new Object();
	private int workerId;
	TimerThread worker;
	final Timer deathDelayer;

	private Timers() {
		deathDelayer = new Timer(new Executor() {
			public void execute(Runnable command) {
				command.run();
			}
		}, 1, 1000L) {
			public void run() {
				synchronized (lock) {
					timers.remove(new TimerContext(deathDelayer));
					if (timers.isEmpty()) {
						worker = null;
						LOCK_wakeWorker(false);
					}
				}
			}
		};
	}

	public boolean isRunning(Timer timer) {
		return timers.contains(new TimerContext(timer));
	}

	public void restart(Timer timer) {
		synchronized (lock) {
			stop(timer);
			start(timer);
		}
	}

	public void start(Timer timer) {
		TimerContext context = new TimerContext(timer);
		synchronized (lock) {
			if (!timers.contains(context)) {
				timers.add(context);
				LOCK_wakeWorker(true);
			}
		}
	}

	public void stop(Timer timer) {
		TimerContext context = new TimerContext(timer);
		synchronized (lock) {
			timers.remove(context);
			if (timers.isEmpty() && worker != null) {
				timers.remove(new TimerContext(deathDelayer));
				timers.add(new TimerContext(deathDelayer));
			}
			LOCK_wakeWorker(false);
		}
	}

	void LOCK_wakeWorker(boolean start) {
		if (worker == null) {
			if (start)
				worker = new TimerThread(++workerId);
		} else {
			lock.notify();
		}
	}

	final class TimerThread extends Thread {
		private boolean alive = true;

		TimerThread(int id) {
			super("TimerThread-" + id);
			setDaemon(true);
			setPriority(MAX_PRIORITY);
			start();
		}

		void fired() {
			alive = false;
		}

		@Override
		public void run() {
			while (alive) {
				synchronized (lock) {
					long smallest = Long.MAX_VALUE;
					for (TimerContext context : timers) {
						Nanos nanos = context.getNanos();
						Timer timer = context.getTimer();
						nanos.checkpoint();
						if (nanos.asMillis() >= timer.getInterval()) {
							nanos.addMillis(-timer.getInterval());
							TODO
						}
					}
					// TODO

					Threads.wait(lock, 100L);
				}
			}
		}
	}
}
