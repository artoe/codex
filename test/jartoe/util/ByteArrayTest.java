package jartoe.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artoe
 */
public final class ByteArrayTest {
	@Test
	public void testInitEmptyArray() {
		ByteArray a = new ByteArray();
		Assert.assertEquals(0, a.available());
		Assert.assertArrayEquals(new byte[0], a.peekAll());
		Assert.assertEquals(-1, a.read());
	}

	@Test
	public void testInitWithByteArray() {
		ByteArray a = new ByteArray(new byte[] { 1, 2, 3, 4 });
		Assert.assertEquals(4, a.available());
	}
}
