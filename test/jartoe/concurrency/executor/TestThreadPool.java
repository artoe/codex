package jartoe.concurrency.executor;

import jartoe.concurrency.Mutable;
import jartoe.concurrency.MutableBoolean;
import jartoe.concurrency.Nanos;
import jartoe.concurrency.Threads;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
	public void test001Singleton() {
		ExtendedExecutor pool = ThreadPool.getInstance();
		Assert.assertNotNull(pool);
		Assert.assertSame(pool, ThreadPool.getInstance());
		ThreadPool other = ThreadPool._instance();
		Assert.assertNotNull(other);
		Assert.assertNotSame(pool, other);
	}

	@Test
	public void test002Execute() {
		final MutableBoolean executed = new MutableBoolean();
		synchronized (executed) {
			ThreadPool._instance().execute(new Runnable() {
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
	public void test003MultipleExecute() {
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
			ThreadPool._instance().execute(ops);
			Nanos n = new Nanos();
			while (executed.get() != count && n.asMillis() < 500L) {
				Threads.wait(executed, 501L);
				n.checkpoint();
			}
		}
		Assert.assertEquals("runnable was not executed", count, executed.get().intValue());
	}

	@Test
	public void test004ExecuteBusy() {
		ThreadPool._instance().execute(Collections.nCopies(200, new Runnable() {
			public void run() {
				Threads.sleep(100L);
			}
		}));
		final MutableBoolean executed = new MutableBoolean();
		synchronized (executed) {
			ThreadPool._instance().execute(new Runnable() {
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

	@Test
	public void test005OverrideCoreCount() throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, NoSuchMethodException, InvocationTargetException {
		ExtendedExecutor pool = ThreadPool._instance();
		((ThreadPool) pool)._overrideCoreCount(17);
		Field safeField = ThreadPool.class.getDeclaredField("safe");
		safeField.setAccessible(true);
		Object safeObject = safeField.get(pool);
		Method coreCountMethod = safeObject.getClass().getMethod("getCoreCount");
		int count = (int) coreCountMethod.invoke(safeObject);
		Assert.assertEquals(17, count);
	}

	@Test
	public void test006CoreCountLimitsThreadCount() {
		final int coreCount = 17;
		ThreadPool pool = ThreadPool._instance();
		pool._overrideCoreCount(coreCount);
		final Mutable<Integer> started = new Mutable<>(0);
		final Mutable<Integer> ended = new Mutable<>(0);
		synchronized (started) {
			for (int i = 5; --i >= 0;) {
				pool.execute(Collections.nCopies(20, new Runnable() {
					public void run() {
						try {
							synchronized (started) {
								int value = started.get().intValue() + 1;
								started.set(value);
								if (value == coreCount)
									started.notify();
							}
							for (;;)
								Threads.sleep(5000L);
						} finally {
							synchronized (started) {
								ended.set(ended.get().intValue() + 1);
							}
						}
					}
				}));
			}
			Nanos n = new Nanos();
			while (started.get() < coreCount && n.asMillis() < 1000L) {
				Threads.wait(started, 1000L);
				n.checkpoint();
			}
		}
		Threads.sleep(400L);
		synchronized (started) {
			Assert.assertEquals(coreCount, started.get().intValue());
			Assert.assertEquals(0, ended.get().intValue());
		}
	}

	@Test
	public void test007PollerDoesNotEatThreads() {
		final int coreCount = 3;
		ThreadPool pool = ThreadPool._instance();
		pool._overrideCoreCount(coreCount);
		final Mutable<Integer> started = new Mutable<>(0);
		final MutableBoolean pollerStarted = new MutableBoolean();
		synchronized (started) {
			pool.poller(new Runnable() {
				public void run() {
					synchronized (started) {
						pollerStarted.set(true);
					}
					for (;;)
						Threads.sleep(5000L);
				}
			});
			for (int i = 5; --i > 0;) {
				pool.execute(Collections.nCopies(20, new Runnable() {
					public void run() {
						synchronized (started) {
							int value = started.get().intValue() + 1;
							started.set(value);
							if (value == coreCount)
								started.notify();
						}
						for (;;)
							Threads.sleep(5000L);
					}
				}));
			}
			Nanos n = new Nanos();
			while (started.get() < coreCount && n.asMillis() < 1000L) {
				Threads.wait(started, 1000L);
				n.checkpoint();
			}
		}
		Threads.sleep(400L);
		synchronized (started) {
			Assert.assertEquals(true, pollerStarted.is());
			Assert.assertEquals(coreCount, started.get().intValue());
		}
	}
}
