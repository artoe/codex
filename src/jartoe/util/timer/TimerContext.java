package jartoe.util.timer;

import jartoe.concurrency.Nanos;

public final class TimerContext {
	private final Nanos nanos = new Nanos();
	private int runCount;
	private final Timer timer;

	TimerContext(Timer timer) {
		this.timer = timer;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof TimerContext && timer.equals(((TimerContext) obj).getTimer());
	}

	@Override
	public int hashCode() {
		return timer.hashCode();
	}

	Nanos getNanos() {
		return nanos;
	}

	int getRunCount() {
		return runCount;
	}

	Timer getTimer() {
		return timer;
	}

	void setRunCount(int runCount) {
		this.runCount = runCount;
	}
}
