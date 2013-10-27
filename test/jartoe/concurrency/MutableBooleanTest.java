package jartoe.concurrency;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class MutableBooleanTest {
	@Test
	public void testNoInitialValue() {
		Assert.assertFalse(new MutableBoolean().is());
	}

	@Test
	public void testInitialValue() {
		Assert.assertTrue(new MutableBoolean(true).is());
	}

	@Test
	public void testSet() {
		MutableBoolean m = new MutableBoolean();
		m.set(true);
		Assert.assertTrue(m.is());
		m.set(false);
		Assert.assertFalse(m.is());
	}
}
