package jartoe.concurrency;

import org.junit.Assert;
import org.junit.Test;

public final class NanosTest {
	@Test
	public void test001Init() {
		Assert.assertEquals(new Nanos().asNanos(), 0);
		Assert.assertEquals(new Nanos().asMillis(), 0);
	}

	@Test
	public void test002AddNanos() {
		Nanos nanos = new Nanos();
		nanos.addNanos(20000000L);
		Assert.assertEquals(20000000L, nanos.asNanos());
		nanos.addNanos(-2L);
		Assert.assertEquals(19999998L, nanos.asNanos());
	}

	@Test
	public void test003AddMillis() {
		Nanos nanos = new Nanos();
		nanos.addMillis(12);
		Assert.assertEquals(12000000L, nanos.asNanos());
		nanos.addMillis(-2);
		Assert.assertEquals(10000000L, nanos.asNanos());
	}

	@Test
	public void test004Rounding() {
		Nanos nanos = new Nanos();
		nanos.addNanos(1499999L);
		Assert.assertEquals(1L, nanos.asMillis());
		nanos.addNanos(1L);
		Assert.assertEquals(2L, nanos.asMillis());
	}

	@Test
	public void test005Checkpoint() {
		Nanos nanos = new Nanos();
		Threads.sleep(10);
		nanos.checkpoint();
		Assert.assertTrue(nanos.asNanos() > 0);

	}

	@Test
	public void test006DurationReset() {
		Nanos nanos = new Nanos();
		Threads.sleep(10);
		nanos.checkpoint();
		Assert.assertTrue(nanos.asNanos() > 0);
		nanos.resetDuration();
		Assert.assertEquals(0, nanos.asNanos());
	}

	@Test
	public void test007StartReset() {
		Nanos nanos = new Nanos();
		Threads.sleep(100);
		nanos.resetStart();
		Threads.sleep(10);
		nanos.checkpoint();
		Assert.assertTrue(nanos.asNanos() > 0);
		Assert.assertTrue(nanos.asNanos() < 50000000);
	}

	@Test
	public void test008Reset() {
		Nanos nanos = new Nanos();
		nanos.addMillis(1L);
		Threads.sleep(100);
		nanos.reset();
		Assert.assertEquals(0, nanos.asNanos());
		Threads.sleep(10);
		nanos.checkpoint();
		Assert.assertTrue(nanos.asNanos() > 0);
		Assert.assertTrue(nanos.asNanos() < 50000000);
	}
}
