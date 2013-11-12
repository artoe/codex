package jartoe.util.timer;

import jartoe.concurrency.Nanos;

public final class TimerContext {
	private final Timer timer;
	private int runCount;
	private final Nanos nanos = new Nanos();

	TimerContext(Timer timer) {
		this.timer = timer;
	}
}
