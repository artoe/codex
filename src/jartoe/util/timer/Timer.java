package jartoe.util.timer;

import java.util.concurrent.Executor;

/**
 * @author Artoe
 */
public abstract class Timer implements Runnable {
	public static final int FOREVER = 0;
	public static final long MIN_INTERVAL = 10L;

	private final Executor executor;
	private final long interval;
	private final int repeats;

	public Timer(Executor executor, int repeats, long interval) {
		this.executor = executor;
		this.repeats = repeats < 0 ? FOREVER : repeats;
		this.interval = interval < MIN_INTERVAL ? MIN_INTERVAL : interval;
	}

	public Executor getExecutor() {
		return executor;
	}

	public long getInterval() {
		return interval;
	}

	public int getRepeats() {
		return repeats;
	}

	public boolean isRunning() {
		return Timers.isRunning(this);
	}

	public final void restart() {
		Timers.restart(this);
	}

	public final void start() {
		Timers.start(this);
	}

	public final void stop() {
		Timers.stop(this);
	}
}
