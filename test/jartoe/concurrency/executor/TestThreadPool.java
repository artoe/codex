package jartoe.concurrency.executor;

import jartoe.concurrency.Mutable;
import jartoe.concurrency.MutableBoolean;
import jartoe.concurrency.Nanos;
import jartoe.concurrency.Threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class TestThreadPool {
	@Test
	public void test001Execute() {
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
			while (!executed.is() && n.asMillis() < 200L) {
				Threads.wait(executed, 201L);
				n.checkpoint();
			}
		}
		Assert.assertTrue("runnable was not executed", executed.is());
	}

	@Test
	public void test002MultipleExecute() {
		final int count = 10;
		List<Runnable> ops = new ArrayList<>(count);
		final Mutable<Integer> executed = new Mutable<>(0);
		for (int i = count; --i >= 0;) {
			ops.add(new Runnable() {
				public void run() {
					synchronized (executed) {
						int value = executed.get() + 1;
						executed.set(value);
						if (value == count)
							executed.notify();
					}
				}
			});
		}
		synchronized (executed) {
			ThreadPool.getInstance().execute(ops);
			Nanos n = new Nanos();
			while (executed.get() != count && n.asMillis() < 200L) {
				Threads.wait(executed, 201L);
				n.checkpoint();
			}
		}
		Assert.assertEquals("runnable was not executed", count, executed.get().intValue());
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
