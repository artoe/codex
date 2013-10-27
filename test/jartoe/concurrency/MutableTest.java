package jartoe.concurrency;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class MutableTest {
	@Test
	public void testNoInitialValue() {
		Assert.assertNull(new Mutable<>().get());
	}

	@Test
	public void testInitialValue() {
		Object initialValue = new Object();
		Assert.assertEquals(initialValue, new Mutable<>(initialValue).get());
	}

	@Test
	public void testSet() {
		Object o1 = new Object(), o2 = new Object();
		Mutable<Object> m = new Mutable<>();
		m.set(o1);
		Assert.assertEquals(o1, m.get());
		m.set(o2);
		Assert.assertEquals(o2, m.get());
		m.set(null);
		Assert.assertEquals(null, m.get());
	}
}
