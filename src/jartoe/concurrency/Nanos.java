package jartoe.concurrency;

public final class Nanos {
	private static final long HALF_MILLI_NS = 500000L;
	private static final long MILLI_NS = 1000000L;

	private long duration;
	private long start;

	public Nanos() {
		reset();
	}

	public void addMillis(long millis) {
		addNanos(MILLI_NS * millis);
	}

	public void addNanos(long nanos) {
		duration += nanos;
	}

	public long asMillis() {
		return (duration + HALF_MILLI_NS) / MILLI_NS;
	}

	public long asNanos() {
		return duration;
	}

	public void checkpoint() {
		long time = System.nanoTime();
		duration += time - start;
		start = time;
	}

	public void reset() {
		resetDuration();
		resetStart();
	}

	public void resetDuration() {
		duration = 0L;
	}

	public void resetStart() {
		this.start = System.nanoTime();
	}
}
