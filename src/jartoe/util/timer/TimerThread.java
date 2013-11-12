package jartoe.util.timer;

import java.util.ArrayList;
import java.util.List;

final class TimerThread extends Thread {
	private static final TimerThread INSTANCE = new TimerThread();

	private final List<TimerContext> timers = new ArrayList<>();

	private TimerThread() {
		super("TimerThread-1");
		setDaemon(true);
		setPriority(MAX_PRIORITY);
		start();
	}

	@Override
	public void run() {
		//TODO
	}
}
