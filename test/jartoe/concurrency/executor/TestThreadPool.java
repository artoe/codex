package jartoe.concurrency.executor;

import jartoe.concurrency.MutableBoolean;
import jartoe.concurrency.Nanos;
import jartoe.concurrency.Threads;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class TestThreadPool {
	@Test
	public void testExecute() {
		final MutableBoolean executed = new MutableBoolean();
		synchronized (executed) {
			ThreadPool.getInstance().execute(new Runnable() {
				public void run() {
					synchronized (executed) {
						executed.set(true);
						executed.notify();
					}
				}
			});
			Nanos n = new Nanos();
			while (!executed.is() && n.asMillis() < 100L) {
				Threads.wait(executed, 101L);
				n.checkpoint();
			}
		}
		Assert.assertTrue("runnable was not executed", executed.is());
	}

	@Test
	public void testExecuteBusy() {
		ThreadPool.getInstance().execute(Collections.nCopies(200, new Runnable() {
			public void run() {
				Threads.sleep(100L);
			}
		}));
		final MutableBoolean executed = new MutableBoolean();
		synchronized (executed) {
			ThreadPool.getInstance().execute(new Runnable() {
				public void run() {
					synchronized (executed) {
						executed.set(true);
						executed.notify();
					}
				}
			});
			Nanos n = new Nanos();
			while (!executed.is() && n.asMillis() < 500L) {
				Threads.wait(executed, 501L);
				n.checkpoint();
			}
		}
		Assert.assertTrue("runnable was not executed", executed.is());
	}
}
