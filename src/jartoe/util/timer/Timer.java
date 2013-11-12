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
		return TimerThread.getInstance().isRunning(this);
	}

	public final void restart() {
		TimerThread.getInstance().restart(this);
	}

	public final void start() {
		TimerThread.getInstance().start(this);
	}

	public final void stop() {
		TimerThread.getInstance().stop(this);
	}
}
