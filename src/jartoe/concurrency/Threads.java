package jartoe.concurrency;

import jartoe.common.Condition;

/**
 * @author Artoe
 */
public final class Threads {
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			// ignored
		}
	}

	public static void wait(Object o) {
		wait(o, 0);
	}

	public static void wait(Object o, long timeout) {
		try {
			o.wait(timeout);
		} catch (InterruptedException ex) {
			// ignored
		}
	}

	public static <Type> void waitUntil(Mutable<Type> o, Condition<Type> until) {
		waitUntil(o, o, until);
	}

	public static void waitUntil(MutableBoolean o, boolean until) {
		waitUntil(o, o, until);
	}

	public static <Type> void waitUntil(Object lock, Mutable<Type> o, Condition<Type> until) {
		synchronized (lock) {
			while (!until.test(o.get()))
				wait(lock);
		}
	}

	public static void waitUntil(Object lock, MutableBoolean o, boolean until) {
		synchronized (lock) {
			while (o.is() != until)
				wait(lock);
		}
	}

	// no instantiation
	private Threads() {}
}
