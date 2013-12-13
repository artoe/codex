package jartoe.util.timer;

import java.util.concurrent.Executor;

/**
 * @author Artoe
 */
public abstract class Timer implements Runnable {
	public static final int FOREVER = 0;
	public static final long MIN_INTERVAL = 5L;

	private final Executor executor;
	private final long interval;
	private final int repeats;
	private final Timers timers;

	public Timer(Executor executor, int repeats, long interval) {
		this.executor = executor;
		this.repeats = repeats < 0 ? FOREVER : repeats;
		this.interval = interval < MIN_INTERVAL ? MIN_INTERVAL : interval;
		this.timers = Timers.getInstance();
	}

	public final Executor getExecutor() {
		return executor;
	}

	public final long getInterval() {
		return interval;
	}

	public final int getRepeats() {
		return repeats;
	}

	public final boolean isRunning() {
		return timers.isRunning(this);
	}

	public final void restart() {
		timers.restart(this);
	}

	public final void start() {
		timers.start(this);
	}

	public final void stop() {
		timers.stop(this);
	}
}
